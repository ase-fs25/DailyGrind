#!/usr/bin/env bash
set -e

echo "🌍 Waiting for Terraform to finish..."
sleep 5
while docker ps | grep terraform > /dev/null 2>&1; do
  echo "⏳ Terraform not finished yet. Sleeping 5 seconds..."
  sleep 5;
done;

echo "✅ Terraform is done. Proceeding with deployment."

npm install --prefix /app/frontend
npm run build --prefix /app/frontend

echo "📦 Waiting for S3 bucket $BUCKET_NAME..."

until aws --endpoint-url=http://localstack:4566 s3 ls "s3://$BUCKET_NAME" 2>/dev/null; do
  echo "⏳ Bucket not ready yet. Sleeping 5 seconds..."
  sleep 5
done

echo "✅ Bucket is ready. Deploying frontend..."

aws --endpoint-url=http://localstack:4566 s3api put-bucket-website --bucket "$BUCKET_NAME" --website-configuration '{
  "IndexDocument": { "Suffix": "index.html" },
  "ErrorDocument": { "Key": "index.html" }
}'

aws --endpoint-url=http://localstack:4566 s3 sync /app/frontend/dist "s3://$BUCKET_NAME"

echo "✅ Frontend deployed to S3."

### 🐳 Deploy Microservices to ECS

echo "📡 Waiting for ECS cluster to become available..."

until aws --endpoint-url=http://localstack:4566 ecs describe-clusters \
    --clusters dailygrind-cluster \
    --query 'clusters[0].status' \
    --output text 2>/dev/null | grep -q "ACTIVE"; do
  echo "⏳ ECS cluster not ready yet. Sleeping 5 seconds..."
  sleep 5
done

echo "✅ ECS cluster is active."

for dir in /microservices/*/; do
  service_name=$(basename "$dir")
  service_name="${service_name//-/_}"
  image_name="$service_name:latest"

  echo "🚧 Building Docker image for $service_name..."
  docker build --no-cache -t "$image_name" "$dir"

  echo "📦 Registering task definition update for $service_name..."

  aws --endpoint-url=http://localstack:4566 ecs update-service \
    --cluster dailygrind-cluster \
    --service "$service_name" \
    --force-new-deployment

  echo "✅ Service $service_name updated in ECS."
done

echo "🎉 Deployment of all services finished."
