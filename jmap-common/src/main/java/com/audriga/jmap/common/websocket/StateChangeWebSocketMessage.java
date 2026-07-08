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

package com.audriga.jmap.common.websocket;

import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.StateChange;
import com.google.common.base.MoreObjects;
import java.util.Map;
import lombok.Getter;

@Getter
@Type("StateChange")
public class StateChangeWebSocketMessage extends StateChange implements WebSocketMessage {

    private String pushState;

    public StateChangeWebSocketMessage(
            Map<String, Map<Class<? extends Identifiable>, String>> changed, String pushState) {
        super(changed);
        this.pushState = pushState;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("changed", changed)
                .add("pushState", pushState)
                .toString();
    }
}
