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

package com.audriga.jmap.client.api;

import com.audriga.jmap.client.JmapRequest;
import com.audriga.jmap.client.MethodResponses;
import com.audriga.jmap.client.util.ResponseAnalyzer;
import com.audriga.jmap.common.ErrorResponse;
import com.audriga.jmap.common.GenericResponse;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.method.MethodErrorResponse;
import com.audriga.jmap.common.method.MethodResponse;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJmapApiClient implements JmapApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJmapApiClient.class);

    private final SessionStateListener sessionStateListener;

    protected AbstractJmapApiClient(final SessionStateListener sessionStateListener) {
        this.sessionStateListener = sessionStateListener;
    }

    protected void processResponse(final JmapRequest jmapRequest, final GenericResponse genericResponse) {
        if (genericResponse instanceof ErrorResponse) {
            jmapRequest.setException(new ErrorResponseException((ErrorResponse) genericResponse));
        } else if (genericResponse instanceof Response response) {
            final ResponseAnalyzer responseAnalyzer = ResponseAnalyzer.analyse(response);
            final Map<Request.Invocation, SettableFuture<MethodResponses>> map =
                    jmapRequest.getInvocationFutureImmutableMap();

            // Notify about potentially updated session state *before* setting the response futures.
            // This way we'll
            // make sure that additional requests guarded by a wait on one of the response futures
            // will trigger
            // re-fetching the session resource.
            onSessionStateRetrieved(response.sessionState());

            for (Map.Entry<Request.Invocation, SettableFuture<MethodResponses>> entry : map.entrySet()) {
                final Request.Invocation invocation = entry.getKey();
                final SettableFuture<MethodResponses> future = entry.getValue();
                final MethodResponses methodResponses = responseAnalyzer.find(invocation);
                if (methodResponses == null) {
                    future.setException(new MethodResponseNotFoundException(invocation));
                    continue;
                }
                final MethodResponse main = methodResponses.getMain();
                if (main instanceof MethodErrorResponse) {
                    future.setException(new MethodErrorResponseException(
                            (MethodErrorResponse) main, methodResponses.getAdditional(), invocation.methodCall()));
                } else {
                    future.set(methodResponses);
                }
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unable to process response of type %s",
                    genericResponse.getClass().getName()));
        }
    }

    private void onSessionStateRetrieved(final String sessionState) {
        LOGGER.debug("Notified of session state='{}'", sessionState);
        if (sessionStateListener != null) {
            sessionStateListener.onSessionStateRetrieved(sessionState);
        }
    }
}
