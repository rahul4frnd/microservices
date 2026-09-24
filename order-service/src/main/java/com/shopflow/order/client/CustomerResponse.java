package com.shopflow.order.client;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}