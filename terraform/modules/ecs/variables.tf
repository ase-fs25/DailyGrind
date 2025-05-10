variable "subnet_ids" {
  type = list(string)
}

variable "security_group_ids" {
  type = list(string)
}

variable "tg_post_service_arn" {
  type = string
}

variable "tg_push_notification_service_arn" {
  type = string
}

variable "tg_user_service_arn" {
  type = string
}

