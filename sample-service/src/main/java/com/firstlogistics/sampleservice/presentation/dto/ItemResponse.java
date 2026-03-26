package com.firstlogistics.sampleservice.presentation.dto;

import com.firstlogistics.sampleservice.infrastructure.entity.Item;

import java.time.LocalDateTime;

public record ItemResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ItemResponse fromEntity(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
