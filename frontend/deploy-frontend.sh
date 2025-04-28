#!/usr/bin/env bash

echo "Waiting for the S3 bucket to exist..."

until aws --endpoint-url=http://localstack:4566 s3 ls "s3://$BUCKET_NAME" 2>/dev/null; do
  echo "Bucket not ready yet. Sleeping 2 seconds..."
  sleep 2
done

echo "Bucket is ready! Deploying frontend to $BUCKET_NAME..."

aws --endpoint-url=http://localstack:4566 s3api put-bucket-website --bucket "$BUCKET_NAME" --website-configuration '{
  "IndexDocument": {
    "Suffix": "index.html"
  },
  "ErrorDocument": {
    "Key": "index.html"
  }
}'

echo "Deployment finished!"
