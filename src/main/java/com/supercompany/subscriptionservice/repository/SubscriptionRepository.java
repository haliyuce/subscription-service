package com.supercompany.subscriptionservice.repository;

import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<UserSubscription, Integer> {
    Optional<UserSubscription> findByUserIdAndStatusIn(int userId, List<SubscriptionStatus> statuses);

    @Modifying(clearAutomatically = true)
    @Query(value = "update user_subscription set status = 'ACTIVE' where status = 'TRIAL'", nativeQuery = true)
    int updateExpiredTrialsToActive();
}
