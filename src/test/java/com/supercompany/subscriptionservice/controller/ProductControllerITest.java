package com.supercompany.subscriptionservice.controller;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.service.ProductService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
class ProductControllerITest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    public void getAllProducts() {
        //given
        final var products = List.of(Product.builder()
                .price(BigDecimal.TEN)
                .name("some cool product")
                .description("cool product")
                .taxRate(BigDecimal.TEN)
                .period(Period.ofMonths(1))
                .build());
        doReturn(products)
                .when(productService)
                .findAll();
        final var expectedResponseStr = "[" +
                "{" +
                "   \"id\":0," +
                "   \"name\":\"some cool product\"," +
                "   \"description\":\"cool product\"," +
                "   \"period\":\"P1M\"," +
                "   \"price\":10," +
                "   \"taxRate\":10" +
                "}]";

        //when
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseStr));

        //then
    }
}