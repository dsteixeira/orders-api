package com.orders.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_order_product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 5961885848299280804L;

    @EmbeddedId
    private ProductId id;

    @Column(name = "price")
    private Double value;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Order order;
}
