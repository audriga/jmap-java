package com.audriga.jmap.sieve;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.sieve.method.GetSieveScriptCall;
import com.audriga.jmap.sieve.method.GetSieveScriptResponse;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class SieveIT {
    @Test
    void adminCanGetSieveScripts() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(SieveAccountCapability.class);
                assertNotNull(accountId);

                var res = client.call(GetSieveScriptCall.builder()
                                .accountId(accountId)
                                .build())
                        .get()
                        .getMain(GetSieveScriptResponse.class);
                assertNotNull(res.list());
                assertTrue(res.list().isEmpty());
            }
        }
    }
}
