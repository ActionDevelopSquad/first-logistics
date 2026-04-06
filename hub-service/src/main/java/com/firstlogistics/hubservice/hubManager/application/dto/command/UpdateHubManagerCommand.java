package com.firstlogistics.hubservice.hubManager.application.dto.command;

import java.util.UUID;

public record UpdateHubManagerCommand(
        UUID hubId
) {
}
