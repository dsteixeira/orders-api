package com.orders.service;

import com.orders.model.User;
import com.orders.repository.UserOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserOrderServiceTest {

    @Mock
    private UserOrderRepository userOrderRepository;

    @InjectMocks
    private UserOrderService userOrderService;

    private BufferedReader mockBufferedReader;

    @BeforeEach
    void setUp() {
        String data = "1234567890John Doe                                     " +
                "0000001234" +
                "0000005678" +
                "000000001234" +
                "20240304";
        mockBufferedReader = new BufferedReader(new StringReader(data));
    }

    @Test
    void testFindByUserIdAndOrderDate() {
        Long orderId = 1234L;
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 10);

        User mockUser = new User(1234567890L, "John Doe", Collections.emptyList());
        when(userOrderRepository.findUsersWithOrdersByDate(orderId, startDate, endDate))
                .thenReturn(List.of(mockUser));

        List<User> users = userOrderService.findByUserIdAndOrderDate(orderId, startDate, endDate);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void testAddUserOrders() throws IOException {
        User mockUser = new User(1234567890L, "John Doe", Collections.emptyList());
        doReturn(List.of(mockUser)).when(userOrderRepository).saveAllAndFlush(any());
        userOrderService.addUserOrders(mockBufferedReader);
        verify(userOrderRepository, times(1)).saveAllAndFlush(any());
    }
}
