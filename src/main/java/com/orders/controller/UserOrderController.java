package com.orders.controller;

import com.orders.constant.Messages;
import com.orders.mapper.UserOrderMapper;
import com.orders.response.UserOrderResponse;
import com.orders.service.UserOrderService;
import com.orders.validation.ConsistentDateParameters;
import com.orders.validation.NotEmptyMultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/orders")
@Validated
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @ConsistentDateParameters(message = Messages.INVALID_DATE_RANGE)
    @GetMapping("")
    public ResponseEntity<List<UserOrderResponse>> findUserOrders(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOrderDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endOrderDate,
                                                                  @RequestParam(required = false) Long orderId) {

        var response = userOrderService.findByUserIdAndOrderDate(orderId, startOrderDate, endOrderDate)
                .stream()
                .map(UserOrderMapper::valueOf)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserOrders(@Valid @NotEmptyMultipartFile(message = Messages.INVALID_FIELD_EMPTY) @RequestPart(name = "file") MultipartFile file) throws IOException {
            userOrderService.addUserOrders(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)));
        return ResponseEntity.noContent().build();
    }
}
