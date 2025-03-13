#!/bin/sh

# Remove the localstack_providers_override.tf file if it exists
rm -f localstack_providers_override.tf

# Initialize and apply Terraform with LocalStack
tflocal init
tflocal apply -auto-approve