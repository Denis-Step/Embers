import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration} from "@aws-cdk/core";
import * as path from "path";
import {TransactionLambdasRoles} from "./lambdaroles";
import {JPTables} from "../tables/tables";

export class TransactionLambdas extends Construct {
    private readonly tables: JPTables;
    public readonly loadTransactionsLambda: lambda.Function;
    public readonly receiveTransactionsLambda: lambda.Function;
    public readonly newTransactionLambda: lambda.Function;
    public readonly getTransactionsLambda: lambda.Function;

    private readonly roles: TransactionLambdasRoles;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.tables = new JPTables(this, 'Tables');

        this.roles = new TransactionLambdasRoles(this, 'TxLambdaRoles', {
            transactionsTable: this.tables.transactionsTable
        });

        this.loadTransactionsLambda = new lambda.Function(this, 'LoadTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.LoadTransactionsHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.loadTransactionsLambdaRole,
            timeout: Duration.seconds(300),
        })

        this.receiveTransactionsLambda = new lambda.Function(this, 'ReceiveTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.ReceiveTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.receiveTransactionsLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.newTransactionLambda = new lambda.Function(this, 'NewTransactionLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.NewTransactionHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.newTransactionLambdaRole,
            timeout: Duration.seconds(300)
        });

        this.getTransactionsLambda = new lambda.Function(this, 'GetTransactionsLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.GetTransactionsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            role: this.roles.getTransactionsLambdaRole,
            timeout: Duration.seconds(300)
        });
    }
}