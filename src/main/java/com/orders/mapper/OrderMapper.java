package com.orders.mapper;

import com.orders.response.OrderResponse;
import com.orders.model.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {

    public static OrderResponse valueOf(final Order order) {

        var products = Optional.ofNullable(order.getProducts())
                .filter(l -> !l.isEmpty())
                .orElse(List.of())
                .stream().map(ProductMapper::valueOf)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .date(order.getOrderDate())
                .total(order.getTotal())
                .products(products)
                .build();
    }

}
