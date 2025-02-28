package com.orders.mapper;

import com.orders.response.UserOrderResponse;
import com.orders.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOrderMapper {

    public static UserOrderResponse valueOf(User user) {

        var orders = Optional.ofNullable(user.getOrders())
                .filter(l -> !l.isEmpty())
                .orElse(List.of())
                .stream().map(OrderMapper::valueOf)
                .collect(Collectors.toList());

        return UserOrderResponse.builder()
                .name(user.getName())
                .userId(user.getUserId())
                .orders(orders)
                .build();
    }
}