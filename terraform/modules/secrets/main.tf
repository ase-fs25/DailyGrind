# File: modules/secrets/main.tf

resource "aws_secretsmanager_secret" "cognito_client_secret" {
  name = "cognito_client_secret"
}

resource "aws_secretsmanager_secret_version" "cognito_client_secret_version" {
  secret_id = aws_secretsmanager_secret.cognito_client_secret.id
  secret_string = jsonencode({
    value = var.cognito_client_secret
  })
}
