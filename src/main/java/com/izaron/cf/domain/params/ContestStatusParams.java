package com.izaron.cf.domain.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContestStatusParams {

    private Long contestId;
    private String handle;
    private Long from;
    private Long count;
}