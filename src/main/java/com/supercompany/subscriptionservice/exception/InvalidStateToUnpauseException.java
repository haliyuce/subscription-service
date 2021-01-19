package com.supercompany.subscriptionservice.exception;

import com.supercompany.subscriptionservice.model.SubscriptionStatus;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class InvalidStateToUnpauseException extends ResponseStatusException {
    public InvalidStateToUnpauseException(
            final int subscriptionId,
            @NonNull final SubscriptionStatus status) {
        super(
                HttpStatus.BAD_REQUEST,
                "Subscription with id " + subscriptionId + " is not in PAUSED but " + status + " state");
    }
}
