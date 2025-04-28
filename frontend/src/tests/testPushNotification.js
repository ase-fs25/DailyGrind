//const webpush = require('web-push');
import webpush from 'web-push';

const mockVapidKeys = {
  publicKey: 'BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8',
  privateKey: '1tyL-Kw64dsVRGE7uUew77koormjIC67lu-jbTLWW0k',
};

webpush.setVapidDetails('mailto:tim.vorburger@uzh.ch', mockVapidKeys.publicKey, mockVapidKeys.privateKey);

const pushSubscription = {
  endpoint:
    'https://fcm.googleapis.com/fcm/send/cujueS3bVpo:APA91bHCxJiZmLPD0GDMAKfbaM1QxcShFCzT7iw3xCuivzIGaH6MzVNixLRYai0u9YPrCgWw1b1cc_Xy6fi9FwVDAXtPOVwsBoDOkUFn1abfjAkpfUDkCxiaLbp5WusqBV7xcEWbalxk',
  expirationTime: null,
  keys: {
    p256dh: 'BA17Oc-yKU53lTcznORcDeW3AXkEXABGqw-ZNavimyp0vJPR5SWWTAjXEhLNuR-cemfDG5rvbVGuDX9OCrLBApk',
    auth: 'AO6Gk1fw-xcnniGbwofRUA',
  },
};

webpush.sendNotification(pushSubscription, 'This is the message body from node!');
