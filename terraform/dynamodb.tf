# DynamoDB Table for Users
resource "aws_dynamodb_table" "Users" {
  name           = "Users"  # Pascal case
  hash_key       = "userId"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "userId"
    type = "S"  # String type for userId
  }

  attribute {
    name = "name"
    type = "S"  # String type for name
  }

  # Global Secondary Index for name (optional if you plan to query by name)
  global_secondary_index {
    name               = "NameIndex"
    hash_key           = "name"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "Users Table"
  }
}

# DynamoDB Table for Friend Requests
resource "aws_dynamodb_table" "FriendRequests" {
  name           = "FriendRequests"  # Pascal case
  hash_key       = "senderId"
  range_key      = "receiverId"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "senderId"
    type = "S"  # String type for senderId
  }

  attribute {
    name = "receiverId"
    type = "S"  # String type for receiverId
  }

  attribute {
    name = "requestStatus"
    type = "S"  # String type for requestStatus
  }

  # Global Secondary Index for requestStatus (optional if you plan to query by requestStatus)
  global_secondary_index {
    name               = "RequestStatusIndex"
    hash_key           = "requestStatus"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "FriendRequests Table"
  }
}

# DynamoDB Table for Posts
resource "aws_dynamodb_table" "Posts" {
  name           = "Posts"  # Pascal case
  hash_key       = "postId"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "postId"
    type = "S"  # String type for postId
  }

  attribute {
    name = "userId"
    type = "S"  # String type for userId
  }

  attribute {
    name = "content"
    type = "S"  # String type for content of the post
  }

  attribute {
    name = "timestamp"
    type = "S"  # String type for timestamp
  }

  # Global Secondary Index for userId (optional if you plan to query by userId)
  global_secondary_index {
    name               = "UserIdIndex"
    hash_key           = "userId"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  # Global Secondary Index for timestamp (optional if you plan to query posts by timestamp)
  global_secondary_index {
    name               = "TimestampIndex"
    hash_key           = "timestamp"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  # Global Secondary Index for content (optional if you plan to query posts by content)
  global_secondary_index {
    name               = "ContentIndex"
    hash_key           = "content"
    projection_type    = "ALL"
    read_capacity      = 5
    write_capacity     = 5
  }

  tags = {
    Name = "Posts Table"
  }
}

# Outputs for reference
output "Users_table_name" {
  value = aws_dynamodb_table.Users.name
}

output "FriendRequests_table_name" {
  value = aws_dynamodb_table.FriendRequests.name
}

output "Posts_table_name" {
  value = aws_dynamodb_table.Posts.name
}