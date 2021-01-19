package com.supercompany.subscriptionservice.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercompany.subscriptionservice.exception.*;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(SubscriptionController.class)
@AutoConfigureMockMvc
public class SubscriptionControllerITest {

    @MockBean
    private SubscriptionService subscriptionService;

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
                "\"startDate\":\"" + startDateStr + "\"," +
                "\"endDate\":\"" + endDateStr + "\"," +
                "\"status\":\"ACTIVE\"}";

        //when
        mockMvc.perform(get("/subscriptions").param("userId", "" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonStr));

        //then
        verify(subscriptionService).getActiveUserSubscription(userId);
        verifyNoMoreInteractions(subscriptionService);
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
        verify(subscriptionService).subscribe(1, 10);
        verifyNoMoreInteractions(subscriptionService);
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
                .andExpect(status().is(e.getStatus().value()))
                .andExpect(status().reason(expectedStr));

        //then
    }

    public static Stream<Arguments> possibleExceptionsAndMessagesWhileSubs() {
        return Stream.of(
                Arguments.of(new UserAlreadySubscribedException(1), "User with id 1 has already subscribed to the a product"),
                Arguments.of(new ProductNotFoundException(1), "No product exists with the id:1"));
    }

    @SneakyThrows
    @Test
    public void cancel_works() {
        //given
        final var subsId = Integer.valueOf(1);

        //when
        mockMvc
                .perform(put("/subscriptions/" + subsId.toString() + "/cancel"))
                .andExpect(status().isNoContent());

        //then
        verify(subscriptionService).cancel(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    @SneakyThrows
    @Test
    public void cancel_fails_when_subscription_is_missing() {
        //given
        final var subsId = Integer.valueOf(1);
        doThrow(new SubscriptionNotFoundException(subsId))
                .when(subscriptionService)
                .cancel(subsId);

        //when
        mockMvc.perform(put("/subscriptions/" + subsId.toString() + "/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Missing subscription with id:" + subsId));

        //then
        verify(subscriptionService).cancel(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    @SneakyThrows
    @Test
    public void cancel_fails_when_subscription_is_not_cancellable() {
        //given
        final var subsId = Integer.valueOf(1);
        doThrow(new InvalidStateToCancelException(subsId))
                .when(subscriptionService)
                .cancel(subsId);

        //when
        mockMvc.perform(put("/subscriptions/" + subsId.toString() + "/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Subscription with id " + subsId + " is not cancellable!"));

        //then
        verify(subscriptionService).cancel(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    @SneakyThrows
    @Test
    public void pause_works() {
        //given
        final var subsId = Integer.valueOf(1);
        doReturn(mock(UserSubscription.class))
                .when(subscriptionService)
                .pause(anyInt());

        //when
        mockMvc.perform(put("/subscriptions/" + subsId + "/pause"))
                .andExpect(status().isNoContent());

        //then
        verify(subscriptionService).pause(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("possibleExceptionsAndMessagesWhilePause")
    public void pause_fails_when_service_throws_known_exception(ResponseStatusException e, String expectedStr) {
        //given
        final var subsId = Integer.valueOf(1);

        doThrow(e)
                .when(subscriptionService)
                .pause(anyInt());

        //when
        mockMvc.perform(put("/subscriptions/" + subsId + "/pause")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(e.getStatus().value()))
                .andExpect(status().reason(expectedStr));

        //then
        verify(subscriptionService).pause(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    public static Stream<Arguments> possibleExceptionsAndMessagesWhilePause() {
        return Stream.of(
                Arguments.of(new SubscriptionNotFoundException(1), "Missing subscription with id:1"),
                Arguments.of(new InvalidStateToPauseException(1, SubscriptionStatus.CANCELLED), "Subscription with id 1 is not in ACTIVE but CANCELLED state"));
    }

    @SneakyThrows
    @Test
    public void unpause_works() {
        //given
        final var subsId = Integer.valueOf(1);
        doReturn(mock(UserSubscription.class))
                .when(subscriptionService)
                .unpause(anyInt());

        //when
        mockMvc.perform(put("/subscriptions/" + subsId + "/unpause"))
                .andExpect(status().isNoContent());

        //then
        verify(subscriptionService).unpause(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("possibleExceptionsAndMessagesWhileUnpause")
    public void unpause_fails_when_service_throws_known_exception(ResponseStatusException e, String expectedStr) {
        //given
        final var subsId = Integer.valueOf(1);

        doThrow(e)
                .when(subscriptionService)
                .unpause(anyInt());

        //when
        mockMvc.perform(put("/subscriptions/" + subsId + "/unpause")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(e.getStatus().value()))
                .andExpect(status().reason(expectedStr));

        //then
        verify(subscriptionService).unpause(subsId);
        verifyNoMoreInteractions(subscriptionService);
    }

    public static Stream<Arguments> possibleExceptionsAndMessagesWhileUnpause() {
        return Stream.of(
                Arguments.of(new SubscriptionNotFoundException(1), "Missing subscription with id:1"),
                Arguments.of(new InvalidStateToUnpauseException(1, SubscriptionStatus.CANCELLED), "Subscription with id 1 is not in PAUSED but CANCELLED state"));
    }
}