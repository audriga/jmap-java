package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/get")
@RecordBuilder
public record GetFileNodeResponse(String accountId, String state, List<FileNode> list, List<String> notFound)
        implements GetMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
