package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller;


import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/push-notifications")
@RequiredArgsConstructor
public class pushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<PushSubscription> saveSubscription(@RequestBody SubscriptionDto pushSubscription, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pushNotificationService.saveSubscription(pushSubscription, principal.getName()));

    }

    /*
    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> deleteSubscription(@RequestBody PushSubscription pushSubscription) {
        pushNotificationService.deleteSubscription(pushSubscription);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
     */

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody String message) {
        pushNotificationService.sendNotification(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
