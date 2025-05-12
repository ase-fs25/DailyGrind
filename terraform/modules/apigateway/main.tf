resource "aws_apigatewayv2_api" "http_api" {
  name          = "dailygrind-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = [
      "http://localhost:3000", "http://localhost:30001", "http://localhost:4566", "http://localstack:4566", "https://oauth.pstmn.io/v1/callback"
    ]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_headers = ["Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"]
    expose_headers = ["Authorization", "Content-Type"]
    max_age           = 3600
    allow_credentials = true
  }
}

resource "aws_apigatewayv2_integration" "ms_user" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type = "HTTP_PROXY"
  # integration_uri = "http://${var.alb_dns_name}/users"
  integration_uri    = "http://host.docker.internal:8082/users"
  integration_method = "ANY"
}

resource "aws_apigatewayv2_integration" "ms_post" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type = "HTTP_PROXY"
  # integration_uri = "http://${var.alb_dns_name}/posts"
  integration_uri    = "http://host.docker.internal:8080/posts"
  integration_method = "ANY"
}

resource "aws_apigatewayv2_integration" "ms_push" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type = "HTTP_PROXY"
  # integration_uri = "http://${var.alb_dns_name}/push-notifications"
  integration_uri    = "http://host.docker.internal:8081/push-notifications"
  integration_method = "ANY"
}


resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_apigatewayv2_authorizer" "jwt_authorizer" {
  api_id          = aws_apigatewayv2_api.http_api.id
  authorizer_type = "JWT"
  name            = "cognito-authorizer"
  identity_sources = ["$request.header.Authorization"]

  jwt_configuration {
    audience = [var.app_client_id]
    issuer = "http://localstack:4566/${var.user_pool_id}"
  }
}

resource "aws_apigatewayv2_route" "users_route" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "ANY /users/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_user.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_authorizer.id
}

resource "aws_apigatewayv2_route" "posts_route" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "ANY /posts/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_post.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_authorizer.id
}

resource "aws_apigatewayv2_route" "push_route" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "ANY /push-notifications/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_push.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_authorizer.id
}

# === OPTIONS Routes (Unauthenticated for CORS Preflight) ===
resource "aws_apigatewayv2_route" "users_options" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "OPTIONS /users/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_user.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "posts_options" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "OPTIONS /posts/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_post.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "push_options" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "OPTIONS /push-notifications/{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.ms_push.id}"
  authorization_type = "NONE"
}
