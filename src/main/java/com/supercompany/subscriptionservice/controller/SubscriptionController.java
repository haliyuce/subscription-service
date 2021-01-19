package com.supercompany.subscriptionservice.controller;

import com.supercompany.subscriptionservice.model.UserSubscription;
import com.supercompany.subscriptionservice.model.dto.SubscriptionRequest;
import com.supercompany.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public Optional<UserSubscription> getActiveUserSubscription(int userId) {

        return subscriptionService.getActiveUserSubscription(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscription subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
        return subscriptionService.subscribe(
                subscriptionRequest.getUserId(),
                subscriptionRequest.getProductId());
    }

    @PutMapping("/{subscriptionId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable final int subscriptionId) {
        subscriptionService.cancel(subscriptionId);
    }

    @PutMapping("/{subscriptionId}/pause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pause(@PathVariable final int subscriptionId) {
        subscriptionService.pause(subscriptionId);
    }

    @PutMapping("/{subscriptionId}/unpause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpause(@PathVariable final int subscriptionId) {
        subscriptionService.unpause(subscriptionId);
    }
}
