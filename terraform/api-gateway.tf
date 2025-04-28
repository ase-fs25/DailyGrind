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

# Integration to ms-user backend
resource "aws_apigatewayv2_integration" "ms_user_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-user-service:8080"
  integration_method = "ANY"
}

# Integration to ms-post backend
resource "aws_apigatewayv2_integration" "ms_post_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://ms-post-service:8080"
  integration_method = "ANY"
}

# Integration to frontend
resource "aws_apigatewayv2_integration" "frontend_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://localhost:4566/dailygrind/index.html"
  integration_method = "ANY"
}

################################
# 3. Create Routes (map URLs to services)
################################

# Route for ms-user
resource "aws_apigatewayv2_route" "ms_user_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /users/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_user_integration.id}"
}

# Route for ms-post
resource "aws_apigatewayv2_route" "ms_post_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /posts/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ms_post_integration.id}"
}

# Route for frontend (catch-all fallback)
resource "aws_apigatewayv2_route" "frontend_route" {
  api_id = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /{proxy+}"  # Any other route falls back to frontend
  target = "integrations/${aws_apigatewayv2_integration.frontend_integration.id}"
}

################################
# 4. (Optional) Auto-Deploy API
################################

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

output "api_gateway_base_url" {
  value = format(
    "Frontend: http://localhost:4566/_aws/execute-api/%s/$default/\nMS User: http://localhost:4566/_aws/execute-api/%s/$default/users/\nMS Post: http://localhost:4566/_aws/execute-api/%s/$default/posts/",
    aws_apigatewayv2_api.http_api.id,
    aws_apigatewayv2_api.http_api.id,
    aws_apigatewayv2_api.http_api.id
  )
}
