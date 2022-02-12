import * as lambda from '@aws-cdk/aws-lambda';
import * as cdk from '@aws-cdk/core';
import * as apigw from '@aws-cdk/aws-apigateway';
import { StateMachine } from "@aws-cdk/aws-stepfunctions";
export interface PlaidLinkApiProps {
    linkLambda: lambda.Function;
    itemLambda: lambda.Function;
    getTransactionsLambda: lambda.Function;
    pullTransactionsMachine: StateMachine;
}
export declare class JpApi extends cdk.Construct {
    private readonly userPoolsAuthorizer;
    restApi: apigw.RestApi;
    constructor(scope: cdk.Construct, id: string, props: PlaidLinkApiProps);
}
