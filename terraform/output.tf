output "daily_grind_user_pool_id" {
  value = aws_cognito_user_pool.daily_grind_user_pool.id
}

output "daily_grind_app_client_id" {
  value = aws_cognito_user_pool_client.daily_grind_app_client.id
}