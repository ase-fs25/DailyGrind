output "post_service_service_name" {
  value = aws_ecs_service.post_service.name
}

output "post_service_task_arn" {
  value = aws_ecs_task_definition.post_service.arn
}

output "push_notification_service_service_name" {
  value = aws_ecs_service.push_notification_service.name
}

output "push_notification_service_task_arn" {
  value = aws_ecs_task_definition.push_notification_service.arn
}

output "user_service_service_name" {
  value = aws_ecs_service.user_service.name
}

output "user_service_task_arn" {
  value = aws_ecs_task_definition.user_service.arn
}

