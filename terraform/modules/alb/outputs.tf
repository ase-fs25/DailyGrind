output "alb_dns_name" {
  value = aws_lb.main.dns_name
}

output "tg_user_service_arn" {
  value = aws_lb_target_group.user_service.arn
}

output "tg_post_service_arn" {
  value = aws_lb_target_group.post_service.arn
}

output "tg_push_notification_service_arn" {
  value = aws_lb_target_group.push_notification_service.arn
}
