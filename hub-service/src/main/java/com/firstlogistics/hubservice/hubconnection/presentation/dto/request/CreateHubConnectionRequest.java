package com.firstlogistics.hubservice.hubconnection.presentation.dto.request;

import com.firstlogistics.hubservice.hubconnection.application.dto.command.CreateHubConnectionCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record CreateHubConnectionRequest(
        @NotNull(message = "출발 허브 ID는 필수입니다.") UUID sourceHubId,
        @NotNull(message = "도착 허브 ID는 필수입니다.") UUID destinationHubId,
        @NotNull(message = "소요 시간은 필수입니다.")
        @Min(value = 1, message = "시간은 최소 1분 이상이어야 합니다.")
        Integer minutes,
        @NotNull(message = "거리는 필수입니다.")
        @PositiveOrZero(message = "거리는 0 이상이어야 합니다.")
        @NotNull Integer meters
) {
    public CreateHubConnectionCommand toCommand(){
        return new CreateHubConnectionCommand(
                sourceHubId,
                destinationHubId,
                minutes,
                meters
        );
    }
}
