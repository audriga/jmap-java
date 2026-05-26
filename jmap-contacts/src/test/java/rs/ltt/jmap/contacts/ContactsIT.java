package rs.ltt.jmap.contacts;

import com.audriga.stalwart.StalwartAccount;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.client.ConnectionConfig;
import rs.ltt.jmap.client.JmapClient;
import rs.ltt.jmap.client.http.BasicAuthHttpAuthentication;

class ContactsIT {
    @Test
    void run() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var res = client.call(new StalwartAccount.Get("a", new String[] {"b"}, null, null))
                        .get()
                        .getMain(StalwartAccount.Get.Response.class);
                System.out.println(res);
            }
        }
    }
}
