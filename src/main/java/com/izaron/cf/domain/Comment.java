package com.izaron.cf.domain;

import lombok.Data;

@Data
public class Comment {

    private Long id;
    private Long creationTimeSeconds;
    private String commentatorHandle;
    private String locale;
    private String text;
    private Long parentCommentId;
    private Long rating;
}
