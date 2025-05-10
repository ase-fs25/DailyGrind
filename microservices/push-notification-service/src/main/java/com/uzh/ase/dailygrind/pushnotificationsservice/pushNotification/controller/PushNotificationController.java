package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller;


import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/push-notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @Operation(summary = "Subscribe to push notifications", description = "Registers a new browser subscription for push notifications for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Subscription created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PushSubscription.class)))
    @PostMapping("/subscribe")
    public ResponseEntity<PushSubscription> saveSubscription(@RequestBody SubscriptionDto pushSubscription, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pushNotificationService.saveSubscription(pushSubscription, principal.getName()));

    }

    @Operation(summary = "Send notification to all users", description = "Sends a push notification to all subscribed users")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully", content = @Content)
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody String message) {
        pushNotificationService.sendNotification(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Delete a push notification subscription", description = "Removes a user's push notification subscription by ID")
    @ApiResponse(responseCode = "204", description = "Subscription successfully deleted", content = @Content)
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String subscriptionId) {
        pushNotificationService.deleteSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a subscription by ID", description = "Retrieves details of a specific push notification subscription")
    @ApiResponse(responseCode = "200", description = "Subscription found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PushSubscription.class)))
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<PushSubscription> getSubscription(@PathVariable String subscriptionId) {
        return pushNotificationService.getSubscriptionById(subscriptionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
