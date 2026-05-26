package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.common.entity.Identifiable;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type("Card")
public record ContactCard(
        // JMAP Additions
        @Immutable @ServerSet String id,
        Set<String> addressBookIds,
        // Metadata
        String version,
        @Nullable Instant created,
        @Default("\"individual\"") String kind,
        @Nullable String language,
        @Nullable Set<String> members,
        @Nullable String prodId,
        @Nullable Map<String, CardRelation> relatedTo,
        String uid,
        @Nullable Instant updated,
        // Name and Organization
        @Nullable Name name,
        @Nullable Map<String, Nickname> nicknames,
        @Nullable Map<String, Organization> organizations,
        @Nullable SpeakToAs speakToAs,
        @Nullable Map<String, Title> titles,
        // Contact
        @Nullable Map<String, EmailAddress> emails,
        @Nullable Map<String, OnlineService> onlineServices,
        @Nullable Map<String, Phone> phones,
        @Nullable Map<String, LanguagePref> preferredLanguages,
        // Calendaring and Scheduling
        @Nullable Map<String, Calendar> calendars,
        @Nullable Map<String, SchedulingAddress> schedulingAddresses,
        // Address and Location
        @Nullable Map<String, Address> addresses,
        // Resource
        @Nullable Map<String, CryptoKey> cryptoKeys,
        @Nullable Map<String, Directory> directories,
        @Nullable Map<String, Link> links,
        @Nullable Map<String, Media> media,
        // Multilingual
        // TODO: PatchObject repr
        @Nullable Map<String, JsonObject> localizations,
        // Additional
        @Nullable Map<String, Anniversary> anniversaries,
        @Nullable Set<String> keywords,
        @Nullable Map<String, Note> notes,
        @Nullable Map<String, PersonalInfo> personalInfo)
        implements Identifiable {}
