package com.supercompany.subscriptionservice.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercompany.subscriptionservice.exception.ProductNotFoundException;
import com.supercompany.subscriptionservice.exception.UserAlreadySubscribedException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
import com.supercompany.subscriptionservice.service.ProductService;
import com.supercompany.subscriptionservice.service.SubscriptionService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest
@Import({
        SubscriptionController.class
})
@AutoConfigureMockMvc
public class SubscriptionControllerITest {

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void getActiveUserSubscription_works() {
        //given
        final var userId = 1;
        final var subscription = UserSubscription.builder()
                .userId(userId)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(5))
                .build();
        doReturn(Optional.of(subscription))
                .when(subscriptionService)
                .getActiveUserSubscription(anyInt());
        final var startDateStr = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                .format(subscription.getStartDate());
        final var endDateStr = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                .format(subscription.getEndDate());
        final var expectedJsonStr = "{" +
                "\"id\":0," +
                "\"userId\":1," +
                "\"product\":null," +
                "\"startDate\":\"" + startDateStr + "\"," +
                "\"endDate\":\"" + endDateStr + "\"," +
                "\"pauseDate\":null," +
                "\"status\":\"ACTIVE\"," +
                "\"version\":0}";

        //when
        mockMvc.perform(get("/subscriptions").param("userId", "" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonStr, true));

        //then
    }

    @SneakyThrows
    @Test
    public void subscribe_works() {
        //given
        final var subsReq = "{" +
                "\"userId\": 1," +
                "\"productId\": 10" +
                "}";
        final var userSubs = UserSubscription.builder()
                .userId(1)
                .product(Product.builder().build())
                .build();
        doReturn(userSubs)
                .when(subscriptionService)
                .subscribe(anyInt(), anyInt());
        final var expectedJsonStr = "{" +
                "\"id\":0," +
                "\"userId\":1," +
                "\"product\":{" +
                "   \"id\":0}" +
                "}";

        //when
        mockMvc.perform(post("/subscriptions")
                    .content(subsReq)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJsonStr, true));

        //then
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("possibleExceptionsAndMessagesWhileSubs")
    public void subscribe_fails_when_service_throws_known_exception(ResponseStatusException e, String expectedStr) {
        //given
        final var subsReq = "{" +
                "\"userId\": 1," +
                "\"productId\": 10" +
                "}";
        doThrow(e)
                .when(subscriptionService)
                .subscribe(anyInt(), anyInt());

        //when
        mockMvc.perform(post("/subscriptions")
                .content(subsReq)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(expectedStr));

        //then
    }

    public static Stream<Arguments> possibleExceptionsAndMessagesWhileSubs() {
        return Stream.of(
                Arguments.of(new UserAlreadySubscribedException(1), "User with id 1 has already subscribed to the a product"),
                Arguments.of(new ProductNotFoundException(1), "No product exists with the id:1"));
    }

}