package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper subscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.ofEpochSecond(3))
                .build();

        var actualResult = subscriptionMapper.map(subscriptionDto);

        var subscription = Subscription.builder()
                .userId(1)
                .name("askar")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.ofEpochSecond(3))
                .status(Status.ACTIVE)
                .build();

        Assertions.assertThat(subscription).isEqualTo(actualResult);
    }
}