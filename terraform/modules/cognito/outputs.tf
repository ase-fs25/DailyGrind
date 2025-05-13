output "user_pool_id" {
  value = aws_cognito_user_pool.user_pool.id
}

output "app_client_id" {
  value = aws_cognito_user_pool_client.app_client.id
}

output "client_secret" {
  value     = aws_cognito_user_pool_client.app_client.client_secret
  sensitive = true
}
