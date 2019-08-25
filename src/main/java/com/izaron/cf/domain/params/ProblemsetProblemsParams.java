package com.izaron.cf.domain.params;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemsetProblemsParams {

    private List<String> tags;
    private String problemsetName;
}
