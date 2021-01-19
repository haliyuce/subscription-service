package com.supercompany.subscriptionservice.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubscriptionRequest {
    int userId;
    int productId;
}
