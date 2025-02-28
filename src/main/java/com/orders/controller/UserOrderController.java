package com.orders.controller;

import com.orders.constant.Messages;
import com.orders.constant.SwaggerInfo;
import com.orders.mapper.UserOrderMapper;
import com.orders.response.UserOrderResponse;
import com.orders.service.UserOrderService;
import com.orders.validation.ConsistentDateParameters;
import com.orders.validation.NotEmptyMultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = SwaggerInfo.USER_ORDERS_TAG_NAME, description = SwaggerInfo.USER_ORDERS_TAG_DESCRIPTION)
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/orders")
@Validated
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @Operation(tags = SwaggerInfo.USER_ORDERS_TAG_NAME,
            summary = SwaggerInfo.USER_ORDERS_GET_DESCRIPTION,
            description = SwaggerInfo.USER_ORDERS_GET_DESCRIPTION)
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found user orders", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserOrderResponse.class)) }),
            @ApiResponse(responseCode = "500", description = SwaggerInfo.GENERAL_ERROR_DESCRIPTION, content = @Content) })
    @ConsistentDateParameters(message = Messages.INVALID_DATE_RANGE)
    @GetMapping("")
    public ResponseEntity<List<UserOrderResponse>> findUserOrders(@Parameter(description = "Start date to retrieve orders.", example = "2021-03-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOrderDate,
                                                                  @Parameter(description = "End date to retrieve orders.", example = "2021-03-10") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endOrderDate,
                                                                  @RequestParam(required = false) Long orderId) {

        var response = userOrderService.findByUserIdAndOrderDate(orderId, startOrderDate, endOrderDate)
                .stream()
                .map(UserOrderMapper::valueOf)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(tags = SwaggerInfo.USER_ORDERS_TAG_NAME,
            summary = SwaggerInfo.USER_ORDERS_POST_DESCRIPTION,
            description = SwaggerInfo.USER_ORDERS_POST_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "500", description = SwaggerInfo.GENERAL_ERROR_DESCRIPTION, content = @Content) })
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserOrders(@Parameter(description = "File to load user orders.", required = true) @Valid @NotEmptyMultipartFile(message = Messages.INVALID_FIELD_EMPTY) @RequestPart(name = "file") MultipartFile file) throws IOException {
            userOrderService.addUserOrders(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)));
        return ResponseEntity.noContent().build();
    }
}
