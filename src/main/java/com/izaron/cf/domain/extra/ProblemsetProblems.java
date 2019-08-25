package com.izaron.cf.domain.extra;

import com.izaron.cf.domain.Problem;
import com.izaron.cf.domain.ProblemStatistics;
import lombok.Data;

import java.util.List;

@Data
public class ProblemsetProblems {

    private List<Problem> problems;
    private List<ProblemStatistics> problemStatistics;
}
