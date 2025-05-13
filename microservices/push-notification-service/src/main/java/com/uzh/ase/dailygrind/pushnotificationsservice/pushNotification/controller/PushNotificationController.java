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

/**
 * REST controller that handles the operations related to push notifications subscriptions.
 * <p>
 * This controller provides endpoints to manage push notification subscriptions, send notifications,
 * and delete or retrieve subscription details for authenticated users.
 */
@RestController
@RequestMapping("/push-notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    /**
     * Endpoint to subscribe to push notifications.
     * <p>
     * This endpoint registers a new browser subscription for push notifications for the authenticated user.
     * The subscription details are received in the request body as a {@link SubscriptionDto}.
     *
     * @param pushSubscription the subscription details
     * @param principal the authenticated user making the request
     * @return a response entity containing the saved subscription
     */
    @Operation(summary = "Subscribe to push notifications", description = "Registers a new browser subscription for push notifications for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Subscription created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PushSubscription.class)))
    @PostMapping("/subscribe")
    public ResponseEntity<PushSubscription> saveSubscription(@RequestBody SubscriptionDto pushSubscription, Principal principal) {
        PushSubscription savedSubscription = pushNotificationService.saveSubscription(pushSubscription, principal.getName());
        if (savedSubscription == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSubscription);
    }

    /**
     * Endpoint to send a notification to all subscribed users.
     * <p>
     * This endpoint sends a push notification with the provided message to all users who have subscribed.
     *
     * @param message the message to be sent in the notification
     * @return a response entity indicating the success of the operation
     */
    @Operation(summary = "Send notification to all users", description = "Sends a push notification to all subscribed users")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully", content = @Content)
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody String message) {
        pushNotificationService.sendNotification(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Endpoint to delete a push notification subscription by ID.
     * <p>
     * This endpoint removes a user's push notification subscription based on the provided subscription ID.
     *
     * @param subscriptionId the ID of the subscription to be deleted
     * @return a response entity indicating the success of the deletion
     */
    @Operation(summary = "Delete a push notification subscription", description = "Removes a user's push notification subscription by ID")
    @ApiResponse(responseCode = "204", description = "Subscription successfully deleted", content = @Content)
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String subscriptionId) {
        pushNotificationService.deleteSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to retrieve a subscription by its ID.
     * <p>
     * This endpoint returns the details of a specific push notification subscription.
     *
     * @param subscriptionId the ID of the subscription to be retrieved
     * @return a response entity containing the subscription details
     */
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
