import { Stack, Stage, StageProps } from '@aws-cdk/core';
export declare class DefaultPipelineStage extends Stage {
    readonly mainStack: Stack;
    constructor(scope: Stack, id: string, props?: StageProps);
}
