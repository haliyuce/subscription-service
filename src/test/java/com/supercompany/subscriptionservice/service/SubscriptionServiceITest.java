package com.supercompany.subscriptionservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.supercompany.subscriptionservice.exception.*;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import com.supercompany.subscriptionservice.model.UserSubscription;
import com.supercompany.subscriptionservice.repository.SubscriptionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@DataJpaTest
@Import({
        SubscriptionService.class,
        ProductService.class,
        TestConfig.class,
})
public class SubscriptionServiceITest {

    @Autowired
    private Clock fixedClock;

    @Autowired
    private ProductService productService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    public void subscribe_works() {
        //given
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        product = productService.save(product);
        final int userId = 1;

        //when
        final var actualSubscription = subscriptionService.subscribe(userId, product.getId());

        //then
        final var persistedSubscription = subscriptionRepository.findById(actualSubscription.getId());
        assertThat(persistedSubscription).isNotEmpty();
        assertThat(persistedSubscription.get()).isEqualTo(actualSubscription);
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"ACTIVE", "PAUSED", "TRIAL"})
    public void subscribe_fail_when_user_already_subscribed_for_a_product(final SubscriptionStatus status) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(status)
                .build();
        subscriptionRepository.save(subscription);

        //when
        final var actualException = assertThrows(
                UserAlreadySubscribedException.class,
                () -> subscriptionService.subscribe(userId, persistedProduct.getId()));

        //then
        assertThat(actualException).isEqualTo(new UserAlreadySubscribedException(userId));
    }

    @Test
    public void subscribe_fails_when_product_is_missing() {
        //given
        final int missingProductId = 123;
        final int userId = 1;

        //when
        final var actualException = assertThrows(
                ProductNotFoundException.class,
                () -> subscriptionService.subscribe(userId, missingProductId));

        //then
        assertThat(actualException).isEqualTo(new ProductNotFoundException(missingProductId));
    }

    @Test
    public void pause_works_when_subscription_is_in_active_state() {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        product = productService.save(product);
        var activeSubscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(SubscriptionStatus.ACTIVE)
                .build();
        final var persistedActiveSubscription = subscriptionRepository.save(activeSubscription);

        //when
        final var actualPausedSubscription = subscriptionService.pause(persistedActiveSubscription.getId());

        //then
        final var persistedSubscription = subscriptionRepository.findById(persistedActiveSubscription.getId());
        assertThat(persistedSubscription).isNotEmpty();
        assertThat(persistedSubscription.get()).isEqualTo(actualPausedSubscription);
        assertThat(persistedSubscription.get().getStatus()).isEqualTo(SubscriptionStatus.PAUSED);
    }

    @Test
    public void pause_fails_when_subscription_is_missing() {
        //given
        final int subsId = 1;

        //when
        final var actualException = assertThrows(
                SubscriptionNotFoundException.class,
                () -> subscriptionService.pause(subsId));

        //then
        assertThat(actualException).isEqualTo(new SubscriptionNotFoundException(subsId));
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"PAUSED", "EXPIRED", "CANCELLED", "TRIAL"})
    public void pause_fails_when_subscription_is_not_in_active_state(final SubscriptionStatus status) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        product = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(status)
                .build();
        final var persistedSubscription = subscriptionRepository.save(subscription);

        //when
        final var actualException = assertThrows(
                InvalidStateToPauseException.class,
                () -> subscriptionService.pause(persistedSubscription.getId()));

        //then
        assertThat(actualException).isEqualTo(new InvalidStateToPauseException(persistedSubscription.getId(), status));
    }

    @Test
    public void unpause_works_when_subscription_is_in_active_state() {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        product = productService.save(product);
        var pausedSubscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock).minusDays(2))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(SubscriptionStatus.PAUSED)
                .pauseDate(LocalDateTime.now(fixedClock).minusDays(1))
                .build();
        final var persistedActiveSubscription = subscriptionRepository.save(pausedSubscription);
        final var expectedEndDate = persistedActiveSubscription
                .getEndDate()
                .plus(Duration.between(persistedActiveSubscription.getPauseDate(), LocalDateTime.now(fixedClock)));

        //when
        final var actualPausedSubscription = subscriptionService.unpause(persistedActiveSubscription.getId());

        //then
        final var persistedSubscription = subscriptionRepository.findById(persistedActiveSubscription.getId());
        assertThat(persistedSubscription).isNotEmpty();
        assertThat(persistedSubscription.get()).isEqualTo(actualPausedSubscription);
        assertThat(persistedSubscription.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(persistedSubscription.get().getEndDate()).isEqualTo(expectedEndDate);
    }

    @Test
    public void unpause_fails_when_subscription_is_missing() {
        //given
        final int subsId = 1;

        //when
        final var actualException = assertThrows(
                SubscriptionNotFoundException.class,
                () -> subscriptionService.unpause(subsId));

        //then
        assertThat(actualException).isEqualTo(new SubscriptionNotFoundException(subsId));
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"ACTIVE", "EXPIRED", "CANCELLED", "TRIAL"})
    public void unpause_fails_when_subscription_is_not_in_active_state(final SubscriptionStatus status) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        product = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(status)
                .build();
        final var persistedSubscription = subscriptionRepository.save(subscription);

        //when
        final var actualException = assertThrows(
                InvalidStateToUnpauseException.class,
                () -> subscriptionService.unpause(persistedSubscription.getId()));

        //then
        assertThat(actualException).isEqualTo(new InvalidStateToUnpauseException(persistedSubscription.getId(), status));
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"ACTIVE", "PAUSED", "TRIAL"})
    public void getActiveUserSubscription_works_when_an_active_user_subscription_exists(final SubscriptionStatus subscriptionStatus) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(persistedProduct)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(subscriptionStatus)
                .build();
        subscriptionRepository.save(subscription);

        //when
        final var actualSubs = subscriptionService.getActiveUserSubscription(userId);

        //then
        assertThat(actualSubs).isNotEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"EXPIRED", "CANCELLED"})
    public void getActiveUserSubscription_returns_empty_when_an_active_user_subscription_does_not_exist(final SubscriptionStatus subscriptionStatus) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(persistedProduct)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(subscriptionStatus)
                .build();
        subscriptionRepository.save(subscription);

        //when
        final var actualSubs = subscriptionService
                .getActiveUserSubscription(userId);

        //then
        assertThat(actualSubs).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"ACTIVE", "PAUSED", "TRIAL"})
    public void cancel_works_when_an_active_user_subscription_exists(final SubscriptionStatus subscriptionStatus) {
        //given
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(persistedProduct)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(1)
                .status(subscriptionStatus)
                .build();
        subscription = subscriptionRepository.save(subscription);

        //when
        subscriptionService.cancel(subscription.getId());

        //then
        final var cancelled = subscriptionRepository
                .findById(subscription.getId());
        assertThat(cancelled).isNotEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SubscriptionStatus.class, names = {"CANCELLED", "EXPIRED"})
    public void cancel_fails_when_an_active_user_subscription_does_not_exist(final SubscriptionStatus subscriptionStatus) {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(persistedProduct)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(subscriptionStatus)
                .build();
        final var persistedSubscription = subscriptionRepository.save(subscription);

        //when
        final var actualException = assertThrows(
                InvalidStateToCancelException.class,
                () -> subscriptionService.cancel(persistedSubscription.getId()));

        //then
        assertThat(actualException).isEqualTo(new InvalidStateToCancelException(userId));
    }

    @SneakyThrows
    @Test
    public void updateExpiredTrialsToActive_works() {
        //given
        final int userId = 1;
        var product = Product.builder()
                .description("some desc")
                .name("cool product")
                .period(Period.ofMonths(3))
                .price(BigDecimal.TEN)
                .taxRate(BigDecimal.ONE)
                .build();
        final var persistedProduct = productService.save(product);
        var subscription = UserSubscription.builder()
                .product(product)
                .startDate(LocalDateTime.now(fixedClock))
                .endDate(LocalDateTime.now(fixedClock).plusMonths(1))
                .userId(userId)
                .status(SubscriptionStatus.TRIAL)
                .build();
        subscription = subscriptionRepository.save(subscription);

        //when
        subscriptionService.updateExpiredTrialsToActive();

        //then
        final var expected = subscriptionRepository.findById(subscription.getId());
        assertThat(expected).isNotEmpty();
        assertThat(expected.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

    }
}