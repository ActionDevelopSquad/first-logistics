package com.firstlogistics.hubservice.hub.presentation.dto.request;


import com.firstlogistics.hubservice.hub.application.dto.command.CreateHubCommand;
import jakarta.validation.constraints.*;

public record CreateHubRequest(

        @NotBlank
        @Size(max = 30)
        String name,

        @NotBlank
        @Size(max = 255)
        String roadAddress,

        @NotNull
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double latitude,

        @NotNull
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double longitude,

        @NotNull
        String type
) {
    public CreateHubCommand toCommand(){
        return new CreateHubCommand(
            name,
            roadAddress,
            latitude,
            longitude,
                type
        );
    }
}
