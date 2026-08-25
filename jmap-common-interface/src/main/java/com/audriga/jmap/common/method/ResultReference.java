package com.audriga.jmap.common.method;

public record ResultReference(String id, Class<? extends MethodCall> clazz, String path) {
    public static final class Path {
        public static final String IDS = "/ids";
        public static final String ADDED_IDS = "/added/*/id";
        public static final String LIST_IDS = "/list/*/id";
        public static final String LIST_THREAD_IDS = "/list/*/threadId";
        public static final String LIST_EMAIL_IDS = "/list/*/emailIds";
        public static final String UPDATED = "/updated";
        public static final String CREATED = "/created";
        public static final String UPDATED_PROPERTIES = "/updatedProperties";
    }
}
