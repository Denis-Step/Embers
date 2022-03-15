import {Construct, Stack, StackProps} from "@aws-cdk/core";
import {JPTables} from "../tables/tables";
import {ItemLambdas} from "../lambdas/ItemLambdas";
import {TransactionLambdas} from "../lambdas/transactionLambdas";
import {MessageLambdas} from "../lambdas/messageLambdas";
import {PullTransactionsMachine} from "../statemachines/pullTransactionsMachine";
import {JpApi} from "../JpApi";

// Main logic and wiring goes in here.
export class MainStack extends Stack {
    public readonly tables: JPTables;
    private readonly itemLambdas: ItemLambdas;
    private readonly transactionLambdas: TransactionLambdas;
    private readonly messageLambdas: MessageLambdas;
    public readonly pullTxStateMachine: PullTransactionsMachine;
    public readonly apiStack: JpApi;


    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);
        // Create Tables
        this.tables = new JPTables(this, 'JpTables');

        // Create Lambdas that will be provided to the API.
        this.itemLambdas = new ItemLambdas(this, 'PlaidItem Lambdas', {
            itemsTable: this.tables.itemsTable
        });

        this.transactionLambdas = new TransactionLambdas(this, 'Transaction Lambdas', {
            itemsTable: this.tables.itemsTable,
            transactionsTable: this.tables.transactionsTable
        });

        this.messageLambdas = new MessageLambdas(this, 'MessageLambdas');

        this.pullTxStateMachine = new PullTransactionsMachine(this, 'PullTxStateMachine', {
            loadTransactionsLambda:this.transactionLambdas.pollTransactionsLambda,
            receiveTransactionsLambda: this.transactionLambdas.receiveTransactionsLambda
        })

        this.apiStack = new JpApi(this, 'JpApi',{
            getTransactionsLambda: this.transactionLambdas.getTransactionsLambda,
            linkLambda: this.itemLambdas.createLinkTokenLambda,
            itemLambda: this.itemLambdas.createItemLambda,
            pullTransactionsMachine: this.pullTxStateMachine.stateMachine
        })
    }
}