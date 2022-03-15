import { Stack, Stage, Construct, StageProps } from '@aws-cdk/core';
import {MainStack} from "./stacks/mainStack";

export class DefaultPipelineStage extends Stage {
    public readonly mainStack: Stack

    constructor(scope: Stack, id: string, props?: StageProps) {
        super(scope, id, props);

        new MainStack(this, 'MainStack');
    }
}