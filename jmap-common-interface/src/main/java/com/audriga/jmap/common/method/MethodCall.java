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

package com.audriga.jmap.common.method;

import org.jspecify.annotations.Nullable;

public interface MethodCall {
    sealed interface Arg<T> {
        record Value<T>(T value) implements Arg<T> {}

        record Reference<T>(ResultReference reference) implements Arg<T> {}

        static <T> Value<T> of(T value) {
            return new Value<>(value);
        }

        static <T> Reference<T> of(ResultReference reference) {
            return new Reference<>(reference);
        }

        static <T> @Nullable T unwrapValue(Arg<T> arg) {
            if (arg == null) return null;
            return ((Value<T>) arg).value();
        }

        static <T> T unwrapValueOr(Arg<T> arg, T defaultValue) {
            var unwrapped = unwrapValue(arg);
            return unwrapped != null ? unwrapped : defaultValue;
        }

        static @Nullable ResultReference unwrapReference(Arg<?> arg) {
            if (arg == null) return null;
            return ((Reference<?>) arg).reference();
        }
    }
}
