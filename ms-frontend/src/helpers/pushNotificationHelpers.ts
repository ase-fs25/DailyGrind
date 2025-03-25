import { mockVapidKeys }  from "../mockData/mockVapidKeys";

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
        }
        return subscription;
    } catch (error) {
        console.error('Error subscribing to push:', error);
    }
};