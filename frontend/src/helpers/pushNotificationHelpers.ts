import { getAuthToken } from './authHelper';
import { getApiUrl } from './apiHelper';

let initialized = false;

export const registerUserForSubscription = async (): Promise<any> => {
  if (initialized) return;
  initialized = true;

  console.log('Service Worker Registration');
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker
      .register((import.meta.env.PROD ? '/dailygrind' : '') + '/service-worker.js')
      .then(() => {
        if (Notification.permission !== 'denied') {
          return requestNotificationPermission();
        }
        return null;
      })
      .then((permission) => {
        console.log('Permission result: ', permission);
        if (permission === 'granted') {
          return subscribeUserToPush();
        }
        return null;
      })
      .then(() => {
      })
      .catch((error) => console.error('Service worker or notification error:', error));
  }
};

export const requestNotificationPermission = async (): Promise<string> => {
  try {
    const permissionResult = await Notification.requestPermission();
    if (permissionResult !== 'granted') {
      throw new Error('We weren\'t granted permission.');
    }
    return permissionResult;
  } catch (error) {
    console.error('Permission request error:', error);
    throw error;
  }
};

export const subscribeUserToPush = async () => {
  try {
    const registration = await navigator.serviceWorker.ready;
    console.log('Service Worker is ready');

    let subscription: PushSubscription | null = await registration.pushManager.getSubscription();

    if (subscription) {
      console.log('Received existing PushSubscription: ', JSON.stringify(subscription));
    } else {
      const subscribeOptions = {
        userVisibleOnly: true,
        applicationServerKey: import.meta.env.VITE_VAPID_PUBLIC_KEY,
      };
      subscription = await registration.pushManager.subscribe(subscribeOptions);
      console.log('Created new PushSubscription: ', JSON.stringify(subscription));
      if (subscription) {
        await saveSubscription(subscription);
      }
    }
    return subscription;
  } catch (error) {
    console.error('Error subscribing to push:', error);
  }
};

export const saveSubscription = async (subscription: PushSubscription): Promise<boolean> => {
  try {
    let authToken = await getAuthToken();

    const response = await fetch(getApiUrl('push-notifications/subscribe'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(subscription),
    });

    if (!response.ok) {
      throw new Error(`Failed to send subscription: ${response.status} ${response.statusText}`);
    }

    console.log('Successfully sent subscription to server');
    return true;
  } catch (error) {
    console.error('Error sending subscription to server:', error);
    return false;
  }
};
