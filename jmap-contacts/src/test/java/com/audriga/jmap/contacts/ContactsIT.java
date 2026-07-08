package com.audriga.jmap.contacts;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.contacts.entity.ContactCard;
import com.audriga.jmap.contacts.method.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

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
                {
                    var res = client.call(new QueryContactCardCall(accountId, null, null, null, null, null, null, true))
                            .get()
                            .getMain(QueryContactCardResponse.class);
                    assertEquals(0, res.getTotal());
                }
                String addressBookId;
                {
                    var res = client.call(new GetAddressBookCall(accountId, null, null, null))
                            .get()
                            .getMain(GetAddressBookResponse.class);
                    assertEquals(1, res.getList().length);
                    addressBookId = res.getList()[0].id();
                }
                {
                    var res = client.call(new SetContactCardCall(
                                    accountId,
                                    null,
                                    Map.of(
                                            "a",
                                            ContactCard.builder()
                                                    .addressBookIds(Set.of(addressBookId))
                                                    .build()),
                                    null,
                                    null,
                                    null))
                            .get()
                            .getMain(SetContactCardResponse.class);
                    assertNull(res.getDestroyed());
                    assertNull(res.getUpdated());
                    assertNull(res.getNotCreated());
                    assertNull(res.getNotDestroyed());
                    assertNull(res.getNotUpdated());
                    assertEquals(1, res.getCreated().size());
                    var created = res.getCreated().get("a");
                    assertEquals(
                            ContactCard.builder()
                                    .id(created.id())
                                    .kind("individual")
                                    .build(),
                            created);
                }
            }
        }
    }
}
