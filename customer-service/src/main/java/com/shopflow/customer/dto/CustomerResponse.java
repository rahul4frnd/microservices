package com.shopflow.customer.dto;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}