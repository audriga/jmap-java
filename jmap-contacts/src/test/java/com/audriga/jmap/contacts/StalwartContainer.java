package com.audriga.jmap.contacts;

import com.audriga.jmap.client.JmapClient;
import com.audriga.stalwart.*;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okhttp3.HttpUrl;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class StalwartContainer extends GenericContainer<StalwartContainer> {
    private static final DockerImageName REPOSITORY = DockerImageName.parse("stalwartlabs/stalwart");
    private static final String LATEST = "0.16.12";
    private static final String CONFIG_PATH = "/etc/stalwart/config.json";

    private final PostgreSQLContainer database;
    private HttpUrl publicUrl;
    private String username;
    private String password;

    public StalwartContainer(RemoteDockerImage image) {
        super(image);
        withNetwork(Network.newNetwork());
        database =
                new PostgreSQLContainer("postgres:18").withNetwork(getNetwork()).withNetworkAliases("postgres");
        dependsOn(database);
        waitingFor(Wait.forHttps("/.well-known/jmap").allowInsecure());
    }

    public static StalwartContainer forTag(String tag) {
        return new StalwartContainer(new RemoteDockerImage(REPOSITORY.withTag(tag)));
    }

    public static StalwartContainer latest() {
        return forTag("v" + LATEST + "-alpine");
    }

    @Override
    protected void doStart() {
        try (GenericContainer<?> bootstrap = new GenericContainer<>(getImage())
                .withNetwork(getNetwork())
                .withExposedPorts(8080)
                .withEnv(Map.of("STALWART_RECOVERY_ADMIN", "admin:pw"))
                .waitingFor(Wait.forHttp("/.well-known/jmap"))) {
            bootstrap.start();
            StalwartBootstrap.Set.Response res;
            try (var client = new JmapClient(
                    "admin",
                    "pw",
                    new HttpUrl.Builder()
                            .scheme("http")
                            .host(bootstrap.getHost())
                            .port(bootstrap.getFirstMappedPort())
                            .encodedPath("/.well-known/jmap")
                            .build())) {
                res = client.call(new StalwartBootstrap.Set(
                                "",
                                null,
                                Map.of(
                                        "serverHostname",
                                        bootstrap.getHost(),
                                        "defaultDomain",
                                        "stalwart.test",
                                        "requestTlsCertificate",
                                        false,
                                        "generateDkimKeys",
                                        false,
                                        "dataStore",
                                        new StalwartDataStore.PostgreSql(
                                                null,
                                                new StalwartPostgreSqlStore.Builder()
                                                        .allowInvalidCerts(false)
                                                        .authUsername(database.getUsername())
                                                        .authSecret(new StalwartSecretKeyOptional.Value(
                                                                new StalwartSecretKeyValue(database.getPassword())))
                                                        .database(database.getDatabaseName())
                                                        .host("postgres")
                                                        .useTls(false)
                                                        .readReplicas(Map.of())
                                                        .build()),
                                        "tracer",
                                        new StalwartTracer.Stdout(
                                                null,
                                                new StalwartTracerStdout.Builder()
                                                        .ansi(true)
                                                        .buffered(false)
                                                        .lossy(false)
                                                        .multiline(false)
                                                        .events(Map.of())
                                                        .build()))))
                        .get()
                        .getMain(StalwartBootstrap.Set.Response.class);
            } catch (ExecutionException | InterruptedException e) {
                throw new ContainerLaunchException("Stalwart bootstrap failed", e);
            }
            if (res.getNotUpdated() != null) {
                throw new ContainerLaunchException("couldn't update stalwart x:Bootstrap: " + res.getNotUpdated());
            }
            var updated = res.getUpdated().get("singleton");
            username = updated.username();
            password = updated.secret();

            var config = bootstrap.copyFileFromContainer(CONFIG_PATH, InputStream::readAllBytes);
            withCopyToContainer(Transferable.of(config), CONFIG_PATH);
        }

        // We need to set STALWART_PUBLIC_URL to the correct URL before starting the container.
        // The best solution we have is to allocate an ephemeral port and try to reuse it for the container.
        // A race condition is possible, but unlikely.
        int port;
        try (var socket = new DatagramSocket()) {
            port = socket.getLocalPort();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        addFixedExposedPort(port, 443);
        publicUrl =
                new HttpUrl.Builder().scheme("https").host(getHost()).port(port).build();
        addEnv("STALWART_PUBLIC_URL", publicUrl.toString());
        super.doStart();
    }

    public HttpUrl publicUrl() {
        return publicUrl;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public void close() {
        super.close();
        database.close();
        getNetwork().close();
    }
}
