package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class UserAlreadySubscribedException extends ResponseStatusException {
    public UserAlreadySubscribedException(int userId) {
        super(
                HttpStatus.BAD_REQUEST,
                "User with id " + userId + " has already subscribed to the a product");
    }
}
