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

package com.audriga.jmap.common;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.util.Namespace;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public record Request(List<String> using, List<Invocation> methodCalls) {
    private static final Map<Class<? extends MethodCall>, String> NAMESPACE_CACHE = new ConcurrentHashMap<>();

    private static String getNamespaceFor(final Class<? extends MethodCall> clazz) {
        return NAMESPACE_CACHE.computeIfAbsent(clazz, c -> {
            var ns = Namespace.get(c);
            if (ns == null) {
                throw new IllegalArgumentException(String.format(
                        "%s is missing a namespace. Annotate package with @JmapNamespace", clazz.getSimpleName()));
            }
            return ns;
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public record Invocation(MethodCall methodCall, String id) {
        public ResultReference createReference(String path) {
            return new ResultReference(id, methodCall.getClass(), path);
        }

        /**
         * Internal constructor, only exposed for use by jmap-gson when deserializing.
         */
        public record ResultReference(String id, Class<? extends MethodCall> clazz, String path) {
            public static final class Path {
                public static final String IDS = "/ids";
                public static final String ADDED_IDS = "/added/*/id";
                public static final String LIST_IDS = "/list/*/id";
                public static final String LIST_THREAD_IDS = "/list/*/threadId";
                public static final String LIST_EMAIL_IDS = "/list/*/emailIds";
                public static final String UPDATED = "/updated";
                public static final String CREATED = "/created";
                public static final String UPDATED_PROPERTIES = "/updatedProperties";
            }
        }
    }

    public static final class Builder {
        private final Set<String> using = new TreeSet<>();
        private final List<Invocation> invocations = new ArrayList<>();

        private Builder() {}

        public Builder call(MethodCall call) {
            final int id = invocations.size();
            final Invocation invocation = new Invocation(call, Integer.toString(id));
            return add(invocation);
        }

        public Builder add(Invocation invocation) {
            this.invocations.add(invocation);
            final Class<? extends MethodCall> clazz = invocation.methodCall.getClass();
            this.using.add(com.audriga.jmap.Namespace.CORE);
            this.using.add(getNamespaceFor(clazz));
            this.using.addAll(Namespace.getImplicit(invocation.methodCall));
            return this;
        }

        public Builder using(String namespace) {
            this.using.add(namespace);
            return this;
        }

        public Request build() {
            return new Request(List.copyOf(using), List.copyOf(invocations));
        }
    }
}
