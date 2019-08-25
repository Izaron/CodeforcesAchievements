package com.izaron.cf.domain;

import lombok.Data;

@Data
public class RatingChange {

    private Long contestId;
    private String contestName;
    private String handle;
    private Long rank;
    private Long ratingUpdateTimeSeconds;
    private Long oldRating;
    private Long newRating;
}
