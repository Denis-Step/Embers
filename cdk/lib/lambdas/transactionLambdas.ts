import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration} from "@aws-cdk/core";
import * as path from "path";
import {TransactionLambdasRoles} from "./lambdaroles";
import {Table} from "@aws-cdk/aws-dynamodb";

export class TransactionLambdasProps {
    itemsTable: Table;
    transactionsTable: Table;
}

export class TransactionLambdas extends Construct {
    public readonly loadTransactionsLambda: lambda.Function;
    public readonly receiveTransactionsLambda: lambda.Function;
    public readonly newTransactionLambda: lambda.Function;
    public readonly getTransactionsLambda: lambda.Function;

    private readonly roles: TransactionLambdasRoles;

    constructor(scope: Construct, id: string, props: TransactionLambdasProps) {
        super(scope, id);

        this.roles = new TransactionLambdasRoles(this, 'TxLambdaRoles', {
            transactionsTable: props.transactionsTable,
            itemsTable: props.itemsTable
        });

        this.loadTransactionsLambda = new lambda.Function(this, 'LoadTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_11,
            handler: "lambda.handlers.LoadTransactionsHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.loadTransactionsLambdaRole,
            timeout: Duration.seconds(300),
        })

        this.receiveTransactionsLambda = new lambda.Function(this, 'ReceiveTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_11,
            handler: "lambda.handlers.ReceiveTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.receiveTransactionsLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.newTransactionLambda = new lambda.Function(this, 'NewTransactionLambda', {
            runtime: lambda.Runtime.JAVA_11,
            handler: "lambda.handlers.NewTransactionHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.newTransactionLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.getTransactionsLambda = new lambda.Function(this, 'GetTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_11,
            handler: "lambda.handlers.GetTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.getTransactionsLambdaRole,
            timeout: Duration.seconds(300)
        });
    }
}