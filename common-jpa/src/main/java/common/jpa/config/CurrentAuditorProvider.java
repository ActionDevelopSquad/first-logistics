package common.jpa.config;

import java.util.Optional;
import java.util.UUID;

public interface CurrentAuditorProvider {
    Optional<UUID> getCurrentAuditor();
}