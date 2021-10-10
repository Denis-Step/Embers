import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration, Stack, StackProps} from "@aws-cdk/core";
import * as path from "path";

export class TransactionLambdas extends Stack {
    public readonly loadTransactionsLambda: lambda.Function;
    public readonly receiveTransactionsLambda: lambda.Function;
    public readonly newTransactionLambda: lambda.Function;
    public readonly getTransactionsLambda: lambda.Function;

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        this.loadTransactionsLambda = new lambda.Function(this, 'LoadTransactions', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.LoadTransactionsHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",

            },
            timeout: Duration.seconds(300),
        })

        this.receiveTransactionsLambda = new lambda.Function(this, 'ItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.ReceiveTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            timeout: Duration.seconds(300)
        });

        this.newTransactionLambda = new lambda.Function(this, 'ItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.ReceiveTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            timeout: Duration.seconds(300)
        });

        this.getTransactionsLambda = new lambda.Function(this, 'ItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.GetTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            timeout: Duration.seconds(300)
        });
    }
}