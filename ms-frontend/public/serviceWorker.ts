/// <reference lib="webworker" />
declare const self: ServiceWorkerGlobalScope;

self.addEventListener('push', (event: PushEvent) => {
  const data = event.data ? event.data.text() : 'This is a push notification';

  const options: NotificationOptions = {
    body: data,
    icon: 'https://via.placeholder.com/150',
    badge: 'https://via.placeholder.com/150',
  };

  event.waitUntil(self.registration.showNotification('Push Notification', options));
});

export {};
