package com.firstlogistics.hubservice.hubconnection.presentation.dto.response;

import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record HubConnectionPageResponse (
    List<HubConnectionResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean empty
){
    public static HubConnectionPageResponse from(Page<HubConnectionResult> page){
        return new HubConnectionPageResponse(
                page.getContent().stream().map(HubConnectionResponse::from).toList(),
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
