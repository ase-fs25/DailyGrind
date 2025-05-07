/* global navigator */
import { mockVapidKeys } from '../mockData/mockVapidKeys';
import { fetchAuthSession } from 'aws-amplify/auth';

export const requestNotificationPermission = async (): Promise<string> => {
  try {
    const permissionResult = await Notification.requestPermission();
    if (permissionResult !== 'granted') {
      throw new Error("We weren't granted permission.");
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
        applicationServerKey: mockVapidKeys.publicKey,
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
    let authToken = null;
    try {
      const session = await fetchAuthSession();
      authToken = session.tokens?.accessToken.toString();
      console.log('auth token: ', authToken);
    } catch (e) {
      console.error('Failed to fetch auth token: ', e);
      return false;
    }

    if (!authToken) {
      console.error('User is not authenticated');
      return false;
    }

    const response = await fetch('http://localhost:8082/push-notifications/subscribe', {
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
