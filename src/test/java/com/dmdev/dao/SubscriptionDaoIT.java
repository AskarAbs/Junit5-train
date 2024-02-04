package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();


    @Test
    void findAll() {
        Subscription subscription = getSubscription();
        var subscription2 = getSubscription1();
        subscriptionDao.insert(subscription);
        subscriptionDao.insert(subscription2);

        var subscriptions = subscriptionDao.findAll();

        Assertions.assertThat(subscriptions).isEqualTo(List.of(subscription, subscription2));
    }

    @Test
    void findById() {
        Subscription subscription = getSubscription();
        subscriptionDao.insert(subscription);

        var subscription1 = subscriptionDao.findById(subscription.getId());


        assertAll(
                () -> Assertions.assertThat(subscription1).isEqualTo(Optional.of(subscription)),
                () -> Assertions.assertThat(subscriptionDao.findById(null)).isEqualTo(Optional.empty())
        );
    }

    @Test
    void delete() {
        Subscription subscription = getSubscription();
        subscriptionDao.insert(subscription);

        assertAll(
                () -> Assertions.assertThat(subscriptionDao.delete(subscription.getId())).isTrue(),
                () -> Assertions.assertThat(subscriptionDao.delete(null)).isFalse()
        );
    }

    @Test
    void update() {
        Subscription subscription = getSubscription();
        subscriptionDao.insert(subscription);

        var update = subscriptionDao.update(subscription);
        subscription.setId(6);

        assertAll(
                () -> Assertions.assertThat(subscription).isEqualTo(update),
                () -> assertThrows(Throwable.class, () -> subscriptionDao.update(null))
        );
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription();

        var result = subscriptionDao.insert(subscription);

        Assertions.assertThat(result).isEqualTo(subscription);
    }

    @Test
    void findByUserId() {
        Subscription subscription = getSubscription();
        subscriptionDao.insert(subscription);

        var result = subscriptionDao.findByUserId(subscription.getUserId());

        Assertions.assertThat(result).isEqualTo(List.of(subscription));
    }

    private static Subscription getSubscription() {
        return Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.ofEpochSecond(3))
                .status(Status.ACTIVE)
                .build();
    }

    private static Subscription getSubscription1() {
        return Subscription.builder()
                .userId(2)
                .name("askar1")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.ofEpochSecond(3))
                .status(Status.ACTIVE)
                .build();
    }
}