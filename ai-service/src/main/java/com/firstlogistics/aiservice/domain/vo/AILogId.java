package com.firstlogistics.aiservice.domain.vo;

import java.util.UUID;

public record AILogId(UUID id) {
    public static AILogId of() {
        return AILogId.of(UUID.randomUUID());
    }

    public static AILogId of(UUID id){
        return new AILogId(id);
    }
}
