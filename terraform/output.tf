output "daily_grind_user_pool_id" {
  value = aws_cognito_user_pool.daily_grind_user_pool.id
}

output "daily_grind_app_client_id" {
  value = aws_cognito_user_pool_client.daily_grind_app_client.id
}

output "client_secret" {
  value     = aws_cognito_user_pool_client.daily_grind_app_client.client_secret
  sensitive = true
}

output "api_gateway_base_url" {
  value = format(
    "Frontend: http://localhost:4566/_aws/execute-api/%s/$default/\nMS User: http://localhost:4566/_aws/execute-api/%s/$default/users/\nMS Post: http://localhost:4566/_aws/execute-api/%s/$default/posts/",
    aws_apigatewayv2_api.http_api.id,
    aws_apigatewayv2_api.http_api.id,
    aws_apigatewayv2_api.http_api.id
  )
}

output "frontend_url" {
  # value = aws_s3_bucket_website_configuration.frontend_website.website_endpoint
  value = "http://localhost:4566/dailygrind/index.html"
}
