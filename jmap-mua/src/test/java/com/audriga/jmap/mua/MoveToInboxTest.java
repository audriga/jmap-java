/*
 * Copyright 2019 Daniel Gultsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.audriga.jmap.mua;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveToInboxTest {
    private static final String ACCOUNT_ID = "test@example.com";
    private static final String USERNAME = "test@example.com";
    private static final String PASSWORD = "secret";
    private static final String WELL_KNOWN_PATH = ".well-known/jmap";

    @Test
    public void emailAlreadyInInbox() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("common/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("common/02-mailboxes.json"))
                    .build());
            server.start();

            try (final Mua mua = Mua.builder()
                    .sessionResource(server.url(WELL_KNOWN_PATH))
                    .username(USERNAME)
                    .password(PASSWORD)
                    .accountId(ACCOUNT_ID)
                    .build()) {
                mua.refreshMailboxes().get();
                Assertions.assertFalse(
                        mua.moveToInbox(ImmutableSet.of(new MyIdentifiableEmailWithMailboxes("e0", "mb0")))
                                .get());
            }
        }
    }

    private static String readResourceAsString(String filename) throws IOException {
        return Resources.asCharSource(Resources.getResource(filename), StandardCharsets.UTF_8)
                .read()
                .trim();
    }
}
