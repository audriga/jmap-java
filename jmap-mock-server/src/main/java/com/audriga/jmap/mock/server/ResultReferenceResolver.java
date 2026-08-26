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

import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.Thread;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.response.email.GetEmailMethodResponse;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.common.method.response.thread.GetThreadMethodResponse;
import com.audriga.jmap.common.util.Mapper;
import com.google.common.collect.ListMultimap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ResultReferenceResolver {

    public static String[] resolve(
            final ResultReference resultReference, final ListMultimap<String, Response.Invocation> previousResponses) {
        final MethodResponse methodResponse = find(resultReference, previousResponses);
        final String path = resultReference.path();
        switch (resultReference.path()) {
            case ResultReference.Path.IDS:
                if (methodResponse instanceof QueryMethodResponse<?> query) {
                    return query.ids();
                }
                break;
            case ResultReference.Path.LIST_THREAD_IDS:
                if (methodResponse instanceof GetEmailMethodResponse getEmail) {
                    return Arrays.stream(getEmail.list()).map(Email::threadId).toArray(String[]::new);
                }
                break;
            case ResultReference.Path.LIST_EMAIL_IDS:
                if (methodResponse instanceof GetThreadMethodResponse getThread) {
                    return Arrays.stream(getThread.list())
                            .map(Thread::emailIds)
                            .flatMap(Collection::stream)
                            .toArray(String[]::new);
                }
                break;
            case ResultReference.Path.CREATED:
                if (methodResponse instanceof ChangesMethodResponse<?> changes) {
                    return nullToEmpty(changes.created());
                }
                break;
            case ResultReference.Path.UPDATED:
                if (methodResponse instanceof ChangesMethodResponse<?> changes) {
                    return nullToEmpty(changes.updated());
                }
                break;
            case ResultReference.Path.ADDED_IDS:
                if (methodResponse instanceof QueryChangesMethodResponse<?> queryChanges) {
                    return queryChanges.added().stream().map(AddedItem::getItem).toArray(String[]::new);
                }
                break;
            default:
        }
        throw new IllegalArgumentException(String.format(
                "Unable to resolve path %s for class %s",
                path, methodResponse.getClass().getName()));
    }

    private static MethodResponse find(
            final ResultReference resultReference, final ListMultimap<String, Response.Invocation> previousResponses) {
        final String id = resultReference.id();
        final List<Response.Invocation> invocations = previousResponses.get(id);
        if (invocations.isEmpty()) {
            throw new IllegalArgumentException("Unable to find any method response with id " + id);
        }
        final String methodCallName = Mapper.METHOD_CALLS.inverse().get(resultReference.clazz());
        for (final Response.Invocation invocation : invocations) {
            final String responseCallName = Mapper.METHOD_RESPONSES
                    .inverse()
                    .get(invocation.methodResponse().getClass());
            if (methodCallName.equals(responseCallName)) {
                return invocation.methodResponse();
            }
        }
        throw new IllegalArgumentException("Unable to find matching response for " + methodCallName);
    }

    private static String[] nullToEmpty(final String[] value) {
        return value == null ? new String[0] : value;
    }
}
