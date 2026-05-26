package com.audriga.jmap.contacts.entity;

import lombok.Builder;

@Builder(toBuilder = true)
public record AddressBookRights(boolean mayRead, boolean mayWrite, boolean mayShare, boolean mayDelete) {}
