package com.izaron.cf.domain;

import lombok.Data;

@Data
public class Submission {

    private Long id;
    private Long contestId;
    private Long creationTimeSeconds;
    private Long relativeTimeSeconds;
    private Problem problem;
    private Party author;
    private String programmingLanguage;
    private Verdict verdict;
    private Testset testset;
    private Long passedTestCount;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;

    public enum Verdict {
        FAILED, OK, PARTIAL, COMPILATION_ERROR, RUNTIME_ERROR, WRONG_ANSWER, PRESENTATION_ERROR,
        TIME_LIMIT_EXCEEDED,MEMORY_LIMIT_EXCEEDED, IDLENESS_LIMIT_EXCEEDED, SECURITY_VIOLATED,
        CRASHED, INPUT_PREPARATION_CRASHED, CHALLENGED, SKIPPED, TESTING, REJECTED
    }

    public enum Testset {
        SAMPLES, PRETESTS, TESTS, CHALLENGES, TESTS1, TESTS2, TESTS3, TESTS4,
        TESTS5, TESTS6, TESTS7, TESTS8, TESTS9, TESTS10
    }
}
