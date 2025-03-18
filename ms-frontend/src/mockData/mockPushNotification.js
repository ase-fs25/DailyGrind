const webpush = require('web-push');

const mockVapidKeys = {
    publicKey:
        'BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8',
    privateKey: '1tyL-Kw64dsVRGE7uUew77koormjIC67lu-jbTLWW0k'
}

webpush.setVapidDetails(
    'mailto:tim.vorburger@uzh.ch',
    mockVapidKeys.publicKey,
    mockVapidKeys.privateKey
);

const pushSubscription = {
    "endpoint":"https://fcm.googleapis.com/fcm/send/cAa348WkmDw:APA91bFiNmEKuph02vfnLUZ0HV_G8yIzaF39GA6Jy79NmRUiU7SPCiyqu6_w_7Go83_M-ATzXYrZ_kUmjGlaV0FlSrh4lsyPFX3Bsxl9DNxKqlSN76jTmcMK66Bdr5DYdn5EKxjwUDoz",
    "expirationTime":null,
    "keys":{
        "p256dh":"BF1sFesAfi_i9_KoVEsDr1FPL9diGi_ATCCOSbohZn4kdj3Ha8EaMcm0waEEfHbvavKSvIjtmzqJiLDUvjVRDaA"
        ,"auth":"rI0sqllvfK0IoVNtjb4hCA"
    }
};

webpush.sendNotification(pushSubscription, 'This is the message body!');
