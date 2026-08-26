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

package com.audriga.jmap.mock.server;

import static com.audriga.jmap.common.method.MethodCall.Arg.unwrapValue;
import static com.audriga.jmap.common.method.MethodCall.Arg.unwrapValueOr;

import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.entity.*;
import com.audriga.jmap.common.entity.Thread;
import com.audriga.jmap.common.entity.filter.EmailFilterCondition;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.call.core.SetPushSubscriptionMethodCall;
import com.audriga.jmap.common.method.call.email.*;
import com.audriga.jmap.common.method.call.identity.GetIdentityMethodCall;
import com.audriga.jmap.common.method.call.mailbox.ChangesMailboxMethodCall;
import com.audriga.jmap.common.method.call.mailbox.GetMailboxMethodCall;
import com.audriga.jmap.common.method.call.mailbox.SetMailboxMethodCall;
import com.audriga.jmap.common.method.call.thread.ChangesThreadMethodCall;
import com.audriga.jmap.common.method.call.thread.GetThreadMethodCall;
import com.audriga.jmap.common.method.error.*;
import com.audriga.jmap.common.method.response.core.SetPushSubscriptionMethodResponse;
import com.audriga.jmap.common.method.response.email.*;
import com.audriga.jmap.common.method.response.identity.GetIdentityMethodResponse;
import com.audriga.jmap.common.method.response.mailbox.ChangesMailboxMethodResponse;
import com.audriga.jmap.common.method.response.mailbox.GetMailboxMethodResponse;
import com.audriga.jmap.common.method.response.mailbox.SetMailboxMethodResponse;
import com.audriga.jmap.common.method.response.thread.ChangesThreadMethodResponse;
import com.audriga.jmap.common.method.response.thread.GetThreadMethodResponse;
import com.audriga.jmap.common.websocket.StateChangeWebSocketMessage;
import com.audriga.jmap.mock.server.util.FuzzyRoleParser;
import com.audriga.jmap.mua.util.MailboxUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okhttp3.HttpUrl;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockMailServer extends StubMailServer {

    private static final String VERIFICATION_CODE = "stub-verification-code";

    private static final Logger LOGGER = LoggerFactory.getLogger(MockMailServer.class);

    protected final Map<String, Email> emails = new HashMap<>();
    protected final Map<String, MailboxInfo> mailboxes = new HashMap<>();
    protected final Map<String, PushSubscription> pushSubscriptions = new HashMap<>();

    protected final LinkedHashMap<String, Update> updates = new LinkedHashMap<>();

    private int state = 0;

    private boolean reportCanCalculateQueryChanges = false;

    public MockMailServer(final int numThreads, final int accountIndex) {
        super(accountIndex);
        setup(numThreads, (accountIndex * 2048) + accountIndex);
    }

    protected void setup(final int numThreads, final int offset) {
        this.mailboxes.putAll(Maps.uniqueIndex(generateMailboxes(), MailboxInfo::id));
        generateEmail(numThreads, offset);
    }

    protected List<MailboxInfo> generateMailboxes() {
        return Collections.singletonList(new MailboxInfo(UUID.randomUUID().toString(), "Inbox", Role.INBOX));
    }

    protected void generateEmail(final int numThreads, final int offset) {
        final String mailboxId =
                MailboxUtil.find(mailboxes.values(), Role.INBOX).id();
        int emailCount = offset;
        for (int thread = 0; thread < numThreads; ++thread) {
            final int numInThread = (thread % 4) + 1;
            for (int i = 0; i < numInThread; ++i) {
                final Email email = EmailGenerator.get(account, mailboxId, emailCount, thread, i, numInThread);
                this.emails.put(email.id(), email);
                emailCount++;
            }
        }
    }

    public MockMailServer(final int numThreads) {
        super(0);
        setup(numThreads, 0);
    }

    @Override
    protected Buffer getDownloadBuffer(final String blobId) throws IOException {
        final byte[] attachment = this.inMemoryAttachments.get(blobId);
        if (attachment != null) {
            return new Buffer().readFrom(new ByteArrayInputStream(attachment));
        }
        return super.getDownloadBuffer(blobId);
    }

    public Email generateEmailOnTop() {
        final Email email = EmailGenerator.getOnTop(
                account, MailboxUtil.find(mailboxes.values(), Role.INBOX).id(), emails.size());
        createEmail(email);
        return email;
    }

    private void createEmail(final Email email) {
        final String oldVersion = getState();
        emails.put(email.id(), email);
        incrementState();
        final String newVersion = getState();
        this.pushUpdate(oldVersion, Update.created(email, newVersion));
    }

    private void pushUpdate(final String oldVersion, final Update update) {
        this.updates.put(oldVersion, update);
        final ImmutableMap.Builder<Class<? extends Identifiable>, String> changedBuilder = ImmutableMap.builder();
        changedBuilder.put(Thread.class, update.getNewVersion());
        changedBuilder.put(Email.class, update.getNewVersion());
        changedBuilder.put(Mailbox.class, update.getNewVersion());
        final StateChangeWebSocketMessage stateChange =
                new StateChangeWebSocketMessage(Map.of(accountId(), changedBuilder.build()), update.getNewVersion());
        final StateChange stateChangeMessage = StateChange.builder()
                .changed(accountId(), changedBuilder.build())
                .build();
        pushSubscriptions.values().stream()
                .filter(ps -> VERIFICATION_CODE.equals(ps.verificationCode()))
                .map(PushSubscription::url)
                .forEach(url -> Pusher.push(HttpUrl.get(url), stateChangeMessage));
        final String message = GSON.toJson(stateChange);
        pushEnabledWebSockets.forEach(webSocket -> webSocket.send(message));
    }

    protected void incrementState() {
        this.state++;
    }

    protected String getState() {
        return String.valueOf(this.state);
    }

    public void setReportCanCalculateQueryChanges(final boolean reportCanCalculateQueryChanges) {
        this.reportCanCalculateQueryChanges = reportCanCalculateQueryChanges;
    }

    @Override
    protected MethodResponse[] execute(
            SetPushSubscriptionMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var responseBuilder = SetPushSubscriptionMethodResponse.builder();
        final Map<String, PushSubscription> create = methodCall.create();
        final Map<String, Map<String, Object>> update = methodCall.update();
        final String[] destroy = methodCall.destroy();
        if (destroy != null && destroy.length > 0) {
            throw new IllegalStateException("MockServer does not know how to destroy PushSubscriptions");
        }
        if (create != null && !create.isEmpty()) {
            processCreatePushSubscription(create, responseBuilder);
        }
        if (update != null && !update.isEmpty()) {
            processUpdatePushSubscription(update, responseBuilder);
        }
        return new MethodResponse[] {responseBuilder.build()};
    }

    private void processUpdatePushSubscription(
            Map<String, Map<String, Object>> update, SetPushSubscriptionMethodResponse.Builder responseBuilder) {
        for (final Map.Entry<String, Map<String, Object>> entry : update.entrySet()) {
            final String id = entry.getKey();
            Map<String, Object> patch = entry.getValue();
            try {
                final PushSubscription modifiedPushSubscription = patchPushSubscription(id, patch);
                responseBuilder.updated(id, modifiedPushSubscription);
                this.pushSubscriptions.put(modifiedPushSubscription.id(), modifiedPushSubscription);
            } catch (final IllegalArgumentException e) {
                responseBuilder.notUpdated(id, new SetError(SetErrorType.INVALID_PROPERTIES, e.getMessage()));
            }
        }
    }

    private PushSubscription patchPushSubscription(final String id, final Map<String, Object> patches) {
        final PushSubscription current = this.pushSubscriptions.get(id);
        if (current == null) {
            throw new IllegalArgumentException(String.format("No PushSubscription found with id %s", id));
        }
        for (final Map.Entry<String, Object> patch : patches.entrySet()) {
            final String fullPath = patch.getKey();
            final Object modification = patch.getValue();
            if ("verificationCode".equals(fullPath)) {
                if (modification instanceof String code) {
                    return current.toBuilder().verificationCode(code).build();
                } else {
                    throw new IllegalArgumentException("verificationCode is not the correct type");
                }
            } else {
                throw new IllegalArgumentException("Unable to patch " + fullPath);
            }
        }
        return current;
    }

    private void processCreatePushSubscription(
            Map<String, PushSubscription> create, SetPushSubscriptionMethodResponse.Builder responseBuilder) {
        for (final Map.Entry<String, PushSubscription> entry : create.entrySet()) {
            final String createId = entry.getKey();
            final String id = UUID.randomUUID().toString();
            final PushSubscription pushSubscription =
                    entry.getValue().toBuilder().id(id).build();
            this.pushSubscriptions.put(id, pushSubscription);
            final String url = pushSubscription.url();
            PushVerification pushVerification = PushVerification.builder()
                    .pushSubscriptionId(id)
                    .verificationCode(VERIFICATION_CODE)
                    .build();
            final HttpUrl httpUrl = url == null ? null : HttpUrl.get(url);
            LOGGER.info("Sending PushVerification to {}", httpUrl);
            if (httpUrl != null) {
                if (!Pusher.push(httpUrl, pushVerification)) {
                    LOGGER.info("Failed to send Push Verification");
                }
            }
            responseBuilder.created(createId, pushSubscription);
        }
    }

    @Override
    protected MethodResponse[] execute(
            ChangesEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final String since = unwrapValue(methodCall.sinceState());
        if (since.equals(getState())) {
            return new MethodResponse[] {
                ChangesEmailMethodResponse.builder()
                        .oldState(getState())
                        .newState(getState())
                        .updated(List.of())
                        .created(List.of())
                        .destroyed(List.of())
                        .build()
            };
        } else {
            final Update update = getAccumulatedUpdateSince(since);
            if (update == null) {
                return new MethodResponse[] {new CannotCalculateChangesMethodErrorResponse()};
            } else {
                final Changes changes = update.getChangesFor(Email.class);
                return new MethodResponse[] {
                    ChangesEmailMethodResponse.builder()
                            .oldState(since)
                            .newState(update.getNewVersion())
                            .updated(changes == null ? List.of() : changes.updated())
                            .created(changes == null ? List.of() : changes.created())
                            .destroyed(List.of())
                            .hasMoreChanges(!update.getNewVersion().equals(getState()))
                            .build()
                };
            }
        }
    }

    private Update getAccumulatedUpdateSince(final String oldVersion) {
        final ArrayList<Update> updates = new ArrayList<>();
        for (Map.Entry<String, Update> updateEntry : this.updates.entrySet()) {
            if (updateEntry.getKey().equals(oldVersion) || !updates.isEmpty()) {
                updates.add(updateEntry.getValue());
            }
        }
        if (updates.isEmpty()) {
            return null;
        }
        return Update.merge(updates);
    }

    @Override
    protected MethodResponse[] execute(
            GetEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var idsArg = methodCall.ids();
        final List<String> ids;
        if (idsArg instanceof MethodCall.Arg.Reference<?> ref) {
            try {
                ids = ResultReferenceResolver.resolve(ref.reference(), previousResponses);
            } catch (final IllegalArgumentException e) {
                return new MethodResponse[] {new InvalidResultReferenceMethodErrorResponse()};
            }
        } else if (idsArg instanceof MethodCall.Arg.Value<List<String>> v) {
            ids = v.value();
        } else {
            ids = null;
        }
        final var properties = unwrapValue(methodCall.properties());
        Stream<Email> emailStream = ids.stream().map(emails::get);
        if (Email.Properties.THREAD_ID.equals(properties)) {
            emailStream = emailStream.map(email ->
                    Email.builder().id(email.id()).threadId(email.threadId()).build());
        } else if (Email.Properties.MUTABLE.equals(properties)) {
            emailStream = emailStream.map(email -> Email.builder()
                    .id(email.id())
                    .keywords(email.keywords())
                    .mailboxIds(email.mailboxIds())
                    .build());
        }
        return new MethodResponse[] {
            GetEmailMethodResponse.builder()
                    .list(emailStream.toList())
                    .state(getState())
                    .build()
        };
    }

    @Override
    protected MethodResponse[] execute(
            QueryChangesEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final String since = Objects.requireNonNull(unwrapValue(methodCall.sinceQueryState()));
        if (since.equals(getState())) {
            return new MethodResponse[] {
                QueryChangesEmailMethodResponse.builder()
                        .oldQueryState(getState())
                        .newQueryState(getState())
                        .added(List.of())
                        .removed(List.of())
                        .build()
            };
        } else {
            return new MethodResponse[] {new CannotCalculateChangesMethodErrorResponse()};
        }
    }

    @Override
    protected MethodResponse[] execute(
            QueryEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final Filter<Email> filter = unwrapValue(methodCall.filter());
        Stream<Email> stream = emails.values().stream();
        stream = applyFilter(filter, stream);

        // sort
        stream = stream.sorted(Comparator.comparing(Email::receivedAt).reversed());

        if (Boolean.TRUE.equals(unwrapValue(methodCall.collapseThreads()))) {
            stream = stream.filter(distinctByKey(Email::threadId));
        }
        final List<String> ids = stream.map(Email::id).toList();
        final String anchor = unwrapValue(methodCall.anchor());
        final int position;
        if (anchor != null) {
            final Long anchorOffset = unwrapValue(methodCall.anchorOffset());
            final int anchorPosition = ids.indexOf(anchor);
            if (anchorPosition == -1) {
                return new MethodResponse[] {new AnchorNotFoundMethodErrorResponse()};
            }
            position = Math.toIntExact(anchorPosition + (anchorOffset == null ? 0 : anchorOffset));
        } else {
            position = Math.toIntExact(unwrapValueOr(methodCall.position(), 0L));
        }
        final int limit = Math.toIntExact(unwrapValueOr(methodCall.limit(), 40L));
        final int endPosition = Math.min(position + limit, ids.size());
        final var page = ids.subList(position, endPosition);
        LOGGER.info(
                "query email page between {} and {} (inclusive). Page contains {} items",
                position,
                endPosition - 1,
                page.size());
        final Long total = unwrapValueOr(methodCall.calculateTotal(), false) ? (long) ids.size() : null;
        return new MethodResponse[] {
            QueryEmailMethodResponse.builder()
                    .canCalculateChanges(this.reportCanCalculateQueryChanges)
                    .queryState(getState())
                    .total(total)
                    .ids(page)
                    .position((long) position)
                    .build()
        };
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private static Stream<Email> applyFilter(final Filter<Email> filter, Stream<Email> emailStream) {
        if (filter instanceof EmailFilterCondition emailFilterCondition) {
            final String inMailbox = emailFilterCondition.inMailbox();
            if (inMailbox != null) {
                emailStream = emailStream.filter(email -> email.mailboxIds().containsKey(inMailbox));
            }
            final String[] header = emailFilterCondition.header();
            if (header != null && header.length == 2 && header[0].equals("Autocrypt-Setup-Message")) {
                emailStream = emailStream.filter(email -> header[1].equals(email.autocryptSetupMessage()));
            }
        }
        return emailStream;
    }

    @Override
    protected MethodResponse[] execute(
            SetEmailMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var ifInState = unwrapValue(methodCall.ifInState());
        final var update = unwrapValue(methodCall.update());
        final var create = unwrapValue(methodCall.create());
        final var destroy = unwrapValue(methodCall.destroy());
        if (destroy != null && !destroy.isEmpty()) {
            throw new IllegalStateException("MockMailServer does not know how to destroy");
        }
        final var responseBuilder = SetEmailMethodResponse.builder();
        final String oldState = getState();
        if (ifInState != null) {
            if (!ifInState.equals(oldState)) {
                return new MethodResponse[] {new StateMismatchMethodErrorResponse()};
            }
        }
        if (update != null) {
            final List<Email> modifiedEmails = new ArrayList<>();
            var updated = ImmutableMap.<String, Email>builder();
            var notUpdated = ImmutableMap.<String, SetError>builder();
            for (final Map.Entry<String, Map<String, Object>> entry : update.entrySet()) {
                final String id = entry.getKey();
                try {
                    final Email modifiedEmail = patchEmail(id, entry.getValue(), previousResponses);
                    modifiedEmails.add(modifiedEmail);
                    updated.put(id, modifiedEmail);
                } catch (final IllegalArgumentException e) {
                    notUpdated.put(id, new SetError(SetErrorType.INVALID_PROPERTIES, e.getMessage()));
                }
            }
            responseBuilder.updated(updated.build());
            responseBuilder.notUpdated(notUpdated.build());
            for (final Email email : modifiedEmails) {
                emails.put(email.id(), email);
            }
            incrementState();
            final String newState = getState();
            updates.put(oldState, Update.updated(modifiedEmails, this.mailboxes.keySet(), newState));
        }
        if (create != null && !create.isEmpty()) {
            processCreateEmail(create, responseBuilder, previousResponses);
        }
        return new MethodResponse[] {responseBuilder.build()};
    }

    private void processCreateEmail(
            Map<String, Email> create,
            SetEmailMethodResponse.Builder responseBuilder,
            ListMultimap<String, Response.Invocation> previousResponses) {
        var created = ImmutableMap.<String, Email>builder();
        for (final Map.Entry<String, Email> entry : create.entrySet()) {
            final String createId = entry.getKey();
            final String id = UUID.randomUUID().toString();
            final String threadId = UUID.randomUUID().toString();
            final Email userSuppliedEmail = entry.getValue();
            final Map<String, Boolean> mailboxMap = userSuppliedEmail.mailboxIds();
            final var emailBuilder =
                    userSuppliedEmail.toBuilder().id(id).threadId(threadId).receivedAt(Instant.now());
            emailBuilder.clearMailboxIds();
            for (Map.Entry<String, Boolean> mailboxEntry : mailboxMap.entrySet()) {
                final String mailboxId =
                        CreationIdResolver.resolveIfNecessary(mailboxEntry.getKey(), previousResponses);
                emailBuilder.mailboxId(mailboxId, mailboxEntry.getValue());
            }
            final List<EmailBodyPart> attachments = userSuppliedEmail.attachments();
            emailBuilder.clearAttachments();
            if (attachments != null) {
                for (final EmailBodyPart attachment : attachments) {
                    final String partId = attachment.partId();
                    final EmailBodyValue value = partId == null
                            ? null
                            : userSuppliedEmail.bodyValues().get(partId);
                    if (value != null) {
                        final EmailBodyPart emailBodyPart = injectId(attachment);
                        this.inMemoryAttachments.put(
                                emailBodyPart.blobId(), value.value().getBytes(StandardCharsets.UTF_8));
                        emailBuilder.attachment(emailBodyPart);
                    } else {
                        emailBuilder.attachment(attachment);
                    }
                }
            }
            final Email email = emailBuilder.build();

            createEmail(email);
            created.put(createId, email);
        }
        responseBuilder.created(created.build());
    }

    private static EmailBodyPart injectId(final Attachment attachment) {
        return EmailBodyPart.builder()
                .blobId(UUID.randomUUID().toString())
                .charset(attachment.charset())
                .type(attachment.type())
                .name(attachment.name())
                .size(attachment.size())
                .build();
    }

    @Override
    protected MethodResponse[] execute(
            GetIdentityMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        return new MethodResponse[] {
            GetIdentityMethodResponse.builder()
                    .list(List.of(Identity.builder()
                            .id(accountId())
                            .email(account.email())
                            .name(account.name())
                            .build()))
                    .build()
        };
    }

    @Override
    protected MethodResponse[] execute(
            ChangesMailboxMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var since = unwrapValue(methodCall.sinceState());
        if (since != null && since.equals(getState())) {
            return new MethodResponse[] {
                ChangesMailboxMethodResponse.builder()
                        .oldState(getState())
                        .newState(getState())
                        .updated(List.of())
                        .created(List.of())
                        .destroyed(List.of())
                        .updatedProperties(List.of())
                        .build()
            };
        } else {
            final Update update = getAccumulatedUpdateSince(since);
            if (update == null) {
                return new MethodResponse[] {new CannotCalculateChangesMethodErrorResponse()};
            } else {
                final Changes changes = update.getChangesFor(Mailbox.class);
                return new MethodResponse[] {
                    ChangesMailboxMethodResponse.builder()
                            .oldState(since)
                            .newState(update.getNewVersion())
                            .updated(changes.updated())
                            .created(changes.created())
                            .destroyed(List.of())
                            .hasMoreChanges(!update.getNewVersion().equals(getState()))
                            .build()
                };
            }
        }
    }

    @Override
    protected MethodResponse[] execute(
            GetMailboxMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var idsArg = methodCall.ids();
        final List<String> ids;
        if (idsArg instanceof MethodCall.Arg.Reference<List<String>> ref) {
            try {
                ids = ResultReferenceResolver.resolve(ref.reference(), previousResponses);
            } catch (final IllegalArgumentException e) {
                return new MethodResponse[] {new InvalidResultReferenceMethodErrorResponse()};
            }
        } else if (idsArg instanceof MethodCall.Arg.Value<List<String>> val) {
            ids = val.value();
        } else {
            ids = null;
        }
        Stream<Mailbox> mailboxStream = mailboxes.values().stream().map(this::toMailbox);
        return new MethodResponse[] {
            GetMailboxMethodResponse.builder()
                    .list(mailboxStream
                            .filter(m -> ids == null || ids.contains(m.id()))
                            .toList())
                    .state(getState())
                    .build()
        };
    }

    private Mailbox toMailbox(MailboxInfo mailboxInfo) {
        return Mailbox.builder()
                .id(mailboxInfo.id())
                .name(mailboxInfo.name)
                .role(mailboxInfo.role)
                .totalEmails(emails.values().stream()
                        .filter(e -> e.mailboxIds().containsKey(mailboxInfo.id()))
                        .count())
                .unreadEmails(emails.values().stream()
                        .filter(e -> e.mailboxIds().containsKey(mailboxInfo.id()))
                        .filter(e -> !e.keywords().containsKey(Keyword.SEEN))
                        .count())
                .totalThreads(emails.values().stream()
                        .filter(e -> e.mailboxIds().containsKey(mailboxInfo.id()))
                        .map(Email::threadId)
                        .distinct()
                        .count())
                .unreadThreads(emails.values().stream()
                        .filter(e -> e.mailboxIds().containsKey(mailboxInfo.id()))
                        .filter(e -> !e.keywords().containsKey(Keyword.SEEN))
                        .map(Email::threadId)
                        .distinct()
                        .count())
                .build();
    }

    protected MethodResponse[] execute(
            final SetMailboxMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var ifInState = unwrapValue(methodCall.ifInState());
        final var responseBuilder = SetMailboxMethodResponse.builder();
        final var create = unwrapValue(methodCall.create());
        final var update = unwrapValue(methodCall.update());
        final String oldState = getState();
        if (ifInState != null) {
            if (!ifInState.equals(oldState)) {
                return new MethodResponse[] {new StateMismatchMethodErrorResponse()};
            }
        }

        if (create != null && !create.isEmpty()) {
            processCreateMailbox(create, responseBuilder);
        }
        if (update != null && !update.isEmpty()) {
            processUpdateMailbox(update, responseBuilder, previousResponses);
        }
        incrementState();
        final SetMailboxMethodResponse setMailboxResponse = responseBuilder.build();
        updates.put(oldState, Update.of(setMailboxResponse, getState()));
        return new MethodResponse[] {setMailboxResponse};
    }

    private void processCreateMailbox(
            final Map<String, Mailbox> create, final SetMailboxMethodResponse.Builder responseBuilder) {
        var created = ImmutableMap.<String, Mailbox>builder();
        var notCreated = ImmutableMap.<String, SetError>builder();
        for (Map.Entry<String, Mailbox> entry : create.entrySet()) {
            final String createId = entry.getKey();
            final Mailbox mailbox = entry.getValue();
            final String name = mailbox.name();
            if (mailboxes.values().stream()
                    .anyMatch(mailboxInfo -> mailboxInfo.name().equals(name))) {
                notCreated.put(
                        createId,
                        new SetError(
                                SetErrorType.INVALID_PROPERTIES,
                                "A mailbox with the name " + name + " already exists"));
                continue;
            }
            final String id = UUID.randomUUID().toString();
            final MailboxInfo mailboxInfo = new MailboxInfo(id, name, mailbox.role());
            this.mailboxes.put(id, mailboxInfo);
            created.put(createId, toMailbox(mailboxInfo));
        }
        responseBuilder.created(created.build());
        responseBuilder.notCreated(notCreated.build());
    }

    private void processUpdateMailbox(
            Map<String, Map<String, Object>> update,
            SetMailboxMethodResponse.Builder responseBuilder,
            ListMultimap<String, Response.Invocation> previousResponses) {
        var updated = ImmutableMap.<String, Mailbox>builder();
        var notUpdated = ImmutableMap.<String, SetError>builder();
        for (final Map.Entry<String, Map<String, Object>> entry : update.entrySet()) {
            final String id = entry.getKey();
            try {
                final MailboxInfo modifiedMailbox = patchMailbox(id, entry.getValue(), previousResponses);
                updated.put(id, toMailbox(modifiedMailbox));
                this.mailboxes.put(modifiedMailbox.id(), modifiedMailbox);
            } catch (final IllegalArgumentException e) {
                notUpdated.put(id, new SetError(SetErrorType.INVALID_PROPERTIES, e.getMessage()));
            }
        }
        responseBuilder.updated(updated.build());
        responseBuilder.notUpdated(notUpdated.build());
    }

    private MailboxInfo patchMailbox(
            final String id,
            final Map<String, Object> patches,
            ListMultimap<String, Response.Invocation> previousResponses) {
        final MailboxInfo currentMailbox = this.mailboxes.get(id);
        for (final Map.Entry<String, Object> patch : patches.entrySet()) {
            final String fullPath = patch.getKey();
            final Object modification = patch.getValue();
            final List<String> pathParts = Splitter.on('/').splitToList(fullPath);
            final String parameter = pathParts.get(0);
            if ("role".equals(parameter)) {
                final Role role = FuzzyRoleParser.parse((String) modification);
                return new MailboxInfo(currentMailbox.id(), currentMailbox.name(), role);
            } else {
                throw new IllegalArgumentException("Unable to patch " + fullPath);
            }
        }
        return currentMailbox;
    }

    @Override
    protected MethodResponse[] execute(
            ChangesThreadMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var since = unwrapValue(methodCall.sinceState());
        if (since != null && since.equals(getState())) {
            return new MethodResponse[] {
                ChangesThreadMethodResponse.builder()
                        .oldState(getState())
                        .newState(getState())
                        .updated(List.of())
                        .created(List.of())
                        .destroyed(List.of())
                        .build()
            };
        } else {
            final Update update = getAccumulatedUpdateSince(since);
            if (update == null) {
                return new MethodResponse[] {new CannotCalculateChangesMethodErrorResponse()};
            } else {
                final Changes changes = update.getChangesFor(Thread.class);
                return new MethodResponse[] {
                    ChangesThreadMethodResponse.builder()
                            .oldState(since)
                            .newState(update.getNewVersion())
                            .updated(changes == null ? List.of() : changes.updated())
                            .created(changes == null ? List.of() : changes.created())
                            .destroyed(List.of())
                            .hasMoreChanges(!update.getNewVersion().equals(getState()))
                            .build()
                };
            }
        }
    }

    @Override
    protected MethodResponse[] execute(
            GetThreadMethodCall methodCall, ListMultimap<String, Response.Invocation> previousResponses) {
        final var idsArg = methodCall.ids();
        final List<String> ids;
        if (idsArg instanceof MethodCall.Arg.Reference<List<String>> ref) {
            try {
                ids = ResultReferenceResolver.resolve(ref.reference(), previousResponses);
            } catch (final IllegalArgumentException e) {
                return new MethodResponse[] {new InvalidResultReferenceMethodErrorResponse()};
            }
        } else if (idsArg instanceof MethodCall.Arg.Value<List<String>> val) {
            ids = val.value();
        } else {
            ids = null;
        }
        final var threads = ids.stream()
                .map(threadId -> Thread.builder()
                        .id(threadId)
                        .emailIds(emails.values().stream()
                                .filter(email -> email.threadId().equals(threadId))
                                .sorted(Comparator.comparing(Email::receivedAt))
                                .map(Email::id)
                                .collect(Collectors.toList()))
                        .build())
                .toList();
        return new MethodResponse[] {
            GetThreadMethodResponse.builder().list(threads).state(getState()).build()
        };
    }

    private Email patchEmail(
            final String id,
            final Map<String, Object> patches,
            ListMultimap<String, Response.Invocation> previousResponses) {
        final var emailBuilder = emails.get(id).toBuilder();
        for (final Map.Entry<String, Object> patch : patches.entrySet()) {
            final String fullPath = patch.getKey();
            final Object modification = patch.getValue();
            final List<String> pathParts = Splitter.on('/').splitToList(fullPath);
            final String parameter = pathParts.get(0);
            if (parameter.equals("keywords")) {
                if (pathParts.size() == 2 && modification instanceof Boolean value) {
                    final String keyword = pathParts.get(1);
                    emailBuilder.keyword(keyword, value);
                } else {
                    throw new IllegalArgumentException("Keyword modification was not split into two parts");
                }
            } else if (parameter.equals("mailboxIds")) {
                if (pathParts.size() == 2 && modification instanceof Boolean value) {
                    final String mailboxId = pathParts.get(1);
                    emailBuilder.mailboxId(mailboxId, value);
                } else if (modification instanceof Map) {
                    final Map<String, Boolean> mailboxMap = (Map<String, Boolean>) modification;
                    emailBuilder.clearMailboxIds();
                    for (Map.Entry<String, Boolean> mailboxEntry : mailboxMap.entrySet()) {
                        final String mailboxId =
                                CreationIdResolver.resolveIfNecessary(mailboxEntry.getKey(), previousResponses);
                        emailBuilder.mailboxId(mailboxId, mailboxEntry.getValue());
                    }
                } else {
                    throw new IllegalArgumentException("Unknown patch object for path " + fullPath);
                }
            } else {
                throw new IllegalArgumentException("Unable to patch " + fullPath);
            }
        }
        return emailBuilder.build();
    }

    public record MailboxInfo(String id, String name, Role role) implements IdentifiableMailboxWithRole {}
}
