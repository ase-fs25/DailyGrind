#!/bin/sh

# Generate ECS definitions if in prod profile
echo "Generating ECS task definitions..."
/bin/sh /terraform/generate-ecs.sh

# Run Terraform
cd /terraform || exit 1

tflocal init
tflocal apply -auto-approve

terraform output client_secret
