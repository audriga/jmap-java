/*
 * Copyright 2021 Daniel Gultsch
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

package com.audriga.jmap.mua.service;

import com.audriga.jmap.client.blob.Download;
import com.audriga.jmap.client.blob.Progress;
import com.audriga.jmap.client.blob.Uploadable;
import com.audriga.jmap.common.entity.Attachment;
import com.audriga.jmap.common.entity.Downloadable;
import com.audriga.jmap.common.entity.Upload;
import com.audriga.jmap.mua.util.AttachmentUtil;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;

public class BinaryService extends AbstractMuaService {

    public BinaryService(MuaSession muaSession) {
        super(muaSession);
    }

    public ListenableFuture<Download> download(final Downloadable downloadable) {
        return jmapClient.download(accountId, downloadable);
    }

    public ListenableFuture<Download> download(final Downloadable downloadable, final long rangeStart) {
        Preconditions.checkArgument(rangeStart >= 0, "rangeStart must not be smaller than 0");
        return jmapClient.download(accountId, downloadable, rangeStart);
    }

    public ListenableFuture<Upload> upload(final Uploadable uploadable, final Progress progress) {
        return jmapClient.upload(accountId, uploadable, progress);
    }

    public ListenableFuture<Void> verifyAttachmentsDoNotExceedLimit(
            final Collection<? extends Attachment> attachments) {
        return Futures.transform(
                jmapClient.getSession(),
                session -> {
                    AttachmentUtil.verifyAttachmentsDoNotExceedLimit(session, accountId, attachments);
                    return null;
                },
                MoreExecutors.directExecutor());
    }
}
