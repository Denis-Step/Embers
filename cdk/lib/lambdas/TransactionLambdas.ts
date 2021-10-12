import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration} from "@aws-cdk/core";
import * as path from "path";
import {TransactionLambdasRoles} from "./lambdaroles";

export class TransactionLambdas extends Construct {
    public readonly loadTransactionsLambda: lambda.Function;
    public readonly receiveTransactionsLambda: lambda.Function;
    public readonly newTransactionLambda: lambda.Function;
    public readonly getTransactionsLambda: lambda.Function;

    private readonly roles: TransactionLambdasRoles;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.roles = new TransactionLambdasRoles(this, 'TxLambdaRoles');

        this.loadTransactionsLambda = new lambda.Function(this, 'LoadTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.LoadTransactionsHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",

            },
            memorySize: 512,
            role: this.roles.loadTransactionsLambdarole,
            timeout: Duration.seconds(300),
        })

        this.receiveTransactionsLambda = new lambda.Function(this, 'ReceiveTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.ReceiveTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            memorySize: 512,
            role: this.roles.receiveTransactionsLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.newTransactionLambda = new lambda.Function(this, 'NewTransactionLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.NewTransactionHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            memorySize: 512,
            role: this.roles.newTransactionLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.getTransactionsLambda = new lambda.Function(this, 'GetTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.GetTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            environment: {
                "CLIENT_ID": "5eb13e97fd0ed40013cc0438",
                "DEVELOPMENT_SECRET": "60ea81ee4fa5b9ff9b3c07f72f56da",
                "SANDBOX_SECRET": "68134865febfc98c05f21563bd8b99",
            },
            memorySize: 512,
            timeout: Duration.seconds(300)
        });
    }
}