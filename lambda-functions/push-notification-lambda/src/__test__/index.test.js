const webpush = require('web-push');
const { handler } = require('../index');

// Given
jest.mock('web-push', () => ({
  sendNotification: jest.fn().mockResolvedValue({}),
  setVapidDetails: jest.fn()
}));

// When
describe('push-notification-lambda', () => {
  test('should send notification using web-push', async () => {
    // Adjust event structure to match what your handler expects
    const event = {
      body: JSON.stringify({
        subscription: {
          endpoint: 'https://example.com/push/123',
          keys: {
            p256dh: 'test-p256dh-key',
            auth: 'test-auth-key'
          }
        },
        message: 'This is a test message' // Add direct message property
      })
    };

    await handler(event);

    // Then
    expect(webpush.sendNotification).toHaveBeenCalled();
  });
});
