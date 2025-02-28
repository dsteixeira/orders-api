package com.orders.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({ "orderId", "total", "date", "products"})
public class OrderResponse {

    @JsonProperty("order_id")
    private Long orderId;
    private Double total;
    private LocalDate date;
    private List<ProductResponse> products;

}
