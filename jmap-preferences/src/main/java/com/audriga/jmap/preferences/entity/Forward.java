package com.audriga.jmap.preferences.entity;

@lombok.Builder(toBuilder = true)
public record Forward(boolean isEnabled, String forwardAction, String forwardTo) {}
