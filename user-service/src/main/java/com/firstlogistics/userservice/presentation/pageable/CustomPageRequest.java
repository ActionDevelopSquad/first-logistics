package com.firstlogistics.userservice.presentation.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class CustomPageRequest {

    public static final Set<Integer> ALLOWED_PAGE_SIZES = Set.of(10, 30, 50);
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private CustomPageRequest() {
    }

    public static Pageable defaultPageable() {
        return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, DEFAULT_SORT);
    }

    public static Pageable of(int page, int size, Sort sort) {
        int normalizedPage = Math.max(page, DEFAULT_PAGE);
        int normalizedSize = ALLOWED_PAGE_SIZES.contains(size) ? size : DEFAULT_SIZE;
        Sort normalizedSort = (sort == null || sort.isUnsorted()) ? DEFAULT_SORT : sort;

        return PageRequest.of(normalizedPage, normalizedSize, normalizedSort);
    }
}