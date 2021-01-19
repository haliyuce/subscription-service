package com.supercompany.subscriptionservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.supercompany.subscriptionservice.exception.ProductNotFoundException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        ProductService.class
})
public class ProductServiceITest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    public void findById_works_when_product_exists() {
        //given
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        var persistedProduct = productRepository.save(product);

        //when
        final var actualProduct = productService.findById(persistedProduct.getId());

        //then
        assertThat(actualProduct).isEqualTo(persistedProduct);
    }

    @Test
    public void findById_fails_when_product_is_missing() {
        //given
        final int missingProductId = 123;

        //when
        final var actualException = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findById(missingProductId));

        //then
        assertThat(actualException).isEqualTo(new ProductNotFoundException(missingProductId));
    }

    @Test
    public void save_works() {
        //given
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();

        //when
        final var actualProduct = productService.save(product);

        //then
        final var expectedProduct = productRepository.findById(actualProduct.getId());
        assertThat(expectedProduct).isNotEmpty();
        assertThat(actualProduct).isEqualTo(expectedProduct.get());
    }

    @Test
    public void findAll() {
        //given
        var product1 = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        var product2 = product1.toBuilder()
                .description("some other")
                .build();
        var persistedProducts = productRepository.saveAll(List.of(product1, product2));

        //when
        final var actualProducts = productService.findAll();

        //then
        assertThat(actualProducts).isEqualTo(persistedProducts);
    }
}