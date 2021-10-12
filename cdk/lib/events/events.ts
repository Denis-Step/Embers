import {Construct} from "@aws-cdk/core";
import {EventBus, Rule, RuleTargetInput} from "@aws-cdk/aws-events";
import {LambdaFunction} from "@aws-cdk/aws-events-targets";
import {Function} from "@aws-cdk/aws-lambda";
import {
    NEW_MESSAGE_DETAIL_TYPE,
    NEW_TRANSACTION_DETAIL_TYPE,
    NEW_TRANSACTION_SOURCE,
    RECEIVE_TRANSACTIONS_SOURCE
} from "../constants";

export interface NewTransactionEventsProps {
    newTransactionLambda: Function;
}

export class NewTransactionEvents extends Construct {
    public readonly transactionsBus: EventBus;
    public readonly newTransactionRule: Rule;

    constructor(scope: Construct, id: string, props: NewTransactionEventsProps) {
        super(scope, id);

        this.transactionsBus = new EventBus(this, 'TransactionsBus', {
            eventBusName: 'TransactionsBus'
        })

        this.newTransactionRule = new Rule(this, 'NewTransactionRule', {
            description: "Invokes NewTransactionLambda with new Transaction info",
            ruleName: 'NewTransactionRule',
            targets: [new LambdaFunction(props.newTransactionLambda, {
                event: RuleTargetInput.fromEventPath("$.detail")
            })],
            eventPattern: {
                source: [RECEIVE_TRANSACTIONS_SOURCE],
                detailType: [NEW_TRANSACTION_DETAIL_TYPE]
            },

        })
    }
}

export interface MessageEventsProps {
    sendMessageLambda: Function;
}

export class MessageEvents extends Construct {
    public readonly messagesBus: EventBus;
    public readonly newTransactionRule: Rule;

    constructor(scope: Construct, id: string, props: MessageEventsProps) {
        super(scope, id);

        this.messagesBus = new EventBus(this, 'TxBus', {
            eventBusName: 'SmsBus'
        })

        this.newTransactionRule = new Rule(this, 'NewTransactionRule', {
            description: "Invokes NewTransactionLambda with new Transaction info",
            ruleName: 'NewTransactionRule',
            targets: [new LambdaFunction(props.sendMessageLambda, {
                event: RuleTargetInput.fromEventPath("$.detail")
            })],
            eventPattern: {
                source: [NEW_TRANSACTION_SOURCE],
                detailType: [NEW_MESSAGE_DETAIL_TYPE]
            },

        })
    }
}