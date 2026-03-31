package com.firstlogistics.hubservice.hub.application.dto.command;

public record CreateHubCommand(
        String name,
        String roadAddress,
        double latitude,
        double longitude
) {
}
