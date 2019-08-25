package com.izaron.cf.domain;

import com.izaron.cf.domain.extra.ContestStandings;

import java.util.HashSet;
import java.util.Set;

public class DomainUtils {

    public static Set<String> getParticipantHandles(ContestStandings standings) {
        Set<String> set = new HashSet<>();
        for (RanklistRow row : standings.getRows()) {
            Party party = row.getParty();
            for (Member member : party.getMembers()) {
                set.add(member.getHandle());
            }
        }
        return set;
    }
}
