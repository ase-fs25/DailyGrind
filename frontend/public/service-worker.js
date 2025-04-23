self.addEventListener('push', (event) => {
  console.log('Push event received');

  // Try to parse the data as JSON
  let notificationData;
  try {
    notificationData = event.data.json();
    console.log('Received JSON payload:', notificationData);
  } catch (e) {
    console.error('Failed to parse JSON:', e);
    // Fallback to text if JSON parsing fails
    notificationData = {
      title: 'Push Notification',
      body: event.data ? event.data.text() : 'Default notification message',
    };
  }

  const title = notificationData.title || 'DailyGrind Reminder';

  const options = {
    body: notificationData.body || 'Hey there! It\'s 12pm – time to share your thoughts!',
    icon: 'https://via.placeholder.com/128',
    badge: 'https://via.placeholder.com/64',
    timestamp: notificationData.timestamp ? parseInt(notificationData.timestamp) : Date.now(),
  };

  console.log('Showing notification with title:', title);
  console.log('Notification options:', options);

  event.waitUntil(self.registration.showNotification(title, options));
});
