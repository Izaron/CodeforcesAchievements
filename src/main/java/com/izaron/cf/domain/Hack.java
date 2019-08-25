package com.izaron.cf.domain;

import lombok.Data;

@Data
public class Hack {

    private Long id;
    private Long creationTimeSeconds;
    private Party hacker;
    private Party defender;
    private Verdict verdict;
    private Problem problem;
    private String test;
    private JudgeProtocol judgeProtocol;

    public enum Verdict {
        HACK_SUCCESSFUL, HACK_UNSUCCESSFUL, INVALID_INPUT, GENERATOR_INCOMPILABLE,
        GENERATOR_CRASHED, IGNORED, TESTING, OTHER
    }

    @Data
    public static class JudgeProtocol {
        private Boolean manual;
        private String protocol;
        private String verdict;
    };
}
