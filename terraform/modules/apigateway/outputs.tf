output "http_api_id" {
  value = aws_apigatewayv2_api.http_api.id
}

output "api_url" {
  value = "${aws_apigatewayv2_api.http_api.api_endpoint}/$default/"
}
