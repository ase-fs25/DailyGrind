variable "user_events_topic_name" {
  description = "Name of the SNS topic for user events"
  type        = string
  default     = "user-events-topic"
}

variable "user_events_queue_name" {
  description = "Name of the SQS queue to consume user events"
  type        = string
  default     = "user-events-consumer-queue"
}

variable "filter_event_types" {
  description = "List of event types to filter for SNS subscription"
  type = list(string)
  default = ["USER_CREATED", "USER_UPDATED", "FRIENDSHIP_CREATED", "FRIENDSHIP_DELETED"]
}
