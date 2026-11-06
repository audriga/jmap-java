package com.audriga.jmap.debug;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.api.ErrorResponseException;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.method.call.core.EchoMethodCall;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class DebugStalwartIT {

    @Test
    void stalwartDoesNotAdvertiseDebugCapability() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                assertNull(session.getCapability(DebugCapability.class));
            }
        }
    }

    @Test
    void sendLogsOnUnsupportingServerRejectsTheRequest() throws InterruptedException {
        // Per RFC 8620 3.6, a server MUST reject the whole request if "using" lists a
        // capability it doesn't support. ConnectorJMAP avoids ever hitting this by only
        // setting readLogs once isFeatureEnabled(DEBUG) confirms the server supports it.
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                ExecutionException exception = org.junit.jupiter.api.Assertions.assertThrows(
                        ExecutionException.class,
                        () -> client.call(EchoMethodCall.builder().build(), true)
                                .get());
                assertTrue(exception.getCause() instanceof ErrorResponseException);
                ErrorResponseException ere = (ErrorResponseException) exception.getCause();
                assertTrue(ere.getErrorResponse().type() != null);
            }
        }
    }

    @Test
    void noSendLogsYieldsNoDebugLoggerOutput() throws ExecutionException, InterruptedException {
        // AbstractJmapApiClient logs any logs() it finds on the response under this logger name.
        // Without sendLogs, the request never carries the debug capability, so the server
        // never has a reason to answer with logs, and this logger should stay silent.
        List<LogRecord> records = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        Logger debugLogger = Logger.getLogger("com.audriga.jmap.client.debug");
        debugLogger.addHandler(handler);
        debugLogger.setLevel(Level.ALL);

        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                client.call(EchoMethodCall.builder().build()).get();
            }
        } finally {
            debugLogger.removeHandler(handler);
        }

        assertTrue(records.isEmpty());
    }
}
