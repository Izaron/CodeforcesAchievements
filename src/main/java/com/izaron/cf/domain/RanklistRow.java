package com.izaron.cf.domain;

import lombok.Data;

import java.util.List;

@Data
public class RanklistRow {

    private Party party;
    private Long rank;
    private Double points;
    private Long penalty;
    private Long successfulHackCount;
    private Long unsuccessfulHackCount;
    private List<ProblemResult> problemResults;
    private Long lastSubmissionTimeSeconds;
}
