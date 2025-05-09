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

module "apigateway" {
  source        = "./modules/apigateway"
  app_client_id = module.cognito.app_client_id
  user_pool_id  = module.cognito.user_pool_id
  depends_on = [module.lambda, module.cognito]
}

module "ecs" {
  source             = "./modules/ecs"
  subnet_ids         = module.network.subnet_ids
  security_group_ids = module.network.security_group_ids
}

