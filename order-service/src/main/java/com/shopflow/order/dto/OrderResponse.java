package com.shopflow.order.dto;

import com.shopflow.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(

        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {

    public record OrderItemResponse(
            Long productId,
            Integer quantity,
            BigDecimal price
    ) {
    }
}