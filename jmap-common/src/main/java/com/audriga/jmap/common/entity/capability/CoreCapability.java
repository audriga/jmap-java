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

package com.audriga.jmap.common.entity.capability;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;
import com.audriga.jmap.common.util.Property;
import lombok.Getter;
import lombok.ToString;

@JmapCapability(namespace = Namespace.CORE)
@lombok.Builder
@Getter
@ToString
public class CoreCapability implements Capability {

    private Long maxSizeUpload;
    private Long maxConcurrentUpload;
    private Long maxCallsInRequest;
    private Long maxObjectsInGet;
    private Long maxObjectsInSet;
    private String[] collationAlgorithms;

    public long maxSizeUpload() {
        return Property.expected(maxSizeUpload);
    }

    public long maxConcurrentUpload() {
        return Property.expected(maxConcurrentUpload);
    }

    public long maxCallsInRequest() {
        return Property.expected(maxCallsInRequest);
    }

    public long maxObjectsInGet() {
        return Property.expected(maxObjectsInGet);
    }

    public long maxObjectsInSet() {
        return Property.expected(maxObjectsInSet);
    }
}
