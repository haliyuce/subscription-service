package com.supercompany.subscriptionservice.model.subscription.premium;

import static com.supercompany.subscriptionservice.exception.InvalidProcessException.InvalidProcessCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.supercompany.subscriptionservice.exception.InvalidProcessException;
import com.supercompany.subscriptionservice.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class CancelledPremiumUserSubscriptionTest {

    private CancelledPremiumUserSubscription cancelledPremiumUserSubscription;

    @BeforeEach
    public void before() {
        cancelledPremiumUserSubscription = CancelledPremiumUserSubscription.builder()
                .userId(1)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .product(Product.builder().build())
                .build();
    }

    @Test
    public void cancel() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> cancelledPremiumUserSubscription.cancel());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_CANCELLED));
    }

    @Test
    public void pause() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> cancelledPremiumUserSubscription.pause());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_PAUSED));
    }

    @Test
    public void unpause() {
        //given

        //when
        final var actualException = assertThrows(
                InvalidProcessException.class,
                () -> cancelledPremiumUserSubscription.unpause());

        //then
        assertThat(actualException)
                .isEqualTo(new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_UNPAUSED));
    }
}