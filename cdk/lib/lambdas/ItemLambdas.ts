import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration, Stack, StackProps} from "@aws-cdk/core";
import * as path from "path";
import {ItemLambdaRoles} from "./lambdaroles";

export class ItemLambdas extends Construct {
    public readonly createLinkTokenLambda: lambda.Function;
    public readonly createItemLambda: lambda.Function;
    public readonly getItemLambda: lambda.Function;
    public roles: ItemLambdaRoles;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.roles = new ItemLambdaRoles(scope, 'ItemLambdaRoles');

        this.createLinkTokenLambda = new lambda.Function(this, 'LinkTokenLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.CreateLinkTokenHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",

            },
            timeout: Duration.seconds(300),
            role: this.roles.createLinkTokenLambdaRole
        })

        this.createItemLambda = new lambda.Function(this, 'CreateItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.CreateItemHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            timeout: Duration.seconds(300),
            role: this.roles.createItemLambdaRole
        });

        this.getItemLambda = new lambda.Function(this, 'GetItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.GetItemHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            timeout: Duration.seconds(300),
            role: this.roles.getItemLambdaRole
        });
    }
}