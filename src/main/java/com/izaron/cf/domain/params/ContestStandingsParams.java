package com.izaron.cf.domain.params;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContestStandingsParams {

    private Long contestId;
    private Long from;
    private Long count;
    private List<String> handles;
    private Long room;
    private Boolean showUnofficial;
}
