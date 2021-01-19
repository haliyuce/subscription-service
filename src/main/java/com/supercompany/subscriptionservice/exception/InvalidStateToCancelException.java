package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class InvalidStateToCancelException extends ResponseStatusException {
    public InvalidStateToCancelException(final int subsId) {
        super(HttpStatus.BAD_REQUEST, "Subscription with id " + subsId + " is not cancellable!");
    }
}
