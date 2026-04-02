package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.HubSummaryResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record HubPageResponse(
        List<HubSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {
    public static HubPageResponse from(Page<HubSummaryResult> page){
        return new HubPageResponse(
                page.getContent().stream().map(HubSummaryResponse::from).toList(),
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
