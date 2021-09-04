import { LambdaStack } from './cdk-stack';
import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';

// Let's use one stage for now.
export class DefaultPipelineStage extends Stage {
    private readonly stacks: Stack[]

    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props);

        const lambdaStack = new LambdaStack(this, 'plaidService');
        this.stacks = [lambdaStack];
    }
}