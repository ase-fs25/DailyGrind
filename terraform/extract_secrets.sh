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
EOF

echo ".env file written successfully."

# Optional: Push to AWS Secrets Manager (LocalStack simulated)
aws secretsmanager create-secret \
  --name dailygrind/cognito \
  --secret-string "{\"COGNITO_APP_CLIENT_ID\":\"$APP_CLIENT_ID\",\"COGNITO_CLIENT_SECRET\":\"$CLIENT_SECRET\",\"COGNITO_USER_POOL_ID\":\"$USER_POOL_ID\"}" \
  --endpoint-url=http://localstack:4566 || echo "Secret may already exist."
