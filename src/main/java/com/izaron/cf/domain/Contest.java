package com.izaron.cf.domain;

import lombok.Data;

@Data
public class Contest {

    private Long id;
    private String name;
    private Type type;
    private Phase phase;
    private Boolean frozen;
    private Long durationSeconds;
    private Long startTimeSeconds;
    private Long relativeTimeSeconds;
    private String preparedBy;
    private String websiteUrl;
    private String description;
    private String kind;
    private String icpcRegion;
    private String country;
    private String city;
    private String season;

    public enum Type {
        CF, IOI, ICPC
    }

    public enum Phase {
        BEFORE, CODING, PENDING_SYSTEM_TEST, SYSTEM_TEST, FINISHED
    }
}
