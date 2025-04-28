def lambda_handler(event, context):

    # Confirm the user
    event['response']['autoConfirmUser'] = True
    event['response']['autoVerifyEmail'] = True

    # Return to Amazon Cognito
    return event