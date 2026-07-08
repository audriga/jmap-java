/*
 * Copyright 2025 Daniel Gultsch
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

import com.audriga.jmap.client.http.HttpAuthentication;
import com.audriga.jmap.client.util.WellKnownUtil;
import javax.net.ssl.X509TrustManager;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ConnectionConfig {

    private @NonNull HttpAuthentication authentication;
    private @Nullable HttpUrl sessionResource;
    private @Nullable X509TrustManager trustManager;

    public ConnectionConfig(
            @NonNull HttpAuthentication authentication,
            @Nullable HttpUrl sessionResource,
            @Nullable X509TrustManager trustManager) {
        this.authentication = authentication;
        this.sessionResource = sessionResource;
        this.trustManager = trustManager;
    }

    public @NonNull HttpAuthentication getAuthentication() {
        return authentication;
    }

    public @NonNull HttpUrl getSessionResource() throws WellKnownUtil.MalformedUsernameException {
        final String username = this.authentication.getUsername();
        if (this.sessionResource != null) {
            return this.sessionResource;
        } else {
            return WellKnownUtil.fromUsername(username);
        }
    }

    public @Nullable X509TrustManager getTrustManager() {
        return trustManager;
    }
}
