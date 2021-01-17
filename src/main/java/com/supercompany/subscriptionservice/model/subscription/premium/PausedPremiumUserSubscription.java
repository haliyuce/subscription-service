package com.supercompany.subscriptionservice.model.subscription.premium;

import com.supercompany.subscriptionservice.exception.InvalidProcessException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.supercompany.subscriptionservice.exception.InvalidProcessException.InvalidProcessCode;

public class PausedPremiumUserSubscription extends AbstractPremiumUserSubscription {

    @Builder
    private PausedPremiumUserSubscription(final int userId,
                                            final Product product,
                                            final LocalDateTime startDate,
                                            final LocalDateTime endDate) {
        super(userId, product, startDate, endDate, SubscriptionStatus.PAUSED);
    }

    @Override
    public void cancel() {
        setStatus(SubscriptionStatus.CANCELLED);
    }

    @Override
    public void pause() {
        setStatus(SubscriptionStatus.PAUSED);
    }

    @Override
    public void unpause() {
        setStatus(SubscriptionStatus.ACTIVE);
    }
}
