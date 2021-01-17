package com.supercompany.subscriptionservice.model.subscription.premium;

import com.supercompany.subscriptionservice.exception.InvalidProcessException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.supercompany.subscriptionservice.exception.InvalidProcessException.InvalidProcessCode;

public class ExpiredPremiumUserSubscription extends AbstractPremiumUserSubscription {

    @Builder
    protected ExpiredPremiumUserSubscription(int userId, Product product, LocalDateTime startDate, LocalDateTime endDate) {
        super(userId, product, startDate, endDate, SubscriptionStatus.EXPIRED);
    }

    @Override
    public void cancel() {
        throw new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_CANCELLED);
    }

    @Override
    public void pause() {
        throw new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_PAUSED);
    }

    @Override
    public void unpause() {
        throw new InvalidProcessException(InvalidProcessCode.EXPIRED_SUB_CANNOT_BE_UNPAUSED);
    }
}
