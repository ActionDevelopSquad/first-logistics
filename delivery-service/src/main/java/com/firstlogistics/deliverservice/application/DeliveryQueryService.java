package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

	private final DeliveryRepository deliveryRepository;
	private final DeliveryQueryRepositoryPort deliveryQueryRepositoryPort;

	public boolean existsByOrderId(UUID orderId) {
		return deliveryRepository.existsByOrderId(orderId);
	}

	public DeliveryListResult getDeliveries(DeliveryListQuery query) {
		validateDateRange(query.startDate(), query.endDate());
		return deliveryQueryRepositoryPort.findDeliveries(query);
	}

	private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DATE_RANGE);
		}
	}
}
