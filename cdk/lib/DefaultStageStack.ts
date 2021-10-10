import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {ItemLambdas} from "./lambdas/ItemLambdas";
import {PlaidLinkApiStack} from "./PlaidLinkApiStack";

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    public readonly stacks: Stack[]

    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props);


        const lambdaStack = new ItemLambdas(this, 'plaidService');
        const apiStack = new PlaidLinkApiStack(this, 'PlaidLinkApi',{
            linkLambda: lambdaStack.createLinkTokenLambda,
            itemLambda: lambdaStack.createItemLambda
        })

        this.stacks = [apiStack, lambdaStack];
    }
}