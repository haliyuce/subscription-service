package com.supercompany.subscriptionservice.service;

import com.supercompany.subscriptionservice.exception.*;
import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
import com.supercompany.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final Clock clock;
    private final ProductService productService;
    private final SubscriptionRepository subscriptionRepository;

    public UserSubscription subscribe(final int userId, final int productId) {
        final var activeProfileStatuses = List.of(
                SubscriptionStatus.ACTIVE,
                SubscriptionStatus.PAUSED,
                SubscriptionStatus.TRIAL);
        getUserSubscriptionByUserIdAndStatus(userId, activeProfileStatuses)
                .ifPresent(userSubscription -> {
                    throw new UserAlreadySubscribedException(userId);
                });
        final var product = productService.findById(productId);
        final var trialSubscription = UserSubscription.builder()
                .product(product)
                .userId(userId)
                .build();
        return subscriptionRepository.save(trialSubscription);
    }

    @Transactional
    public UserSubscription pause(final int subscriptionId) {
        final var subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        final var pausedSubscription = Optional.of(subscription)
                .filter(abstractUserSubscription -> SubscriptionStatus.ACTIVE.equals(abstractUserSubscription.getStatus()))
                .map(userSubscription -> userSubscription
                        .toBuilder()
                        .status(SubscriptionStatus.PAUSED)
                        .pauseDate(LocalDateTime.now(clock))
                        .build())
                .orElseThrow(() -> new InvalidStateToPauseException(subscriptionId, subscription.getStatus()));
        return subscriptionRepository.save(pausedSubscription);
    }

    @Transactional
    public UserSubscription unpause(final int subscriptionId) {
        final var subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        final var pausedSubscription = Optional.of(subscription)
                .filter(abstractUserSubscription -> SubscriptionStatus.PAUSED.equals(abstractUserSubscription.getStatus()))
                //I could put below map into a method as {@code UserSubscription.cancel()} , but chose to put all logic in service
                .map(userSubscription -> userSubscription
                        .toBuilder()
                        .status(SubscriptionStatus.ACTIVE)
                        .pauseDate(null)
                        .endDate(userSubscription.getEndDate()
                                .plus(Duration.between(userSubscription.getPauseDate(), LocalDateTime.now(clock))))
                        .build())
                .orElseThrow(() -> new InvalidStateToUnpauseException(subscriptionId, subscription.getStatus()));
        return subscriptionRepository.save(pausedSubscription);
    }

    private Optional<UserSubscription> getUserSubscriptionByUserIdAndStatus(final int userId, final List<SubscriptionStatus> statuses) {
        return subscriptionRepository.findByUserIdAndStatusIn(
                userId,
                statuses);
    }

    @Transactional
    public void cancel(final int userId) {
        final var cancellableStatuses = List.of(
                SubscriptionStatus.ACTIVE,
                SubscriptionStatus.PAUSED,
                SubscriptionStatus.TRIAL);
        final var userSubscription = getUserSubscriptionByUserIdAndStatus(userId, cancellableStatuses)
                .orElseThrow(() -> new UserHasNoActiveSubscriptionException(userId));
        final var cancelledSubscription = userSubscription.toBuilder()
                .status(SubscriptionStatus.CANCELLED)
                .build();
        subscriptionRepository.save(cancelledSubscription);
    }

    public Optional<UserSubscription> getActiveUserSubscription(final int userId) {
        return getUserSubscriptionByUserIdAndStatus(userId,
                List.of(SubscriptionStatus.ACTIVE,
                        SubscriptionStatus.PAUSED,
                        SubscriptionStatus.TRIAL));
    }
}
