import { LambdaStack } from './cdk-stack';
import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {BuildStack} from "./buildStack";

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    public readonly stacks: Stack[]

    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props);

        const lambdaStack = new LambdaStack(this, 'plaidService');

        this.stacks = [lambdaStack];
    }
}