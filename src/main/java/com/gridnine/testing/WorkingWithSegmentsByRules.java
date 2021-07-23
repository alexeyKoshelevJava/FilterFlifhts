package com.gridnine.testing;

import java.util.List;

public class WorkingWithSegmentsByRules {
    private List<Flight> flights;
    private Rule rule;

    public WorkingWithSegmentsByRules(List<Flight> flights, Rule rule) {
        this.flights = flights;
        this.rule = rule;
    }

    public List<Flight> filter() {

        return rule.doSomething(flights);
    }
}