package com.audriga.jmap.gson;

import com.audriga.jmap.common.entity.Role;
import com.audriga.jmap.common.entity.filter.EmailFilterCondition;
import com.audriga.jmap.common.entity.filter.FilterOperator;
import com.audriga.jmap.common.entity.filter.MailboxFilterCondition;
import com.audriga.jmap.common.entity.query.EmailQuery;
import com.audriga.jmap.common.entity.query.MailboxQuery;
import com.audriga.jmap.common.method.call.email.QueryChangesEmailMethodCall;
import com.audriga.jmap.common.method.call.email.QueryEmailMethodCall;
import com.audriga.jmap.common.method.call.mailbox.QueryMailboxMethodCall;
import com.google.gson.Gson;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryCallTest extends AbstractGsonTest {

    @Test
    public void emailQueryCall() throws IOException {
        final Gson gson = getGson();
        final EmailQuery query = EmailQuery.of(
                EmailFilterCondition.builder().inMailbox("inbox-id").build(), true);
        Assertions.assertEquals(
                readResourceAsString("request/query-email.json"),
                gson.toJson(QueryEmailMethodCall.builder()
                        .accountId("accountId")
                        .query(query)
                        .build()));
    }

    @Test
    public void emailQueryChangesCall() throws IOException {
        final Gson gson = getGson();
        final EmailQuery query = EmailQuery.of(
                EmailFilterCondition.builder().inMailbox("inbox-id").build(), true);
        final String json = readResourceAsString("request/query-changes-email.json");
        Assertions.assertEquals(
                json,
                gson.toJson(QueryChangesEmailMethodCall.builder()
                        .accountId("accountId")
                        .sinceQueryState("first")
                        .query(query)
                        .build()));
        Assertions.assertEquals(
                query.filter.toQueryString(),
                gson.fromJson(json, QueryChangesEmailMethodCall.class)
                        .getFilter()
                        .toQueryString());
    }

    @Test
    public void emailQueryCallComplex() throws IOException {
        final Gson gson = getGson();
        final EmailQuery query = EmailQuery.of(
                FilterOperator.or(
                        EmailFilterCondition.builder().inMailbox("inbox-id").build(),
                        EmailFilterCondition.builder().inMailbox("archive-id").build()),
                true);
        final String json = readResourceAsString("request/query-email-complex.json");
        Assertions.assertEquals(
                json,
                gson.toJson(QueryEmailMethodCall.builder()
                        .accountId("accountId")
                        .query(query)
                        .build()));
        Assertions.assertEquals(
                query.filter.toQueryString(),
                gson.fromJson(json, QueryEmailMethodCall.class).getFilter().toQueryString());
    }

    @Test
    public void mailboxQueryCallComplex() throws IOException {
        final Gson gson = getGson();
        final MailboxQuery query = MailboxQuery.of(FilterOperator.or(
                MailboxFilterCondition.builder().role(Role.INBOX).build(),
                MailboxFilterCondition.builder().role(Role.ARCHIVE).build()));
        final String json = readResourceAsString("request/query-mailbox-complex.json");
        Assertions.assertEquals(
                json,
                gson.toJson(QueryMailboxMethodCall.builder()
                        .accountId("accountId")
                        .query(query)
                        .build()));
        Assertions.assertEquals(
                query.filter.toQueryString(),
                gson.fromJson(json, QueryMailboxMethodCall.class).getFilter().toQueryString());
    }
}
