package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface SetMethodResponse<T extends Identifiable> extends MethodResponse {
    String accountId();

    @Nullable
    String oldState();

    String newState();

    @Nullable
    Map<String, T> created();

    @Nullable
    Map<String, T> updated();

    @Nullable
    List<String> destroyed();

    @Nullable
    Map<String, SetError> notCreated();

    @Nullable
    Map<String, SetError> notUpdated();

    @Nullable
    Map<String, SetError> notDestroyed();

    default int updatedCreatedCount() {
        var created = created();
        var updated = updated();
        return (created == null ? 0 : created.size()) + (updated == null ? 0 : updated.size());
    }
}
