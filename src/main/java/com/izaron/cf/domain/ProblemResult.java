package com.izaron.cf.domain;

import lombok.Data;

@Data
public class ProblemResult {

    private Double points;
    private Long penalty;
    private Long rejectedAttemptCount;
    private Type type;
    private Long bestSubmissionTimeSeconds;

    public enum Type {
        PRELIMINARY, FINAL
    }
}
