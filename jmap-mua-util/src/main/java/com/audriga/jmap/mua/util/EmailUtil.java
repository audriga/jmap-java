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

package com.audriga.jmap.mua.util;

import com.audriga.jmap.common.entity.EmailAddress;
import com.audriga.jmap.common.entity.IdentifiableEmailWithAddresses;
import com.audriga.jmap.common.entity.IdentifiableEmailWithSubject;
import com.audriga.jmap.common.entity.IdentifiableEmailWithTime;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EmailUtil {

    private static final String RESPONSE_PREFIX = "Re";

    private static final List<String> RESPONSE_PREFIXES = List.of("re", "aw");

    private EmailUtil() {}

    public static String getResponseSubject(IdentifiableEmailWithSubject emailWithSubject) {
        final String subject = emailWithSubject.subject();
        final int length = subject.length();
        if (length <= 3) {
            return subjectWithPrefix(subject);
        }
        final String prefix = subject.substring(0, 3);
        if (prefix.charAt(2) == ':'
                && RESPONSE_PREFIXES.contains(prefix.substring(0, 2).toLowerCase())) {
            return subjectWithPrefix(subject.substring(3));
        }
        return subjectWithPrefix(subject);
    }

    public static String subjectWithPrefix(final String subject) {
        return String.format("%s: %s", RESPONSE_PREFIX, subject.trim());
    }

    public static Instant getEffectiveDate(final IdentifiableEmailWithTime email) {
        final Instant receivedAt = email.receivedAt();
        final OffsetDateTime sentAt = email.sentAt();
        if (sentAt == null) {
            return receivedAt;
        }
        if (receivedAt.isBefore(sentAt.toInstant())) {
            return receivedAt;
        }
        return sentAt.toInstant();
    }

    public static ReplyAddresses reply(IdentifiableEmailWithAddresses emailWithAddresses) {
        final Collection<EmailAddress> replyTo = emailWithAddresses.replyTo();
        if (replyTo != null && !replyTo.isEmpty()) {
            return new ReplyAddresses(replyTo);
        }
        return new ReplyAddresses(replyTo(emailWithAddresses));
    }

    private static Collection<EmailAddress> replyTo(final IdentifiableEmailWithAddresses emailWithAddresses) {
        final Collection<EmailAddress> from = emailWithAddresses.from();
        if (from != null && !from.isEmpty()) {
            return from;
        }
        final Collection<EmailAddress> sender = emailWithAddresses.sender();
        if (sender != null && !sender.isEmpty()) {
            return sender;
        }
        return Collections.emptyList();
    }

    public static ReplyAddresses replyAll(final IdentifiableEmailWithAddresses emailWithAddresses) {
        return replyAll(emailWithAddresses, Collections.emptyList());
    }

    public static ReplyAddresses replyAll(
            final IdentifiableEmailWithAddresses emailWithAddresses, final Collection<String> identityEmailAddresses) {
        final Collection<EmailAddress> replyTo = emailWithAddresses.replyTo();
        final Collection<EmailAddress> cc = emailWithAddresses.cc();
        if (replyTo != null && !replyTo.isEmpty() && (cc == null || cc.isEmpty())) {
            return new ReplyAddresses(replyTo);
        }
        final Collection<EmailAddress> to = emailWithAddresses.to();
        ImmutableList.Builder<EmailAddress> ccBuilder = new ImmutableList.Builder<>();
        if (to != null) {
            for (final EmailAddress address : to) {
                if (Iterables.any(identityEmailAddresses, i -> i.equalsIgnoreCase(address.email()))) {
                    continue;
                }
                ccBuilder.add(address);
            }
        }
        if (cc != null) {
            ccBuilder.addAll(cc);
        }
        if (replyTo != null && !replyTo.isEmpty()) {
            return new ReplyAddresses(replyTo, ccBuilder.build());
        } else {
            return new ReplyAddresses(replyTo(emailWithAddresses), ccBuilder.build());
        }
    }

    public record ReplyAddresses(Collection<EmailAddress> to, Collection<EmailAddress> cc) {
        public ReplyAddresses(Collection<EmailAddress> to) {
            this(to, Collections.emptyList());
        }
    }
}
