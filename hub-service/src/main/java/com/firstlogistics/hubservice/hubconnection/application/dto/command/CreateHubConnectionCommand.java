package com.firstlogistics.hubservice.hubconnection.application.dto.command;

import java.util.UUID;

public record CreateHubConnectionCommand(
        UUID sourceHubId,
        UUID destinationHubId,
        int minutes,
        int meters
) {
}
