package com.izaron.cf.domain;

import lombok.Data;

@Data
public class ProblemStatistics {

    private Long contestId;
    private String index;
    private Long solvedCount;
}
