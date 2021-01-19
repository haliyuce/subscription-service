package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class UserHasNoActiveSubscriptionException extends ResponseStatusException {
    public UserHasNoActiveSubscriptionException(final int userId) {
        super(HttpStatus.BAD_REQUEST, "User has no active subscriptions!");
    }
}
