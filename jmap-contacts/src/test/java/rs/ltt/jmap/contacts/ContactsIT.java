package rs.ltt.jmap.contacts;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.client.ConnectionConfig;
import rs.ltt.jmap.client.JmapClient;
import rs.ltt.jmap.client.http.BasicAuthHttpAuthentication;
import rs.ltt.jmap.contacts.method.QueryContactCardCall;
import rs.ltt.jmap.contacts.method.QueryContactCardResponse;

class ContactsIT {
    @Test
    void run() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(ContactsAccountCapability.class);
                var res = client.call(new QueryContactCardCall(accountId, null, null, null, null, null, null, true))
                        .get()
                        .getMain(QueryContactCardResponse.class);
                assertEquals(0, res.getTotal());
            }
        }
    }
}
