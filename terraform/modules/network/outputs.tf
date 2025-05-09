output "vpc_id" {
  value = aws_vpc.main.id
}

output "subnet_id" {
  value = aws_subnet.main.id
}

output "security_group_id" {
  value = aws_security_group.allow_all.id
}

output "subnet_ids" {
  value = [aws_subnet.main.id]
}

output "security_group_ids" {
  value = [aws_security_group.allow_all.id]
}
