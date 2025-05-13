variable "subnet_id" {
  description = "Subnet ID for the ALB"
  type        = string
}

variable "security_group_id" {
  description = "Security group ID for the ALB"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID for target groups"
  type        = string
}
