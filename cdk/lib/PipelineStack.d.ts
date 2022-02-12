import * as cdk from '@aws-cdk/core';
import * as codecommit from '@aws-cdk/aws-codecommit';
import { CodePipeline } from "@aws-cdk/pipelines";
export declare class JPPipelineStack extends cdk.Stack {
    readonly pipeline: CodePipeline;
    readonly repo: codecommit.Repository;
    constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps);
}
