package com.firstlogistics.hubservice.hubManager.presentation.dto.response;

import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record HubManagerPageResponse(
        List<HubManagerResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {
    public static HubManagerPageResponse from(Page<HubManagerResult> page){
        return new HubManagerPageResponse(
                page.getContent().stream().map(HubManagerResponse::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
