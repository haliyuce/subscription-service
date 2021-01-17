package com.supercompany.subscriptionservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Duration;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    private int id;
    private String name;
    private String description;
    private Duration duration;
    private BigDecimal price;
    private BigDecimal taxRate;

    @Builder
    protected Product(final String name,
                      final String description,
                      final Duration duration,
                      final BigDecimal price,
                      final BigDecimal taxRate) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.taxRate = taxRate;
    }
}
