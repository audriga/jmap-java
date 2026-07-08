package com.audriga.jmap.gson;

import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.filter.EmailFilterCondition;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterOperator;
import com.google.gson.Gson;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FilterSerializationTest extends AbstractGsonTest {

    @Test
    public void complexEmailFilterSerialization() throws IOException {
        final Gson gson = getGson();
        final Filter<Email> emailFilter = FilterOperator.and(
                EmailFilterCondition.builder().text("two").build(),
                FilterOperator.not(EmailFilterCondition.builder().text("three").build()),
                EmailFilterCondition.builder().text("one").build());
        Assertions.assertEquals(readResourceAsString("filter/one-two-not-three.json"), gson.toJson(emailFilter));
    }

    @Test
    public void emailFilterBetweenSerialization() throws IOException {
        final Gson gson = getGson();
        final Filter<Email> emailFilter = EmailFilterCondition.builder()
                .before(OCTOBER_THIRD_8PM)
                .after(OCTOBER_FIRST_8AM)
                .build();
        Assertions.assertEquals(readResourceAsString("filter/email-filter-between.json"), gson.toJson(emailFilter));
    }
}
