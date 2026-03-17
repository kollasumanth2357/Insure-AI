package com.insure.insurebackend.model;

public class AgentPolicy {

    private Long id;
    private String category; // Car, Bike, Home, Education, etc.
    private String name;
    private String coverage;
    private String premium;

    public AgentPolicy(Long id, String category, String name, String coverage, String premium) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.coverage = coverage;
        this.premium = premium;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getPremium() {
        return premium;
    }
}

