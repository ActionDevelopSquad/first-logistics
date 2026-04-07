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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("[Facade 배송 생성] Feign 호출 시작 - supplierCompanyId: {}, receiverCompanyId: {}", command.supplierCompanyId(), command.receiverCompanyId());
        CompanyResponse supplierCompany = companyPort.getCompany(command.supplierCompanyId());
        CompanyResponse receiverCompany = companyPort.getCompany(command.receiverCompanyId());

        UUID sourceHubId = supplierCompany.hubId();
        UUID destinationHubId = receiverCompany.hubId();
        log.info("[Facade 배송 생성] 허브 경로 조회 - sourceHubId: {}, destHubId: {}", sourceHubId, destinationHubId);

        HubRouteResponse hubRoute = hubPort.getHubRoute(sourceHubId, destinationHubId, command.receiverCompanyId());
        log.info("[Facade 배송 생성] 허브 경로 {}개 수신, 분산락 획득 시작", hubRoute.routes().size());

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
