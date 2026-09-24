package com.shopflow.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "customer-service",
        url = "${services.customer.url}"
)
public interface CustomerClient {

    @GetMapping("/customers/{id}")
    CustomerResponse getCustomer(@PathVariable Long id);
}