package com.supercompany.subscriptionservice.model.subscription.premium;

import static com.supercompany.subscriptionservice.exception.InvalidProcessException.InvalidProcessCode;

import com.supercompany.subscriptionservice.exception.InvalidProcessException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class CancelledPremiumUserSubscription extends AbstractPremiumUserSubscription {

    @Builder
    protected CancelledPremiumUserSubscription(int userId, Product product, LocalDateTime startDate, LocalDateTime endDate) {
        super(userId, product, startDate, endDate, SubscriptionStatus.CANCELLED);
    }

    @Override
    public void cancel() {
        throw new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_CANCELLED);
    }

    @Override
    public void pause() {
        throw new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_PAUSED);
    }

    @Override
    public void unpause() {
        throw new InvalidProcessException(InvalidProcessCode.CANCELED_SUB_CANNOT_BE_UNPAUSED);
    }
}
