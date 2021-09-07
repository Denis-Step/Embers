import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration, Stack, StackProps} from "@aws-cdk/core";
import * as s3 from "@aws-cdk/aws-s3";

export class LambdaStack extends Stack {
    private readonly sourceBucket: s3.IBucket;
    public readonly linkLambda: lambda.Function;
    public readonly itemLambda: lambda.Function;

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        this.sourceBucket = s3.Bucket.fromBucketAttributes(this, 'JPSourceBucket', {
            bucketArn: "arn:aws:s3:::javaplaid-lambda/JavaPlaid-1.0.zip"
        })

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
    }
}