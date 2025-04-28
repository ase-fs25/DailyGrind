# DynamoDB Table for Users
resource "aws_dynamodb_table" "users" {
  name           = "users"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"
  range_key      = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  tags = {
    Name = "users-table",
    Environment = "dev",
    Project = "daily-grind"
  }
}

resource "aws_dynamodb_table" "posts" {
  name           = "posts"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"
  range_key      = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  ttl {
    attribute_name = "ttl"
    enabled        = true
  }

  tags = {
    Name = "posts-table",
    Environment = "dev",
    Project = "daily-grind"
  }
}

# Outputs for reference
output "users_table_name" {
  value = aws_dynamodb_table.users.name
}

output "posts_table_name" {
  value = aws_dynamodb_table.posts.name
}