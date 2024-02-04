package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateSubscriptionValidatorTest {

    CreateSubscriptionValidator subscriptionValidator = CreateSubscriptionValidator.getInstance();

    @Test
    void validateFalse() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name(any())
                .provider(any())
                .expirationDate(null)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(4);
    }

    @Test
    void validateTrue() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.MAX)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(0);
    }

    @Test
    void validateFail1() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(Instant.MAX)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(1);
    }

    @Test
    void validateFail2() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name(any())
                .provider("GOOGLE")
                .expirationDate(Instant.MAX)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(1);
    }

    @Test
    void validateFail3() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider(any())
                .expirationDate(Instant.MAX)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(1);
    }

    @Test
    void validateFail4() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("askar")
                .provider("GOOGLE")
                .expirationDate(null)
                .build();

        var validationResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(validationResult.getErrors()).hasSize(1);
    }
}