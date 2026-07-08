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

package com.audriga.jmap.common.method.response.core;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.PushSubscription;
import com.audriga.jmap.common.method.MethodResponse;
import lombok.Builder;

@JmapMethod("PushSubscription/get")
@Builder
public class GetPushSubscriptionMethodResponse implements MethodResponse {

    protected String state;

    protected String[] notFound;

    protected PushSubscription[] list;
}
