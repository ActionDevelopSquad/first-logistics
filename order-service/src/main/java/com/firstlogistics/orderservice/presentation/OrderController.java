package com.firstlogistics.orderservice.presentation;

import com.firstlogistics.orderservice.application.OrderService;
import com.firstlogistics.orderservice.presentation.dto.request.CreateOrderRequest;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        UUID id = orderService.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_CREATED, id));
    }

}
