package com.shopflow.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Order items are required")
        @Size(min = 1, message = "Order must contain at least one item")
        List<@Valid OrderItemRequest> items
) {
}