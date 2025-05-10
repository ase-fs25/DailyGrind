#!/bin/sh

# Install dependencies for Push Notification Lambda
cd /lambda-functions/push-notification-lambda/src && npm i > /dev/null 2>&1

# generate terraform files for ECS
cd /terraform && /bin/sh /terraform/generate-ecs.sh

# Run Terraform
cd /terraform || exit 1
tflocal init
tflocal apply -auto-approve

# Write secrets to .env files and deploy to AWS Secrets Manager
/bin/sh /terraform/extract_secrets.sh

# Print client_secret  # TODO: Remove this in production?
terraform output client_secret
