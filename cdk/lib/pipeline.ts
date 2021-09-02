import * as cdk from '@aws-cdk/core';
import * as codecommit from '@aws-cdk/aws-codecommit';

export class JPPipelineStack extends cdk.Stack{
    public readonly repo: codecommit.Repository;

    constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        // Offers SNS and CodeStar notifications.
        this.repo = new codecommit.Repository(this, 'JavaPlaidRepo', {
            repositoryName: 'JavaPlaid',
            description: 'Repository for JavaPlaid transactions project'
        })

    }
}