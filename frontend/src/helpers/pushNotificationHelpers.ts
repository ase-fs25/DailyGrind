import { mockVapidKeys } from '../mockData/mockVapidKeys';

export const requestNotificationPermission = async (): Promise<string> => {
  return new Promise(function (resolve, reject) {
    const permissionResult = Notification.requestPermission(function (result) {
      return resolve(result);
    });

    if (permissionResult) {
      permissionResult.then(resolve, reject);
    }
  }).then(function (permissionResult) {
    if (permissionResult !== 'granted') {
      throw new Error("We weren't granted permission.");
    }
    return permissionResult;
  });
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

      /*
            if (subscription) {
                await sendSubscriptionToBackend(subscription);
            }

            */
    }
    return subscription;
  } catch (error) {
    console.error('Error subscribing to push:', error);
  }
};

/*
 * TODO: Implement the Endpoint in the backend
export const sendSubscriptionToBackend = async (subscription: PushSubscription): Promise<boolean> => {
    try {
        const response = await fetch('API_ENDPOINT/subscribe', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(subscription),
        });

        if (!response.ok) {
            throw new Error('Failed to send subscription to server');
        }

        console.log('Successfully sent subscription to server');
        return true;
    } catch (error) {
        console.error('Error sending subscription to server:', error);
        return false;
    }
};

 */
