import {Construct, Duration} from '@aws-cdk/core';
import {Function} from "@aws-cdk/aws-lambda";
import * as tasks from '@aws-cdk/aws-stepfunctions-tasks';
import {StateMachine} from "@aws-cdk/aws-stepfunctions";

export interface PullTransactionsMachineProps {
    loadTransactionsLambda: Function;
    receiveTransactionsLambda: Function;
}

export class PullTransactionsMachine extends Construct {
    public readonly loadTransactionsLambda: Function;
    public readonly receiveTransactionsLambda: Function;
    public readonly stateMachine: StateMachine;

    constructor(scope: Construct, id: string, props: PullTransactionsMachineProps) {
        super(scope, id);
        this.loadTransactionsLambda = props.loadTransactionsLambda;
        this.receiveTransactionsLambda = props.receiveTransactionsLambda;

        // Pull New Transactions.
        const loadTransactionsTask = new tasks.LambdaInvoke(this, 'Load Transactions', {
            lambdaFunction: this.loadTransactionsLambda,
            outputPath: '$.Payload'
        })

        // Process them and create events.
        const receiveTransactionsTask =  new tasks.LambdaInvoke(this, 'Receive Transactions', {
            lambdaFunction: this.receiveTransactionsLambda
        })

        const definition = loadTransactionsTask
            .next(receiveTransactionsTask)

        this.stateMachine = new StateMachine(this, 'Pull Transactions State Machine', {
            definition,
            timeout: Duration.minutes(5)
        })
    }

}

