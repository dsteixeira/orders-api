package com.orders.service;

import com.orders.model.Order;
import com.orders.model.Product;
import com.orders.model.ProductId;
import com.orders.model.User;
import com.orders.repository.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserOrderService {

    @Autowired
    private UserOrderRepository userOrderRepository;

    public List<User> findByUserIdAndOrderDate(Long orderId,
                                               LocalDate startDate,
                                               LocalDate endDate) {
        return userOrderRepository.findUsersWithOrdersByDate(orderId, startDate, endDate);
    }

    public void addUserOrders(BufferedReader bufferedReader) throws IOException {
        var orders = buildUserOrders(bufferedReader);
        userOrderRepository.saveAllAndFlush(orders);
    }

    private List<User> buildUserOrders(final BufferedReader bufferedReader) throws IOException {

        Map<Long, User> usersMap = new HashMap<>();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            // Extract fields using substring (Fixed positions)
            Long userId = Long.parseLong(line.substring(0, 10));
            String name = line.substring(10, 55).stripLeading();
            Long orderId = Long.parseLong(line.substring(55, 65));
            Long productId = Long.parseLong(line.substring(65, 75));
            double productPrice = Double.parseDouble(line.substring(75, 87));
            LocalDate orderDate = LocalDate.parse(line.substring(87, 95), pattern);

            // Create or get User
            usersMap.putIfAbsent(userId, User.builder()
                    .userId(userId)
                    .name(name)
                    .orders(new ArrayList<>())
                    .build());
            User user = usersMap.get(userId);

            // Find existing Order or create a new one
            Order order = user.getOrders().stream()
                    .filter(o -> o.getOrderId().equals(orderId))
                    .findFirst()
                    .orElseGet(() -> {
                        Order newOrder = Order.builder()
                                .orderId(orderId)
                                .orderDate(orderDate)
                                .products(new ArrayList<>())
                                .total(0.0)
                                .user(user)
                                .build();
                        user.getOrders().add(newOrder);
                        return newOrder;
                    });

            // Update total value for the Order
            order.setTotal(order.getTotal() + productPrice);

            // Same product in the Order, merge in 1 product and sum its value/price
            var product = order.getProducts()
                    .stream()
                    .filter(p -> p.getId().getProductId().equals(productId))
                    .findFirst()
                    .orElseGet(() -> {
                        Product newProduct = Product.builder()
                                .id(new ProductId(productId, orderId))
                                .value(0.0)
                                .order(order)
                                .build();
                        order.getProducts().add(newProduct);
                        return newProduct;
                    });

            product.setValue(product.getValue() + productPrice);
        }

        return usersMap.values().stream().toList();
    }
}
