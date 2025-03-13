#!/bin/bash

USER_POOL_ID=$(terraform output -raw user_pool_id)
USERNAME="testuser"
PASSWORD="Test@1234"
EMAIL="testuser@example.com"

aws cognito-idp admin-create-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --user-attributes Name=email,Value="$EMAIL" Name=email_verified,Value=true \
  --temporary-password "$PASSWORD"

aws cognito-idp admin-set-user-password \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --password "$PASSWORD" \
  --permanent

echo "Test user '$USERNAME' created successfully."