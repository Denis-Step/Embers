import { Function } from "@aws-cdk/aws-lambda";
import { Construct } from "@aws-cdk/core";
export declare class MessageLambdas extends Construct {
    readonly sendMessageLambda: Function;
    private readonly roles;
    constructor(scope: Construct, id: string);
}
