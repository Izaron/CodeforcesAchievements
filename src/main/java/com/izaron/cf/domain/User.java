package com.izaron.cf.domain;

import lombok.Data;

@Data
public class User {

    private String handle;
    private String email;
    private String vkId;
    private String openId;
    private String firstName;
    private String lastName;
    private String country;
    private String city;
    private String organization;
    private Long contribution;
    private String rank;
    private Long rating;
    private String maxRank;
    private Long maxRating;
    private Long lastOnlineTimeSeconds;
    private Long registrationTimeSeconds;
    private Long friendOfCount;
    private String avatar;
    private String titlePhoto;
}
