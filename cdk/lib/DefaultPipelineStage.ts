import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {ItemLambdas} from "./lambdas/ItemLambdas";
import {PlaidLinkApi} from "./PlaidLinkApi";

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    public readonly mainStack: Stack

    constructor(scope: Stack, id: string, props?: StageProps) {
        super(scope, id, props);

        // Stages need stacks. Create all resourcs in scope of this stack.
        this.mainStack = new Stack(this, id + "-Stack");


        const itemLambdas = new ItemLambdas(this.mainStack, 'PlaidItem Lambdas');
        const apiStack = new PlaidLinkApi(this.mainStack, 'PlaidLinkApi',{
            linkLambda: itemLambdas.createLinkTokenLambda,
            itemLambda: itemLambdas.createItemLambda
        })

    }
}