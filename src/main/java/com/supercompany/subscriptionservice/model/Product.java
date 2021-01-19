package com.supercompany.subscriptionservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Period period;
    @NotNull
    private BigDecimal price;
    @NotNull
    private BigDecimal taxRate;

    @Builder(toBuilder = true)
    protected Product(final String name,
                      final String description,
                      final Period period,
                      final BigDecimal price,
                      final BigDecimal taxRate) {
        this.name = name;
        this.description = description;
        this.period = period;
        this.price = price;
        this.taxRate = taxRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id
                && name.equals(product.name)
                && Objects.equals(description, product.description)
                && period.equals(product.period)
                && price.equals(product.price)
                && taxRate.equals(product.taxRate);
    }

    @Override
    public int hashCode() {
        return 12;
    }
}
