resource "aws_cognito_user_pool" "daily_grind_user_pool" {
  name = "daily-grind-user-pool"

  username_attributes = ["email"]  # Allows users to sign in using their email

  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Account Confirmation"
    email_message        = "Your confirmation code is {####}"
  }

  # Automatically verify the email attribute during user sign-up
  auto_verified_attributes = ["email"]

  # Automatically confirm users upon sign-up
  admin_create_user_config {
    allow_admin_create_user_only = false
    invite_message_template {
      email_subject = "Welcome to Our Service"
      email_message = "Your account has been created. Your username is {username} and temporary password is {####}."
    }
  }

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }
}

resource "aws_cognito_user_pool_client" "daily_grind_app_client" {
  name = "daily-grind-app-client"
  user_pool_id = aws_cognito_user_pool.daily_grind_user_pool.id
  allowed_oauth_scopes = ["email", "openid", "profile"]
  allowed_oauth_flows_user_pool_client = true
  generate_secret = true
  allowed_oauth_flows = ["code", "implicit"]
  callback_urls = ["http://localhost:3000", "https://oauth.pstmn.io/v1/callback"]
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH"
  ]
}

resource "aws_cognito_user_group" "daily_grind_user_group" {
  name         = "daily-grind-user-group"
  user_pool_id = aws_cognito_user_pool.daily_grind_user_pool.id
}

resource "aws_cognito_user_group" "daily_grind_company_user_group" {
  name         = "daily-grind-company-user-group"
  user_pool_id = aws_cognito_user_pool.daily_grind_user_pool.id
}