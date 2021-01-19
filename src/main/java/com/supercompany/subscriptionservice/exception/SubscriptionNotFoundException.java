package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class SubscriptionNotFoundException extends ResponseStatusException {
    public SubscriptionNotFoundException(int subscriptionId) {
        super(HttpStatus.BAD_REQUEST, "Missing subscription with id:" + subscriptionId);
    }
}
