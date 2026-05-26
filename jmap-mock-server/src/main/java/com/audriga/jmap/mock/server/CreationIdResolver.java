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
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;

public class CreationIdResolver {

    public static String resolveIfNecessary(
            final String id, final ListMultimap<String, Response.Invocation> previousResponses) {
        return isCreationId(id) ? resolve(id, previousResponses) : id;
    }

    private static String resolve(
            final String creationId, final ListMultimap<String, Response.Invocation> previousResponses) {
        Preconditions.checkNotNull(creationId);
        Preconditions.checkArgument(creationId.charAt(0) == '#');
        final String strippedId = creationId.substring(1);
        for (final Response.Invocation invocation : previousResponses.values()) {
            final MethodResponse methodResponse = invocation.methodResponse();
            if (methodResponse instanceof SetMethodResponse) {
                final Identifiable entity =
                        ((SetMethodResponse<?>) methodResponse).getCreated().get(strippedId);
                if (entity != null) {
                    return entity.getId();
                }
            }
        }
        throw new IllegalArgumentException(String.format("Creation id %s not found", strippedId));
    }

    private static boolean isCreationId(final String id) {
        return id != null && id.charAt(0) == '#';
    }
}
