package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.ChangeDeliveryStatusCommand;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.ChangeDeliveryStatusResult;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.DistributedLockPort;
import com.firstlogistics.deliverservice.application.support.DeliveryLockKeyGenerator;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryCommandFacade {

    private final DeliveryCommandService deliveryCommandService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPermissionValidator deliveryPermissionValidator;
    private final CompanyPort companyPort;
    private final HubPort hubPort;
    private final DistributedLockPort distributedLockPort;

    public CreateDeliveryResult createDelivery(CreateDeliveryCommand command, String role, UUID userId) {
        deliveryPermissionValidator.validateRole(role, Set.of(UserRole.MASTER));
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

        HubRouteResponse hubRoute = hubPort.getHubRoute(sourceHubId, destinationHubId);

        List<String> lockKeys = generateLockKeys(hubRoute);

        return
                distributedLockPort.executeWithMultiLock(
                        lockKeys,
                        () -> deliveryCommandService.createDelivery(command, supplierCompany, receiverCompany, hubRoute)
                );
    }

    public ChangeDeliveryStatusResult cancelDelivery(ChangeDeliveryStatusCommand command) {
        Delivery delivery = deliveryRepository.findById(DeliveryId.of(command.deliveryId()))
            .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        DeliveryAccessContext accessContext = DeliveryAccessContext.from(delivery);
        deliveryPermissionValidator.validate(accessContext, command.role(), command.userId(),
            Set.of(UserRole.MASTER, UserRole.HUB_MANAGER));

        return deliveryCommandService.cancelDelivery(delivery);
    }

    public void cancelDeliveryBySystem(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
            return;
        }

        deliveryCommandService.cancelDeliveryBySystem(delivery);
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
