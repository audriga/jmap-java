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

package com.audriga.jmap.common;

import com.audriga.jmap.common.method.call.email.GetEmailMethodCall;
import com.audriga.jmap.common.method.call.email.QueryEmailMethodCall;
import com.audriga.jmap.common.method.call.mailbox.ChangesMailboxMethodCall;
import com.audriga.jmap.common.method.call.mailbox.GetMailboxMethodCall;
import com.audriga.jmap.common.method.call.snippet.GetSearchSnippetsMethodCall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodCallTest {

    @Test
    public void isAccountIdRequiredInGetMailboxMethodCall() {
        Assertions.assertThrows(
                NullPointerException.class, () -> GetMailboxMethodCall.builder().build());
    }

    @Test
    public void isAccountIdRequiredInChangesMailboxMethodCall() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> ChangesMailboxMethodCall.builder().sinceState("dummy").build());
    }

    @Test
    public void isSinceStateRequiredInChangesMailboxMethodCall() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> ChangesMailboxMethodCall.builder().accountId("dummy").build());
    }

    @Test
    public void isAccountIdRequiredInGetSearchSnippetsMethodCall() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> GetSearchSnippetsMethodCall.builder()
                        .emailIds(new String[] {"1", "2"})
                        .build());
    }

    @Test
    public void isOneOfEmailIdRequiredInGetSearchSnippetsMethodCall() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> GetSearchSnippetsMethodCall.builder().accountId("dummy").build());
    }

    @Test
    public void preventReferenceAndIdInGetEmailMethodCall() {
        final Request.Invocation invocation = new Request.Invocation(
                QueryEmailMethodCall.builder().accountId("dummy").build(), "1");
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> GetEmailMethodCall.builder()
                        .accountId("dummy")
                        .ids(new String[] {"1", "2"})
                        .idsReference(
                                invocation.createReference(Request.Invocation.ResultReference.Path.LIST_EMAIL_IDS))
                        .build());
    }
}
