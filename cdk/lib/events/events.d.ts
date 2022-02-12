import { Construct } from "@aws-cdk/core";
import { EventBus, Rule } from "@aws-cdk/aws-events";
import { Function } from "@aws-cdk/aws-lambda";
export interface NewTransactionEventsProps {
    newTransactionLambda: Function;
}
export declare class NewTransactionEvents extends Construct {
    readonly transactionsBus: EventBus;
    readonly newTransactionRule: Rule;
    constructor(scope: Construct, id: string, props: NewTransactionEventsProps);
}
export interface MessageEventsProps {
    sendMessageLambda: Function;
}
export declare class MessageEvents extends Construct {
    readonly messagesBus: EventBus;
    readonly newTransactionRule: Rule;
    constructor(scope: Construct, id: string, props: MessageEventsProps);
}
