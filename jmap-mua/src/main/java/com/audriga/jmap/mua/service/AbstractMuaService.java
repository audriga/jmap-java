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

package com.audriga.jmap.mua.service;

import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.MethodResponses;
import com.audriga.jmap.client.api.MethodErrorResponseException;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.error.CannotCalculateChangesMethodErrorResponse;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.common.util.Mapper;
import com.audriga.jmap.mua.Status;
import com.audriga.jmap.mua.cache.Cache;
import com.audriga.jmap.mua.cache.ObjectsState;
import com.audriga.jmap.mua.util.UpdateUtil;
import com.google.common.util.concurrent.*;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMuaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMuaService.class);
    protected final JmapClient jmapClient;
    protected final Cache cache;
    protected final String accountId;
    protected final ListeningExecutorService ioExecutorService;
    private final MuaSession muaSession;

    public AbstractMuaService(final MuaSession muaSession) {
        this.muaSession = muaSession;
        this.jmapClient = muaSession.getJmapClient();
        this.cache = muaSession.getCache();
        this.accountId = muaSession.getAccountId();
        this.ioExecutorService = muaSession.getIoExecutorService();
    }

    protected <T extends AbstractMuaService> T getService(Class<T> clazz) {
        return muaSession.getService(clazz);
    }

    protected Long getQueryPageSize() {
        return muaSession.getQueryPageSize();
    }

    protected ListenableFuture<ObjectsState> getObjectsState() {
        return ioExecutorService.submit(cache::getObjectsState);
    }

    protected void registerCacheInvalidationCallback(
            UpdateUtil.MethodResponsesFuture methodResponsesFuture, Runnable runnable) {
        methodResponsesFuture.addChangesCallback(
                new FutureCallback<MethodResponses>() {
                    @Override
                    public void onSuccess(@Nullable MethodResponses methodResponses) {
                        final ChangesMethodResponse<?> changesMethodResponse =
                                methodResponses.getMain(ChangesMethodResponse.class);
                        final TypedState<?> oldState = changesMethodResponse.getTypedOldState();
                        final TypedState<?> newState = changesMethodResponse.getTypedNewState();
                        final boolean hasMoreChanges = changesMethodResponse.isHasMoreChanges();
                        if (hasMoreChanges && oldState.equals(newState)) {
                            LOGGER.error(
                                    "Invalid server response to {} oldState==newState despite" + " having more changes",
                                    Mapper.METHOD_RESPONSES
                                            .inverse()
                                            .get(methodResponses.getMain().getClass()));
                            runnable.run();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        if (MethodErrorResponseException.matches(
                                throwable, CannotCalculateChangesMethodErrorResponse.class)) {
                            runnable.run();
                        }
                    }
                },
                ioExecutorService);
    }

    protected static ListenableFuture<Status> transform(List<ListenableFuture<Status>> list) {
        return Futures.transform(
                Futures.allAsList(list),
                statuses -> {
                    if (statuses.contains(Status.HAS_MORE)) {
                        return Status.HAS_MORE;
                    }
                    if (statuses.contains(Status.UPDATED)) {
                        return Status.UPDATED;
                    }
                    return Status.UNCHANGED;
                },
                MoreExecutors.directExecutor());
    }
}
