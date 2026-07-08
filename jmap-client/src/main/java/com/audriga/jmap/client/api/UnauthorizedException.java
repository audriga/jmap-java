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

package com.audriga.jmap.client.api;

import com.audriga.jmap.client.http.HttpAuthentication;
import java.util.Collection;
import java.util.Set;
import okhttp3.Challenge;

public class UnauthorizedException extends JmapApiException {

    private final Collection<Challenge> challenges;

    public UnauthorizedException(final String message, final Collection<Challenge> challenges) {
        super(message);
        this.challenges = challenges;
    }

    public Collection<Challenge> getChallenges() {
        return this.challenges;
    }

    public Set<HttpAuthentication.Scheme> getAuthenticationSchemes() {
        return HttpAuthentication.Scheme.of(this.challenges);
    }
}
