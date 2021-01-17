package com.supercompany.subscriptionservice.model.subscription.premium;

import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.subscription.AbstractUserSubscription;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.subscription.SubscriptionType;

import java.time.LocalDateTime;

public abstract class AbstractPremiumUserSubscription extends AbstractUserSubscription {

    public AbstractPremiumUserSubscription(final int userId,
                                           final Product product,
                                           final LocalDateTime startDate,
                                           final LocalDateTime endDate,
                                           final SubscriptionStatus status) {
        super(userId, product, startDate, endDate, status, SubscriptionType.PREMIUM);
    }
}
