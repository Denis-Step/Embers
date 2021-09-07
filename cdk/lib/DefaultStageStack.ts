import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {BuildStack} from "./buildStack";
import {LambdaStack} from "./LambdaStack";
import {PlaidLinkApiStack} from "./PlaidLinkApiStack";

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    public readonly stacks: Stack[]

    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props);

        const lambdaStack = new LambdaStack(this, 'plaidService');
        const apiStack = new PlaidLinkApiStack(this, 'PlaidLinkApi',{
            linkLambda: lambdaStack.linkLambda,
            itemLambda: lambdaStack.itemLambda
        })

        this.stacks = [apiStack];
    }
}