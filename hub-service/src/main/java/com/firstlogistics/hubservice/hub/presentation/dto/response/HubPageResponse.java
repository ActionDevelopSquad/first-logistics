package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.SearchHubResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public record HubPageResponse(
        List<HubSummary> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {
    public static HubPageResponse from(Page<SearchHubResult> page){
        return new HubPageResponse(
                page.getContent().stream().map(HubSummary::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }


    public record HubSummary(
            UUID hubId,
            String name,
            String status
    ){
        public static HubSummary from(SearchHubResult result){
            return new HubSummary(
                    result.hubId(),
                    result.name(),
                    result.status().name()
            );
        }
    }
}
