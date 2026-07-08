package com.audriga.jmap.gson;

import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.EmailAddress;
import com.audriga.jmap.common.entity.EmailBodyPart;
import com.audriga.jmap.common.entity.EmailBodyValue;
import com.audriga.jmap.common.util.MediaTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmailSerializerTest extends AbstractGsonTest {

    @Test
    public void serializeSimpleEmail() throws IOException {
        GsonBuilder builder = new GsonBuilder();
        JmapAdapters.register(builder);
        Gson gson = builder.create();
        EmailBodyValue emailBodyValue =
                EmailBodyValue.builder().value("Beware the white walkers").build();
        String partId = "1";
        EmailBodyPart emailBodyPart = EmailBodyPart.builder()
                .partId(partId)
                .mediaType(MediaTypes.TEXT_PLAIN)
                .build();
        final Email email = Email.builder()
                .to(EmailAddress.builder()
                        .email("jon.snow@ltt.rs")
                        .name("Jon Snow")
                        .build())
                .from(EmailAddress.builder()
                        .name("Arya Stark")
                        .email("arya.stark@ltt.rs")
                        .build())
                .subject("Winter is coming")
                .sentAt(OCTOBER_FIRST_8AM.atOffset(ZoneOffset.ofHours(2)))
                .receivedAt(OCTOBER_FIRST_8AM)
                .bodyValue(partId, emailBodyValue)
                .textBody(emailBodyPart)
                .build();
        Assertions.assertEquals(readResourceAsString("email/simple.json"), gson.toJson(email));
    }
}
