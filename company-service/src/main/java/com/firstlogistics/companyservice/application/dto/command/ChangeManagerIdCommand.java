package com.firstlogistics.companyservice.application.dto.command;

import java.util.UUID;

public record ChangeManagerIdCommand(
        UUID companyId,
        UUID managerId
) {
}
