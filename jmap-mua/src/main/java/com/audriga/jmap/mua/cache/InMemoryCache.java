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

package com.audriga.jmap.mua.cache;

import com.audriga.jmap.common.entity.*;
import com.audriga.jmap.common.entity.Thread;
import com.audriga.jmap.mua.cache.exception.*;
import com.audriga.jmap.mua.util.QueryResult;
import com.audriga.jmap.mua.util.QueryResultItem;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryCache implements Cache {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCache.class);

    protected final HashMap<String, Mailbox> mailboxes = new HashMap<>();
    protected final HashMap<String, Thread> threads = new HashMap<>();
    protected final HashMap<String, Email> emails = new HashMap<>();
    protected final HashMap<String, Identity> identities = new HashMap<>();
    protected final HashMap<String, InMemoryQueryResult> queryResults = new HashMap<>();
    private String mailboxState = null;
    private String threadState = null;
    private String emailState = null;
    private String identityState = null;

    @Override
    public String getIdentityState() {
        return identityState;
    }

    @Override
    public String getMailboxState() {
        return mailboxState;
    }

    @Override
    @NonNull
    public QueryStateWrapper getQueryState(String query) {
        synchronized (this.queryResults) {
            final String mailboxState = this.mailboxState;
            final String threadState = this.threadState;
            final String emailState = this.emailState;
            final ObjectsState objectsState = new ObjectsState(mailboxState, threadState, emailState);
            final InMemoryQueryResult queryResult = queryResults.get(query);
            if (queryResult == null) {
                return new QueryStateWrapper(null, false, null, objectsState);
            } else {
                final QueryStateWrapper.UpTo upTo;
                if (!queryResult.items.isEmpty()) {
                    final int lastPosition = queryResult.items.size() - 1;
                    final QueryResultItem lastItem = queryResult.items.get(lastPosition);
                    final String id = lastItem.getEmailId();
                    upTo = new QueryStateWrapper.UpTo(id, lastPosition);
                } else {
                    upTo = null;
                }
                return new QueryStateWrapper(
                        queryResult.queryState, queryResult.canCalculateChanges, upTo, objectsState);
            }
        }
    }

    @NonNull
    @Override
    public ObjectsState getObjectsState() {
        return new ObjectsState(mailboxState, threadState, emailState);
    }

    @Override
    public void setMailboxes(TypedState<Mailbox> state, Mailbox[] mailboxes) {
        synchronized (this.mailboxes) {
            this.mailboxes.clear();
            for (Mailbox mailbox : mailboxes) {
                this.mailboxes.put(mailbox.id(), mailbox);
            }
            this.mailboxState = state.getState();
        }
    }

    @Override
    public void updateMailboxes(Update<Mailbox> mailboxUpdate, final String[] updatedProperties)
            throws CacheWriteException {
        synchronized (this.mailboxes) {
            for (Mailbox mailbox : mailboxUpdate.created()) {
                this.mailboxes.put(mailbox.id(), mailbox);
            }
            for (Mailbox mailbox : mailboxUpdate.updated()) {
                Mailbox target = mailboxes.get(mailbox.id());
                if (target == null) {
                    throw new CacheWriteException(
                            String.format("Unable to update Mailbox(%s). Can not find in cache", mailbox.id()));
                }
                if (updatedProperties != null) {
                    for (String property : updatedProperties) { // can be null
                        try {
                            copyProperty(target, mailbox, property, Mailbox.class);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new CacheWriteException(
                                    String.format(
                                            "Unable to update Mailbox(%s). Can not update field %s",
                                            mailbox.id(), property),
                                    e);
                        }
                    }
                } else {
                    this.mailboxes.put(mailbox.id(), mailbox);
                }
            }
            for (String id : mailboxUpdate.destroyed()) {
                this.mailboxes.remove(id);
            }
            this.mailboxState = mailboxUpdate.getNewTypedState().getState();
        }
    }

    @Override
    public Collection<Mailbox> getSpecialMailboxes() throws NotSynchronizedException {
        synchronized (this.mailboxes) {
            if (this.mailboxState == null) {
                throw new NotSynchronizedException("Mailboxes have not been synchronized yet. Run refresh() first.");
            }
            return this.mailboxes.values();
        }
    }

    @Override
    public IdentifiableMailboxWithRoleAndName getMailboxByNameAndParent(String name, String parentId)
            throws NotSynchronizedException {
        synchronized (this.mailboxes) {
            if (this.mailboxState == null) {
                throw new NotSynchronizedException("Mailboxes have not been synchronized yet. Run refresh() first.");
            }
            return this.mailboxes.values().stream()
                    .filter(mailbox -> mailbox.name().equals(name) && matches(mailbox.parentId(), parentId))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public Collection<IdentifiableMailboxWithRoleAndName> getMailboxesByNames(final String[] names) {
        final List<String> filter = Arrays.asList(names);
        return this.mailboxes.values().stream()
                .filter(mailbox -> filter.contains(mailbox.name()))
                .collect(Collectors.toList());
    }

    private static boolean matches(final String a, final String b) {
        return (a == null && b == null || (a != null && a.equals(b)));
    }

    @Override
    public void setThreadsAndEmails(
            TypedState<Thread> threadState, Thread[] threads, TypedState<Email> emailState, Email[] emails) {
        setThreads(threadState, threads);
        setEmails(emailState, emails);
    }

    @Override
    public void addThreadsAndEmail(
            TypedState<Thread> threadState, Thread[] threads, TypedState<Email> emailState, Email[] emails) {
        addThreads(threadState, threads);
        addEmails(emailState, emails);
    }

    private void setThreads(final TypedState<Thread> typedState, Thread[] threads) {
        synchronized (this.threads) {
            this.threads.clear();
            for (Thread thread : threads) {
                this.threads.put(thread.id(), thread);
            }
            this.threadState = typedState.getState();
        }
    }

    private void addThreads(final TypedState<Thread> typedState, final Thread[] threads) throws CacheConflictException {
        synchronized (this.threads) {
            if (typedState.getState() == null || !typedState.getState().equals(this.threadState)) {
                throw new CacheConflictException(String.format(
                        "Trying to add threads with an outdated state. Run update first."
                                + " Cached state=%s. Your state=%s",
                        this.threadState, typedState.getState()));
            }
            for (Thread thread : threads) {
                this.threads.put(thread.id(), thread);
            }
        }
    }

    @Override
    public void updateThreads(Update<Thread> threadUpdate) throws CacheWriteException {
        synchronized (this.threads) {

            // TODO check state

            for (Thread thread : threadUpdate.created()) {
                if (threads.containsKey(thread.id())) {
                    throw new CacheWriteException(
                            String.format("Unable to create Thread(%s). Thread already exists", thread.id()));
                } else {
                    this.threads.put(thread.id(), thread);
                }
            }
            for (Thread thread : threadUpdate.updated()) {
                if (!this.threads.containsKey(thread.id())) {
                    throw new CacheWriteException(
                            String.format("Unable to update Thread(%s). Thread doesnt exists", thread.id()));
                }
                this.threads.put(thread.id(), thread);
            }
            for (String id : threadUpdate.destroyed()) {
                this.threads.remove(id);
            }
            this.threadState = threadUpdate.getNewTypedState().getState();
        }
    }

    private void setEmails(TypedState<Email> typedState, Email[] emails) {
        synchronized (this.emails) {
            this.emails.clear();
            for (Email email : emails) {
                this.emails.put(email.id(), email);
            }
            this.emailState = typedState.getState();
        }
    }

    private void addEmails(TypedState<Email> typedState, Email[] emails) throws CacheConflictException {
        synchronized (this.emails) {
            if (typedState.getState() == null || !typedState.getState().equals(this.emailState)) {
                throw new CacheConflictException(String.format(
                        "Trying to add emails with an outdated state. Run update first."
                                + " Cached state=%s. Your state=%s",
                        this.emailState, typedState.getState()));
            }
            for (Email email : emails) {
                this.emails.put(email.id(), email);
            }
        }
    }

    @Override
    public void updateEmails(Update<Email> emailUpdate, String[] updatedProperties) throws CacheWriteException {
        synchronized (this.emails) {

            // TODO check state

            for (Email email : emailUpdate.created()) {
                this.emails.put(email.id(), email);
            }
            for (Email email : emailUpdate.updated()) {
                Email target = emails.get(email.id());
                if (target == null) {
                    throw new CacheWriteException(
                            String.format("Unable to update Email(%s). Can not find in cache", email.id()));
                }
                for (String property : updatedProperties) {
                    try {
                        copyProperty(target, email, property, Email.class);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new CacheWriteException(
                                String.format(
                                        "Unable to update Mailbox(%s). Can not update field %s", email.id(), property),
                                e);
                    }
                }
            }
            for (String id : emailUpdate.destroyed()) {
                this.emails.remove(id);
            }
            this.emailState = emailUpdate.getNewTypedState().getState();
        }
    }

    @Override
    public void setIdentities(final TypedState<Identity> typedState, final Identity[] identities) {
        synchronized (this.identities) {
            this.identities.clear();
            for (Identity identity : identities) {
                this.identities.put(identity.id(), identity);
            }
            if (typedState.getState() == null) {
                LOGGER.warn("Identity state was null");
            } else {
                this.identityState = typedState.getState();
            }
        }
    }

    @Override
    public void updateIdentities(Update<Identity> identityUpdate) throws CacheWriteException {
        synchronized (this.identities) {
            for (Identity identity : identityUpdate.created()) {
                if (this.identities.containsKey(identity.id())) {
                    throw new CacheWriteException(
                            String.format("Unable to create Identity(%s). Identity already exists", identity.id()));
                } else {
                    this.identities.put(identity.id(), identity);
                }
            }
            for (Identity identity : identityUpdate.updated()) {
                if (!this.identities.containsKey(identity.id())) {
                    throw new CacheWriteException(
                            String.format("Unable to update Identity(%s). Identity doesnt exists", identity.id()));
                }
                this.identities.put(identity.id(), identity);
            }
            for (String id : identityUpdate.destroyed()) {
                this.identities.remove(id);
            }
            this.identityState = identityUpdate.getNewTypedState().getState();
        }
    }

    @Override
    public void invalidateIdentities() {
        synchronized (this.identities) {
            this.identities.clear();
            this.identityState = null;
        }
    }

    @Override
    public void setQueryResult(String query, QueryResult queryResult) {
        synchronized (this.queryResults) {
            final String emailState = queryResult.objectState().getState();
            if (emailState == null || !emailState.equals(this.emailState)) {
                throw new CacheConflictException(String.format(
                        "Email state must match when updating query results. Cached" + " state=%s. Your state=%s",
                        this.emailState, emailState));
            }
            this.queryResults.put(
                    query,
                    new InMemoryQueryResult(
                            queryResult.queryState().getState(),
                            queryResult.canCalculateChanges(),
                            queryResult.items()));
        }
    }

    @Override
    public void addQueryResult(String queryString, String afterEmailId, QueryResult queryResult)
            throws CacheWriteException, CacheConflictException {
        synchronized (this.queryResults) {
            final String emailState = queryResult.objectState().getState();
            final String queryState = queryResult.queryState().getState();

            // TODO simply ignore if already applied

            if (emailState == null || !emailState.equals(this.emailState)) {
                throw new CacheConflictException(String.format(
                        "Email state must match when updating query results. Cached" + " state=%s. Your state=%s",
                        this.emailState, emailState));
            }
            final InMemoryQueryResult inMemoryQueryResult = this.queryResults.get(queryString);
            if (inMemoryQueryResult == null) {
                throw new CacheConflictException("QueryResult does not exist in our database");
            }
            if (queryState == null || !queryState.equals(inMemoryQueryResult.queryState)) {
                throw new CacheConflictException("QueryState does not match");
            }
            final int currentItemCount = inMemoryQueryResult.items.size();

            final String currentLastItemId =
                    inMemoryQueryResult.items.get(currentItemCount - 1).getEmailId();

            if (!currentLastItemId.equals(afterEmailId)) {
                throw new CacheConflictException(String.format(
                        "Current last email id (%s) doesn't match afterId (%s) from" + " request",
                        currentLastItemId, afterEmailId));
            }

            if (currentItemCount != queryResult.position()) {
                throw new CorruptCacheException(String.format(
                        "Unexpected QueryPage. Cache has %d items. Page starts at position" + " %d",
                        currentItemCount, queryResult.position()));
            }
            inMemoryQueryResult.items.addAll(queryResult.items());
        }
    }

    @Override
    public void updateQueryResults(
            String query, QueryUpdate<Email, QueryResultItem> update, TypedState<Email> emailState)
            throws CacheWriteException, CacheConflictException {
        synchronized (this.queryResults) {
            final InMemoryQueryResult queryResult = this.queryResults.get(query);
            if (queryResult == null) {
                throw new CacheWriteException("Unable to update query. Can not find cached version");
            }
            if (emailState.getState() == null || !emailState.getState().equals(this.emailState)) {
                throw new CacheConflictException(String.format(
                        "Email state must match when updating query results. Cached" + " state=%s. Your state=%s",
                        this.emailState, emailState.getState()));
            }
            if (update.getOldTypedState().getState() == null
                    || !update.getOldTypedState().getState().equals(queryResult.queryState)) {
                throw new CacheConflictException(String.format(
                        "OldState (%s) did not match our expectation ",
                        update.getOldTypedState().getState()));
            }
            for (String removed : update.removed()) {
                LOGGER.info("no removing id {}", removed);
                queryResult.remove(removed);
            }
            for (AddedItem<QueryResultItem> addedItem : update.added()) {
                // TODO it is probably save to just not add an item that exceeds the range (position
                // > length) but this indicates a broken uper layer
                LOGGER.info("now adding {} on index {}", addedItem.getItem().getEmailId(), addedItem.getIndex());
                queryResult.items.add((int) addedItem.getIndex(), addedItem.getItem());
            }
            queryResult.queryState = update.getNewTypedState().getState();
        }
    }

    @Override
    public void invalidateEmailThreadsAndQueries() {
        synchronized (this.emails) {
            this.emails.clear();
            this.emailState = null;
        }
        synchronized (this.threads) {
            this.threads.clear();
            this.threadState = null;
        }
        synchronized (this.queryResults) {
            this.queryResults.clear();
        }
    }

    @Override
    public void invalidateMailboxes() {
        synchronized (this.mailboxes) {
            this.mailboxes.clear();
            this.mailboxState = null;
        }
    }

    @Override
    public void invalidateQueryResult(final String queryString) {
        synchronized (this.queryResults) {
            this.queryResults.remove(queryString);
        }
    }

    @Override
    public Missing getMissing(final String query) throws CacheReadException {
        final List<String> threadIds = new ArrayList<>();
        synchronized (this.queryResults) {
            final InMemoryQueryResult queryResult = this.queryResults.get(query);
            if (queryResult == null) {
                throw new CacheReadException("Unable to find cached version");
            }
            for (QueryResultItem item : queryResult.items) {
                threadIds.add(item.threadId());
            }
        }
        synchronized (this.threads) {
            threadIds.removeIf(this.threads::containsKey);
            return new Missing(this.threadState, this.emailState, threadIds);
        }
    }

    private static <T extends Identifiable> void copyProperty(T target, T source, String property, Class<T> clazz)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(property);
        field.setAccessible(true);
        field.set(target, field.get(source));
    }

    protected static class InMemoryQueryResult {

        private String queryState;
        private final boolean canCalculateChanges;
        private final ArrayList<QueryResultItem> items;

        InMemoryQueryResult(String queryState, boolean canCalculateChanges, List<QueryResultItem> items) {
            this.queryState = queryState;
            this.canCalculateChanges = canCalculateChanges;
            this.items = new ArrayList<>(items);
        }

        private void remove(String emailId) {
            Iterator<QueryResultItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                if (emailId.equals(iterator.next().getEmailId())) {
                    iterator.remove();
                    break;
                }
            }
        }

        public List<QueryResultItem> getItems() {
            return ImmutableList.copyOf(items);
        }
    }
}
