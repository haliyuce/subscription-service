package com.supercompany.subscriptionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class TrialToActiveSchedulerService {

    private final SubscriptionService subscriptionService;

    /**
     * If the trial periods are not cancelled and trial period has already ended,
     * than this job makes them active.
     */
    @Scheduled(cron = "0 0 23 * * ?")
    @Transactional
    public void convertTrialsToActive() {
        subscriptionService.updateExpiredTrialsToActive();
    }
}
