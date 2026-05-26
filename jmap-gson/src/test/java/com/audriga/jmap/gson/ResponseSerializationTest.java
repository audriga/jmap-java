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

package com.audriga.jmap.gson;

import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.method.MethodErrorResponse;
import com.audriga.jmap.common.method.MethodResponse;
import com.google.gson.JsonIOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResponseSerializationTest extends AbstractGsonTest {

    @Test
    public void customNotAnnotatedMethodError() {
        Response response = new Response(
                List.of(new Response.Invocation(
                        new CustomMethodError(), UUID.randomUUID().toString())),
                "session-state-01");
        final JsonIOException jsonIOException =
                Assertions.assertThrows(JsonIOException.class, () -> getGson().toJson(response));
        Assertions.assertEquals(
                "Unable to serialize CustomMethodError. Did you annotate the Method with" + " @JmapError?",
                jsonIOException.getMessage());
    }

    @Test
    public void customNotAnnotatedMethodResponse() {
        Response response = new Response(
                List.of(new Response.Invocation(
                        new CustomMethodResponse(), UUID.randomUUID().toString())),
                "session-state-01");
        final JsonIOException jsonIOException = Assertions.assertThrows(JsonIOException.class, () -> {
            getGson().toJson(response);
        });
        Assertions.assertEquals(
                "Unable to serialize CustomMethodResponse. Did you annotate the method with" + " @JmapMethod?",
                jsonIOException.getMessage());
    }

    public static class CustomMethodResponse implements MethodResponse {}

    public static class CustomMethodError extends MethodErrorResponse {}
}
