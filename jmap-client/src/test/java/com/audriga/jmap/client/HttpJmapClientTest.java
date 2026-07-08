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

package com.audriga.jmap.client;

import com.audriga.jmap.client.api.EndpointNotFoundException;
import com.audriga.jmap.client.api.MethodErrorResponseException;
import com.audriga.jmap.client.api.MethodResponseNotFoundException;
import com.audriga.jmap.client.api.UnauthorizedException;
import com.audriga.jmap.client.event.CloseAfter;
import com.audriga.jmap.client.http.HttpAuthentication;
import com.audriga.jmap.client.session.FileSessionCache;
import com.audriga.jmap.client.session.InMemorySessionCache;
import com.audriga.jmap.client.session.Session;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.Mailbox;
import com.audriga.jmap.common.entity.capability.WebSocketCapability;
import com.audriga.jmap.common.method.call.core.EchoMethodCall;
import com.audriga.jmap.common.method.call.mailbox.GetMailboxMethodCall;
import com.audriga.jmap.common.method.error.InvalidArgumentsMethodErrorResponse;
import com.audriga.jmap.common.method.error.UnknownMethodMethodErrorResponse;
import com.audriga.jmap.common.method.response.mailbox.GetMailboxMethodResponse;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.SocketEffect;
import okhttp3.Challenge;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HttpJmapClientTest {

    public static final String WELL_KNOWN_PATH = ".well-known/jmap";
    private static final String ACCOUNT_ID = "test@example.com";
    private static final String USERNAME = "test@example.com";
    private static final String PASSWORD = "secret";

    @TempDir
    File tempDir;

    @Test
    public void fetchMailboxes() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/02-mailboxes.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ListenableFuture<MethodResponses> future = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final GetMailboxMethodResponse mailboxResponse = future.get().getMain(GetMailboxMethodResponse.class);

            Assertions.assertEquals(7, mailboxResponse.getList().length);
        }
    }

    public static String readResourceAsString(String filename) throws IOException {
        return Resources.asCharSource(Resources.getResource(filename), StandardCharsets.UTF_8)
                .read()
                .trim();
    }

    @Test
    public void repeatedSessionFetches() throws Exception {
        try (var server = new MockWebServer()) {
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ListenableFuture<Session> firstSessionFuture = jmapClient.getSession();
            final ListenableFuture<Session> secondSessionFuture = jmapClient.getSession();

            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder().code(404).build());

            final Session firstSession = firstSessionFuture.get();
            final Session secondSession = secondSessionFuture.get();
            Assertions.assertEquals("/jmap/", firstSession.getApiUrl().encodedPath());
            Assertions.assertEquals("/jmap/", secondSession.getApiUrl().encodedPath());

            final ListenableFuture<Session> thirdSessionFuture = jmapClient.getSession();
            Assertions.assertEquals(
                    "/jmap/", thirdSessionFuture.get().getApiUrl().encodedPath());
        }
    }

    @Test
    public void fileSessionCache() throws Exception {
        try (var server = new MockWebServer()) {
            server.start();

            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder().code(404).build());

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));
            jmapClient.setSessionCache(new FileSessionCache(tempDir));

            final ListenableFuture<Session> firstSessionFuture = jmapClient.getSession();
            final Session firstSession = firstSessionFuture.get();
            Assertions.assertEquals("/jmap/", firstSession.getApiUrl().encodedPath());

            final JmapClient jmapClient2 = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));
            jmapClient2.setSessionCache(new FileSessionCache(tempDir));

            final ListenableFuture<Session> secondSessionFuture = jmapClient2.getSession();

            final Session secondSession = secondSessionFuture.get();
            Assertions.assertEquals("/jmap/", secondSession.getApiUrl().encodedPath());

            final ListenableFuture<Session> thirdSessionFuture = jmapClient2.getSession();
            Assertions.assertEquals(
                    "/jmap/", thirdSessionFuture.get().getApiUrl().encodedPath());
        }
    }

    @Test
    public void fetchMailboxesWithMethodError() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/unknown-method.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            ListenableFuture<MethodResponses> future = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final ExecutionException exception = Assertions.assertThrows(ExecutionException.class, future::get);
            final Throwable cause = exception.getCause();
            MatcherAssert.assertThat(cause, CoreMatchers.instanceOf(MethodErrorResponseException.class));
            final MethodErrorResponseException methodErrorResponseException = (MethodErrorResponseException) cause;
            MatcherAssert.assertThat(
                    methodErrorResponseException.getMethodErrorResponse(),
                    CoreMatchers.instanceOf(UnknownMethodMethodErrorResponse.class));
            Assertions.assertEquals(
                    "unknownMethod in response to Mailbox/get", methodErrorResponseException.getMessage());
            Assertions.assertEquals(0, methodErrorResponseException.getAdditional().length);
        }
    }

    @Test
    public void invalidArgumentsMethodError() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/invalid-arguments.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ExecutionException exception = Assertions.assertThrows(
                    ExecutionException.class,
                    () -> jmapClient
                            .call(GetMailboxMethodCall.builder()
                                    .accountId(ACCOUNT_ID)
                                    .build())
                            .get());
            final Throwable cause = exception.getCause();
            MatcherAssert.assertThat(cause, CoreMatchers.instanceOf(MethodErrorResponseException.class));
            final MethodErrorResponseException methodErrorResponseException = (MethodErrorResponseException) cause;
            MatcherAssert.assertThat(
                    methodErrorResponseException.getMethodErrorResponse(),
                    CoreMatchers.instanceOf(InvalidArgumentsMethodErrorResponse.class));
            Assertions.assertEquals(
                    "invalidArguments in response to Mailbox/get (I provide more details)",
                    methodErrorResponseException.getMessage());
        }
    }

    @Test
    public void fetchMailboxesException() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/unknown-method-call-id.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ExecutionException exception = Assertions.assertThrows(ExecutionException.class, () -> {
                jmapClient
                        .call(GetMailboxMethodCall.builder()
                                .accountId(ACCOUNT_ID)
                                .build())
                        .get();
            });
            MatcherAssert.assertThat(
                    exception.getCause(), CoreMatchers.instanceOf(MethodResponseNotFoundException.class));
        }
    }

    @Test
    public void endpointNotFound() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder().code(404).build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ExecutionException exception = Assertions.assertThrows(
                    ExecutionException.class,
                    () -> jmapClient
                            .call(EchoMethodCall.builder()
                                    .libraryName(Version.getUserAgent())
                                    .build())
                            .get());

            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(EndpointNotFoundException.class));
        }
    }

    @Test
    public void updateSessionResourceIfNecessary() throws IOException, InterruptedException, ExecutionException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("update-session-resource/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("update-session-resource/02-mailboxes.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("update-session-resource/03-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("update-session-resource/04-echo.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ListenableFuture<MethodResponses> mailboxFuture = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            // Wait for result
            mailboxFuture.get();

            // Skip session request
            server.takeRequest();

            Assertions.assertEquals(server.url("/jmap/"), server.takeRequest().getUrl());

            final ListenableFuture<MethodResponses> echoFuture =
                    jmapClient.call(EchoMethodCall.builder().build());

            // Wait for result
            echoFuture.get();

            // Skip session request
            server.takeRequest();

            Assertions.assertEquals(
                    server.url("/api/jmap/"), server.takeRequest().getUrl());
        }
    }

    @Test
    public void useStoredSessionResource() throws IOException, ExecutionException, InterruptedException {
        final AtomicInteger cacheReadAttempts = new AtomicInteger();
        final AtomicInteger cacheHits = new AtomicInteger();
        final InMemorySessionCache sessionCache = new InMemorySessionCache() {
            @Override
            public ListenableFuture<Session> load(final String username, final HttpUrl sessionResource) {
                cacheReadAttempts.incrementAndGet();
                final ListenableFuture<Session> future = super.load(username, sessionResource);
                try {
                    if (future.get() != null) {
                        cacheHits.incrementAndGet();
                    }
                } catch (Exception e) {
                    // ignored
                }
                return future;
            }
        };
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/02-mailboxes.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/02-mailboxes.json"))
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));
            jmapClient.setSessionCache(sessionCache);
            jmapClient.getSession().get();

            final JmapClient secondJmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));
            secondJmapClient.setSessionCache(sessionCache);

            final ListenableFuture<MethodResponses> firstFuture = secondJmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final GetMailboxMethodResponse firstMailboxResponse =
                    firstFuture.get().getMain(GetMailboxMethodResponse.class);

            Assertions.assertEquals(7, firstMailboxResponse.getList().length);

            final ListenableFuture<MethodResponses> secondFuture = secondJmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final GetMailboxMethodResponse secondMailboxResponse =
                    secondFuture.get().getMain(GetMailboxMethodResponse.class);

            Assertions.assertEquals(7, secondMailboxResponse.getList().length);

            Assertions.assertEquals(2, cacheReadAttempts.get(), "Unexpected number of session cache read attempts");

            Assertions.assertEquals(1, cacheHits.get(), "Unexpected number of session cache read attempts");
        }
    }

    @Test
    public void redirectFromWellKnown() throws IOException, ExecutionException, InterruptedException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .code(301)
                    .addHeader("Location", "/jmap")
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .code(301)
                    .addHeader("Location", "/jmap/")
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("redirect/01-session.json"))
                    .build());

            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            Session session = jmapClient.getSession().get();

            Assertions.assertEquals(server.url("/jmap/"), session.getBase());
        }
    }

    @Test
    public void downloadUploadAndEventSourceUrlTest() throws IOException, ExecutionException, InterruptedException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("session-urls/01-session.json"))
                    .build());
            server.start();
            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final Session session = jmapClient.getSession().get();

            HttpUrl download = session.getDownloadUrl(USERNAME, "B10B1D", "lttrs", "text/plain");
            HttpUrl upload = session.getUploadUrl(USERNAME);
            HttpUrl eventSource =
                    session.getEventSourceUrl(Arrays.asList(Email.class, Mailbox.class), CloseAfter.STATE, 300L);

            Assertions.assertEquals(
                    server.url("/jmap/download/test%40example.com/B10B1D/lttrs?accept=text%2Fplain"), download);
            Assertions.assertEquals(server.url("/jmap/upload/test%40example.com/"), upload);
            Assertions.assertEquals(
                    server.url("jmap/eventsource/?types=Email,Mailbox&closeafter=state&ping=300"), eventSource);
        }
    }

    @Test
    public void incompleteSessionResource() throws IOException, ExecutionException, InterruptedException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("broken-session-urls/01-session.json"))
                    .build());
            server.start();
            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final Session session = jmapClient.getSession().get();
            Assertions.assertThrows(IllegalStateException.class, () -> session.getUploadUrl(USERNAME));
        }
    }

    @Test
    public void webSocketUrl() throws IOException, ExecutionException, InterruptedException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("session-urls/02-session-ws.json"))
                    .build());
            server.start();
            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final Session session = jmapClient.getSession().get();
            Assertions.assertNotNull(
                    session.getCapability(WebSocketCapability.class).getUrl());
        }
    }

    @Test
    public void invalidJsonResponse() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("session-urls/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder().body("Garbage").build());
            server.start();
            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));
            final ListenableFuture<MethodResponses> future = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final ExecutionException executionException = Assertions.assertThrows(
                    ExecutionException.class, () -> future.get().getMain(GetMailboxMethodResponse.class));

            MatcherAssert.assertThat(executionException.getCause(), CoreMatchers.instanceOf(JsonParseException.class));
        }
    }

    @Test
    public void callIsCancelableSession() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .onRequestStart(SocketEffect.Stall.INSTANCE)
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ListenableFuture<MethodResponses> future = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final Dispatcher dispatcher = Services.okHttpClient().dispatcher();
            future.cancel(true);
            Thread.sleep(1000); // wait for cancel to propagate.
            Assertions.assertEquals(
                    0, dispatcher.runningCallsCount() + dispatcher.queuedCallsCount(), "Call has not been cancelled");
        }
    }

    @Test
    public void callIsCancelableRequest() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/01-session.json"))
                    .build());
            server.enqueue(new MockResponse.Builder()
                    .body(readResourceAsString("fetch-mailboxes/02-mailboxes.json"))
                    .throttleBody(1, 1, TimeUnit.SECONDS)
                    .build());
            server.start();

            final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH));

            final ListenableFuture<MethodResponses> future = jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build());

            final Dispatcher dispatcher = Services.okHttpClient().dispatcher();

            Thread.sleep(1000);
            future.cancel(true);
            Thread.sleep(1000); // wait for cancel to propagate

            Assertions.assertEquals(
                    0, dispatcher.runningCallsCount() + dispatcher.queuedCallsCount(), "Call has not been cancelled");
        }
    }

    @Test
    public void authenticationRequired() throws IOException {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder()
                    .code(401)
                    .addHeader(
                            "WWW-Authenticate",
                            "Digest"
                                    + " nonce=\"arSDq0NLJbAtLqpKIGcP6hSK4GA78ggjJZ+48c2FGqs=\",realm=\"example\",qop=\"auth\",charset=utf-8,algorithm=md5-sess")
                    .addHeader("WWW-Authenticate", "Basic realm=\"example\"")
                    .addHeader("WWW-Authenticate", "Bearer")
                    .build());
            server.start();
            try (final JmapClient jmapClient = new JmapClient(USERNAME, PASSWORD, server.url(WELL_KNOWN_PATH))) {
                final ListenableFuture<Session> future = jmapClient.getSession();

                final ExecutionException executionException =
                        Assertions.assertThrows(ExecutionException.class, future::get);
                final Throwable cause = executionException.getCause();
                MatcherAssert.assertThat(cause, CoreMatchers.instanceOf(UnauthorizedException.class));
                final UnauthorizedException unauthorizedException = (UnauthorizedException) cause;
                final Collection<Challenge> challenges = unauthorizedException.getChallenges();
                Assertions.assertEquals(3, challenges.size());
                Assertions.assertTrue(challenges.stream().anyMatch(c -> "Bearer".equals(c.scheme())));
                Assertions.assertEquals(
                        ImmutableSet.of(
                                HttpAuthentication.Scheme.BASIC,
                                HttpAuthentication.Scheme.BEARER,
                                HttpAuthentication.Scheme.DIGEST),
                        unauthorizedException.getAuthenticationSchemes());
            }
        }
    }
}
