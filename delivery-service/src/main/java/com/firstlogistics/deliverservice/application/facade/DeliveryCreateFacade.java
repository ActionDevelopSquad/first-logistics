package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryResult;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.infrastructure.redis.lock.DeliveryDistributedLockService;
import com.firstlogistics.deliverservice.infrastructure.redis.lock.DeliveryLockKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DeliveryCreateFacade {

    private final DeliveryCommandService deliveryCommandService;
    private final CompanyPort companyPort;
    private final HubPort hubPort;
    private final DeliveryDistributedLockService deliveryDistributedLockService;

    public DeliveryResult createDelivery(CreateDeliveryCommand command) {
        CompanyResponse company = companyPort.getCompany(command.receiverCompanyId());
        UUID destinationHubId = company.hubId();

        HubRouteResponse hubRoute = hubPort.getHubRoute(command.sourceHubId(), destinationHubId);

        List<String> lockKeys = generateLockKeys(hubRoute, destinationHubId);

        return deliveryDistributedLockService.executeWithMultiLock(
                lockKeys,
                () -> deliveryCommandService.createDelivery(command, company, hubRoute)
        );
    }

    private List<String> generateLockKeys(HubRouteResponse hubRoute, UUID destinationHubId) {
        return Stream.concat(
                    hubRoute.routes().stream().map(HubRouteStepResponse::sourceHubId),
                    Stream.of(destinationHubId)
                )
                .distinct()
                .sorted()
                .map(DeliveryLockKeyGenerator::hubStaffAssignKey)
                .toList();
    }
}
