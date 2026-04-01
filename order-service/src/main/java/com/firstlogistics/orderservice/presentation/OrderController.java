package com.firstlogistics.orderservice.presentation;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.presentation.dto.request.CreateOrderRequest;
import com.firstlogistics.orderservice.presentation.dto.response.OrderIdResponse;
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

    private final OrderCommandService orderCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderIdResponse>> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        UUID id = orderCommandService.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_CREATED, OrderIdResponse.from(id)));
    }

}
