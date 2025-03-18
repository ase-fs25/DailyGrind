self.addEventListener('push', event => {
  const data = event.data ? event.data.text() : 'This is a push notification text';
  const options = {
    body: data,
    icon: 'https://via.placeholder.com/128',  // placeholder icon
    badge: 'https://via.placeholder.com/64',  // placeholder badge
  };

  event.waitUntil(
      self.registration.showNotification('Push Notification', options)
  );
});


