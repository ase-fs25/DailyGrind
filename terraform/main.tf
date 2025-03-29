# Cognito User Pool
resource "aws_cognito_user_pool" "daily_grind_user_pool" {
  name = "daily-grind-user-pool"

  username_attributes = ["email"]
  auto_verified_attributes = ["email"]

  lambda_config {
    pre_sign_up = aws_lambda_function.confirm_user_lambda.arn
  }

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }

}

# Cognito Client
resource "aws_cognito_user_pool_client" "daily_grind_app_client" {
  name = "daily-grind-app-client"
  user_pool_id = aws_cognito_user_pool.daily_grind_user_pool.id
  allowed_oauth_scopes = ["email", "openid", "profile"]
  allowed_oauth_flows_user_pool_client = true
  generate_secret = true
  allowed_oauth_flows = ["code", "implicit"]
  callback_urls = ["http://localhost:3000", "https://oauth.pstmn.io/v1/callback"]
  logout_urls = ["http://localhost:3000"]
  supported_identity_providers = ["COGNITO"]
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

# S3 Bucket for Amplify
resource "aws_s3_bucket" "daily_grind_amplify_bucket" {
  bucket = "daily-grind-amplify-bucket"
}

# IAM Role for Amplify
resource "aws_iam_role" "amplify_role" {
  name = "amplify-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "amplify.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role" "iam_for_amplify" {
  name               = "iam_for_amplify"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

# Amplify App
resource "aws_amplify_app" "daily_grind_amplify_app" {
  name = "DailyGrindApp"
  iam_service_role_arn = aws_iam_role.iam_for_amplify.arn

  environment_variables = {
    "AMPLIFY_BUCKET" = aws_s3_bucket.daily_grind_amplify_bucket.bucket
  }

  enable_branch_auto_build = false
}

resource "aws_amplify_branch" "main" {
  app_id      = aws_amplify_app.daily_grind_amplify_app.id
  branch_name = "main"

  enable_auto_build = false
}