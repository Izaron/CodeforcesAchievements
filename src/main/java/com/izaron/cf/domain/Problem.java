package com.izaron.cf.domain;

import lombok.Data;

import java.util.List;

@Data
public class Problem {

    private Long contestId;
    private String problemsetName;
    private String index;
    private String name;
    private Type type;
    private Double points;
    private Long rating;
    private List<String> tags;

    public enum Type {
        PROGRAMMING, QUESTION
    }
}
