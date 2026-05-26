package com.audriga.jmap.contacts;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.calendars.CalendarsAccountCapability;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.calendars.entity.Participant;
import com.audriga.jmap.calendars.method.*;
import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.DateTimePeriod;
import com.audriga.jmap.common.entity.capability.MailAccountCapability;
import com.audriga.jmap.common.method.call.email.GetEmailMethodCall;
import com.audriga.jmap.common.method.response.email.GetEmailMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import com.audriga.jmap.contacts.method.*;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ContactsIT {
    @Test
    @Disabled
    void interactive() throws InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            System.out.println(server.publicUrl());
            System.out.println(server.username());
            System.out.println(server.password());
            while (true) {
                Thread.sleep(1000 * 60);
            }
        }
    }

    @Test
    @Disabled
    void interactiveClient() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(new ConnectionConfig(
                new BasicAuthHttpAuthentication("admin@stalwart.test", "kTPSxek9uREcGTKh"),
                HttpUrl.parse("https://localhost:43697/.well-known/jmap"),
                InsecureX509TrustManager.INSTANCE))) {
            var session = client.getSession().get();
            String accountId = session.getPrimaryAccount(CalendarsAccountCapability.class);
            var calId = client.call(new GetCalendarCall(accountId, null, null, null))
                    .get()
                    .getMain(GetCalendarResponse.class)
                    .getList()[0]
                    .id();
            var res = client.call(new SetCalendarEventCall(
                            accountId,
                            null,
                            Map.of(
                                    "a",
                                    CalendarEvent.builder()
                                            .calendarIds(Set.of(calId))
                                            .title("Event diff org!")
                                            .organizerCalendarAddress(URI.create("mailto:foo@stalwart.test"))
                                            .description("heya")
                                            .start(LocalDateTime.now().plusDays(2))
                                            .timeZone(ZoneId.of("Europe/Berlin"))
                                            .duration(new DateTimePeriod(Period.ZERO, Duration.ofHours(1)))
                                            .participants(Map.of(
                                                    "or",
                                                    Participant.builder()
                                                            .name("Foo (organizer)")
                                                            .calendarAddress(URI.create("mailto:foo@stalwart.test"))
                                                            .roles(Set.of("owner", "attendee"))
                                                            .build()))
                                            .build()),
                            null,
                            null,
                            null,
                            true))
                    .get()
                    .getMain(SetCalendarEventResponse.class);
            assertNull(res.getNotCreated());
            var res2 = client.call(
                            GetCalendarEventCall.builder().accountId(calId).build())
                    .get()
                    .getMain(GetCalendarEventResponse.class);
            System.out.println(Arrays.toString(res2.getList()));
        }
    }

    @Test
    @Disabled
    void interactiveReceive() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(new ConnectionConfig(
                new BasicAuthHttpAuthentication("foo@stalwart.test", "D8AWdFRrXyBcn24l"),
                HttpUrl.parse("https://localhost:60890/.well-known/jmap"),
                InsecureX509TrustManager.INSTANCE))) {
            var session = client.getSession().get();
            String accountId = session.getPrimaryAccount(MailAccountCapability.class);

            var emailRes = client.call(
                            GetEmailMethodCall.builder().accountId(accountId).build())
                    .get()
                    .getMain(GetEmailMethodResponse.class);
            System.out.println(Arrays.toString(emailRes.getList()));

            var eventRes = client.call(
                            GetCalendarEventCall.builder().accountId(accountId).build())
                    .get()
                    .getMain(GetCalendarEventResponse.class);
            System.out.println(Arrays.toString(eventRes.getList()));
        }
    }

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
