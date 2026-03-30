package com.firstlogistics.hubservice.application.dto.command;

public record CreateHubCommand(
        String name,
        String roadAddress,
        double latitude,
        double longitude
) {
}
