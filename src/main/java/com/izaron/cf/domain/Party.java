package com.izaron.cf.domain;

import lombok.Data;

import java.util.List;

@Data
public class Party {

    private Long contestId;
    private List<Member> members;
    private ParticipantType participantType;
    private Long teamId;
    private String teamName;
    private Boolean ghost;
    private Long room;
    private Long startTimeSeconds;

    public enum ParticipantType {
        CONTESTANT, PRACTICE, VIRTUAL, MANAGER, OUT_OF_COMPETITION
    }
}
