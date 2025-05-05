resource "aws_sns_topic" "user_events_topic" {
  name = "user-events-topic"
}

resource "aws_sqs_queue" "user_events_queue" {
  name = "example-user-consumer-queue"
}

resource "aws_sns_topic_subscription" "user_events_subscription" {
  topic_arn = aws_sns_topic.user_events_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.user_events_queue.arn

  filter_policy = jsonencode({
    eventType = ["USER_CREATED", "USER_UPDATED", "FRIENDSHIP_CREATED", "FRIENDSHIP_DELETED"]
  })

  endpoint_auto_confirms = true
  raw_message_delivery   = true
}

resource "aws_sqs_queue_policy" "example_queue_policy" {
  queue_url = aws_sqs_queue.user_events_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "SQS:SendMessage"
      Resource = aws_sqs_queue.user_events_queue.arn
      Condition = {
        ArnEquals = {
          "aws:SourceArn" = aws_sns_topic.user_events_topic.arn
        }
      }
    }]
  })
}

output "user_events_topic_arn" {
  description = "SNS topic ARN for user events"
  value       = aws_sns_topic.user_events_topic.arn
}
