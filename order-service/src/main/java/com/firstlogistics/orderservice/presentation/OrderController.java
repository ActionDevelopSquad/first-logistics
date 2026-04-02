package com.firstlogistics.orderservice.presentation;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.presentation.dto.request.CreateOrderRequest;
import com.firstlogistics.orderservice.presentation.dto.response.OrderIdResponse;
import com.firstlogistics.orderservice.presentation.dto.response.OrderStatusResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderIdResponse>> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        UUID id = orderCommandService.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_CREATED, OrderIdResponse.from(id)));
    }

    @PatchMapping("/{orderId}/acceptance")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> acceptOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.acceptOrder(userId, orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/rejection")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> rejectOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.rejectOrder(userId, orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

}
