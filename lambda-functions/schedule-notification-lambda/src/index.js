const http = require('http');

exports.handler = async (event) => {
    try {
        console.log('Starting scheduled notification trigger');

        const options = {
            hostname: process.env.API_HOSTNAME,
            port: parseInt(process.env.API_PORT),
            path: '/push-notifications/send',
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            }
        };

        console.log(`Sending request to: ${options.hostname}:${options.port}${options.path}`);

        const message = "Hey there! It's 12pm – time to share your knowledge!";

        const response = await new Promise((resolve, reject) => {
            const req = http.request(options, (res) => {
                let data = '';

                res.on('data', (chunk) => {
                    data += chunk;
                });

                res.on('end', () => {
                    resolve({
                        statusCode: res.statusCode,
                        body: data
                    });
                });
            });

            req.on('error', (error) => {
                console.error('Request error:', error);
                reject(error);
            });

            req.write(message);
            req.end();
        });

        console.log('Push notification triggered with status:', response.statusCode);
        return {
            statusCode: 200,
            body: JSON.stringify({
                message: 'Daily notification successfully triggered',
                serviceResponse: response
            })
        };
    } catch (error) {
        console.error('Error triggering push notification:', error);
        return {
            statusCode: 500,
            body: JSON.stringify({
                message: 'Failed to trigger daily notification',
                error: error.message
            })
        };
    }
};