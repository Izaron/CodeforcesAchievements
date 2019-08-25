package com.izaron.cf.domain.extra;

import com.izaron.cf.domain.Contest;
import com.izaron.cf.domain.Problem;
import com.izaron.cf.domain.RanklistRow;
import lombok.Data;

import java.util.List;

@Data
public class ContestStandings {

    private Contest contest;
    private List<Problem> problems;
    private List<RanklistRow> rows;
}
