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

package com.audriga.jmap.common.method.call.email;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Email/get")
@RecordBuilder
public record GetEmailMethodCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Default("""
                [ "id", "blobId", "threadId", "mailboxIds", "keywords", "size",
                "receivedAt", "messageId", "inReplyTo", "references", "sender", "from",
                "to", "cc", "bcc", "replyTo", "subject", "sentAt", "hasAttachment",
                "preview", "bodyValues", "textBody", "htmlBody", "attachments" ]
                """) @Nullable Arg<List<String>> properties,
        @Default("""
                [ "partId", "blobId", "size", "name", "type", "charset",
                  "disposition", "cid", "language", "location" ]
                """) @Nullable Arg<List<String>> bodyProperties,
        @Default("false") @Nullable Boolean fetchTextBodyValues,
        @Default("false") @Nullable Boolean fetchHTMLBodyValues,
        @Default("false") @Nullable Boolean fetchAllBodyValues,
        @Default("0") @Nullable Long maxBodyValueBytes)
        implements GetMethodCall<Email> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetEmailMethodCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
