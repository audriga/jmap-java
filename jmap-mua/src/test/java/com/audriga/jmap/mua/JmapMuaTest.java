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

import com.audriga.jmap.client.api.ErrorResponseException;
import com.audriga.jmap.client.api.InvalidSessionResourceException;
import com.audriga.jmap.client.api.MethodErrorResponseException;
import com.audriga.jmap.client.api.UnauthorizedException;
import com.audriga.jmap.client.util.WellKnownUtil;
import com.audriga.jmap.common.ErrorResponse;
import com.audriga.jmap.common.GenericResponse;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.entity.ErrorType;
import com.audriga.jmap.common.entity.Mailbox;
import com.audriga.jmap.common.entity.Role;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.call.mailbox.GetMailboxMethodCall;
import com.audriga.jmap.common.method.response.mailbox.GetMailboxMethodResponse;
import com.audriga.jmap.mock.server.JmapDispatcher;
import com.audriga.jmap.mock.server.StubMailServer;
import com.audriga.jmap.mua.cache.InMemoryCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JmapMuaTest {
    @Test
    public void oneInboxMailbox() throws ExecutionException, InterruptedException, IOException {
        try (var server = new MockWebServer()) {
            final EmailServer emailServer = new EmailServer();
            server.setDispatcher(emailServer);
            server.start();

            final MyInMemoryCache myInMemoryCache = new MyInMemoryCache();

            try (Mua mua = Mua.builder()
                    .cache(myInMemoryCache)
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username(emailServer.getUsername())
                    .password(JmapDispatcher.PASSWORD)
                    .accountId(emailServer.getAccountId())
                    .build()) {
                mua.refreshMailboxes().get();
            }
            final Mailbox mailbox = Iterables.getFirst(myInMemoryCache.getMailboxes(), null);
            Assertions.assertNotNull(mailbox);
            Assertions.assertEquals(Role.INBOX, mailbox.getRole());
        }
    }

    @Test
    public void methodNotFound() throws IOException {
        try (var server = new MockWebServer()) {
            final EmailServer emailServer = new EmailServer();
            server.setDispatcher(emailServer);
            server.start();

            final ExecutionException executionException;
            try (Mua mua = Mua.builder()
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username(emailServer.getUsername())
                    .password(JmapDispatcher.PASSWORD)
                    .accountId(emailServer.getAccountId())
                    .build()) {
                executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
            }
            MatcherAssert.assertThat(
                    executionException.getCause(), CoreMatchers.instanceOf(MethodErrorResponseException.class));
        }
    }

    @Test
    public void errorResponse() throws IOException {
        try (var server = new MockWebServer()) {
            final UnknownCapabilityMailServer emailServer = new UnknownCapabilityMailServer();
            server.setDispatcher(emailServer);
            server.start();

            try (final Mua mua = Mua.builder()
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username(emailServer.getUsername())
                    .password(JmapDispatcher.PASSWORD)
                    .accountId(emailServer.getAccountId())
                    .build()) {
                final ExecutionException executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
                MatcherAssert.assertThat(
                        executionException.getCause(), CoreMatchers.instanceOf(ErrorResponseException.class));

                final ErrorResponseException errorResponseException =
                        (ErrorResponseException) executionException.getCause();
                Assertions.assertEquals(
                        ErrorType.UNKNOWN_CAPABILITY,
                        errorResponseException.getErrorResponse().getType());
            }
        }
    }

    @Test
    public void unauthorized() throws IOException {
        try (var server = new MockWebServer()) {
            final EmailServer emailServer = new EmailServer();
            server.setDispatcher(emailServer);
            server.start();

            try (final Mua mua = Mua.builder()
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username(emailServer.getUsername())
                    .password("wrong")
                    .accountId(emailServer.getAccountId())
                    .build()) {
                final ExecutionException executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
                MatcherAssert.assertThat(
                        executionException.getCause(), CoreMatchers.instanceOf(UnauthorizedException.class));
            }
        }
    }

    @Test
    public void trailingSpaceUsername() throws IOException {
        try (final MockWebServer server = new MockWebServer()) {
            final EmailServer emailServer = new EmailServer();
            server.setDispatcher(emailServer);
            server.start();

            try (final Mua mua = Mua.builder()
                    .username("test@example.com ")
                    .password("wrong")
                    .accountId(emailServer.getAccountId())
                    .build()) {
                final ExecutionException executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
                MatcherAssert.assertThat(
                        executionException.getCause(),
                        CoreMatchers.instanceOf(WellKnownUtil.MalformedUsernameException.class));
            }
        }
    }

    @Test
    public void invalidSessionResourceEmpty() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder().body("{}").code(200).build());
            server.start();

            try (final Mua mua = Mua.builder()
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username("irrelevant")
                    .password("wrong")
                    .accountId("irrelevant")
                    .build()) {
                final ExecutionException executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
                MatcherAssert.assertThat(
                        executionException.getCause(), CoreMatchers.instanceOf(InvalidSessionResourceException.class));
            }
        }
    }

    @Test
    public void invalidSessionResourceJson() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder().body("{]").code(200).build());
            server.start();

            try (final Mua mua = Mua.builder()
                    .sessionResource(server.url(JmapDispatcher.WELL_KNOWN_PATH))
                    .username("irrelevant")
                    .password("wrong")
                    .accountId("irrelevant")
                    .build()) {
                final ExecutionException executionException = Assertions.assertThrows(
                        ExecutionException.class, () -> mua.refreshIdentities().get());
                MatcherAssert.assertThat(
                        executionException.getCause(), CoreMatchers.instanceOf(InvalidSessionResourceException.class));
            }
        }
    }

    private static class EmailServer extends StubMailServer {
        @Override
        protected MethodResponse[] execute(
                final GetMailboxMethodCall methodCall,
                final ListMultimap<String, Response.Invocation> previousResponses) {
            return new MethodResponse[] {
                GetMailboxMethodResponse.builder()
                        .list(new Mailbox[] {
                            Mailbox.builder().name("Inbox").role(Role.INBOX).build()
                        })
                        .accountId(getAccountId())
                        .build()
            };
        }
    }

    private static class UnknownCapabilityMailServer extends StubMailServer {
        @Override
        protected GenericResponse dispatch(final Request request) {
            return new ErrorResponse(ErrorType.UNKNOWN_CAPABILITY, 400);
        }
    }

    private static class MyInMemoryCache extends InMemoryCache {
        public Collection<Mailbox> getMailboxes() {
            return this.mailboxes.values();
        }
    }
}
