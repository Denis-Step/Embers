import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {ItemLambdas} from "./lambdas/ItemLambdas";
import {JpApi} from "./JpApi";
import {PullTransactionsMachine} from "./statemachines/pullTransactionsMachine";
import {TransactionLambdas} from "./lambdas/TransactionLambdas";
import {MessageLambdas} from "./lambdas/messageLambdas";
import {MessageEvents, NewTransactionEvents} from "./events/events";
import {JPTables} from "./tables/tables";

// Main logic and wiring goes in here.
export class DefaultPipelineStage extends Stage {
    public readonly mainStack: Stack
    public readonly messageStack: Stack
    public readonly tables: JPTables;

    constructor(scope: Stack, id: string, props?: StageProps) {
        super(scope, id, props);

        // Stages need stacks. Create all resources in scope of this stack.
        // This will be refactored declaratively later.
        this.mainStack = new Stack(this,  'MainStack');
        this.messageStack = new Stack(this, 'MessageStack');


        // Create Tables
        this.tables = new JPTables(this.mainStack, 'JpTables');

        // Create Lambdas that will be provided to the API.
        const itemLambdas = new ItemLambdas(this.mainStack, 'PlaidItem Lambdas', {
            itemsTable: this.tables.itemsTable
            });

        const transactionLambdas = new TransactionLambdas(this.mainStack, 'Transaction Lambdas', {
            itemsTable: this.tables.itemsTable,
            transactionsTable: this.tables.transactionsTable
        });

        const messageLambdas = new MessageLambdas(this.messageStack, 'MessageLambdas');


        const stateMachine = new PullTransactionsMachine(this.mainStack, 'PullTxStateMachine', {
            loadTransactionsLambda: transactionLambdas.loadTransactionsLambda,
            receiveTransactionsLambda: transactionLambdas.receiveTransactionsLambda
        })


        const apiStack = new JpApi(this.mainStack, 'JpApi',{
            getTransactionsLambda: transactionLambdas.getTransactionsLambda,
            linkLambda: itemLambdas.createLinkTokenLambda,
            itemLambda: itemLambdas.createItemLambda,
            pullTransactionsMachine: stateMachine.stateMachine
        })

        // NewTransactions EventBus & Matching Rules.
        const newTransactionEvents = new NewTransactionEvents(this.mainStack, 'NewTransactionEvents',
            {newTransactionLambda: transactionLambdas.newTransactionLambda
            })

        const messageEvents = new MessageEvents(this.messageStack, 'MessageEvents', {
            sendMessageLambda: messageLambdas.sendMessageLambda
        })

    }
}