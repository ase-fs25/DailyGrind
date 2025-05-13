export const isProd = false; // import.meta.env.PROD;

// Only used in production
export const prodGatewayUrl =
  import.meta.env.VITE_API_URL.replace('localstack', 'localhost').slice(0, -1) + '$default/';

// Dev ports per microservice
const devServiceMap: Record<string, string> = {
  users: 'http://localhost:8082/',
  posts: 'http://localhost:8080/',
  'push-notifications': 'http://localhost:8081/',
};

// Returns the full API URL based on current mode and path
export function getApiUrl(path: string): string {
  if (isProd) {
    return `${prodGatewayUrl}${path}`;
  }

  // Dev mode: Match based on prefix
  const entry = Object.entries(devServiceMap).find(([prefix]) => path.startsWith(prefix));

  if (!entry) {
    throw new Error(`No dev mapping found for API path: "${path}"`);
  }

  const [prefix, baseUrl] = entry;
  const subPath = path.slice(prefix.length); // remove prefix from path
  return `${baseUrl}${prefix}${subPath}`;
}
