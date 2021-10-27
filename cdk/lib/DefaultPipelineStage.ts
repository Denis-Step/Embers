import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {ItemLambdas} from "./lambdas/ItemLambdas";
import {JpApi} from "./JpApi";
import {PullTransactionsMachine} from "./statemachines/pullTransactionsMachine";
import {TransactionLambdas} from "./lambdas/TransactionLambdas";
import {MessageLambdas} from "./lambdas/messageLambdas";
import {MessageEvents, NewTransactionEvents} from "./events/events";

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    public readonly mainStack: Stack
    public readonly messageStack: Stack

    constructor(scope: Stack, id: string, props?: StageProps) {
        super(scope, id, props);

        // Stages need stacks. Create all resources in scope of this stack.
        this.mainStack = new Stack(this,  'MainStack');
        this.messageStack = new Stack(this, 'MessageStack');

        const itemLambdas = new ItemLambdas(this.mainStack, 'PlaidItem Lambdas');
        const transactionLambdas = new TransactionLambdas(this.mainStack, 'Transaction Lambdas');
        const apiStack = new JpApi(this.mainStack, 'PlaidLinkApi',{
            getTransactionsLambda: transactionLambdas.getTransactionsLambda,
            linkLambda: itemLambdas.createLinkTokenLambda,
            itemLambda: itemLambdas.createItemLambda
        })

        const stateMachine = new PullTransactionsMachine(this.mainStack, 'PullTxStateMachine', {
            loadTransactionsLambda: transactionLambdas.loadTransactionsLambda,
            receiveTransactionsLambda: transactionLambdas.receiveTransactionsLambda
        })

        const messageLambdas = new MessageLambdas(this.messageStack, 'MessageLambdas');

        // NewTransactions EventBus & Matching Rules.
        const newTransactionEvents = new NewTransactionEvents(this.mainStack, 'NewTransactionEvents',
            {newTransactionLambda: transactionLambdas.newTransactionLambda
            })

        const messageEvents = new MessageEvents(this.messageStack, 'MessageEvents', {
            sendMessageLambda: messageLambdas.sendMessageLambda
        })

    }
}