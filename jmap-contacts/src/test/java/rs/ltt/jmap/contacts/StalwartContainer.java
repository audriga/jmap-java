package rs.ltt.jmap.contacts;

import com.audriga.stalwart.*;
import com.google.gson.JsonObject;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okhttp3.HttpUrl;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import rs.ltt.jmap.client.JmapClient;

public final class StalwartContainer extends GenericContainer<StalwartContainer> {
    private static final DockerImageName REPOSITORY = DockerImageName.parse("stalwartlabs/stalwart");
    private static final String LATEST = "0.16.10";
    private static final String CONFIG_PATH = "/etc/stalwart/config.json";

    private final PostgreSQLContainer database;
    private final String configJson;
    private String publicUrl;

    public StalwartContainer(RemoteDockerImage image) {
        super(image);
        withNetwork(Network.newNetwork());
        database =
                new PostgreSQLContainer("postgres:18").withNetwork(getNetwork()).withNetworkAliases("postgres");
        dependsOn(database);

        var config = new JsonObject();
        config.addProperty("@type", "PostgreSql");
        config.addProperty("host", "postgres");
        config.addProperty("database", database.getDatabaseName());
        config.addProperty("authUsername", database.getUsername());
        var authSecret = new JsonObject();
        authSecret.addProperty("@type", "Value");
        authSecret.addProperty("secret", database.getPassword());
        config.add("authSecret", authSecret);
        configJson = config.toString();
        withCopyToContainer(Transferable.of(configJson), CONFIG_PATH);
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
                .withEnv(Map.of("STALWART_RECOVERY_ADMIN", "admin:pw"))) {
            bootstrap.start();
            var client = new JmapClient(
                    "admin",
                    "pw",
                    new HttpUrl.Builder()
                            .scheme("http")
                            .host(bootstrap.getHost())
                            .port(bootstrap.getFirstMappedPort())
                            .encodedPath("/.well-known/jmap")
                            .build());
            var res = client.call(new StalwartBootstrap.Set(
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
            if (res.getNotUpdated() != null) {
                throw new ContainerLaunchException("couldn't update stalwart x:Bootstrap: " + res.getNotUpdated());
            }
            System.out.println(res.getUpdated());
        } catch (ExecutionException | InterruptedException e) {
            throw new ContainerLaunchException("Stalwart bootstrap failed", e);
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
        publicUrl = "https://%s:%s".formatted(urlHost(this), port);
        addEnv("STALWART_PUBLIC_URL", publicUrl);
        super.doStart();
    }

    public String publicUrl() {
        return publicUrl;
    }

    // enclose IPv6 addresses in square brackets
    private static String urlHost(ContainerState container) {
        var host = container.getHost();
        return host.contains(":") ? '[' + host + ']' : host;
    }

    @Override
    public void close() {
        super.close();
        database.close();
        getNetwork().close();
    }
}
