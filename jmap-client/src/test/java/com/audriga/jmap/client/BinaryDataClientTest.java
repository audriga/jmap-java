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

import static org.hamcrest.CoreMatchers.instanceOf;

import com.audriga.jmap.client.blob.BinaryDataClient;
import com.audriga.jmap.client.blob.BlobTransferException;
import com.audriga.jmap.client.blob.FileUpload;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BinaryDataClientTest {

    @TempDir
    Path tempDir;

    @Test
    public void invalidUploadResponse() throws IOException {
        final Path textFileLocation = tempDir.resolve("test.txt");
        Files.write(textFileLocation, "hello world".getBytes(StandardCharsets.UTF_8));

        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder().body("{}").build());
            server.start();

            final var connectionConfig =
                    new ConnectionConfig(new BasicAuthHttpAuthentication("foo", "bar"), null, null);

            final BinaryDataClient binaryDataClient = new BinaryDataClient(connectionConfig);

            ExecutionException ee = Assertions.assertThrows(
                    ExecutionException.class,
                    () -> binaryDataClient
                            .upload(server.url("/upload"), FileUpload.of(textFileLocation), null)
                            .get());
            MatcherAssert.assertThat(ee.getCause(), instanceOf(IllegalStateException.class));
        }
    }

    @Test
    public void unauthenticated() throws IOException {
        final Path textFileLocation = tempDir.resolve("test.txt");
        Files.writeString(textFileLocation, "hello world");

        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse.Builder().code(401).body("{}").build());
            server.start();

            final var connectionConfig =
                    new ConnectionConfig(new BasicAuthHttpAuthentication("foo", "bar"), null, null);

            final BinaryDataClient binaryDataClient = new BinaryDataClient(connectionConfig);

            ExecutionException ee = Assertions.assertThrows(
                    ExecutionException.class,
                    () -> binaryDataClient
                            .upload(server.url("/upload"), FileUpload.of(textFileLocation), null)
                            .get());
            MatcherAssert.assertThat(ee.getCause(), instanceOf(BlobTransferException.class));
        }
    }
}
