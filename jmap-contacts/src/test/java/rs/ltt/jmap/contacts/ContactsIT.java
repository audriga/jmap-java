package rs.ltt.jmap.contacts;

import org.junit.jupiter.api.Test;

class ContactsIT {
    @Test
    void run() {
        try (var server = StalwartContainer.latest()) {
            server.start();
        }
    }
}
