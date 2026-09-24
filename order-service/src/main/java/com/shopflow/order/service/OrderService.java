package com.shopflow.order.service;

import com.shopflow.order.client.CustomerClient;
import com.shopflow.order.client.ProductClient;
import com.shopflow.order.client.ProductResponse;
import com.shopflow.order.dto.OrderItemRequest;
import com.shopflow.order.dto.OrderRequest;
import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.entity.Order;
import com.shopflow.order.entity.OrderItem;
import com.shopflow.order.entity.OrderStatus;
import com.shopflow.order.exception.OrderNotFoundException;
import com.shopflow.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    public OrderService(
            OrderRepository orderRepository,
            CustomerClient customerClient,
            ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        // Verify customer exists
        customerClient.getCustomer(request.customerId());

        Order order = new Order(
                request.customerId(),
                BigDecimal.ZERO,
                OrderStatus.CREATED,
                LocalDateTime.now()
        );

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {

            ProductResponse product =
                    productClient.getProduct(
                            itemRequest.productId()
                    );

            BigDecimal itemTotal =
                    product.price().multiply(
                            BigDecimal.valueOf(
                                    itemRequest.quantity()
                            )
                    );

            OrderItem orderItem = new OrderItem(
                    product.id(),
                    itemRequest.quantity(),
                    product.price()
            );

            order.addItem(orderItem);

            totalAmount = totalAmount.add(itemTotal);
        }

        Order finalOrder = new Order(
                request.customerId(),
                totalAmount,
                OrderStatus.CREATED,
                LocalDateTime.now()
        );

        for (OrderItemRequest itemRequest : request.items()) {

            ProductResponse product =
                    productClient.getProduct(
                            itemRequest.productId()
                    );

            OrderItem orderItem = new OrderItem(
                    product.id(),
                    itemRequest.quantity(),
                    product.price()
            );

            finalOrder.addItem(orderItem);
        }

        return mapToResponse(
                orderRepository.save(finalOrder)
        );
    }

    public OrderResponse getOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(id)
                );

        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByCustomer(
            Long customerId) {

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(id)
                );

        order.setStatus(OrderStatus.CANCELLED);

        return mapToResponse(
                orderRepository.save(order)
        );
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderResponse.OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderResponse.OrderItemResponse(
                                        item.getProductId(),
                                        item.getQuantity(),
                                        item.getPrice()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
}