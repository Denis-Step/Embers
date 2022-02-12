"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.JPPipelineStack = void 0;
const cdk = require("@aws-cdk/core");
const codecommit = require("@aws-cdk/aws-codecommit");
const pipelines_1 = require("@aws-cdk/pipelines");
const DefaultStageStack_1 = require("./DefaultStageStack");
class JPPipelineStack extends cdk.Stack {
    constructor(scope, id, props) {
        super(scope, id, props);
        // Offers SNS and CodeStar notifications.
        this.repo = new codecommit.Repository(this, 'JavaPlaidRepo', {
            repositoryName: 'JavaPlaid',
            description: 'Repository for JavaPlaid transactions project'
        });
        // We'll be using the 'cdk2' branch in our repo for the cloud assembly.
        const sourceCode = pipelines_1.CodePipelineSource.codeCommit(this.repo, 'cdk2');
        this.pipeline = new pipelines_1.CodePipeline(this, 'JPPipeline', {
            selfMutation: true,
            synth: new pipelines_1.CodeBuildStep('CloudSynth', {
                input: sourceCode,
                commands: [
                    'cd cdk',
                    'npm ci',
                    'npm run build',
                    'npm run bundle',
                    'npx cdk synth',
                ],
                primaryOutputDirectory: 'cdk/cdk.out' // Set this if it's not at the top level.
            })
        });
        // Now, we can add stages. The stages consume Stacks. This allows per-stage
        // stack deployment (useful for alarms and monitoring).
        this.pipeline.addStage(new DefaultStageStack_1.DefaultPipelineStage(this, 'Beta'));
        this.pipeline.addStage(new DefaultStageStack_1.DefaultPipelineStage(this, 'Prod'));
    }
}
exports.JPPipelineStack = JPPipelineStack;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUGlwZWxpbmVTdGFjay5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIlBpcGVsaW5lU3RhY2sudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEscUNBQXFDO0FBQ3JDLHNEQUFzRDtBQUN0RCxrREFBbUY7QUFDbkYsMkRBQXlEO0FBRXpELE1BQWEsZUFBZ0IsU0FBUSxHQUFHLENBQUMsS0FBSztJQUkxQyxZQUFZLEtBQW9CLEVBQUUsRUFBVSxFQUFFLEtBQXNCO1FBQ2hFLEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxFQUFFLEtBQUssQ0FBQyxDQUFDO1FBRXhCLHlDQUF5QztRQUN6QyxJQUFJLENBQUMsSUFBSSxHQUFHLElBQUksVUFBVSxDQUFDLFVBQVUsQ0FBQyxJQUFJLEVBQUUsZUFBZSxFQUFFO1lBQ3pELGNBQWMsRUFBRSxXQUFXO1lBQzNCLFdBQVcsRUFBRSwrQ0FBK0M7U0FDL0QsQ0FBQyxDQUFBO1FBRUYsdUVBQXVFO1FBQ3ZFLE1BQU0sVUFBVSxHQUFHLDhCQUFrQixDQUFDLFVBQVUsQ0FBQyxJQUFJLENBQUMsSUFBSSxFQUFFLE1BQU0sQ0FBQyxDQUFDO1FBRXBFLElBQUksQ0FBQyxRQUFRLEdBQUcsSUFBSSx3QkFBWSxDQUFDLElBQUksRUFBRSxZQUFZLEVBQUU7WUFDakQsWUFBWSxFQUFFLElBQUk7WUFDbEIsS0FBSyxFQUFFLElBQUkseUJBQWEsQ0FBQyxZQUFZLEVBQUU7Z0JBQ25DLEtBQUssRUFBRSxVQUFVO2dCQUNqQixRQUFRLEVBQUU7b0JBQ04sUUFBUTtvQkFDUixRQUFRO29CQUNSLGVBQWU7b0JBQ2YsZ0JBQWdCO29CQUNoQixlQUFlO2lCQUNsQjtnQkFDRCxzQkFBc0IsRUFBRSxhQUFhLENBQUMseUNBQXlDO2FBQ2xGLENBQUM7U0FDTCxDQUFDLENBQUM7UUFFSCwyRUFBMkU7UUFDM0UsdURBQXVEO1FBRXZELElBQUksQ0FBQyxRQUFRLENBQUMsUUFBUSxDQUFDLElBQUksd0NBQW9CLENBQUMsSUFBSSxFQUFFLE1BQU0sQ0FBQyxDQUFDLENBQUM7UUFDL0QsSUFBSSxDQUFDLFFBQVEsQ0FBQyxRQUFRLENBQUMsSUFBSSx3Q0FBb0IsQ0FBQyxJQUFJLEVBQUUsTUFBTSxDQUFDLENBQUMsQ0FBQztJQUUvRCxDQUFDO0NBQUM7QUFyQ1YsMENBcUNVIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0ICogYXMgY2RrIGZyb20gJ0Bhd3MtY2RrL2NvcmUnO1xuaW1wb3J0ICogYXMgY29kZWNvbW1pdCBmcm9tICdAYXdzLWNkay9hd3MtY29kZWNvbW1pdCc7XG5pbXBvcnQge0NvZGVCdWlsZFN0ZXAsIENvZGVQaXBlbGluZSwgQ29kZVBpcGVsaW5lU291cmNlfSBmcm9tIFwiQGF3cy1jZGsvcGlwZWxpbmVzXCI7XG5pbXBvcnQge0RlZmF1bHRQaXBlbGluZVN0YWdlfSBmcm9tIFwiLi9EZWZhdWx0U3RhZ2VTdGFja1wiO1xuXG5leHBvcnQgY2xhc3MgSlBQaXBlbGluZVN0YWNrIGV4dGVuZHMgY2RrLlN0YWNre1xuICAgIHB1YmxpYyByZWFkb25seSBwaXBlbGluZTogQ29kZVBpcGVsaW5lO1xuICAgIHB1YmxpYyByZWFkb25seSByZXBvOiBjb2RlY29tbWl0LlJlcG9zaXRvcnk7XG5cbiAgICBjb25zdHJ1Y3RvcihzY29wZTogY2RrLkNvbnN0cnVjdCwgaWQ6IHN0cmluZywgcHJvcHM/OiBjZGsuU3RhY2tQcm9wcykge1xuICAgICAgICBzdXBlcihzY29wZSwgaWQsIHByb3BzKTtcblxuICAgICAgICAvLyBPZmZlcnMgU05TIGFuZCBDb2RlU3RhciBub3RpZmljYXRpb25zLlxuICAgICAgICB0aGlzLnJlcG8gPSBuZXcgY29kZWNvbW1pdC5SZXBvc2l0b3J5KHRoaXMsICdKYXZhUGxhaWRSZXBvJywge1xuICAgICAgICAgICAgcmVwb3NpdG9yeU5hbWU6ICdKYXZhUGxhaWQnLFxuICAgICAgICAgICAgZGVzY3JpcHRpb246ICdSZXBvc2l0b3J5IGZvciBKYXZhUGxhaWQgdHJhbnNhY3Rpb25zIHByb2plY3QnXG4gICAgICAgIH0pXG5cbiAgICAgICAgLy8gV2UnbGwgYmUgdXNpbmcgdGhlICdjZGsyJyBicmFuY2ggaW4gb3VyIHJlcG8gZm9yIHRoZSBjbG91ZCBhc3NlbWJseS5cbiAgICAgICAgY29uc3Qgc291cmNlQ29kZSA9IENvZGVQaXBlbGluZVNvdXJjZS5jb2RlQ29tbWl0KHRoaXMucmVwbywgJ2NkazInKTtcblxuICAgICAgICB0aGlzLnBpcGVsaW5lID0gbmV3IENvZGVQaXBlbGluZSh0aGlzLCAnSlBQaXBlbGluZScsIHtcbiAgICAgICAgICAgIHNlbGZNdXRhdGlvbjogdHJ1ZSwgLy8gQ2FuIGJlIHR1cm5lZCBvZmYgdG8gZW5zdXJlIHN0YWJpbGl0eS5cbiAgICAgICAgICAgIHN5bnRoOiBuZXcgQ29kZUJ1aWxkU3RlcCgnQ2xvdWRTeW50aCcsIHtcbiAgICAgICAgICAgICAgICBpbnB1dDogc291cmNlQ29kZSxcbiAgICAgICAgICAgICAgICBjb21tYW5kczogW1xuICAgICAgICAgICAgICAgICAgICAnY2QgY2RrJyxcbiAgICAgICAgICAgICAgICAgICAgJ25wbSBjaScsIC8vIFNwZWNpYWwgbnBtIGNvbW1hbmQgZm9yIGluc3RhbGxpbmcgaW4gdGVzdCBlbnZzLlxuICAgICAgICAgICAgICAgICAgICAnbnBtIHJ1biBidWlsZCcsXG4gICAgICAgICAgICAgICAgICAgICducG0gcnVuIGJ1bmRsZScsXG4gICAgICAgICAgICAgICAgICAgICducHggY2RrIHN5bnRoJyxcbiAgICAgICAgICAgICAgICBdLFxuICAgICAgICAgICAgICAgIHByaW1hcnlPdXRwdXREaXJlY3Rvcnk6ICdjZGsvY2RrLm91dCcgLy8gU2V0IHRoaXMgaWYgaXQncyBub3QgYXQgdGhlIHRvcCBsZXZlbC5cbiAgICAgICAgICAgIH0pXG4gICAgICAgIH0pO1xuXG4gICAgICAgIC8vIE5vdywgd2UgY2FuIGFkZCBzdGFnZXMuIFRoZSBzdGFnZXMgY29uc3VtZSBTdGFja3MuIFRoaXMgYWxsb3dzIHBlci1zdGFnZVxuICAgICAgICAvLyBzdGFjayBkZXBsb3ltZW50ICh1c2VmdWwgZm9yIGFsYXJtcyBhbmQgbW9uaXRvcmluZykuXG5cbiAgICAgICAgdGhpcy5waXBlbGluZS5hZGRTdGFnZShuZXcgRGVmYXVsdFBpcGVsaW5lU3RhZ2UodGhpcywgJ0JldGEnKSk7XG4gICAgICAgIHRoaXMucGlwZWxpbmUuYWRkU3RhZ2UobmV3IERlZmF1bHRQaXBlbGluZVN0YWdlKHRoaXMsICdQcm9kJykpO1xuXG4gICAgICAgIH19Il19