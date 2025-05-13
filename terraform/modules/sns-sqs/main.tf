resource "aws_sns_topic" "user_events_topic" {
  name = var.user_events_topic_name
}

resource "aws_sqs_queue" "user_events_queue" {
  name = var.user_events_queue_name
}

resource "aws_sns_topic_subscription" "user_events_subscription" {
  topic_arn = aws_sns_topic.user_events_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.user_events_queue.arn
  filter_policy = jsonencode({
    eventType = var.filter_event_types
  })
  endpoint_auto_confirms = true
  raw_message_delivery   = true
}

resource "aws_sqs_queue_policy" "user_events_queue_policy" {
  queue_url = aws_sqs_queue.user_events_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "SQS:SendMessage"
        Resource  = aws_sqs_queue.user_events_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.user_events_topic.arn
          }
        }
      }
    ]
  })
}
