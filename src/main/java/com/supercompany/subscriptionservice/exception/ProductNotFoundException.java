package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@EqualsAndHashCode
public class ProductNotFoundException extends ResponseStatusException {
    public ProductNotFoundException(final int productId) {
        super(HttpStatus.NOT_FOUND, "No product exists with the id:" + productId);
    }
}
