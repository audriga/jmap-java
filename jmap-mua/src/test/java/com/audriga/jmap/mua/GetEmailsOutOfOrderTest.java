/*
 * Copyright 2020 Daniel Gultsch
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

import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.Keyword;
import com.audriga.jmap.common.entity.Mailbox;
import com.audriga.jmap.common.entity.Role;
import com.audriga.jmap.common.entity.query.EmailQuery;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.call.email.GetEmailMethodCall;
import com.audriga.jmap.common.method.response.email.GetEmailMethodResponse;
import com.audriga.jmap.mock.server.JmapDispatcher;
import com.audriga.jmap.mock.server.MockMailServer;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetEmailsOutOfOrderTest {
    @Test
    public void emailGetIsOutOfOrder() throws IOException, ExecutionException, InterruptedException {
        try (var server = new MockWebServer()) {
            final MockMailServer mailServer = new GetEmailOutOfOrder(2);
            server.setDispatcher(mailServer);
            server.start();

            final MyInMemoryCache cache = new MyInMemoryCache();
            try (final Mua mua = Mua.builder()
                    .cache(cache)
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username(mailServer.getUsername())
                    .password(JmapDispatcher.PASSWORD)
                    .accountId(mailServer.getUsername())
                    .build()) {
                mua.query(EmailQuery.unfiltered()).get();
                final Mailbox mailboxBeforeModification = cache.getMailbox(Role.INBOX);
                Assertions.assertEquals(
                        2, mailboxBeforeModification.getUnreadThreads(), "Miss match in unread threads");
                Assertions.assertEquals(3, mailboxBeforeModification.getUnreadEmails(), "Miss match in unread emails");
                final List<CachedEmail> emails = cache.getEmails("T1");
                mua.setKeyword(emails, Keyword.SEEN).get();

                mua.refresh().get();
            }
        }
    }

    private static class GetEmailOutOfOrder extends MockMailServer {
        public GetEmailOutOfOrder(int numThreads) {
            super(numThreads);
        }

        @Override
        protected MethodResponse[] execute(
                GetEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
            final MethodResponse[] response = super.execute(methodCall, previousResponses);
            GetEmailMethodResponse getEmailMethodResponse = (GetEmailMethodResponse) response[0];
            return new MethodResponse[] {
                GetEmailMethodResponse.builder()
                        .list(Lists.reverse(Arrays.asList(getEmailMethodResponse.getList()))
                                .toArray(new Email[0]))
                        .state(getState())
                        .build()
            };
        }
    }
}
