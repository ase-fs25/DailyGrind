#!/bin/sh

# Remove the localstack_providers_override.tf file if it exists
rm -f localstack_providers_override.tf

# Use this line if you are running on Apple M- chip (tested for M1 chip)
# export GODEBUG=asyncpreemptoff=1

# Use this line to see logs while running the init command
# export TF_LOG=DEBUG

# Initialize and apply Terraform with LocalStack
tflocal init
tflocal apply -auto-approve
