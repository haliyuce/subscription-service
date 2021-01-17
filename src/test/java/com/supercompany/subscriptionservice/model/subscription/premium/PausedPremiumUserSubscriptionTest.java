package com.supercompany.subscriptionservice.model.subscription.premium;

import static org.assertj.core.api.Assertions.assertThat;

import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PausedPremiumUserSubscriptionTest {

    private PausedPremiumUserSubscription pausedPremiumUserSubscription;

    @BeforeEach
    public void before() {
        pausedPremiumUserSubscription = PausedPremiumUserSubscription.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .userId(2)
                .product(Product.builder().build())
                .build();
    }

    @Test
    public void cancel() {
        //given

        //when
        pausedPremiumUserSubscription.cancel();

        //then
        assertThat(pausedPremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    public void pause() {
        //given

        //when
        pausedPremiumUserSubscription.pause();

        //then
        assertThat(pausedPremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.PAUSED);
    }

    @Test
    public void unpause() {
        //given

        //when
        pausedPremiumUserSubscription.unpause();

        //then
        assertThat(pausedPremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }
}