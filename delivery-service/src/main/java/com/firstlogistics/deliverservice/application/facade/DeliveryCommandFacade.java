package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.DistributedLockPort;
import com.firstlogistics.deliverservice.application.support.DeliveryLockKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryCommandFacade {

    private final DeliveryCommandService deliveryCommandService;
    private final CompanyPort companyPort;
    private final HubPort hubPort;
    private final DistributedLockPort distributedLockPort;

    public CreateDeliveryResult createDelivery(CreateDeliveryCommand command) {
        return executeCreateDelivery(command);
    }

    public CreateDeliveryResult createDeliveryBySystem(CreateDeliveryCommand command) {
        return executeCreateDelivery(command);
    }

    private CreateDeliveryResult executeCreateDelivery(CreateDeliveryCommand command) {
        CompanyResponse supplierCompany = companyPort.getCompany(command.supplierCompanyId());
        CompanyResponse receiverCompany = companyPort.getCompany(command.receiverCompanyId());

        UUID sourceHubId = supplierCompany.hubId();
        UUID destinationHubId = receiverCompany.hubId();

        HubRouteResponse hubRoute = hubPort.getHubRoute(sourceHubId, destinationHubId, command.receiverCompanyId());

        List<String> lockKeys = generateLockKeys(hubRoute);

        return
                distributedLockPort.executeWithMultiLock(
                        lockKeys,
                        () -> deliveryCommandService.createDelivery(command, supplierCompany, receiverCompany, hubRoute)
                );
    }

    private List<String> generateLockKeys(HubRouteResponse hubRoute) {
        return hubRoute.routes().stream()
                .map(HubRouteStepResponse::sourceHubId)
                .distinct()
                .sorted()
                .map(DeliveryLockKeyGenerator::hubManagerAssignKey)
                .toList();
    }
}
