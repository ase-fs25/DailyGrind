const { handler } = require('../index');
const http = require('http');

// Given
jest.mock('http', () => {
  const mockRequest = {
    write: jest.fn(),
    end: jest.fn(),
    on: jest.fn()
  };

  return {
    request: jest.fn((options, callback) => {
      const mockResponse = {
        statusCode: 200,
        on: jest.fn((event, handler) => {
          if (event === 'end') {
            setTimeout(() => handler(), 0);
          }
          return mockResponse;
        })
      };

      setTimeout(() => callback(mockResponse), 0);
      return mockRequest;
    })
  };
});

const originalEnv = process.env;

describe('schedule-notification-lambda', () => {
  // Given
  beforeEach(() => {
    process.env = {
      ...originalEnv,
      API_HOSTNAME: 'localhost',
      API_PORT: '8080'
    };
  });

  afterEach(() => {
    process.env = originalEnv;
    jest.clearAllMocks();
  });

  test('should successfully trigger a notification', async () => {
    // Given
    const event = {};

    // When
    const result = await handler(event);

    // Then
    expect(http.request).toHaveBeenCalledWith(
      expect.objectContaining({
        hostname: 'localhost',
        port: 8080,
        path: '/push-notifications/send',
        method: 'POST'
      }),
      expect.any(Function)
    );

    expect(result.statusCode).toBe(200);
    expect(JSON.parse(result.body)).toHaveProperty('message', 'Daily notification successfully triggered');
  });
});
