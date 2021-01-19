package com.supercompany.subscriptionservice.controller;

import com.supercompany.subscriptionservice.model.UserSubscription;
import com.supercompany.subscriptionservice.model.dto.SubscriptionRequest;
import com.supercompany.subscriptionservice.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Get the active subscription of the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You get the active subscription of the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSubscription.class))})})
    @GetMapping
    public Optional<UserSubscription> getActiveUserSubscription(int userId) {

        return subscriptionService.getActiveUserSubscription(userId);
    }

    @Operation(summary = "Subscribes a user to a product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Process successfully finished",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSubscription.class))}),
            @ApiResponse(responseCode = "400", description = "User already subscribed to a product.",
                    content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404", description = "Product cannot be found.",
                    content = {@Content(mediaType = "application/json")})
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscription subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
        return subscriptionService.subscribe(
                subscriptionRequest.getUserId(),
                subscriptionRequest.getProductId());
    }

    @Operation(summary = "Cancels a user's active subscription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancellation successfully finished",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSubscription.class))}),
            @ApiResponse(responseCode = "400", description = "Subscription is not valid to cancel.",
                    content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404", description = "Subscription cannot be found.",
                    content = {@Content(mediaType = "application/json")})
    })
    @PutMapping("/{subscriptionId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable final int subscriptionId) {
        subscriptionService.cancel(subscriptionId);
    }

    @Operation(summary = "Pauses a user's active subscription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pause process successfully finished",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSubscription.class))}),
            @ApiResponse(responseCode = "400", description = "Subscription is not valid to pause.",
                    content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404", description = "Subscription cannot be found.",
                    content = {@Content(mediaType = "application/json")})
    })
    @PutMapping("/{subscriptionId}/pause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pause(@PathVariable final int subscriptionId) {
        subscriptionService.pause(subscriptionId);
    }

    @Operation(summary = "Unpauses a user's paused subscription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unpause process successfully finished",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSubscription.class))}),
            @ApiResponse(responseCode = "400", description = "Subscription is not valid to unpause.",
                    content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404", description = "Subscription cannot be found.",
                    content = {@Content(mediaType = "application/json")})
    })
    @PutMapping("/{subscriptionId}/unpause")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpause(@PathVariable final int subscriptionId) {
        subscriptionService.unpause(subscriptionId);
    }
}
