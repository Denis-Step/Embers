import { Construct, Stack, StackProps } from "@aws-cdk/core";
import { JPTables } from "../tables/tables";
import { PullTransactionsMachine } from "../statemachines/pullTransactionsMachine";
import { JpApi } from "../JpApi";
export declare class MainStack extends Stack {
    readonly tables: JPTables;
    private readonly itemLambdas;
    private readonly transactionLambdas;
    private readonly messageLambdas;
    readonly pullTxStateMachine: PullTransactionsMachine;
    readonly apiStack: JpApi;
    constructor(scope: Construct, id: string, props?: StackProps);
}
