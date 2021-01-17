package com.supercompany.subscriptionservice.model.subscription.premium;

import static org.assertj.core.api.Assertions.assertThat;

import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ActivePremiumUserSubscriptionTest {

    private ActivePremiumUserSubscription activePremiumUserSubscription;

    @BeforeEach
    public void before() {
        activePremiumUserSubscription = ActivePremiumUserSubscription.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .userId(2)
                .product(Product.builder().build())
                .build();
    }

    @Test
    public void cancel_works() {
        //given

        //when
        activePremiumUserSubscription.cancel();

        //then
        assertThat(activePremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    public void pause_works() {
        //given

        activePremiumUserSubscription.pause();

        //then
        assertThat(activePremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.PAUSED);
    }

    @Test
    public void unpause_works() {
        //given

        activePremiumUserSubscription.unpause();

        //then
        assertThat(activePremiumUserSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }
}