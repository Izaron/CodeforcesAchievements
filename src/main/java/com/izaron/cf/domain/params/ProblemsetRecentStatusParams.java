package com.izaron.cf.domain.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemsetRecentStatusParams {

    Long count;
    String problemsetName;
}
