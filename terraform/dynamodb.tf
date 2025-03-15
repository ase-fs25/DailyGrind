# DynamoDB Table for Users
resource "aws_dynamodb_table" "users" {
  name           = "users"
  hash_key       = "user_id"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "user_id"
    type = "S"
  }

  attribute {
    name = "name"
    type = "S"
  }

  # Global Secondary Index for name
  global_secondary_index {
    name               = "name-index"
    hash_key           = "name"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "users-table"
  }
}

# DynamoDB Table for Friend Requests
resource "aws_dynamodb_table" "friend_requests" {
  name           = "friend-requests"  # Lowercase with hyphens
  hash_key       = "sender_id"
  range_key      = "receiver_id"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "sender_id"
    type = "S"
  }

  attribute {
    name = "receiver_id"
    type = "S"
  }

  attribute {
    name = "request_status"
    type = "S"
  }

  # Global Secondary Index for request_status
  global_secondary_index {
    name               = "request-status-index"
    hash_key           = "request_status"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "friend-requests-table"
  }
}

# DynamoDB Table for Posts
resource "aws_dynamodb_table" "posts" {
  name           = "posts"
  hash_key       = "post_id"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "post_id"
    type = "S"
  }

  attribute {
    name = "user_id"
    type = "S"
  }

  attribute {
    name = "title"
    type = "S"
  }

  attribute {
    name = "content"
    type = "S"
  }

  attribute {
    name = "timestamp"
    type = "S"
  }

  # Global Secondary Index for user_id
  global_secondary_index {
    name               = "user-id-index"
    hash_key           = "user_id"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  # Global Secondary Index for timestamp
  global_secondary_index {
    name               = "timestamp-index"
    hash_key           = "timestamp"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  global_secondary_index {
    name               = "title-index"
    hash_key           = "title"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  # Global Secondary Index for content
  global_secondary_index {
    name               = "content-index"
    hash_key           = "content"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "posts-table"
  }
}

# Outputs for reference
output "users_table_name" {
  value = aws_dynamodb_table.users.name
}

output "friend_requests_table_name" {
  value = aws_dynamodb_table.friend_requests.name
}

output "posts_table_name" {
  value = aws_dynamodb_table.posts.name
}