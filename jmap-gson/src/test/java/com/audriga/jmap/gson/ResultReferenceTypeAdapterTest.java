package com.audriga.jmap.gson;

import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.email.QueryEmailMethodCall;
import com.audriga.jmap.gson.adapter.ResultReferenceTypeAdapter;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResultReferenceTypeAdapterTest {

    private static final String METHOD_CALL_ID = "0";

    @Test
    public void writeAndReadBack() {
        Request.Invocation emailQuery = new Request.Invocation(
                QueryEmailMethodCall.builder().accountId("accountId").build(), METHOD_CALL_ID);
        ResultReference resultReferenceOut = emailQuery.createReference("/ids");
        GsonBuilder gsonBuilder = new GsonBuilder();
        ResultReferenceTypeAdapter.register(gsonBuilder);
        String json = gsonBuilder.create().toJson(resultReferenceOut);
        ResultReference resultReferenceIn = gsonBuilder.create().fromJson(json, ResultReference.class);
        Assertions.assertEquals(resultReferenceIn.clazz(), resultReferenceOut.clazz());
        Assertions.assertEquals(resultReferenceIn.id(), resultReferenceOut.id());
        Assertions.assertEquals(resultReferenceIn.path(), resultReferenceOut.path());
    }
}
