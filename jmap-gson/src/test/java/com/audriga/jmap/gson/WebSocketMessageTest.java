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

import com.audriga.jmap.common.ErrorResponse;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.entity.ErrorType;
import com.audriga.jmap.common.method.call.email.SetEmailMethodCall;
import com.audriga.jmap.common.util.Patches;
import com.audriga.jmap.common.websocket.ErrorResponseWebSocketMessage;
import com.audriga.jmap.common.websocket.RequestWebSocketMessage;
import com.audriga.jmap.common.websocket.ResponseWebSocketMessage;
import com.audriga.jmap.common.websocket.WebSocketMessage;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WebSocketMessageTest extends AbstractGsonTest {

    @Test
    public void serializeRequest() throws IOException {
        final Request request = Request.builder()
                .call(SetEmailMethodCall.builder()
                        .accountId("accountId")
                        .ifInState("state")
                        .update(ImmutableMap.of("M123", Patches.remove("keywords/$seen")))
                        .build())
                .build();
        RequestWebSocketMessage message =
                RequestWebSocketMessage.builder().id("my-id").request(request).build();
        Assertions.assertEquals(
                readResourceAsString("websocket/request.json"), getGson().toJson(message));
    }

    @Test
    public void deserializeResponse() throws IOException {
        final WebSocketMessage webSocketMessage = parseFromResource("websocket/response.json", WebSocketMessage.class);
        Assertions.assertInstanceOf(ResponseWebSocketMessage.class, webSocketMessage);
        final ResponseWebSocketMessage responseWebSocketMessage = (ResponseWebSocketMessage) webSocketMessage;
        Assertions.assertEquals(
                2, responseWebSocketMessage.getResponse().methodResponses().size());
    }

    @Test
    public void deserializeUnknownCapability() throws IOException {
        final WebSocketMessage webSocketMessage =
                parseFromResource("websocket/unknown-capability.json", WebSocketMessage.class);
        MatcherAssert.assertThat(webSocketMessage, CoreMatchers.instanceOf(ErrorResponseWebSocketMessage.class));
        ErrorResponse errorResponse = ((ErrorResponseWebSocketMessage) webSocketMessage).getPayload();
        Assertions.assertEquals(ErrorType.UNKNOWN_CAPABILITY, errorResponse.getType());
    }

    @Test
    public void deserializeEmpty() {
        final JsonParseException jsonParseException = Assertions.assertThrows(
                JsonParseException.class, () -> parseFromResource("websocket/empty.json", WebSocketMessage.class));
        Assertions.assertEquals("WebSocketMessage had no @type attribute", jsonParseException.getMessage());
    }

    @Test
    public void deserializeUnknownType() {
        final JsonParseException jsonParseException = Assertions.assertThrows(
                JsonParseException.class,
                () -> parseFromResource("websocket/unknown-type.json", WebSocketMessage.class));
        Assertions.assertEquals("Unknown WebSocketMessage type unknown", jsonParseException.getMessage());
    }
}
