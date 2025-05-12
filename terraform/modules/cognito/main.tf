resource "aws_cognito_user_pool" "user_pool" {
  name              = var.name
  username_attributes = ["email"]
  auto_verified_attributes = ["email"]
  mfa_configuration = "OFF"

  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Your verification code"
    email_message        = "Your verification code is {####}"
  }

  lambda_config {
    pre_sign_up = var.lambda_pre_signup_arn
  }

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }
}

resource "aws_cognito_user_pool_client" "app_client" {
  name                                 = "${var.name}-app-client"
  user_pool_id                         = aws_cognito_user_pool.user_pool.id
  allowed_oauth_scopes = ["email", "openid", "profile"]
  allowed_oauth_flows_user_pool_client = true
  generate_secret                      = true
  allowed_oauth_flows = ["code", "implicit"]
  callback_urls                        = var.callback_urls
  logout_urls                          = var.logout_urls
  supported_identity_providers = ["COGNITO"]
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH"
  ]
}

resource "aws_cognito_user_group" "user_group" {
  name         = "user-group"
  user_pool_id = aws_cognito_user_pool.user_pool.id
}

resource "aws_cognito_user_group" "company_user_group" {
  name         = "company-user-group"
  user_pool_id = aws_cognito_user_pool.user_pool.id
}
