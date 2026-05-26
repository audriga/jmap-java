package com.audriga.jmap.stalwartgenerator.schema;

import java.util.List;

public record StalwartDashboard(String id, String label, List<StalwartCard> cards, List<StalwartChart> charts) {}
