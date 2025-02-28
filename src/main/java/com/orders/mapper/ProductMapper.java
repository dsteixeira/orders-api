package com.orders.mapper;

import com.orders.response.ProductResponse;
import com.orders.model.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static ProductResponse valueOf(final Product product) {
        return ProductResponse.builder()
                .id(product.getId().getProductId())
                .value(product.getValue())
                .build();
    }
}
