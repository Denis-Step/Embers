import * as s3 from "@aws-cdk/aws-s3";
import * as lambda from '@aws-cdk/aws-lambda';
import * as cdk from '@aws-cdk/core';
import {Duration, StackProps} from '@aws-cdk/core';
import * as apigw from '@aws-cdk/aws-apigateway';
import {PassthroughBehavior} from '@aws-cdk/aws-apigateway';

export class LambdaStack extends cdk.Stack {

  // (Optional) Set instance vars. I prefer to do this to make reading these
  // stacks easier. Access modifier does not affect creation details.
  public restApi: apigw.RestApi;
  public readonly linkLambda: lambda.Function;
  public readonly itemLambda: lambda.Function;
  private readonly sourceBucket: s3.IBucket;

  constructor(scope: cdk.Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    // An S3 bucket already exists, so we have to use a static method
    // on the Bucket class to avoid instantiating a new bucket.
    this.sourceBucket = s3.Bucket.fromBucketAttributes(this, 'JPSourceBucket', {
      bucketArn: "arn:aws:s3:::javaplaid-lambda/JavaPlaid-1.0.zip"
    })

    // Be careful not to shadow vanilla JS Function type.
    this.linkLambda = new lambda.Function(this, 'LinkTokenLambda', {
      runtime: lambda.Runtime.JAVA_8_CORRETTO,
      handler: "lambda.handlers.CreateLinkTokenHandler",

      // Code supports local build steps, S3 buckets, and inlining.
      code: lambda.Code.fromBucket(this.sourceBucket, "JavaPlaid-1.0.zip"),
      environment: {
        "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
        "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
        "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",

      },
      timeout: Duration.seconds(300),
    })

    this.itemLambda = new lambda.Function(this, 'ItemLambda', {
      runtime: lambda.Runtime.JAVA_8_CORRETTO,
      handler: "lambda.handlers.CreateItemHandler",
      code: lambda.Code.fromBucket(this.sourceBucket, "JavaPlaid-1.0.zip"),
      environment: {
        "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
        "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
        "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
      },
      timeout: Duration.seconds(300)
    });

    // There are great constructs for a Proxy integration. Here,
    // we need multiple resources and so will configure them
    // individually.
    this.restApi = new apigw.RestApi(this, 'PlaidLinkApi');

    // We define the JSON Schema for the transformed valid response
    const linkTokenRequestModel = this.restApi.addModel('RequestModel', {
      contentType: 'application/json',
      modelName: 'ResponseModel',
      schema: {
        schema: apigw.JsonSchemaVersion.DRAFT4,
        title: 'LinkTokenRequest',
        type: apigw.JsonSchemaType.OBJECT,
        properties: {
          user: { type: apigw.JsonSchemaType.STRING },
          products: { type: apigw.JsonSchemaType.STRING }
        }
      }
    });

    /*const validator = this.restApi.addRequestValidator('DefaultValidator', {
      validateRequestBody: true,
      validateRequestParameters: true
    }); */

    // Let's do the integration for linkTokens:
    const postLinkTokenIntegration = new apigw.LambdaIntegration(this.linkLambda, {
      proxy: false,
      allowTestInvoke: true,
      passthroughBehavior: PassthroughBehavior.WHEN_NO_MATCH,
      /*requestParameters: {
        // You can define mapping parameters from your method to your integration
        // - Destination parameters (the key) are the integration parameters (used in mappings)
        // - Source parameters (the value) are the source request parameters or expressions
        // @see: https://docs.aws.amazon.com/apigateway/latest/developerguide/request-response-data-mappings.html
        'integration.request.body': 'method.request.body'
      },
      requestTemplates: {
        // You can define a mapping that will build a payload for your integration, based
        //  on the integration parameters that you have specified
        // Check: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
        'application/json': JSON.stringify( '$util.escapeJavaScript($input.body)' )
      }, */
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