"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.JpApi = void 0;
const aws_cognito_1 = require("@aws-cdk/aws-cognito");
const cdk = require("@aws-cdk/core");
const apigw = require("@aws-cdk/aws-apigateway");
const aws_apigateway_1 = require("@aws-cdk/aws-apigateway");
const constants_1 = require("./constants");
const aws_iam_1 = require("@aws-cdk/aws-iam");
class JpApi extends cdk.Construct {
    constructor(scope, id, props) {
        super(scope, id);
        this.restApi = new apigw.RestApi(this, 'PlaidLinkApi', {
            description: "Transaction Service API",
        });
        const userPool = aws_cognito_1.UserPool.fromUserPoolArn(this, 'DefaultUserPool', constants_1.DEFAULT_COGNITO_USER_POOL_ARN);
        this.userPoolsAuthorizer = new apigw.CognitoUserPoolsAuthorizer(this, 'UserPoolAuthorizer', {
            cognitoUserPools: [userPool]
        });
        // Integrate for linkTokens:
        const postLinkTokenIntegration = new apigw.LambdaIntegration(props.linkLambda, {
            proxy: false,
            allowTestInvoke: true,
            passthroughBehavior: aws_apigateway_1.PassthroughBehavior.WHEN_NO_MATCH,
            requestTemplates: { "application/json": '{"user" : "$context.authorizer.claims[\'cognito:username\']",' +
                    '"products" : $input.json(\'$.products\'),' +
                    '"webhook" : "$util.escapeJavaScript($input.json(\'$.webhook\'))"}' },
            integrationResponses: [
                {
                    // Successful response from the Lambda function, no filter defined
                    statusCode: "200",
                    responseTemplates: {
                        // Check https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
                        'application/json': 'input.body' // Just return the accessToken string.
                    },
                    responseParameters: {
                        // We can map response parameters
                        // - Destination parameters (the key) are the response parameters (used in mappings)
                        // - Source parameters (the value) are the integration response parameters or expressions
                        // Do this for CORS.
                        // WARNING: DOES NOT SUPPORT ALL HEADERS.
                        'method.response.header.X-Requested-With': "'*'",
                        'method.response.header.Content-Type': "'application/json'",
                        'method.response.header.Access-Control-Allow-Origin': "'http://localhost:3000'",
                        'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,x-requested-with'",
                        'method.response.header.Access-Control-Allow-Methods': "'POST,GET,OPTIONS'"
                    }
                },
            ],
        });
        const linkResource = this.restApi.root.addResource("linktoken");
        linkResource.addMethod("POST", postLinkTokenIntegration, {
            authorizer: this.userPoolsAuthorizer,
            authorizationType: apigw.AuthorizationType.COGNITO,
            methodResponses: [{
                    statusCode: "200",
                    responseParameters: {
                        'method.response.header.X-Requested-With': true,
                        'method.response.header.Content-Type': true,
                        'method.response.header.Access-Control-Allow-Origin': true,
                        'method.response.header.Access-Control-Allow-Headers': true,
                        'method.response.header.Access-Control-Allow-Methods': true
                    }
                }]
        });
        linkResource.addCorsPreflight({
            allowOrigins: ['http://localhost:3000'],
            allowMethods: ['OPTIONS', 'GET', 'POST'],
            allowCredentials: true,
            allowHeaders: apigw.Cors.DEFAULT_HEADERS
        });
        // Integrate the postItem lambda:
        const postItemIntegration = new apigw.LambdaIntegration(props.itemLambda, {
            proxy: false,
            allowTestInvoke: true,
            requestTemplates: { "application/json": '{"user" : "$context.authorizer.claims[\'cognito:username\']",' +
                    '"availableProducts" : $input.json(\'$.availableProducts\'),' +
                    '"publicToken" : $input.json(\'$.publicToken\'),' +
                    '"institutionId": $input.json(\'$.institutionId\'),' +
                    '"accounts": $input.json(\'$.accounts\'),' +
                    '"dateCreated": $input.json(\'$.dateCreated\'),' +
                    '"metaData" : $input.json(\'$.metaData\'),' +
                    '"webhook" : $input.json(\'$.webhook\')}'
            },
            passthroughBehavior: aws_apigateway_1.PassthroughBehavior.WHEN_NO_MATCH, integrationResponses: [
                {
                    // Successful response from the Lambda function, no filter defined
                    statusCode: "200",
                    responseTemplates: {
                        // Check https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
                        'application/json': JSON.stringify('$util.escapeJavaScript($input.body)') // Just return the accessToken string.
                    },
                    responseParameters: {
                        // We can map response parameters
                        // - Destination parameters (the key) are the response parameters (used in mappings)
                        // - Source parameters (the value) are the integration response parameters or expressions
                        // Do this for CORS.
                        // WARNING: DOES NOT SUPPORT ALL HEADERS.
                        'method.response.header.X-Requested-With': "'*'",
                        'method.response.header.Content-Type': "'application/json'",
                        'method.response.header.Access-Control-Allow-Origin': "'http://localhost:3000'",
                        'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,x-requested-with'",
                        'method.response.header.Access-Control-Allow-Methods': "'POST,GET,OPTIONS'"
                    }
                },
            ],
        });
        const itemResource = this.restApi.root.addResource("items");
        itemResource.addMethod('POST', postItemIntegration, {
            authorizer: this.userPoolsAuthorizer,
            authorizationType: apigw.AuthorizationType.COGNITO,
            methodResponses: [{
                    statusCode: "200",
                    responseParameters: {
                        'method.response.header.X-Requested-With': true,
                        'method.response.header.Content-Type': true,
                        'method.response.header.Access-Control-Allow-Origin': true,
                        'method.response.header.Access-Control-Allow-Headers': true,
                        'method.response.header.Access-Control-Allow-Methods': true
                    }
                }]
        });
        itemResource.addCorsPreflight({
            allowOrigins: ['http://localhost:3000'],
            allowMethods: ['OPTIONS', 'GET', 'POST'],
            allowCredentials: true,
            allowHeaders: apigw.Cors.DEFAULT_HEADERS
        });
        // Integrate getTransactions lambda:
        const getTransactionsIntegration = new apigw.LambdaIntegration(props.getTransactionsLambda, {
            proxy: false,
            allowTestInvoke: true,
            passthroughBehavior: aws_apigateway_1.PassthroughBehavior.WHEN_NO_MATCH,
            requestParameters: {
                'integration.request.querystring.user': 'method.request.querystring.user',
                'integration.request.querystring.startDate': 'method.request.querystring.startDate'
            },
            requestTemplates: { "application/json": '{"user" : "$context.authorizer.claims[\'cognito:username\']",\n' +
                    '"startDate" : "$util.escapeJavaScript($input.params(\'startDate\'))"}' },
            integrationResponses: [
                {
                    // Successful response from the Lambda function, no filter defined
                    statusCode: "200",
                    responseTemplates: {
                        // Check https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
                        'application/json': JSON.stringify('$util.escapeJavaScript($input.body)')
                    },
                    responseParameters: {
                        // We can map response parameters
                        // - Destination parameters (the key) are the response parameters (used in mappings)
                        // - Source parameters (the value) are the integration response parameters or expressions
                        // Do this for CORS.
                        // WARNING: DOES NOT SUPPORT ALL HEADERS.
                        'method.response.header.X-Requested-With': "'*'",
                        'method.response.header.Content-Type': "'application/json'",
                        'method.response.header.Access-Control-Allow-Origin': "'http://localhost:3000'",
                        'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,x-requested-with'",
                        'method.response.header.Access-Control-Allow-Methods': "'POST,GET,OPTIONS'"
                    }
                },
            ],
        });
        const transactionsResource = this.restApi.root.addResource("transactions");
        transactionsResource.addMethod('GET', getTransactionsIntegration, {
            authorizer: this.userPoolsAuthorizer,
            authorizationType: apigw.AuthorizationType.COGNITO,
            requestParameters: {
                'method.request.querystring.user': false,
                'method.request.querystring.startDate': false
            },
            methodResponses: [{
                    statusCode: "200",
                    responseParameters: {
                        'method.response.header.X-Requested-With': true,
                        'method.response.header.Content-Type': true,
                        'method.response.header.Access-Control-Allow-Origin': true,
                        'method.response.header.Access-Control-Allow-Headers': true,
                        'method.response.header.Access-Control-Allow-Methods': true
                    },
                }]
        });
        transactionsResource.addCorsPreflight({
            allowOrigins: ['http://localhost:3000'],
            allowMethods: ['OPTIONS', 'GET', 'POST'],
            allowCredentials: true,
            allowHeaders: apigw.Cors.DEFAULT_HEADERS
        });
        // Integrate postTransactions lambda:
        const postTransactionsIntegration = new apigw.AwsIntegration({
            service: "states",
            action: "StartExecution",
            integrationHttpMethod: "POST",
            options: {
                credentialsRole: aws_iam_1.Role.fromRoleArn(this, 'StepFunctionsAPIRole', "arn:aws:iam::397250182609:role/JPApiGatewayToStepFunctions"),
                integrationResponses: [
                    {
                        // Successful response from the Lambda function, no filter defined
                        statusCode: "200",
                        responseTemplates: {
                            // Check https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
                            'application/json': JSON.stringify('$util.escapeJavaScript($input.body)')
                        },
                        responseParameters: {
                            // We can map response parameters
                            // - Destination parameters (the key) are the response parameters (used in mappings)
                            // - Source parameters (the value) are the integration response parameters or expressions
                            // Do this for CORS.
                            // WARNING: DOES NOT SUPPORT ALL HEADERS.
                            'method.response.header.X-Requested-With': "'*'",
                            'method.response.header.Content-Type': "'application/json'",
                            'method.response.header.Access-Control-Allow-Origin': "'http://localhost:3000'",
                            'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,x-requested-with'",
                            'method.response.header.Access-Control-Allow-Methods': "'POST,GET,OPTIONS'"
                        }
                    },
                ],
                requestTemplates: {
                    "application/json": `{
              "input": "$util.escapeJavaScript($input.body)",
              "stateMachineArn": "${props.pullTransactionsMachine.stateMachineArn}"
            }`,
                },
            }
        });
        transactionsResource.addMethod('POST', postTransactionsIntegration, {
            authorizer: this.userPoolsAuthorizer,
            authorizationType: apigw.AuthorizationType.COGNITO,
            methodResponses: [{
                    statusCode: "200",
                    responseParameters: {
                        'method.response.header.X-Requested-With': true,
                        'method.response.header.Content-Type': true,
                        'method.response.header.Access-Control-Allow-Origin': true,
                        'method.response.header.Access-Control-Allow-Headers': true,
                        'method.response.header.Access-Control-Allow-Methods': true
                    },
                }]
        });
    }
}
exports.JpApi = JpApi;
/* Useful:
  https://awscdk.io/packages/@aws-cdk/aws-apigateway@1.25.0/#/
  https://docs.aws.amazon.com/cdk/api/latest/docs/@aws-cdk_aws-lambda.FunctionOptions.html

  ApiGateway: V2 has experimental features and is for HTTP API's currently.

    - Can be configured to use resources across stacks to avoid 500-resource
    stack limit.

  CORS: CORS does not work out-the-box as it's supposed to. Requires manual setting
  of headers on methods.

  Assets: This construct takes local dirs and uploads them to S3. Essentially
 syntactic sugar for using an S3 bucket in your app.

  Bootstrap: Call only once. Sets up CDK toolkit for the AWS account.

  Destinations: Can set Lambda destinations directly in FunctionProps,
 onSuccess and onFailure.

  Docker: First-class Docker support. Assets allows intra-container commands
 in a build step. Most constructs and SDK's include Docker integration.

  Notifications: SNS has deep integrations with multiple Construct libraries,
 such as S3. Eg: Instantiate an S3 topic and it can be passed into addEventNotification()
 on a bucket.

  Singleton: Ensures lambda only uploaded once, works through a uuid.
 */
/* Gotchas:

  Naming: The CDK will auto-prepend the Stack name onto resources. Be careful
 hard-coding names or ARN's of actively-developed CDK-managed resources.

  Roles: Autogenerated role if no role set. Provided roles will NOT automatically
 get permissions to invoke the lambda. Again, manual management is required.

  Versioning: Easy to set $LATEST version when building or inlining code,
 but CDK runtime is unable to validate S3 deployments. Manual version management
 necessary in this case.

  Tips:
    - There is a significant amount of hidden, hard-to-predict complexity.
    A reason for this is that the CDK API's surface area is deceivingly large.
    For example, new lambda.Function has only 3 parameters but one of those parameters
    has dozens of its own parameters -- and many of those take their own parameters.

      CDK provides a tremendous amount of wrapper classes and convenience methods for
    developer experience. For example, the S3 Bucket has options for setting notifications
    on it but SNS also has a method to set notifications on an S3 Bucket. Then there is also
    an s3-notifications SDK.

      Thus, in many cases there is an O(N^2) number of possible ways to structure your app
    and it is unclear what the best practice is, if the BP has even established yet. The resources
    are still quite new. The constructs can interact with each other in unforeseeable ways. Be cautious
    with unit testing and manual testing inside staging environments.

    - Can deploy stacks separately, with --all, or integrate into one single enclosing Stack.
 */ 
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiSnBBcGkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyJKcEFwaS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSxzREFBOEM7QUFFOUMscUNBQXFDO0FBRXJDLGlEQUFpRDtBQUNqRCw0REFBNkU7QUFDN0UsMkNBQTBEO0FBRTFELDhDQUFzQztBQVN0QyxNQUFhLEtBQU0sU0FBUSxHQUFHLENBQUMsU0FBUztJQU90QyxZQUFZLEtBQW9CLEVBQUUsRUFBVSxFQUFFLEtBQXdCO1FBQ3BFLEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxDQUFDLENBQUM7UUFFakIsSUFBSSxDQUFDLE9BQU8sR0FBRyxJQUFJLEtBQUssQ0FBQyxPQUFPLENBQUMsSUFBSSxFQUFFLGNBQWMsRUFBRTtZQUNyRCxXQUFXLEVBQUUseUJBQXlCO1NBQ3ZDLENBQUMsQ0FBQztRQUVILE1BQU0sUUFBUSxHQUFHLHNCQUFRLENBQUMsZUFBZSxDQUFDLElBQUksRUFBRSxpQkFBaUIsRUFBRSx5Q0FBNkIsQ0FBYSxDQUFDO1FBRTlHLElBQUksQ0FBQyxtQkFBbUIsR0FBRyxJQUFJLEtBQUssQ0FBQywwQkFBMEIsQ0FBQyxJQUFJLEVBQUUsb0JBQW9CLEVBQUU7WUFDMUYsZ0JBQWdCLEVBQUUsQ0FBQyxRQUFRLENBQUM7U0FDN0IsQ0FBQyxDQUFBO1FBRUYsNEJBQTRCO1FBQzVCLE1BQU0sd0JBQXdCLEdBQUcsSUFBSSxLQUFLLENBQUMsaUJBQWlCLENBQUMsS0FBSyxDQUFDLFVBQVUsRUFBRTtZQUM3RSxLQUFLLEVBQUUsS0FBSztZQUNaLGVBQWUsRUFBRSxJQUFJO1lBQ3JCLG1CQUFtQixFQUFFLG9DQUFtQixDQUFDLGFBQWE7WUFDdEQsZ0JBQWdCLEVBQUUsRUFBQyxrQkFBa0IsRUFBRSwrREFBK0Q7b0JBQ2hHLDJDQUEyQztvQkFDM0MsbUVBQW1FLEVBQUM7WUFDMUUsb0JBQW9CLEVBQUU7Z0JBQ3BCO29CQUVFLGtFQUFrRTtvQkFDbEUsVUFBVSxFQUFFLEtBQUs7b0JB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S0FBSztvQkFDakIsa0JBQWtCLEVBQUU7d0JBQ2xCLHlDQUF5QyxFQUFFLElBQUk7d0JBQy9DLHFDQUFxQyxFQUFFLElBQUk7d0JBQzNDLG9EQUFvRCxFQUFFLElBQUk7d0JBQzFELHFEQUFxRCxFQUFFLElBQUk7d0JBQzNELHFEQUFxRCxFQUFFLElBQUk7cUJBQzVEO2lCQUNGLENBQUM7U0FDSCxDQUFDLENBQUE7SUFFSixDQUFDO0NBS0Y7QUE5UEQsc0JBOFBDO0FBRUQ7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7R0E0Qkc7QUFFSDs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7R0E2QkciLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQge1VzZXJQb29sfSBmcm9tICdAYXdzLWNkay9hd3MtY29nbml0byc7XG5pbXBvcnQgKiBhcyBsYW1iZGEgZnJvbSAnQGF3cy1jZGsvYXdzLWxhbWJkYSc7XG5pbXBvcnQgKiBhcyBjZGsgZnJvbSAnQGF3cy1jZGsvY29yZSc7XG5pbXBvcnQge1N0YWNrUHJvcHN9IGZyb20gJ0Bhd3MtY2RrL2NvcmUnO1xuaW1wb3J0ICogYXMgYXBpZ3cgZnJvbSAnQGF3cy1jZGsvYXdzLWFwaWdhdGV3YXknO1xuaW1wb3J0IHtJbnRlZ3JhdGlvblR5cGUsIFBhc3N0aHJvdWdoQmVoYXZpb3J9IGZyb20gJ0Bhd3MtY2RrL2F3cy1hcGlnYXRld2F5JztcbmltcG9ydCB7REVGQVVMVF9DT0dOSVRPX1VTRVJfUE9PTF9BUk59IGZyb20gXCIuL2NvbnN0YW50c1wiO1xuaW1wb3J0IHtTdGF0ZU1hY2hpbmV9IGZyb20gXCJAYXdzLWNkay9hd3Mtc3RlcGZ1bmN0aW9uc1wiO1xuaW1wb3J0IHtSb2xlfSBmcm9tIFwiQGF3cy1jZGsvYXdzLWlhbVwiO1xuXG5leHBvcnQgaW50ZXJmYWNlIFBsYWlkTGlua0FwaVByb3BzIHtcbiAgbGlua0xhbWJkYTogbGFtYmRhLkZ1bmN0aW9uO1xuICBpdGVtTGFtYmRhOiBsYW1iZGEuRnVuY3Rpb247XG4gIGdldFRyYW5zYWN0aW9uc0xhbWJkYTogbGFtYmRhLkZ1bmN0aW9uO1xuICBwdWxsVHJhbnNhY3Rpb25zTWFjaGluZTogU3RhdGVNYWNoaW5lXG59XG5cbmV4cG9ydCBjbGFzcyBKcEFwaSBleHRlbmRzIGNkay5Db25zdHJ1Y3Qge1xuXG4gIC8vIChPcHRpb25hbCkgU2V0IGluc3RhbmNlIHZhcnMuIEkgcHJlZmVyIHRvIGRvIHRoaXMgdG8gbWFrZSByZWFkaW5nIHRoZXNlXG4gIC8vIHN0YWNrcyBlYXNpZXIuIEFjY2VzcyBtb2RpZmllciBkb2VzIG5vdCBhZmZlY3QgY3JlYXRpb24gZGV0YWlscy5cbiAgcHJpdmF0ZSByZWFkb25seSB1c2VyUG9vbHNBdXRob3JpemVyOiBhcGlndy5Db2duaXRvVXNlclBvb2xzQXV0aG9yaXplclxuICBwdWJsaWMgcmVzdEFwaTogYXBpZ3cuUmVzdEFwaTtcblxuICBjb25zdHJ1Y3RvcihzY29wZTogY2RrLkNvbnN0cnVjdCwgaWQ6IHN0cmluZywgcHJvcHM6IFBsYWlkTGlua0FwaVByb3BzKSB7XG4gICAgc3VwZXIoc2NvcGUsIGlkKTtcblxuICAgIHRoaXMucmVzdEFwaSA9IG5ldyBhcGlndy5SZXN0QXBpKHRoaXMsICdQbGFpZExpbmtBcGknLCB7XG4gICAgICBkZXNjcmlwdGlvbjogXCJUcmFuc2FjdGlvbiBTZXJ2aWNlIEFQSVwiLFxuICAgIH0pO1xuXG4gICAgY29uc3QgdXNlclBvb2wgPSBVc2VyUG9vbC5mcm9tVXNlclBvb2xBcm4odGhpcywgJ0RlZmF1bHRVc2VyUG9vbCcsIERFRkFVTFRfQ09HTklUT19VU0VSX1BPT0xfQVJOKSBhcyBVc2VyUG9vbDtcblxuICAgIHRoaXMudXNlclBvb2xzQXV0aG9yaXplciA9IG5ldyBhcGlndy5Db2duaXRvVXNlclBvb2xzQXV0aG9yaXplcih0aGlzLCAnVXNlclBvb2xBdXRob3JpemVyJywge1xuICAgICAgY29nbml0b1VzZXJQb29sczogW3VzZXJQb29sXVxuICAgIH0pXG5cbiAgICAvLyBJbnRlZ3JhdGUgZm9yIGxpbmtUb2tlbnM6XG4gICAgY29uc3QgcG9zdExpbmtUb2tlbkludGVncmF0aW9uID0gbmV3IGFwaWd3LkxhbWJkYUludGVncmF0aW9uKHByb3BzLmxpbmtMYW1iZGEsIHtcbiAgICAgIHByb3h5OiBmYWxzZSxcbiAgICAgIGFsbG93VGVzdEludm9rZTogdHJ1ZSxcbiAgICAgIHBhc3N0aHJvdWdoQmVoYXZpb3I6IFBhc3N0aHJvdWdoQmVoYXZpb3IuV0hFTl9OT19NQVRDSCxcbiAgICAgIHJlcXVlc3RUZW1wbGF0ZXM6IHtcImFwcGxpY2F0aW9uL2pzb25cIjogJ3tcInVzZXJcIiA6IFwiJGNvbnRleHQuYXV0aG9yaXplci5jbGFpbXNbXFwnY29nbml0bzp1c2VybmFtZVxcJ11cIiwnICtcbiAgICAgICAgICAgICdcInByb2R1Y3RzXCIgOiAkaW5wdXQuanNvbihcXCckLnByb2R1Y3RzXFwnKSwnICtcbiAgICAgICAgICAgICdcIndlYmhvb2tcIiA6IFwiJHV0aWwuZXNjYXBlSmF2YVNjcmlwdCgkaW5wdXQuanNvbihcXCckLndlYmhvb2tcXCcpKVwifSd9LFxuICAgICAgaW50ZWdyYXRpb25SZXNwb25zZXM6IFtcbiAgICAgICAge1xuXG4gICAgICAgICAgLy8gU3VjY2Vzc2Z1bCByZXNwb25zZSBmcm9tIHRoZSBMYW1iZGEgZnVuY3Rpb24sIG5vIGZpbHRlciBkZWZpbmVkXG4gICAgICAgICAgc3RhdHVzQ29kZTogXCIyMDBcIixcbiAgICAgICAgICByZXNwb25zZVRlbXBsYXRlczoge1xuICAgICAgICAgICAgLy8gQ2hlY2sgaHR0cHM6Ly9kb2NzLmF3cy5hbWF6b24uY29tL2FwaWdhdGV3YXkvbGF0ZXN0L2RldmVsb3Blcmd1aWRlL2FwaS1nYXRld2F5LW1hcHBpbmctdGVtcGxhdGUtcmVmZXJlbmNlLmh0bWxcbiAgICAgICAgICAgICdhcHBsaWNhdGlvbi9qc29uJzogJ2lucHV0LmJvZHknIC8vIEp1c3QgcmV0dXJuIHRoZSBhY2Nlc3NUb2tlbiBzdHJpbmcuXG4gICAgICAgICAgfSxcbiAgICAgICAgICByZXNwb25zZVBhcmFtZXRlcnM6IHtcbiAgICAgICAgICAgIC8vIFdlIGNhbiBtYXAgcmVzcG9uc2UgcGFyYW1ldGVyc1xuICAgICAgICAgICAgLy8gLSBEZXN0aW5hdGlvbiBwYXJhbWV0ZXJzICh0aGUga2V5KSBhcmUgdGhlIHJlc3BvbnNlIHBhcmFtZXRlcnMgKHVzZWQgaW4gbWFwcGluZ3MpXG4gICAgICAgICAgICAvLyAtIFNvdXJjZSBwYXJhbWV0ZXJzICh0aGUgdmFsdWUpIGFyZSB0aGUgaW50ZWdyYXRpb24gcmVzcG9uc2UgcGFyYW1ldGVycyBvciBleHByZXNzaW9uc1xuICAgICAgICAgICAgLy8gRG8gdGhpcyBmb3IgQ09SUy5cbiAgICAgICAgICAgIC8vIFdBUk5JTkc6IERPRVMgTk9UIFNVUFBPUlQgQUxMIEhFQURFUlMuXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5YLVJlcXVlc3RlZC1XaXRoJzogXCInKidcIixcbiAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkNvbnRlbnQtVHlwZSc6IFwiJ2FwcGxpY2F0aW9uL2pzb24nXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1PcmlnaW4nOiBcIidodHRwOi8vbG9jYWxob3N0OjMwMDAnXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1IZWFkZXJzJzogXCInQ29udGVudC1UeXBlLFgtQW16LURhdGUsQXV0aG9yaXphdGlvbixYLUFwaS1LZXkseC1yZXF1ZXN0ZWQtd2l0aCdcIixcbiAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LU1ldGhvZHMnOiBcIidQT1NULEdFVCxPUFRJT05TJ1wiXG4gICAgICAgICAgfVxuICAgICAgICB9LFxuICAgICAgXSxcbiAgICB9KTtcbiAgICBjb25zdCBsaW5rUmVzb3VyY2UgPSB0aGlzLnJlc3RBcGkucm9vdC5hZGRSZXNvdXJjZShcImxpbmt0b2tlblwiKTtcbiAgICBsaW5rUmVzb3VyY2UuYWRkTWV0aG9kKFwiUE9TVFwiLCBwb3N0TGlua1Rva2VuSW50ZWdyYXRpb24sIHtcbiAgICAgIGF1dGhvcml6ZXI6IHRoaXMudXNlclBvb2xzQXV0aG9yaXplcixcbiAgICAgIGF1dGhvcml6YXRpb25UeXBlOiBhcGlndy5BdXRob3JpemF0aW9uVHlwZS5DT0dOSVRPLFxuICAgICAgbWV0aG9kUmVzcG9uc2VzOiBbe1xuICAgICAgICBzdGF0dXNDb2RlOiBcIjIwMFwiLFxuICAgICAgICByZXNwb25zZVBhcmFtZXRlcnM6IHtcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5YLVJlcXVlc3RlZC1XaXRoJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5Db250ZW50LVR5cGUnOiB0cnVlLFxuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LU9yaWdpbic6IHRydWUsXG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctSGVhZGVycyc6IHRydWUsXG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctTWV0aG9kcyc6IHRydWVcbiAgICAgICAgfVxuICAgICAgfV1cbiAgICB9KTtcbiAgICBsaW5rUmVzb3VyY2UuYWRkQ29yc1ByZWZsaWdodCh7XG4gICAgICBhbGxvd09yaWdpbnM6IFsnaHR0cDovL2xvY2FsaG9zdDozMDAwJ10sXG4gICAgICBhbGxvd01ldGhvZHM6IFsnT1BUSU9OUycsICdHRVQnLCAnUE9TVCddLFxuICAgICAgYWxsb3dDcmVkZW50aWFsczogdHJ1ZSxcbiAgICAgIGFsbG93SGVhZGVyczogYXBpZ3cuQ29ycy5ERUZBVUxUX0hFQURFUlNcbiAgICB9KVxuXG4gICAgLy8gSW50ZWdyYXRlIHRoZSBwb3N0SXRlbSBsYW1iZGE6XG4gICAgY29uc3QgcG9zdEl0ZW1JbnRlZ3JhdGlvbiA9IG5ldyBhcGlndy5MYW1iZGFJbnRlZ3JhdGlvbihwcm9wcy5pdGVtTGFtYmRhLCB7XG4gICAgICBwcm94eTogZmFsc2UsXG4gICAgICBhbGxvd1Rlc3RJbnZva2U6IHRydWUsXG4gICAgICByZXF1ZXN0VGVtcGxhdGVzOiB7XCJhcHBsaWNhdGlvbi9qc29uXCI6ICd7XCJ1c2VyXCIgOiBcIiRjb250ZXh0LmF1dGhvcml6ZXIuY2xhaW1zW1xcJ2NvZ25pdG86dXNlcm5hbWVcXCddXCIsJyArXG4gICAgICAgICAgJ1wiYXZhaWxhYmxlUHJvZHVjdHNcIiA6ICRpbnB1dC5qc29uKFxcJyQuYXZhaWxhYmxlUHJvZHVjdHNcXCcpLCcgK1xuICAgICAgICAgICdcInB1YmxpY1Rva2VuXCIgOiAkaW5wdXQuanNvbihcXCckLnB1YmxpY1Rva2VuXFwnKSwnICtcbiAgICAgICAgICAgICdcImluc3RpdHV0aW9uSWRcIjogJGlucHV0Lmpzb24oXFwnJC5pbnN0aXR1dGlvbklkXFwnKSwnICtcbiAgICAgICAgICAgICdcImFjY291bnRzXCI6ICRpbnB1dC5qc29uKFxcJyQuYWNjb3VudHNcXCcpLCcgK1xuICAgICAgICAgICAgJ1wiZGF0ZUNyZWF0ZWRcIjogJGlucHV0Lmpzb24oXFwnJC5kYXRlQ3JlYXRlZFxcJyksJyArXG4gICAgICAgICAgICAnXCJtZXRhRGF0YVwiIDogJGlucHV0Lmpzb24oXFwnJC5tZXRhRGF0YVxcJyksJyArXG4gICAgICAgICAgICAnXCJ3ZWJob29rXCIgOiAkaW5wdXQuanNvbihcXCckLndlYmhvb2tcXCcpfSdcbiAgICAgIH0sXG4gICAgICBwYXNzdGhyb3VnaEJlaGF2aW9yOiBQYXNzdGhyb3VnaEJlaGF2aW9yLldIRU5fTk9fTUFUQ0gsIGludGVncmF0aW9uUmVzcG9uc2VzOiBbXG4gICAgICAgIHtcblxuICAgICAgICAgIC8vIFN1Y2Nlc3NmdWwgcmVzcG9uc2UgZnJvbSB0aGUgTGFtYmRhIGZ1bmN0aW9uLCBubyBmaWx0ZXIgZGVmaW5lZFxuICAgICAgICAgIHN0YXR1c0NvZGU6IFwiMjAwXCIsXG4gICAgICAgICAgcmVzcG9uc2VUZW1wbGF0ZXM6IHtcbiAgICAgICAgICAgIC8vIENoZWNrIGh0dHBzOi8vZG9jcy5hd3MuYW1hem9uLmNvbS9hcGlnYXRld2F5L2xhdGVzdC9kZXZlbG9wZXJndWlkZS9hcGktZ2F0ZXdheS1tYXBwaW5nLXRlbXBsYXRlLXJlZmVyZW5jZS5odG1sXG4gICAgICAgICAgICAnYXBwbGljYXRpb24vanNvbic6IEpTT04uc3RyaW5naWZ5KCckdXRpbC5lc2NhcGVKYXZhU2NyaXB0KCRpbnB1dC5ib2R5KScpIC8vIEp1c3QgcmV0dXJuIHRoZSBhY2Nlc3NUb2tlbiBzdHJpbmcuXG4gICAgICAgICAgfSxcbiAgICAgICAgICByZXNwb25zZVBhcmFtZXRlcnM6IHtcbiAgICAgICAgICAgIC8vIFdlIGNhbiBtYXAgcmVzcG9uc2UgcGFyYW1ldGVyc1xuICAgICAgICAgICAgLy8gLSBEZXN0aW5hdGlvbiBwYXJhbWV0ZXJzICh0aGUga2V5KSBhcmUgdGhlIHJlc3BvbnNlIHBhcmFtZXRlcnMgKHVzZWQgaW4gbWFwcGluZ3MpXG4gICAgICAgICAgICAvLyAtIFNvdXJjZSBwYXJhbWV0ZXJzICh0aGUgdmFsdWUpIGFyZSB0aGUgaW50ZWdyYXRpb24gcmVzcG9uc2UgcGFyYW1ldGVycyBvciBleHByZXNzaW9uc1xuICAgICAgICAgICAgLy8gRG8gdGhpcyBmb3IgQ09SUy5cbiAgICAgICAgICAgIC8vIFdBUk5JTkc6IERPRVMgTk9UIFNVUFBPUlQgQUxMIEhFQURFUlMuXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5YLVJlcXVlc3RlZC1XaXRoJzogXCInKidcIixcbiAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkNvbnRlbnQtVHlwZSc6IFwiJ2FwcGxpY2F0aW9uL2pzb24nXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1PcmlnaW4nOiBcIidodHRwOi8vbG9jYWxob3N0OjMwMDAnXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1IZWFkZXJzJzogXCInQ29udGVudC1UeXBlLFgtQW16LURhdGUsQXV0aG9yaXphdGlvbixYLUFwaS1LZXkseC1yZXF1ZXN0ZWQtd2l0aCdcIixcbiAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LU1ldGhvZHMnOiBcIidQT1NULEdFVCxPUFRJT05TJ1wiXG4gICAgICAgICAgfVxuICAgICAgICB9LFxuICAgICAgXSxcbiAgICB9KVxuICAgIGNvbnN0IGl0ZW1SZXNvdXJjZSA9IHRoaXMucmVzdEFwaS5yb290LmFkZFJlc291cmNlKFwiaXRlbXNcIilcbiAgICBpdGVtUmVzb3VyY2UuYWRkTWV0aG9kKCdQT1NUJywgcG9zdEl0ZW1JbnRlZ3JhdGlvbiwge1xuICAgICAgYXV0aG9yaXplcjogdGhpcy51c2VyUG9vbHNBdXRob3JpemVyLFxuICAgICAgYXV0aG9yaXphdGlvblR5cGU6IGFwaWd3LkF1dGhvcml6YXRpb25UeXBlLkNPR05JVE8sXG4gICAgICBtZXRob2RSZXNwb25zZXM6IFt7XG4gICAgICAgIHN0YXR1c0NvZGU6IFwiMjAwXCIsXG4gICAgICAgIHJlc3BvbnNlUGFyYW1ldGVyczoge1xuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLlgtUmVxdWVzdGVkLVdpdGgnOiB0cnVlLFxuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkNvbnRlbnQtVHlwZSc6IHRydWUsXG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctT3JpZ2luJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1IZWFkZXJzJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1NZXRob2RzJzogdHJ1ZVxuICAgICAgICB9XG4gICAgICB9XVxuICAgIH0pXG4gICAgaXRlbVJlc291cmNlLmFkZENvcnNQcmVmbGlnaHQoe1xuICAgICAgYWxsb3dPcmlnaW5zOiBbJ2h0dHA6Ly9sb2NhbGhvc3Q6MzAwMCddLFxuICAgICAgYWxsb3dNZXRob2RzOiBbJ09QVElPTlMnLCAnR0VUJywgJ1BPU1QnXSxcbiAgICAgIGFsbG93Q3JlZGVudGlhbHM6IHRydWUsXG4gICAgICBhbGxvd0hlYWRlcnM6IGFwaWd3LkNvcnMuREVGQVVMVF9IRUFERVJTXG4gICAgfSlcblxuICAgIC8vIEludGVncmF0ZSBnZXRUcmFuc2FjdGlvbnMgbGFtYmRhOlxuICAgIGNvbnN0IGdldFRyYW5zYWN0aW9uc0ludGVncmF0aW9uID0gbmV3IGFwaWd3LkxhbWJkYUludGVncmF0aW9uKHByb3BzLmdldFRyYW5zYWN0aW9uc0xhbWJkYSwge1xuICAgICAgcHJveHk6IGZhbHNlLFxuICAgICAgYWxsb3dUZXN0SW52b2tlOiB0cnVlLFxuICAgICAgcGFzc3Rocm91Z2hCZWhhdmlvcjogUGFzc3Rocm91Z2hCZWhhdmlvci5XSEVOX05PX01BVENILFxuICAgICAgcmVxdWVzdFBhcmFtZXRlcnM6IHtcbiAgICAgICAgJ2ludGVncmF0aW9uLnJlcXVlc3QucXVlcnlzdHJpbmcudXNlcic6ICdtZXRob2QucmVxdWVzdC5xdWVyeXN0cmluZy51c2VyJyxcbiAgICAgICAgJ2ludGVncmF0aW9uLnJlcXVlc3QucXVlcnlzdHJpbmcuc3RhcnREYXRlJzogJ21ldGhvZC5yZXF1ZXN0LnF1ZXJ5c3RyaW5nLnN0YXJ0RGF0ZSdcbiAgICAgIH0sXG4gICAgICByZXF1ZXN0VGVtcGxhdGVzOiB7XCJhcHBsaWNhdGlvbi9qc29uXCI6ICd7XCJ1c2VyXCIgOiBcIiRjb250ZXh0LmF1dGhvcml6ZXIuY2xhaW1zW1xcJ2NvZ25pdG86dXNlcm5hbWVcXCddXCIsXFxuJyArXG4gICAgICAgICAgICAnXCJzdGFydERhdGVcIiA6IFwiJHV0aWwuZXNjYXBlSmF2YVNjcmlwdCgkaW5wdXQucGFyYW1zKFxcJ3N0YXJ0RGF0ZVxcJykpXCJ9J30sXG4gICAgICBpbnRlZ3JhdGlvblJlc3BvbnNlczogW1xuICAgICAgICB7XG5cbiAgICAgICAgICAvLyBTdWNjZXNzZnVsIHJlc3BvbnNlIGZyb20gdGhlIExhbWJkYSBmdW5jdGlvbiwgbm8gZmlsdGVyIGRlZmluZWRcbiAgICAgICAgICBzdGF0dXNDb2RlOiBcIjIwMFwiLFxuICAgICAgICAgIHJlc3BvbnNlVGVtcGxhdGVzOiB7XG4gICAgICAgICAgICAvLyBDaGVjayBodHRwczovL2RvY3MuYXdzLmFtYXpvbi5jb20vYXBpZ2F0ZXdheS9sYXRlc3QvZGV2ZWxvcGVyZ3VpZGUvYXBpLWdhdGV3YXktbWFwcGluZy10ZW1wbGF0ZS1yZWZlcmVuY2UuaHRtbFxuICAgICAgICAgICAgJ2FwcGxpY2F0aW9uL2pzb24nOiBKU09OLnN0cmluZ2lmeSgnJHV0aWwuZXNjYXBlSmF2YVNjcmlwdCgkaW5wdXQuYm9keSknKVxuICAgICAgICAgIH0sXG4gICAgICAgICAgcmVzcG9uc2VQYXJhbWV0ZXJzOiB7XG4gICAgICAgICAgICAvLyBXZSBjYW4gbWFwIHJlc3BvbnNlIHBhcmFtZXRlcnNcbiAgICAgICAgICAgIC8vIC0gRGVzdGluYXRpb24gcGFyYW1ldGVycyAodGhlIGtleSkgYXJlIHRoZSByZXNwb25zZSBwYXJhbWV0ZXJzICh1c2VkIGluIG1hcHBpbmdzKVxuICAgICAgICAgICAgLy8gLSBTb3VyY2UgcGFyYW1ldGVycyAodGhlIHZhbHVlKSBhcmUgdGhlIGludGVncmF0aW9uIHJlc3BvbnNlIHBhcmFtZXRlcnMgb3IgZXhwcmVzc2lvbnNcbiAgICAgICAgICAgIC8vIERvIHRoaXMgZm9yIENPUlMuXG4gICAgICAgICAgICAvLyBXQVJOSU5HOiBET0VTIE5PVCBTVVBQT1JUIEFMTCBIRUFERVJTLlxuICAgICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuWC1SZXF1ZXN0ZWQtV2l0aCc6IFwiJyonXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5Db250ZW50LVR5cGUnOiBcIidhcHBsaWNhdGlvbi9qc29uJ1wiLFxuICAgICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctT3JpZ2luJzogXCInaHR0cDovL2xvY2FsaG9zdDozMDAwJ1wiLFxuICAgICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctSGVhZGVycyc6IFwiJ0NvbnRlbnQtVHlwZSxYLUFtei1EYXRlLEF1dGhvcml6YXRpb24sWC1BcGktS2V5LHgtcmVxdWVzdGVkLXdpdGgnXCIsXG4gICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1NZXRob2RzJzogXCInUE9TVCxHRVQsT1BUSU9OUydcIlxuICAgICAgICAgIH1cbiAgICAgICAgfSxcbiAgICAgIF0sXG4gICAgfSlcbiAgICBjb25zdCB0cmFuc2FjdGlvbnNSZXNvdXJjZSA9IHRoaXMucmVzdEFwaS5yb290LmFkZFJlc291cmNlKFwidHJhbnNhY3Rpb25zXCIpXG4gICAgdHJhbnNhY3Rpb25zUmVzb3VyY2UuYWRkTWV0aG9kKCdHRVQnLCBnZXRUcmFuc2FjdGlvbnNJbnRlZ3JhdGlvbiwge1xuICAgICAgYXV0aG9yaXplcjogdGhpcy51c2VyUG9vbHNBdXRob3JpemVyLFxuICAgICAgYXV0aG9yaXphdGlvblR5cGU6IGFwaWd3LkF1dGhvcml6YXRpb25UeXBlLkNPR05JVE8sXG4gICAgICByZXF1ZXN0UGFyYW1ldGVyczoge1xuICAgICAgICAnbWV0aG9kLnJlcXVlc3QucXVlcnlzdHJpbmcudXNlcic6IGZhbHNlLFxuICAgICAgICAnbWV0aG9kLnJlcXVlc3QucXVlcnlzdHJpbmcuc3RhcnREYXRlJzogZmFsc2VcbiAgICAgIH0sXG4gICAgICBtZXRob2RSZXNwb25zZXM6IFt7XG4gICAgICAgIHN0YXR1c0NvZGU6IFwiMjAwXCIsXG4gICAgICAgIHJlc3BvbnNlUGFyYW1ldGVyczoge1xuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLlgtUmVxdWVzdGVkLVdpdGgnOiB0cnVlLFxuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkNvbnRlbnQtVHlwZSc6IHRydWUsXG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQWNjZXNzLUNvbnRyb2wtQWxsb3ctT3JpZ2luJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1IZWFkZXJzJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1NZXRob2RzJzogdHJ1ZVxuICAgICAgICB9LFxuICAgICAgfV1cbiAgICB9KVxuICAgIHRyYW5zYWN0aW9uc1Jlc291cmNlLmFkZENvcnNQcmVmbGlnaHQoe1xuICAgICAgYWxsb3dPcmlnaW5zOiBbJ2h0dHA6Ly9sb2NhbGhvc3Q6MzAwMCddLFxuICAgICAgYWxsb3dNZXRob2RzOiBbJ09QVElPTlMnLCAnR0VUJywgJ1BPU1QnXSxcbiAgICAgIGFsbG93Q3JlZGVudGlhbHM6IHRydWUsXG4gICAgICBhbGxvd0hlYWRlcnM6IGFwaWd3LkNvcnMuREVGQVVMVF9IRUFERVJTXG4gICAgfSlcblxuICAgIC8vIEludGVncmF0ZSBwb3N0VHJhbnNhY3Rpb25zIGxhbWJkYTpcbiAgICBjb25zdCBwb3N0VHJhbnNhY3Rpb25zSW50ZWdyYXRpb24gPSBuZXcgYXBpZ3cuQXdzSW50ZWdyYXRpb24oe1xuICAgICAgc2VydmljZTogXCJzdGF0ZXNcIixcbiAgICAgIGFjdGlvbjogXCJTdGFydEV4ZWN1dGlvblwiLFxuICAgICAgaW50ZWdyYXRpb25IdHRwTWV0aG9kOiBcIlBPU1RcIixcbiAgICAgIG9wdGlvbnM6IHtcbiAgICAgICAgY3JlZGVudGlhbHNSb2xlOiBSb2xlLmZyb21Sb2xlQXJuKHRoaXMsXG4gICAgICAgICAgICAnU3RlcEZ1bmN0aW9uc0FQSVJvbGUnLFxuICAgICAgICAgICAgXCJhcm46YXdzOmlhbTo6Mzk3MjUwMTgyNjA5OnJvbGUvSlBBcGlHYXRld2F5VG9TdGVwRnVuY3Rpb25zXCIpLFxuICAgICAgICBpbnRlZ3JhdGlvblJlc3BvbnNlczogW1xuICAgICAgICAgIHtcblxuICAgICAgICAgICAgLy8gU3VjY2Vzc2Z1bCByZXNwb25zZSBmcm9tIHRoZSBMYW1iZGEgZnVuY3Rpb24sIG5vIGZpbHRlciBkZWZpbmVkXG4gICAgICAgICAgICBzdGF0dXNDb2RlOiBcIjIwMFwiLFxuICAgICAgICAgICAgcmVzcG9uc2VUZW1wbGF0ZXM6IHtcbiAgICAgICAgICAgICAgLy8gQ2hlY2sgaHR0cHM6Ly9kb2NzLmF3cy5hbWF6b24uY29tL2FwaWdhdGV3YXkvbGF0ZXN0L2RldmVsb3Blcmd1aWRlL2FwaS1nYXRld2F5LW1hcHBpbmctdGVtcGxhdGUtcmVmZXJlbmNlLmh0bWxcbiAgICAgICAgICAgICAgJ2FwcGxpY2F0aW9uL2pzb24nOiBKU09OLnN0cmluZ2lmeSgnJHV0aWwuZXNjYXBlSmF2YVNjcmlwdCgkaW5wdXQuYm9keSknKVxuICAgICAgICAgICAgfSxcbiAgICAgICAgICAgIHJlc3BvbnNlUGFyYW1ldGVyczoge1xuICAgICAgICAgICAgICAvLyBXZSBjYW4gbWFwIHJlc3BvbnNlIHBhcmFtZXRlcnNcbiAgICAgICAgICAgICAgLy8gLSBEZXN0aW5hdGlvbiBwYXJhbWV0ZXJzICh0aGUga2V5KSBhcmUgdGhlIHJlc3BvbnNlIHBhcmFtZXRlcnMgKHVzZWQgaW4gbWFwcGluZ3MpXG4gICAgICAgICAgICAgIC8vIC0gU291cmNlIHBhcmFtZXRlcnMgKHRoZSB2YWx1ZSkgYXJlIHRoZSBpbnRlZ3JhdGlvbiByZXNwb25zZSBwYXJhbWV0ZXJzIG9yIGV4cHJlc3Npb25zXG4gICAgICAgICAgICAgIC8vIERvIHRoaXMgZm9yIENPUlMuXG4gICAgICAgICAgICAgIC8vIFdBUk5JTkc6IERPRVMgTk9UIFNVUFBPUlQgQUxMIEhFQURFUlMuXG4gICAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLlgtUmVxdWVzdGVkLVdpdGgnOiBcIicqJ1wiLFxuICAgICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5Db250ZW50LVR5cGUnOiBcIidhcHBsaWNhdGlvbi9qc29uJ1wiLFxuICAgICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1PcmlnaW4nOiBcIidodHRwOi8vbG9jYWxob3N0OjMwMDAnXCIsXG4gICAgICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LUhlYWRlcnMnOiBcIidDb250ZW50LVR5cGUsWC1BbXotRGF0ZSxBdXRob3JpemF0aW9uLFgtQXBpLUtleSx4LXJlcXVlc3RlZC13aXRoJ1wiLFxuICAgICAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1NZXRob2RzJzogXCInUE9TVCxHRVQsT1BUSU9OUydcIlxuICAgICAgICAgICAgfVxuICAgICAgICAgIH0sXG4gICAgICAgIF0sXG4gICAgICAgIHJlcXVlc3RUZW1wbGF0ZXM6IHtcbiAgICAgICAgICBcImFwcGxpY2F0aW9uL2pzb25cIjogYHtcbiAgICAgICAgICAgICAgXCJpbnB1dFwiOiBcIiR1dGlsLmVzY2FwZUphdmFTY3JpcHQoJGlucHV0LmJvZHkpXCIsXG4gICAgICAgICAgICAgIFwic3RhdGVNYWNoaW5lQXJuXCI6IFwiJHtwcm9wcy5wdWxsVHJhbnNhY3Rpb25zTWFjaGluZS5zdGF0ZU1hY2hpbmVBcm59XCJcbiAgICAgICAgICAgIH1gLFxuICAgICAgICB9LFxuICAgICAgfVxuICAgIH0pXG4gICAgdHJhbnNhY3Rpb25zUmVzb3VyY2UuYWRkTWV0aG9kKCdQT1NUJywgcG9zdFRyYW5zYWN0aW9uc0ludGVncmF0aW9uLCB7XG4gICAgICBhdXRob3JpemVyOiB0aGlzLnVzZXJQb29sc0F1dGhvcml6ZXIsXG4gICAgICBhdXRob3JpemF0aW9uVHlwZTogYXBpZ3cuQXV0aG9yaXphdGlvblR5cGUuQ09HTklUTyxcbiAgICAgIG1ldGhvZFJlc3BvbnNlczogW3tcbiAgICAgICAgc3RhdHVzQ29kZTogXCIyMDBcIixcbiAgICAgICAgcmVzcG9uc2VQYXJhbWV0ZXJzOiB7XG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuWC1SZXF1ZXN0ZWQtV2l0aCc6IHRydWUsXG4gICAgICAgICAgJ21ldGhvZC5yZXNwb25zZS5oZWFkZXIuQ29udGVudC1UeXBlJzogdHJ1ZSxcbiAgICAgICAgICAnbWV0aG9kLnJlc3BvbnNlLmhlYWRlci5BY2Nlc3MtQ29udHJvbC1BbGxvdy1PcmlnaW4nOiB0cnVlLFxuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LUhlYWRlcnMnOiB0cnVlLFxuICAgICAgICAgICdtZXRob2QucmVzcG9uc2UuaGVhZGVyLkFjY2Vzcy1Db250cm9sLUFsbG93LU1ldGhvZHMnOiB0cnVlXG4gICAgICAgIH0sXG4gICAgICB9XVxuICAgIH0pXG5cbiAgfVxuXG5cblxuXG59XG5cbi8qIFVzZWZ1bDpcbiAgaHR0cHM6Ly9hd3NjZGsuaW8vcGFja2FnZXMvQGF3cy1jZGsvYXdzLWFwaWdhdGV3YXlAMS4yNS4wLyMvXG4gIGh0dHBzOi8vZG9jcy5hd3MuYW1hem9uLmNvbS9jZGsvYXBpL2xhdGVzdC9kb2NzL0Bhd3MtY2RrX2F3cy1sYW1iZGEuRnVuY3Rpb25PcHRpb25zLmh0bWxcblxuICBBcGlHYXRld2F5OiBWMiBoYXMgZXhwZXJpbWVudGFsIGZlYXR1cmVzIGFuZCBpcyBmb3IgSFRUUCBBUEkncyBjdXJyZW50bHkuXG5cbiAgICAtIENhbiBiZSBjb25maWd1cmVkIHRvIHVzZSByZXNvdXJjZXMgYWNyb3NzIHN0YWNrcyB0byBhdm9pZCA1MDAtcmVzb3VyY2VcbiAgICBzdGFjayBsaW1pdC5cblxuICBDT1JTOiBDT1JTIGRvZXMgbm90IHdvcmsgb3V0LXRoZS1ib3ggYXMgaXQncyBzdXBwb3NlZCB0by4gUmVxdWlyZXMgbWFudWFsIHNldHRpbmdcbiAgb2YgaGVhZGVycyBvbiBtZXRob2RzLlxuXG4gIEFzc2V0czogVGhpcyBjb25zdHJ1Y3QgdGFrZXMgbG9jYWwgZGlycyBhbmQgdXBsb2FkcyB0aGVtIHRvIFMzLiBFc3NlbnRpYWxseVxuIHN5bnRhY3RpYyBzdWdhciBmb3IgdXNpbmcgYW4gUzMgYnVja2V0IGluIHlvdXIgYXBwLlxuXG4gIEJvb3RzdHJhcDogQ2FsbCBvbmx5IG9uY2UuIFNldHMgdXAgQ0RLIHRvb2xraXQgZm9yIHRoZSBBV1MgYWNjb3VudC5cblxuICBEZXN0aW5hdGlvbnM6IENhbiBzZXQgTGFtYmRhIGRlc3RpbmF0aW9ucyBkaXJlY3RseSBpbiBGdW5jdGlvblByb3BzLFxuIG9uU3VjY2VzcyBhbmQgb25GYWlsdXJlLlxuXG4gIERvY2tlcjogRmlyc3QtY2xhc3MgRG9ja2VyIHN1cHBvcnQuIEFzc2V0cyBhbGxvd3MgaW50cmEtY29udGFpbmVyIGNvbW1hbmRzXG4gaW4gYSBidWlsZCBzdGVwLiBNb3N0IGNvbnN0cnVjdHMgYW5kIFNESydzIGluY2x1ZGUgRG9ja2VyIGludGVncmF0aW9uLlxuXG4gIE5vdGlmaWNhdGlvbnM6IFNOUyBoYXMgZGVlcCBpbnRlZ3JhdGlvbnMgd2l0aCBtdWx0aXBsZSBDb25zdHJ1Y3QgbGlicmFyaWVzLFxuIHN1Y2ggYXMgUzMuIEVnOiBJbnN0YW50aWF0ZSBhbiBTMyB0b3BpYyBhbmQgaXQgY2FuIGJlIHBhc3NlZCBpbnRvIGFkZEV2ZW50Tm90aWZpY2F0aW9uKClcbiBvbiBhIGJ1Y2tldC5cblxuICBTaW5nbGV0b246IEVuc3VyZXMgbGFtYmRhIG9ubHkgdXBsb2FkZWQgb25jZSwgd29ya3MgdGhyb3VnaCBhIHV1aWQuXG4gKi9cblxuLyogR290Y2hhczpcblxuICBOYW1pbmc6IFRoZSBDREsgd2lsbCBhdXRvLXByZXBlbmQgdGhlIFN0YWNrIG5hbWUgb250byByZXNvdXJjZXMuIEJlIGNhcmVmdWxcbiBoYXJkLWNvZGluZyBuYW1lcyBvciBBUk4ncyBvZiBhY3RpdmVseS1kZXZlbG9wZWQgQ0RLLW1hbmFnZWQgcmVzb3VyY2VzLlxuXG4gIFJvbGVzOiBBdXRvZ2VuZXJhdGVkIHJvbGUgaWYgbm8gcm9sZSBzZXQuIFByb3ZpZGVkIHJvbGVzIHdpbGwgTk9UIGF1dG9tYXRpY2FsbHlcbiBnZXQgcGVybWlzc2lvbnMgdG8gaW52b2tlIHRoZSBsYW1iZGEuIEFnYWluLCBtYW51YWwgbWFuYWdlbWVudCBpcyByZXF1aXJlZC5cblxuICBWZXJzaW9uaW5nOiBFYXN5IHRvIHNldCAkTEFURVNUIHZlcnNpb24gd2hlbiBidWlsZGluZyBvciBpbmxpbmluZyBjb2RlLFxuIGJ1dCBDREsgcnVudGltZSBpcyB1bmFibGUgdG8gdmFsaWRhdGUgUzMgZGVwbG95bWVudHMuIE1hbnVhbCB2ZXJzaW9uIG1hbmFnZW1lbnRcbiBuZWNlc3NhcnkgaW4gdGhpcyBjYXNlLlxuXG4gIFRpcHM6XG4gICAgLSBUaGVyZSBpcyBhIHNpZ25pZmljYW50IGFtb3VudCBvZiBoaWRkZW4sIGhhcmQtdG8tcHJlZGljdCBjb21wbGV4aXR5LlxuICAgIEEgcmVhc29uIGZvciB0aGlzIGlzIHRoYXQgdGhlIENESyBBUEkncyBzdXJmYWNlIGFyZWEgaXMgZGVjZWl2aW5nbHkgbGFyZ2UuXG4gICAgRm9yIGV4YW1wbGUsIG5ldyBsYW1iZGEuRnVuY3Rpb24gaGFzIG9ubHkgMyBwYXJhbWV0ZXJzIGJ1dCBvbmUgb2YgdGhvc2UgcGFyYW1ldGVyc1xuICAgIGhhcyBkb3plbnMgb2YgaXRzIG93biBwYXJhbWV0ZXJzIC0tIGFuZCBtYW55IG9mIHRob3NlIHRha2UgdGhlaXIgb3duIHBhcmFtZXRlcnMuXG5cbiAgICAgIENESyBwcm92aWRlcyBhIHRyZW1lbmRvdXMgYW1vdW50IG9mIHdyYXBwZXIgY2xhc3NlcyBhbmQgY29udmVuaWVuY2UgbWV0aG9kcyBmb3JcbiAgICBkZXZlbG9wZXIgZXhwZXJpZW5jZS4gRm9yIGV4YW1wbGUsIHRoZSBTMyBCdWNrZXQgaGFzIG9wdGlvbnMgZm9yIHNldHRpbmcgbm90aWZpY2F0aW9uc1xuICAgIG9uIGl0IGJ1dCBTTlMgYWxzbyBoYXMgYSBtZXRob2QgdG8gc2V0IG5vdGlmaWNhdGlvbnMgb24gYW4gUzMgQnVja2V0LiBUaGVuIHRoZXJlIGlzIGFsc29cbiAgICBhbiBzMy1ub3RpZmljYXRpb25zIFNESy5cblxuICAgICAgVGh1cywgaW4gbWFueSBjYXNlcyB0aGVyZSBpcyBhbiBPKE5eMikgbnVtYmVyIG9mIHBvc3NpYmxlIHdheXMgdG8gc3RydWN0dXJlIHlvdXIgYXBwXG4gICAgYW5kIGl0IGlzIHVuY2xlYXIgd2hhdCB0aGUgYmVzdCBwcmFjdGljZSBpcywgaWYgdGhlIEJQIGhhcyBldmVuIGVzdGFibGlzaGVkIHlldC4gVGhlIHJlc291cmNlc1xuICAgIGFyZSBzdGlsbCBxdWl0ZSBuZXcuIFRoZSBjb25zdHJ1Y3RzIGNhbiBpbnRlcmFjdCB3aXRoIGVhY2ggb3RoZXIgaW4gdW5mb3Jlc2VlYWJsZSB3YXlzLiBCZSBjYXV0aW91c1xuICAgIHdpdGggdW5pdCB0ZXN0aW5nIGFuZCBtYW51YWwgdGVzdGluZyBpbnNpZGUgc3RhZ2luZyBlbnZpcm9ubWVudHMuXG5cbiAgICAtIENhbiBkZXBsb3kgc3RhY2tzIHNlcGFyYXRlbHksIHdpdGggLS1hbGwsIG9yIGludGVncmF0ZSBpbnRvIG9uZSBzaW5nbGUgZW5jbG9zaW5nIFN0YWNrLlxuICovIl19