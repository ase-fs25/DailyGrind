resource "aws_cognito_user_pool" "daily_grind_user_pool" {
  name = "daily-grind-user-pool"

  auto_verified_attributes = ["email"]

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = false
    require_uppercase = true
  }
}

resource "aws_cognito_user_pool_client" "daily_grind_app_client" {
  name         = "daily-grind-app-client"
  user_pool_id = aws_cognito_user_pool.daily_grind_user_pool.id
  generate_secret = true
  explicit_auth_flows = [
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH"
  ]
}