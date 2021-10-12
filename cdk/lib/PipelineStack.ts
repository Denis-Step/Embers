import * as cdk from '@aws-cdk/core';
import * as codecommit from '@aws-cdk/aws-codecommit';
import {CodeBuildStep, CodePipeline, CodePipelineSource} from "@aws-cdk/pipelines";
import {DefaultPipelineStage} from "./DefaultPipelineStage";

export class JPPipelineStack extends cdk.Stack{
    public readonly pipeline: CodePipeline;
    public readonly repo: codecommit.Repository;

    constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        // Offers SNS and CodeStar notifications.
        this.repo = new codecommit.Repository(this, 'JavaPlaidRepo', {
            repositoryName: 'JavaPlaid',
            description: 'Repository for JavaPlaid transactions project'
        })

        // We'll be using the 'cdk2' branch in our repo for the cloud assembly.
        const sourceCode = CodePipelineSource.codeCommit(this.repo, 'cdk2');

        this.pipeline = new CodePipeline(this, 'JPPipeline', {
            selfMutation: true, // Can be turned off to ensure stability.
            synth: new CodeBuildStep('CloudSynth', {
                input: sourceCode,
                commands: [
                    'cd cdk',
                    'npm ci', // Special npm command for installing in test envs.
                    'npm run build',
                    'npm run bundle',
                    'npx cdk synth',
                ],
                primaryOutputDirectory: 'cdk/cdk.out' // Set this if it's not at the top level.
            })
        });

        // Now, we can add stages. The stages consume Stacks. This allows per-stage
        // stack deployment (useful for alarms and monitoring).

        this.pipeline.addStage(new DefaultPipelineStage(this, 'Prod'));

        }}