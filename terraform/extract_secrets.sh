#!/bin/sh
set -e

# Extract sensitive Terraform output as JSON
terraform output -json > tf_outputs.json

# Parse with jq
APP_CLIENT_ID=$(jq -r '.app_client_id.value' tf_outputs.json)
#CLIENT_SECRET=$(jq -r '.client_secret.value' tf_outputs.json)
USER_POOL_ID=$(jq -r '.user_pool_id.value' tf_outputs.json)
API_GATEWAY_URL=$(jq -r '.api_gateway_url.value' tf_outputs.json)

# Write to .env file (local use)
cat <<EOF > /frontend/.env
VITE_USER_POOL_CLIENT_ID=$APP_CLIENT_ID
VITE_USER_POOL_ID=$USER_POOL_ID
VITE_USER_POOL_ENDPOINT=http://localhost:4566/aws/cognito-idp
VITE_DOMAIN=http://localhost:4566/aws/cognito-idp/login
VITE_API_URL=$API_GATEWAY_URL
VITE_VAPID_PUBLIC_KEY=BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8
EOF

cat <<EOF > /microservices/.env
AWS_COGNITO_USER_POOL_ID=$USER_POOL_ID
AWS_HOSTNAME=localhost
EOF

echo ".env files written successfully."

# Push to AWS Secrets Manager
SECRET_NAME="dailygrind/cognito"
SECRET_STRING="{\"AWS_COGNITO_USER_POOL_ID\":\"$USER_POOL_ID\"}"

# Try to create the secret (will fail if it exists)
aws secretsmanager create-secret \
  --name "$SECRET_NAME" \
  --secret-string "$SECRET_STRING" \
  --endpoint-url=http://localstack:4566 2>/dev/null || {

  # If creation fails (e.g. already exists), just overwrite it
  aws secretsmanager put-secret-value \
    --secret-id "$SECRET_NAME" \
    --secret-string "$SECRET_STRING" \
    --endpoint-url=http://localstack:4566
}

echo "Secret '$SECRET_NAME' upserted successfully."

