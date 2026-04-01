package com.firstlogistics.userservice.application.dto.command;

public record LoginCommand (
        String username,
        String password
)
{}
