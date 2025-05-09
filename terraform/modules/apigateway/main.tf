resource "aws_apigatewayv2_api" "http_api" {
  name          = "dailygrind-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "ms_user" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-user-service:8080"
  integration_method = "ANY"
}

resource "aws_apigatewayv2_integration" "ms_post" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-post-service:8080"
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
    issuer = "http://localhost:4566/${var.user_pool_id}"
  }
}

resource "aws_apigatewayv2_route" "users_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /users/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_user.id}"

  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_authorizer.id
}

resource "aws_apigatewayv2_route" "posts_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /posts/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_post.id}"

  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.jwt_authorizer.id
}
