import * as codebuild from '@aws-cdk/aws-codebuild';
import * as codecommit from '@aws-cdk/aws-codecommit';
import {Construct, Stack, StackProps, Stage, StageProps} from "@aws-cdk/core";
import * as s3 from "@aws-cdk/aws-s3";


export class BuildStack extends Stack {
    public readonly project: codebuild.Project;
    public readonly source: codebuild.Source;
    public readonly outputBucket: s3.Bucket;

    constructor(scope: Construct, id: string,  props?: StackProps) {
        super(scope, id, props);

        const repo = codecommit.Repository.fromRepositoryName(this, 'JavaPlaidRepo', 'JavaPlaid');
        this.source = codebuild.Source.codeCommit({repository: repo, branchOrRef: 'cdk'});
        this.outputBucket = new s3.Bucket(this, 'builtcodebucket');

        this.project = new codebuild.Project(this, 'JP', {
            source: this.source,
            artifacts:  codebuild.Artifacts.s3({
                bucket: this.outputBucket,
                includeBuildId: true,
                packageZip: true,
                path: 'javaplaid.zip',
            }),
            buildSpec: codebuild.BuildSpec.fromObject({
                version: '0.2',
                phases: {
                    build: {
                        commands: [
                            'gradle packageFat',
                        ],
                    },
                },
            }),
        })

    }
}