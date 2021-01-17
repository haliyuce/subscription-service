package com.supercompany.subscriptionservice.model.subscription.premium;

import com.supercompany.subscriptionservice.exception.InvalidProcessException;
import com.supercompany.subscriptionservice.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.supercompany.subscriptionservice.exception.InvalidProcessException.InvalidProcessCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpiredPremiumUserSubscriptionTest {

    private ExpiredPremiumUserSubscription expiredPremiumUserSubscription;

    @BeforeEach
    public void before() {
        expiredPremiumUserSubscription = ExpiredPremiumUserSubscription.builder()
                .userId(1)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .product(Product.builder().build())
                .build();
    }

    //TODO: name methods
    @Test
    public void cancel() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> expiredPremiumUserSubscription.cancel());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_CANCELLED));
    }

    @Test
    public void pause() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> expiredPremiumUserSubscription.pause());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_PAUSED));
    }

    @Test
    public void unpause() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> expiredPremiumUserSubscription.unpause());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_UNPAUSED));
    }
}