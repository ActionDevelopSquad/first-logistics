package com.firstlogistics.userservice.application.dto.command;

import java.util.UUID;

public record UserUpdateCommand(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String slackId
) {}