package com.izaron.cf.domain;

import lombok.Data;

@Data
public class RecentAction {

    private Long timeSeconds;
    private BlogEntry blogEntry;
    private Comment comment;
}
