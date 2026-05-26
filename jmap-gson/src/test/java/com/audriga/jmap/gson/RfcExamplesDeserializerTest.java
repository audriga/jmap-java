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

package com.audriga.jmap.gson;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audriga.jmap.common.Response;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.Identity;
import com.audriga.jmap.common.method.response.email.GetEmailMethodResponse;
import com.audriga.jmap.common.method.response.identity.GetIdentityMethodResponse;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public class RfcExamplesDeserializerTest extends AbstractGsonTest {

    @Test
    public void emailGetResponse() throws IOException {
        Response.Invocation[] responseInvocation =
                parseFromResource("rfc-example/email-get-response.json", Response.Invocation[].class);
        assertEquals(1, responseInvocation.length);
        MatcherAssert.assertThat(responseInvocation[0].methodResponse(), instanceOf(GetEmailMethodResponse.class));
        final GetEmailMethodResponse methodResponse = (GetEmailMethodResponse) responseInvocation[0].methodResponse();
        final Email[] emails = methodResponse.getList();
        assertEquals(1, emails.length);
        final Email email = emails[0];
        assertEquals("f123u457", email.getId());
        assertEquals(2, email.getBodyValues().size());
        assertEquals(1, email.getFrom().size());
        assertEquals("Dinner on Thursday?", email.getSubject());
        assertEquals(email.getReceivedAt(), email.getSentAt().toInstant());
    }

    @Test
    public void identityGetResponse() throws IOException {
        Response.Invocation invocation =
                parseFromResource("rfc-example/identity-get-response.json", Response.Invocation.class);
        MatcherAssert.assertThat(invocation.methodResponse(), instanceOf(GetIdentityMethodResponse.class));
        GetIdentityMethodResponse methodResponse = (GetIdentityMethodResponse) invocation.methodResponse();
        Identity[] identities = methodResponse.getList();
        assertEquals(2, identities.length);
        assertEquals("Joe Bloggs", identities[0].getName());
    }
}
