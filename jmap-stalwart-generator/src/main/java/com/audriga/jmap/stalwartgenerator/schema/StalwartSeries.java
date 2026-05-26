package com.audriga.jmap.stalwartgenerator.schema;

import java.util.List;

public record StalwartSeries(String label, List<String> metrics, StalwartAggregate aggregate) {}
