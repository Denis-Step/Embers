import * as sns from '@aws-cdk/aws-sns';
import * as s3 from "@aws-cdk/aws-s3";
import * as lambda from '@aws-cdk/aws-lambda';
import * as subs from '@aws-cdk/aws-sns-subscriptions';
import * as sqs from '@aws-cdk/aws-sqs';
import * as cdk from '@aws-cdk/core';
import {Runtime} from "inspector";
import {Duration} from "@aws-cdk/core";

export class LambdaStack extends cdk.Stack {

  // (Optional) Set instance vars. I prefer to do this to make reading these
  // stacks easier. Access modifier does not affect creation details.
  private readonly sourceBucket: s3.IBucket;
  public readonly linkLambda: lambda.Function;

  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
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

  }

}

/* Useful:
  https://docs.aws.amazon.com/cdk/api/latest/docs/@aws-cdk_aws-lambda.FunctionOptions.html

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