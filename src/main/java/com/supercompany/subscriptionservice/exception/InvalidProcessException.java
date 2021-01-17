package com.supercompany.subscriptionservice.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class InvalidProcessException extends RuntimeException {

    private final InvalidProcessCode invalidProcessCode;

    public InvalidProcessException(final InvalidProcessCode invalidProcessCode) {
        this.invalidProcessCode = invalidProcessCode;
    }

    @Override
    public String getMessage() {
        return invalidProcessCode.getMessage();
    }

    @Getter
    public enum InvalidProcessCode {
        CANCELED_SUB_CANNOT_BE_CANCELLED("A cancelled user subscription cannot be cancelled again!"),
        CANCELED_SUB_CANNOT_BE_PAUSED("A cancelled user subscription cannot be paused!"),
        CANCELED_SUB_CANNOT_BE_UNPAUSED("A cancelled user subscription cannot be unpause!"),
        EXPIRED_SUB_CANNOT_BE_CANCELLED("An expired user subscription cannot be cancelled!"),
        EXPIRED_SUB_CANNOT_BE_PAUSED("An expired user subscription cannot be paused!"),
        EXPIRED_SUB_CANNOT_BE_UNPAUSED("An expired user subscription cannot be unpause!")
        ;

        String message;

        InvalidProcessCode(String message) {
            this.message = message;
        }
    }
}
