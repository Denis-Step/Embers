import * as codebuild from '@aws-cdk/aws-codebuild';
import * as codecommit from '@aws-cdk/aws-codecommit';
import {Construct, Stack, StackProps, Stage, StageProps} from "@aws-cdk/core";
import * as s3 from "@aws-cdk/aws-s3";
import {BuildStack} from "./buildStack";


 export class BuildStage extends Stage {
    public readonly stacks: Stack[]

    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props);

        this.stacks = [new BuildStack(this, 'buildStack', )];

    }
}