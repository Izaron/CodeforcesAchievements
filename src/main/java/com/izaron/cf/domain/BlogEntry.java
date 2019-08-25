package com.izaron.cf.domain;

import lombok.Data;

import java.util.List;

@Data
public class BlogEntry {

    private Long id;
    private String originalLocale;
    private Long creationTimeSeconds;
    private String authorHandle;
    private String title;
    private String content;
    private String locale;
    private Long modificationTimeSeconds;
    private Boolean allowViewHistory;
    private List<String> tags;
    private Long rating;
}
