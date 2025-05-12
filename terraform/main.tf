module "network" {
  source = "./modules/network"
}

module "s3" {
  source      = "./modules/s3"
  bucket_name = var.bucket_name
}

module "dynamodb" {
  source = "./modules/dynamodb"
}

module "cognito" {
  source                = "./modules/cognito"
  lambda_pre_signup_arn = module.lambda.lambda_pre_signup_arn
  depends_on = [module.lambda]
}

module "lambda" {
  source = "./modules/lambda"
}

module "secrets" {
  source                = "./modules/secrets"
  cognito_client_secret = var.cognito_client_secret
}

module "ecs" {
  source                           = "./modules/ecs"
  subnet_ids                       = module.network.subnet_ids
  security_group_ids               = module.network.security_group_ids
  tg_user_service_arn              = module.alb.tg_user_service_arn
  tg_post_service_arn              = module.alb.tg_post_service_arn
  tg_push_notification_service_arn = module.alb.tg_push_notification_service_arn
}

module "alb" {
  source            = "./modules/alb"
  vpc_id            = module.network.vpc_id
  subnet_id         = module.network.subnet_id
  security_group_id = module.network.security_group_id
}

module "apigateway" {
  source        = "./modules/apigateway"
  app_client_id = module.cognito.app_client_id
  user_pool_id  = module.cognito.user_pool_id
  depends_on = [module.lambda, module.cognito]
  alb_dns_name  = module.alb.alb_dns_name
}
