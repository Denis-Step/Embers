import { Construct } from '@aws-cdk/core';
import { Function } from "@aws-cdk/aws-lambda";
import { StateMachine } from "@aws-cdk/aws-stepfunctions";
export interface PullTransactionsMachineProps {
    loadTransactionsLambda: Function;
    receiveTransactionsLambda: Function;
}
export declare class PullTransactionsMachine extends Construct {
    readonly loadTransactionsLambda: Function;
    readonly receiveTransactionsLambda: Function;
    readonly stateMachine: StateMachine;
    constructor(scope: Construct, id: string, props: PullTransactionsMachineProps);
}
