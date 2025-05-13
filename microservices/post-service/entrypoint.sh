#!/bin/bash

# Fetch secret from LocalStack Secrets Manager
SECRET_JSON=$(aws secretsmanager get-secret-value \
    --secret-id dailygrind/cognito \
    --query SecretString \
    --output text \
    --endpoint-url http://localstack:4566)

export AWS_COGNITO_USER_POOL_ID=$(echo "$SECRET_JSON" | jq -r .AWS_COGNITO_USER_POOL_ID)
export PORT=8080
export AWS_HOSTNAME=localstack

# Start app with env vars
exec java -jar app.jar
