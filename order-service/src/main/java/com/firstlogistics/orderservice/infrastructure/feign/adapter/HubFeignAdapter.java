package com.firstlogistics.orderservice.infrastructure.feign.adapter;

import com.firstlogistics.orderservice.application.port.HubPort;
import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.infrastructure.feign.HubClient;
import common.response.ApiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubFeignAdapter implements HubPort {

    private final HubClient hubClient;

    @Override
    public Optional<HubManagerResponse> getHubManagerByUserId(UUID userId) {
        try {
            ApiResponse<HubManagerResponse> response = hubClient.getHubManagerInfo(userId);

            if (response != null && response.getStatus().is2xxSuccessful() && response.getData() != null) {
                return Optional.of(response.getData());
            }

            return Optional.empty();

        } catch (FeignException.NotFound e) {
            log.warn("허브 매니저 정보를 찾을 수 없습니다. userId: {}", userId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("허브 서비스 통신 중 오류 발생: {}", e.getMessage());
            throw new OrderException(OrderErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

}
