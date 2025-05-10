output "post-service_service_name" {
  value = aws_ecs_service.post-service.name
}

output "post-service_task_arn" {
  value = aws_ecs_task_definition.post-service.arn
}

output "push-notification-service_service_name" {
  value = aws_ecs_service.push-notification-service.name
}

output "push-notification-service_task_arn" {
  value = aws_ecs_task_definition.push-notification-service.arn
}

output "user-service_service_name" {
  value = aws_ecs_service.user-service.name
}

output "user-service_task_arn" {
  value = aws_ecs_task_definition.user-service.arn
}

