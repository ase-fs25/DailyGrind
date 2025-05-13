output "cognito_secret_arn" {
  value = aws_secretsmanager_secret.cognito_client_secret.arn
}
