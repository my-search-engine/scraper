package com.shyndard.scraper.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Page {
    private String url;
    private String content;
    private String title;
    private Integer statusCode;
    private List<String> urls;
    private List<String> titles;
}
