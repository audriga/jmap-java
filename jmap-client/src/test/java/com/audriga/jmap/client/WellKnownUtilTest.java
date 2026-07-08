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

package com.audriga.jmap.client;

import com.audriga.jmap.client.util.WellKnownUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WellKnownUtilTest {

    @Test
    public void validUsername() throws WellKnownUtil.MalformedUsernameException {
        Assertions.assertEquals(
                "https://example.com/.well-known/jmap",
                WellKnownUtil.fromUsername("test@example.com").toString());
    }

    @Test
    public void noUsername() {
        Assertions.assertThrows(
                WellKnownUtil.MalformedUsernameException.class, () -> WellKnownUtil.fromUsername("example.com"));
    }

    @Test
    public void trailingSpaceUsername() {
        Assertions.assertThrows(
                WellKnownUtil.MalformedUsernameException.class, () -> WellKnownUtil.fromUsername("test@example.com "));
    }
}
