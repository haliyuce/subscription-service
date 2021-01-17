package com.supercompany.subscriptionservice.model.subscription;

import com.supercompany.subscriptionservice.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractUserSubscription {

    private int id;
    private int userId;
    private Product product;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private SubscriptionType type;

    protected AbstractUserSubscription(
            final int userId,
            final Product product,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final SubscriptionStatus status,
            final SubscriptionType type) {
        this.userId = userId;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.type = type;
    }

    public abstract void cancel();

    public abstract void pause();

    public abstract void unpause();
}
