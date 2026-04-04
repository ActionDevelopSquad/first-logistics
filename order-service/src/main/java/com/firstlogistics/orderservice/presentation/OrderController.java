package com.firstlogistics.orderservice.presentation;

import com.firstlogistics.orderservice.application.OrderCommandService;
import com.firstlogistics.orderservice.application.OrderQueryService;
import com.firstlogistics.orderservice.presentation.dto.request.CreateOrderRequest;
import com.firstlogistics.orderservice.presentation.dto.request.SearchOrderRequest;
import com.firstlogistics.orderservice.presentation.dto.response.OrderIdResponse;
import com.firstlogistics.orderservice.presentation.dto.response.OrderStatusResponse;
import com.firstlogistics.orderservice.presentation.dto.response.OrderSummaryResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final OrderQueryService orderQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderIdResponse>> createOrder(
            @RequestBody @Valid CreateOrderRequest request
    ) {
        UUID id = orderCommandService.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_CREATED, OrderIdResponse.from(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getOrders(
            @ModelAttribute SearchOrderRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<OrderSummaryResponse> response = orderQueryService.getOrders(request.toQuery(), pageable)
                .map(OrderSummaryResponse::from);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_OK, response));
    }

    @PatchMapping("/{orderId}/acceptance")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> acceptOrder(
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.acceptOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/rejection")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> rejectOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.rejectOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/cancellation")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> cancelOrder(
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/cancellation-request")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> requestCancel(
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.requestCancel(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/cancellation-request/approval")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> approveCancelRequest(
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.approveCancelRequest(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

    @PatchMapping("/{orderId}/cancellation-request/rejection")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> rejectCancelRequest(
            @PathVariable UUID orderId
    ) {
        String status = orderCommandService.rejectCancelRequest(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderSuccessCode.ORDER_STATUS_UPDATED,
                        OrderStatusResponse.of(orderId, status)));
    }

}
