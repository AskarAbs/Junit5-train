package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.ValidationException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;


@ExtendWith(
        MockitoExtension.class
)
class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;
    private static final Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    void upsert() {
        var subscription1 = Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.MAX)
                .status(Status.ACTIVE)
                .build();

        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.MAX)
                .build();

        var subscription = subscriptionService.upsert(subscriptionDto);

        Assertions.assertThat(subscription).isEqualTo(subscription1.setId(subscription.getId()));
    }

    @Test
    void cancel() {
        var subscription1 = getSubscription1();
        subscriptionDao.insert(subscription1);

        subscriptionService.cancel(subscription1.getId());

        var subscription = subscriptionDao.findById(subscription1.getId());

        Assertions.assertThat(subscription).isEqualTo(Optional.of(
                subscription1.setStatus(Status.CANCELED)));

    }

    @Test
    void expire() {
        var subscription1 = getSubscription1();
        subscriptionDao.insert(subscription1);

        subscriptionService.expire(subscription1.getId());

        var subscription = subscriptionDao.findById(subscription1.getId());

        subscription1.setStatus(Status.EXPIRED);
        subscription1.setExpirationDate(Instant.EPOCH);
        Assertions.assertThat(subscription).isEqualTo(Optional.of(subscription1));
    }

    private static Subscription getSubscription1() {
        return Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.EPOCH)
                .status(Status.ACTIVE)
                .build();
    }

}