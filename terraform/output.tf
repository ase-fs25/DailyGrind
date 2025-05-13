output "user_pool_id" {
  value = module.cognito.user_pool_id
}

output "app_client_id" {
  value = module.cognito.app_client_id
}

output "frontend_url" {
  value = module.s3.website_url
}

output "api_gateway_url" {
  value = module.apigateway.api_url
}

output "client_secret" {
  value     = module.cognito.client_secret
  sensitive = true
}
