const webpush = require('web-push')

exports.handler = async (event, context) => {
    try {
        const publicVapidKey = process.env.PUBLIC_VAPID_KEY;
        const privateVapidKey = process.env.PRIVATE_VAPID_KEY;
        const vapidSubject = process.env.VAPID_SUBJECT;
        console.log("index.js reached");

        webpush.setVapidDetails(vapidSubject, publicVapidKey, privateVapidKey);

        console.log('Event received:', JSON.stringify(event));

        let payload;
        if (event.body) {
            try {
                payload = typeof event.body === 'string' ? JSON.parse(event.body) : event.body;
            } catch (e) {
                console.error('Error parsing body:', e);
                return {
                    statusCode: 400,
                    body: JSON.stringify({ message: 'Invalid body format' })
                };
            }
        } else {
            payload = event;
        }

        const { subscription, message } = payload;

        if (!subscription || !message) {
            console.error('Missing subscription or message in payload:', JSON.stringify(payload));
            return {
                statusCode: 400,
                body: JSON.stringify({ message: 'Missing subscription or message' })
            };
        }

        console.log('Sending notification to subscription:', JSON.stringify(subscription));

        const result = await webpush.sendNotification(subscription, JSON.stringify(message));
        console.log('Notification sent successfully:', result);

        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Notification sent successfully' })
        };

    } catch (error) {
        console.error('Error sending notification:', error);

        return {
            statusCode: 500,
            body: JSON.stringify({
                message: 'Failed to send notification',
                error: error.message,
                stack: error.stack
            })
        };
    }
};
