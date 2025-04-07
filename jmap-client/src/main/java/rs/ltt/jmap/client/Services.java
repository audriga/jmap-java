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

package rs.ltt.jmap.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ltt.jmap.client.api.UserAgentInterceptor;
import rs.ltt.jmap.gson.JmapAdapters;

public final class Services {

    public static final Logger OK_HTTP_LOGGER = LoggerFactory.getLogger(OkHttpClient.class);
    private static final OkHttpClient OK_HTTP_CLIENT;
    private static final OkHttpClient OK_HTTP_CLIENT_LOGGING;
    public static final Gson GSON;
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();

    static {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new UserAgentInterceptor());
        OK_HTTP_CLIENT = builder.build();
        if (OK_HTTP_LOGGER.isInfoEnabled()) {
            final OkHttpClient.Builder loggingBuilder = OK_HTTP_CLIENT.newBuilder();
            final HttpLoggingInterceptor loggingInterceptor;
            if (OK_HTTP_LOGGER.isDebugEnabled()) {
                loggingInterceptor = new HttpLoggingInterceptor(OK_HTTP_LOGGER::debug);
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                loggingInterceptor = new HttpLoggingInterceptor(OK_HTTP_LOGGER::info);
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            }
            loggingBuilder.addInterceptor(loggingInterceptor);
            OK_HTTP_CLIENT_LOGGING = loggingBuilder.build();
        } else {
            OK_HTTP_CLIENT_LOGGING = OK_HTTP_CLIENT;
        }
        final GsonBuilder gsonBuilder = new GsonBuilder();
        JmapAdapters.register(gsonBuilder);
        GSON = gsonBuilder.create();
    }

    public static OkHttpClient okHttpClient() {
        return OK_HTTP_CLIENT;
    }

    public static OkHttpClient okHttpClient(@Nullable final X509TrustManager trustManager) {
        return withTrustManager(OK_HTTP_CLIENT, trustManager);
    }

    public static OkHttpClient okHttpClientLogging(@Nullable final X509TrustManager trustManager) {
        return withTrustManager(OK_HTTP_CLIENT_LOGGING, trustManager);
    }

    private static OkHttpClient withTrustManager(
            final OkHttpClient okHttpClient, @Nullable final X509TrustManager trustManager) {
        if (trustManager == null) {
            return okHttpClient;
        }
        try {
            final var context = getSSLContext();
            context.init(null, new X509TrustManager[] {trustManager}, SECURE_RANDOM);
            final var socketFactory = context.getSocketFactory();
            return okHttpClient.newBuilder().sslSocketFactory(socketFactory, trustManager).build();
        } catch (final NoSuchAlgorithmException | KeyManagementException e) {
            return okHttpClient;
        }
    }

    public static SSLContext getSSLContext() throws NoSuchAlgorithmException {
        try {
            return SSLContext.getInstance("TLSv1.3");
        } catch (final NoSuchAlgorithmException e) {
            return SSLContext.getInstance("TLSv1.2");
        }
    }

    private Services() {
        throw new IllegalStateException("Do not instantiate this class");
    }
}
