package com.uzh.ase.dailygrind.mspushnotifications.pushNotification.controller;


import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push-notifications")
@RequiredArgsConstructor
public class pushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<PushSubscription> saveSubscription(@RequestBody PushSubscription pushSubscription) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pushNotificationService.saveSubscription(pushSubscription));
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
