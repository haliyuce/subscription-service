package com.supercompany.subscriptionservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private int userId;
    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    private LocalDateTime pauseDate;
    @NonNull
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    @Version
    private int version;

    @Builder(toBuilder = true)
    private UserSubscription(
            final int id,
            final int userId,
            final Product product,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final LocalDateTime pauseDate,
            final SubscriptionStatus status) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pauseDate = pauseDate;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserSubscription that = (UserSubscription) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return 12;
    }
}
