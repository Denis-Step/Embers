import * as lambda from "@aws-cdk/aws-lambda";
import { Construct } from "@aws-cdk/core";
import { Table } from "@aws-cdk/aws-dynamodb";
export declare class TransactionLambdasProps {
    itemsTable: Table;
    transactionsTable: Table;
}
export declare class TransactionLambdas extends Construct {
    readonly loadTransactionsLambda: lambda.Function;
    readonly receiveTransactionsLambda: lambda.Function;
    readonly newTransactionLambda: lambda.Function;
    readonly getTransactionsLambda: lambda.Function;
    private readonly roles;
    constructor(scope: Construct, id: string, props: TransactionLambdasProps);
}
