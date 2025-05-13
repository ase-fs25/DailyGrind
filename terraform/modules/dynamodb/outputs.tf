output "users_table_name" {
  value = aws_dynamodb_table.users.name
}

output "posts_table_name" {
  value = aws_dynamodb_table.posts.name
}

output "push_subscriptions_table_name" {
  value = aws_dynamodb_table.push_subscriptions.name
}
