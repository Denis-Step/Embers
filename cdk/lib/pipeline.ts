import * as cdk from '@aws-cdk/core';
import * as codecommit from '@aws-cdk/aws-codecommit';
import * as codepipeline_actions from '@aws-cdk/aws-codepipeline-actions';
import * as codepipeline from '@aws-cdk/aws-codepipeline';
import {SimpleSynthAction, CodePipeline, CodePipelineSource, ShellStep} from "@aws-cdk/pipelines";
import {DefaultPipelineStage} from "./stage";
import * as s3 from "@aws-cdk/aws-s3";
import {Pipeline} from "@aws-cdk/aws-codepipeline";
import {BuildStage} from "./buildstage";

export class JPPipelineStack extends cdk.Stack{
    public readonly pipeline: CodePipeline;
    public readonly repo: codecommit.Repository;
    public readonly outputBucket: s3.Bucket;

    constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        // Offers SNS and CodeStar notifications.
        this.repo = new codecommit.Repository(this, 'JavaPlaidRepo', {
            repositoryName: 'JavaPlaid',
            description: 'Repository for JavaPlaid transactions project'
        })

        this.outputBucket = new s3.Bucket(this, 'builtcodebucket');

        // We'll be using the 'cdb' branch in our repo for the cloud assembly.
        const sourceCode = CodePipelineSource.codeCommit(this.repo, 'cdk2');

        this.pipeline = new CodePipeline(this, 'JPPipeline', {
            selfMutation: true, // Can be turned off to ensure stability.
            synth: new ShellStep('CloudSynth', {
                input: sourceCode,
                commands: [
                    'cd cdk',
                    'npm ci', // Special npm command for installing in test envs.
                    'npm run build',
                    'npx cdk synth',
                ],
                primaryOutputDirectory: 'cdk/cdk.out' // Set this if it's not at the top level.
            })
        });

        // Now, we can add stages. The stages consume Stacks. This allows per-stage
        // stack deployment (useful for alarms and monitoring).

        this.pipeline.addStage(new DefaultPipelineStage(this, 'Beta'));
        this.pipeline.addStage(new DefaultPipelineStage(this, 'Prod'));

        }}

        /*
        * Input (Synth): First-class integration with CodeCommit, GitHub, & S3 Buckets.
        *   - Uses a static method here instead of the repo instance variable we set up
        * for clarity.
        *
        * ShellStep: Can include multiple sources. Useful for combining repos.
        *   - Protip: This can be used to merge in GitHub repos even if your primary source is
        *   CodeCommit.
        *
        *   - Protip: Can include multiple output dirs. This is useful for multiple stacks
        *   if you'd like to avoid building them all into a single template artifact.
        *
        * Rollback: Still have to manually delete stuck stacks.
        *
        * Synth: Cloud assembly step. Can use multiple output directories.
        * Can use different language-specific builds.
        *
        * Waves: These are just stages executed in parallel. This is not the same
        * as an LPT wave. The wave terminology as used in LPT sometimes leaks out to
        * CDK packages but they're unrelated.  */