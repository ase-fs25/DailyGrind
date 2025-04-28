################################
# 1. Create the API Gateway
################################

resource "aws_apigatewayv2_api" "http_api" {
  name          = "dailygrind-api"
  protocol_type = "HTTP"
}

################################
# 2. Create Integrations (connections to services)
################################

resource "aws_apigatewayv2_integration" "ms_user_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-user-service:8080"
  integration_method = "ANY"
}

resource "aws_apigatewayv2_integration" "ms_post_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-post-service:8080"
  integration_method = "ANY"
}

resource "aws_apigatewayv2_integration" "frontend_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://localhost:4566/dailygrind/index.html"
  integration_method = "ANY"
}

################################
# 3. Cognito Authorizer
################################

resource "aws_apigatewayv2_authorizer" "cognito_authorizer" {
  api_id          = aws_apigatewayv2_api.http_api.id
  authorizer_type = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name            = "dailygrind-cognito-authorizer"

  jwt_configuration {
    audience = [aws_cognito_user_pool_client.daily_grind_app_client.id]
    issuer = aws_cognito_user_pool.daily_grind_user_pool.endpoint
  }
}

################################
# 4. Create Routes (map URLs to services)
################################

resource "aws_apigatewayv2_route" "ms_user_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /users/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_user_integration.id}"

  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito_authorizer.id
}

resource "aws_apigatewayv2_route" "ms_post_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /posts/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_post_integration.id}"

  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.cognito_authorizer.id
}

# Frontend route - no auth needed for frontend
resource "aws_apigatewayv2_route" "frontend_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.frontend_integration.id}"
}

################################
# 5. Auto-Deploy API
################################

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}
