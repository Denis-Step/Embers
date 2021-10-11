import * as s3 from "@aws-cdk/aws-s3";
import * as lambda from '@aws-cdk/aws-lambda';
import * as cdk from '@aws-cdk/core';
import {Duration, StackProps} from '@aws-cdk/core';
import * as apigw from '@aws-cdk/aws-apigateway';
import {PassthroughBehavior} from '@aws-cdk/aws-apigateway';

export interface PlaidLinkApiProps extends StackProps {
  linkLambda: lambda.Function;
  itemLambda: lambda.Function;
}

export class PlaidLinkApi extends cdk.Stack {

  // (Optional) Set instance vars. I prefer to do this to make reading these
  // stacks easier. Access modifier does not affect creation details.
  public restApi: apigw.RestApi;

  constructor(scope: cdk.Construct, id: string, props: PlaidLinkApiProps) {
    super(scope, id, props);

    // There are great constructs for a Proxy integration. Here,
    // we need multiple resources and so will configure them
    // individually.
    this.restApi = new apigw.RestApi(this, 'PlaidLinkApi');

    // Let's do the integration for linkTokens:
    const postLinkTokenIntegration = new apigw.LambdaIntegration(props.linkLambda, {
      proxy: false,
      allowTestInvoke: true,
      passthroughBehavior: PassthroughBehavior.WHEN_NO_MATCH,
      integrationResponses: [
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
            'method.response.header.Content-Type': "'application/json'",
            'method.response.header.Access-Control-Allow-Origin': "'*'",
            'method.response.header.Access-Control-Allow-Headers': "'Content-Type,Authorization'",
          }
        },
      ],
    });
    const linkResource = this.restApi.root.addResource("linktoken");
    linkResource.addMethod('OPTIONS');
    linkResource.addMethod("POST", postLinkTokenIntegration, {
      methodResponses: [{
        statusCode: "200",
        responseParameters: {
          'method.response.header.Content-Type': true,
          'method.response.header.Access-Control-Allow-Origin': true,
          'method.response.header.Access-Control-Allow-Headers': true,
        }
      }]
    });


    // Now we'll integrate the postItem lambda:
    const postItemIntegration = new apigw.LambdaIntegration(props.itemLambda, {
      proxy: false,
      allowTestInvoke: true,
      passthroughBehavior: PassthroughBehavior.WHEN_NO_MATCH, integrationResponses: [
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
            'method.response.header.Content-Type': "'application/json'",
            'method.response.header.Access-Control-Allow-Origin': "'*'",
            'method.response.header.Access-Control-Allow-Headers': "'Content-Type,Authorization'",
          }
        },
      ],
    })
    const itemResource = this.restApi.root.addResource("items")
    itemResource.addMethod('OPTIONS');
    itemResource.addMethod('POST', postItemIntegration, {
      methodResponses: [{
        statusCode: "200",
        responseParameters: {
          'method.response.header.Content-Type': true,
          'method.response.header.Access-Control-Allow-Origin': true,
          'method.response.header.Access-Control-Allow-Headers': true,
        }
      }]
    })
  }

}

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