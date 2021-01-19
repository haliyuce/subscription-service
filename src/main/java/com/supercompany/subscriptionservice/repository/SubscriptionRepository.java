package com.supercompany.subscriptionservice.repository;

import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<UserSubscription, Integer> {
    Optional<UserSubscription> findByUserIdAndStatusIn(int userId, List<SubscriptionStatus> statuses);
}
