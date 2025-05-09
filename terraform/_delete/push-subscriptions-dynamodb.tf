# DynamoDB Table for Push Subscriptions
resource "aws_dynamodb_table" "push_subscriptions" {
  name           = "push-subscriptions"
  hash_key       = "subscription_id"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "subscription_id"
    type = "S"
  }

  attribute {
    name = "user_id"
    type = "S"
  }

  # Global Secondary Index for subscription_id
  global_secondary_index {
    name            = "subscription-id-index"
    hash_key        = "subscription_id"
    projection_type = "ALL"
    read_capacity   = 5
    write_capacity  = 5
  }

  # Global Secondary Index for user_id
  global_secondary_index {
    name            = "user-id-index"
    hash_key        = "user_id"
    projection_type = "ALL"
    read_capacity   = 5
    write_capacity  = 5
  }

  tags = {
    Name = "push-subscriptions-table"
  }
}

# output for reference
output "push_subscriptions_table_name" {
  value = aws_dynamodb_table.push_subscriptions.name
}
