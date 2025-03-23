package com.letzgo.LetzgoBe.global.common.response;

import lombok.Getter;

import java.util.List;

@Getter
public class Page<T> {
    private List<T> contents;

    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalCount;

    public static <T> Page<T> of(org.springframework.data.domain.Page<T> pagedContents) {
        Page<T> converted = new Page<>();
        converted.contents = pagedContents.getContent();
        converted.pageNumber = pagedContents.getNumber();
        converted.pageSize = pagedContents.getSize();
        converted.totalPages = pagedContents.getTotalPages();
        converted.totalCount = pagedContents.getTotalElements();
        return converted;
    }
}

