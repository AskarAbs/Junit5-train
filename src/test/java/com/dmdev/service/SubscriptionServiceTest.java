package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import net.bytebuddy.pool.TypePool;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class SubscriptionServiceTest {

    @Mock
    ValidationResult validationResult = new ValidationResult();
    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;

    private final Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void init(){
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                createSubscriptionMapper,
                createSubscriptionValidator,
                clock
        );
    }

    @Test
    void upsert() {
        var subscription = Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.ofEpochSecond(3))
                .status(Status.ACTIVE)
                .build();

        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.ofEpochSecond(3))
                .build();

        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        var subscription1 = subscriptionService.upsert(subscriptionDto);

        Assertions.assertThat(subscription1).isNotNull();
    }

    @Test
    void upsertFail() {

        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.EPOCH)
                .build();
        ValidationResult validationResult1 = new ValidationResult();
        validationResult1.add(Error.of(1, "sasa"));
        doReturn(validationResult1).when(createSubscriptionValidator).validate(subscriptionDto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(subscriptionDto));
    }

    @Test
    void cancel() {
        var subscription1 = getSubscription1();
        var subscription2 = Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.EPOCH)
                .status(Status.CANCELED)
                .build();

        doReturn(Optional.of(subscription1)).when(subscriptionDao).findById(subscription1.getId());

        subscriptionService.cancel(subscription1.getId());

        Assertions.assertThat(subscription1).isEqualTo(subscription2);
        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscription2.getId()));
    }

    @Test
    void expire() {
        var subscription1 = getSubscription1();
        var subscription2 = Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.EPOCH)
                .status(Status.EXPIRED)
                .build();

        doReturn(Optional.of(subscription1)).when(subscriptionDao).findById(subscription1.getId());

        subscriptionService.expire(subscription1.getId());

        Assertions.assertThat(subscription1).isEqualTo(subscription2);
        assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscription2.getId()));
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