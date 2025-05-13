output "user_events_topic_arn" {
  description = "SNS topic ARN for user events"
  value       = aws_sns_topic.user_events_topic.arn
}

output "user_events_queue_url" {
  description = "SQS queue URL for user events"
  value       = aws_sqs_queue.user_events_queue.id
}
