"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MessageLambdaRoles = exports.TransactionLambdasRoles = exports.ItemLambdaRoles = void 0;
const aws_iam_1 = require("@aws-cdk/aws-iam");
const core_1 = require("@aws-cdk/core");
const constants_1 = require("../constants");
class PlaidSecretsPolicy extends core_1.Construct {
    constructor(scope, id) {
        super(scope, id);
        this.policy = new aws_iam_1.Policy(this, id, {
            statements: [
                new aws_iam_1.PolicyStatement(({
                    resources: [constants_1.PLAID_SECRETS_ARN],
                    effect: aws_iam_1.Effect.ALLOW,
                    actions: [
                        "secretsmanager:GetSecretValue",
                        "secretsmanager:DescribeSecret",
                        "secretsmanager:ListSecretVersionIds",
                    ]
                }))
            ]
        });
    }
}
class ItemLambdaRoles extends core_1.Construct {
    constructor(scope, id, props) {
        super(scope, id);
        this.createLinkTokenLambdaRole = new aws_iam_1.Role(this, 'CreateLinkTokenLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new aws_iam_1.PolicyDocument({ statements: [
                        new aws_iam_1.PolicyStatement(({
                            resources: [constants_1.PLAID_SECRETS_ARN],
                            effect: aws_iam_1.Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ] })
            }
        });
        this.createItemLambdaRole = new aws_iam_1.Role(this, 'CreateItemLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new aws_iam_1.PolicyDocument({ statements: [
                        new aws_iam_1.PolicyStatement(({
                            resources: [constants_1.PLAID_SECRETS_ARN],
                            effect: aws_iam_1.Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ] })
            }
        });
        this.getItemLambdaRole = new aws_iam_1.Role(this, 'GetItemLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new aws_iam_1.PolicyDocument({ statements: [
                        new aws_iam_1.PolicyStatement(({
                            resources: [constants_1.PLAID_SECRETS_ARN],
                            effect: aws_iam_1.Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ] })
            }
        });
        props.itemsTable.grantReadWriteData(this.createItemLambdaRole);
        props.itemsTable.grantReadData(this.getItemLambdaRole);
    }
}
exports.ItemLambdaRoles = ItemLambdaRoles;
class TransactionLambdasRoles extends core_1.Construct {
    constructor(scope, id, props) {
        super(scope, id);
        this.getTransactionsLambdaRole = new aws_iam_1.Role(this, 'GetTransactionsLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
        });
        this.loadTransactionsLambdaRole = new aws_iam_1.Role(this, 'LoadTransactionsLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new aws_iam_1.PolicyDocument({ statements: [
                        new aws_iam_1.PolicyStatement(({
                            resources: [constants_1.PLAID_SECRETS_ARN],
                            effect: aws_iam_1.Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ] })
            }
        });
        this.receiveTransactionsLambdaRole = new aws_iam_1.Role(this, 'ReceiveTransactionsLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        });
        this.receiveTransactionsLambdaRole.addToPrincipalPolicy(new aws_iam_1.PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }));
        this.newTransactionLambdaRole = new aws_iam_1.Role(this, 'NewTransactionLambdarole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
        });
        this.newTransactionLambdaRole.addToPrincipalPolicy(new aws_iam_1.PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }));
        props.itemsTable.grantReadWriteData(this.loadTransactionsLambdaRole);
        props.itemsTable.grantReadWriteData(this.receiveTransactionsLambdaRole);
        props.itemsTable.grantReadData(this.newTransactionLambdaRole);
        props.transactionsTable.grantReadWriteData(this.receiveTransactionsLambdaRole);
        props.transactionsTable.grantReadData(this.getTransactionsLambdaRole);
    }
}
exports.TransactionLambdasRoles = TransactionLambdasRoles;
class MessageLambdaRoles extends core_1.Construct {
    constructor(scope, id) {
        super(scope, id);
        this.sendMessageLambdaRole = new aws_iam_1.Role(this, 'SendMessageLambdaRole', {
            assumedBy: new aws_iam_1.ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                aws_iam_1.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        });
    }
}
exports.MessageLambdaRoles = MessageLambdaRoles;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibGFtYmRhcm9sZXMuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyJsYW1iZGFyb2xlcy50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSw4Q0FVMEI7QUFDMUIsd0NBQXdDO0FBRXhDLDRDQUErQztBQUUvQyxNQUFNLGtCQUFtQixTQUFRLGdCQUFTO0lBR3RDLFlBQVksS0FBZ0IsRUFBRSxFQUFVO1FBQ3BDLEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxDQUFDLENBQUM7UUFFakIsSUFBSSxDQUFDLE1BQU0sR0FBRyxJQUFJLGdCQUFNLENBQUMsSUFBSSxFQUFFLEVBQUUsRUFBRTtZQUMvQixVQUFVLEVBQUU7Z0JBQ1IsSUFBSSx5QkFBZSxDQUFDLENBQUM7b0JBQ2pCLFNBQVMsRUFBRSxDQUFDLDZCQUFpQixDQUFDO29CQUM5QixNQUFNLEVBQUUsZ0JBQU0sQ0FBQyxLQUFLO29CQUNwQixPQUFPLEVBQUU7d0JBQ0wsK0JBQStCO3dCQUMvQiwrQkFBK0I7d0JBQy9CLHFDQUFxQztxQkFDeEM7aUJBQ0osQ0FBQyxDQUFDO2FBQ047U0FDSixDQUFDLENBQUE7SUFDTixDQUFDO0NBQ0o7QUFPRCxNQUFhLGVBQWdCLFNBQVEsZ0JBQVM7SUFLMUMsWUFBWSxLQUFnQixFQUFFLEVBQVUsRUFBRSxLQUEyQjtRQUNqRSxLQUFLLENBQUMsS0FBSyxFQUFFLEVBQUUsQ0FBQyxDQUFDO1FBRWpCLElBQUksQ0FBQyx5QkFBeUIsR0FBRyxJQUFJLGNBQUksQ0FBQyxJQUFJLEVBQUUsMkJBQTJCLEVBQUU7WUFDekUsU0FBUyxFQUFFLElBQUksMEJBQWdCLENBQUMsc0JBQXNCLENBQUM7WUFDdkQsZUFBZSxFQUFFO2dCQUNiLHVCQUFhLENBQUMsd0JBQXdCLENBQUMsMENBQTBDLENBQUM7YUFDckY7WUFDRCxjQUFjLEVBQUU7Z0JBQ1osa0JBQWtCLEVBQUUsSUFBSSx3QkFBYyxDQUFDLEVBQUMsVUFBVSxFQUFFO3dCQUM1QyxJQUFJLHlCQUFlLENBQUMsQ0FB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LENBQUMsMEJBQTBCLENBQUMsQ0FBQztRQUNyRSxLQUFLLENBQUMsVUFBVSxDQUFDLGtCQUFrQixDQUFDLElBQUksQ0FBQyw2QkFBNkIsQ0FBQyxDQUFBO1FBQ3ZFLEtBQUssQ0FBQyxVQUFVLENBQUMsYUFBYSxDQUFDLElBQUksQ0FBQyx3QkFBd0IsQ0FBQyxDQUFDO1FBQzlELEtBQUssQ0FBQyxpQkFBaUIsQ0FBQyxrQkFBa0IsQ0FBQyxJQUFJLENBQUMsNkJBQTZCLENBQUMsQ0FBQztRQUMvRSxLQUFLLENBQUMsaUJBQWlCLENBQUMsYUFBYSxDQUFDLElBQUksQ0FBQyx5QkFBeUIsQ0FBQyxDQUFDO0lBQzFFLENBQUM7Q0FDSjtBQWpFRCwwREFpRUM7QUFFRCxNQUFhLGtCQUFtQixTQUFRLGdCQUFTO0lBRzdDLFlBQVksS0FBZ0IsRUFBRSxFQUFVO1FBQ3BDLEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxDQUFDLENBQUM7UUFFakIsSUFBSSxDQUFDLHFCQUFxQixHQUFHLElBQUksY0FBSSxDQUFDLElBQUksRUFBRSx1QkFBdUIsRUFBRTtZQUNqRSxTQUFTLEVBQUUsSUFBSSwwQkFBZ0IsQ0FBQyxzQkFBc0IsQ0FBQztZQUN2RCxlQUFlLEVBQUU7Z0JBQ2IsdUJBQWEsQ0FBQyx3QkFBd0IsQ0FBQywwQ0FBMEMsQ0FBQzthQUNyRjtTQUNKLENBQUMsQ0FBQTtJQUVOLENBQUM7Q0FDSjtBQWRELGdEQWNDIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHtcbiAgICBFZmZlY3QsXG4gICAgSVBvbGljeSxcbiAgICBJUm9sZSxcbiAgICBNYW5hZ2VkUG9saWN5LFxuICAgIFBvbGljeSxcbiAgICBQb2xpY3lEb2N1bWVudCxcbiAgICBQb2xpY3lTdGF0ZW1lbnQsXG4gICAgUm9sZSxcbiAgICBTZXJ2aWNlUHJpbmNpcGFsXG59IGZyb20gXCJAYXdzLWNkay9hd3MtaWFtXCI7XG5pbXBvcnQge0NvbnN0cnVjdH0gZnJvbSBcIkBhd3MtY2RrL2NvcmVcIjtcbmltcG9ydCB7SVRhYmxlLCBUYWJsZX0gZnJvbSBcIkBhd3MtY2RrL2F3cy1keW5hbW9kYlwiO1xuaW1wb3J0IHtQTEFJRF9TRUNSRVRTX0FSTn0gZnJvbSBcIi4uL2NvbnN0YW50c1wiO1xuXG5jbGFzcyBQbGFpZFNlY3JldHNQb2xpY3kgZXh0ZW5kcyBDb25zdHJ1Y3Qge1xuICAgIHB1YmxpYyByZWFkb25seSBwb2xpY3k6IFBvbGljeTtcblxuICAgIGNvbnN0cnVjdG9yKHNjb3BlOiBDb25zdHJ1Y3QsIGlkOiBzdHJpbmcpIHtcbiAgICAgICAgc3VwZXIoc2NvcGUsIGlkKTtcblxuICAgICAgICB0aGlzLnBvbGljeSA9IG5ldyBQb2xpY3kodGhpcywgaWQsIHtcbiAgICAgICAgICAgIHN0YXRlbWVudHM6IFtcbiAgICAgICAgICAgICAgICBuZXcgUG9saWN5U3RhdGVtZW50KCh7XG4gICAgICAgICAgICAgICAgICAgIHJlc291cmNlczogW1BMQUlEX1NFQ1JFVFNfQVJOXSxcbiAgICAgICAgICAgICAgICAgICAgZWZmZWN0OiBFZmZlY3QuQUxMT1csXG4gICAgICAgICAgICAgICAgICAgIGFjdGlvbnM6IFtcbiAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6R2V0U2VjcmV0VmFsdWVcIixcbiAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6RGVzY3JpYmVTZWNyZXRcIixcbiAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6TGlzdFNlY3JldFZlcnNpb25JZHNcIixcbiAgICAgICAgICAgICAgICAgICAgXVxuICAgICAgICAgICAgICAgIH0pKVxuICAgICAgICAgICAgXVxuICAgICAgICB9KVxuICAgIH1cbn1cblxuZXhwb3J0IGludGVyZmFjZSBJdGVtTGFtYmRhUm9sZXNQcm9wcyB7XG4gICAgaXRlbXNUYWJsZTogVGFibGU7XG59XG5cblxuZXhwb3J0IGNsYXNzIEl0ZW1MYW1iZGFSb2xlcyBleHRlbmRzIENvbnN0cnVjdCB7XG4gICAgcHVibGljIGNyZWF0ZUxpbmtUb2tlbkxhbWJkYVJvbGU6IFJvbGU7XG4gICAgcHVibGljIGNyZWF0ZUl0ZW1MYW1iZGFSb2xlOiBJUm9sZTtcbiAgICBwdWJsaWMgZ2V0SXRlbUxhbWJkYVJvbGU6IElSb2xlO1xuXG4gICAgY29uc3RydWN0b3Ioc2NvcGU6IENvbnN0cnVjdCwgaWQ6IHN0cmluZywgcHJvcHM6IEl0ZW1MYW1iZGFSb2xlc1Byb3BzKSB7XG4gICAgICAgIHN1cGVyKHNjb3BlLCBpZCk7XG5cbiAgICAgICAgdGhpcy5jcmVhdGVMaW5rVG9rZW5MYW1iZGFSb2xlID0gbmV3IFJvbGUodGhpcywgJ0NyZWF0ZUxpbmtUb2tlbkxhbWJkYVJvbGUnLCB7XG4gICAgICAgICAgICBhc3N1bWVkQnk6IG5ldyBTZXJ2aWNlUHJpbmNpcGFsKCdsYW1iZGEuYW1hem9uYXdzLmNvbScpLFxuICAgICAgICAgICAgbWFuYWdlZFBvbGljaWVzOiBbXG4gICAgICAgICAgICAgICAgTWFuYWdlZFBvbGljeS5mcm9tQXdzTWFuYWdlZFBvbGljeU5hbWUoXCJzZXJ2aWNlLXJvbGUvQVdTTGFtYmRhQmFzaWNFeGVjdXRpb25Sb2xlXCIpXG4gICAgICAgICAgICBdLFxuICAgICAgICAgICAgaW5saW5lUG9saWNpZXM6IHtcbiAgICAgICAgICAgICAgICBwbGFpZFNlY3JldHNQb2xpY3k6IG5ldyBQb2xpY3lEb2N1bWVudCh7c3RhdGVtZW50czogW1xuICAgICAgICAgICAgICAgICAgICAgICAgbmV3IFBvbGljeVN0YXRlbWVudCgoe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlc291cmNlczogW1BMQUlEX1NFQ1JFVFNfQVJOXSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlZmZlY3Q6IEVmZmVjdC5BTExPVyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBhY3Rpb25zOiBbXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6R2V0U2VjcmV0VmFsdWVcIixcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgXCJzZWNyZXRzbWFuYWdlcjpEZXNjcmliZVNlY3JldFwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBcInNlY3JldHNtYW5hZ2VyOkxpc3RTZWNyZXRWZXJzaW9uSWRzXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgXVxuICAgICAgICAgICAgICAgICAgICAgICAgfSkpXG4gICAgICAgICAgICAgICAgICAgIF19IClcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuY3JlYXRlSXRlbUxhbWJkYVJvbGUgPSBuZXcgUm9sZSh0aGlzLCAnQ3JlYXRlSXRlbUxhbWJkYVJvbGUnLCB7XG4gICAgICAgICAgICBhc3N1bWVkQnk6IG5ldyBTZXJ2aWNlUHJpbmNpcGFsKCdsYW1iZGEuYW1hem9uYXdzLmNvbScpLFxuICAgICAgICAgICAgbWFuYWdlZFBvbGljaWVzOiBbXG4gICAgICAgICAgICAgICAgTWFuYWdlZFBvbGljeS5mcm9tQXdzTWFuYWdlZFBvbGljeU5hbWUoXCJzZXJ2aWNlLXJvbGUvQVdTTGFtYmRhQmFzaWNFeGVjdXRpb25Sb2xlXCIpXG4gICAgICAgICAgICBdLFxuICAgICAgICAgICAgaW5saW5lUG9saWNpZXM6IHtcbiAgICAgICAgICAgICAgICBwbGFpZFNlY3JldHNQb2xpY3k6IG5ldyBQb2xpY3lEb2N1bWVudCh7c3RhdGVtZW50czogW1xuICAgICAgICAgICAgICAgICAgICAgICAgbmV3IFBvbGljeVN0YXRlbWVudCgoe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlc291cmNlczogW1BMQUlEX1NFQ1JFVFNfQVJOXSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlZmZlY3Q6IEVmZmVjdC5BTExPVyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBhY3Rpb25zOiBbXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6R2V0U2VjcmV0VmFsdWVcIixcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgXCJzZWNyZXRzbWFuYWdlcjpEZXNjcmliZVNlY3JldFwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBcInNlY3JldHNtYW5hZ2VyOkxpc3RTZWNyZXRWZXJzaW9uSWRzXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgXVxuICAgICAgICAgICAgICAgICAgICAgICAgfSkpXG4gICAgICAgICAgICAgICAgICAgIF19IClcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSlcblxuICAgICAgICB0aGlzLmdldEl0ZW1MYW1iZGFSb2xlID0gbmV3IFJvbGUodGhpcywgJ0dldEl0ZW1MYW1iZGFSb2xlJywge1xuICAgICAgICAgICAgYXNzdW1lZEJ5OiBuZXcgU2VydmljZVByaW5jaXBhbCgnbGFtYmRhLmFtYXpvbmF3cy5jb20nKSxcbiAgICAgICAgICAgIG1hbmFnZWRQb2xpY2llczogW1xuICAgICAgICAgICAgICAgIE1hbmFnZWRQb2xpY3kuZnJvbUF3c01hbmFnZWRQb2xpY3lOYW1lKFwic2VydmljZS1yb2xlL0FXU0xhbWJkYUJhc2ljRXhlY3V0aW9uUm9sZVwiKVxuICAgICAgICAgICAgXSxcbiAgICAgICAgICAgIGlubGluZVBvbGljaWVzOiB7XG4gICAgICAgICAgICAgICAgcGxhaWRTZWNyZXRzUG9saWN5OiBuZXcgUG9saWN5RG9jdW1lbnQoe3N0YXRlbWVudHM6IFtcbiAgICAgICAgICAgICAgICAgICAgICAgIG5ldyBQb2xpY3lTdGF0ZW1lbnQoKHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXNvdXJjZXM6IFtQTEFJRF9TRUNSRVRTX0FSTl0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWZmZWN0OiBFZmZlY3QuQUxMT1csXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgYWN0aW9uczogW1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBcInNlY3JldHNtYW5hZ2VyOkdldFNlY3JldFZhbHVlXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6RGVzY3JpYmVTZWNyZXRcIixcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgXCJzZWNyZXRzbWFuYWdlcjpMaXN0U2VjcmV0VmVyc2lvbklkc1wiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIF1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pKVxuICAgICAgICAgICAgICAgICAgICBdfSApXG4gICAgICAgICAgICB9XG4gICAgICAgIH0pXG5cbiAgICAgICAgcHJvcHMuaXRlbXNUYWJsZS5ncmFudFJlYWRXcml0ZURhdGEodGhpcy5jcmVhdGVJdGVtTGFtYmRhUm9sZSk7XG4gICAgICAgIHByb3BzLml0ZW1zVGFibGUuZ3JhbnRSZWFkRGF0YSh0aGlzLmdldEl0ZW1MYW1iZGFSb2xlKTtcbiAgICB9XG59XG5cblxuZXhwb3J0IGludGVyZmFjZSBUcmFuc2FjdGlvbkxhbWJkYVJvbGVzUHJvcHMge1xuICAgIHRyYW5zYWN0aW9uc1RhYmxlOiBUYWJsZTtcbiAgICBpdGVtc1RhYmxlOiBUYWJsZTtcbn1cblxuZXhwb3J0IGNsYXNzIFRyYW5zYWN0aW9uTGFtYmRhc1JvbGVzIGV4dGVuZHMgQ29uc3RydWN0IHtcbiAgICBwdWJsaWMgZ2V0VHJhbnNhY3Rpb25zTGFtYmRhUm9sZTogUm9sZTtcbiAgICBwdWJsaWMgbG9hZFRyYW5zYWN0aW9uc0xhbWJkYVJvbGU6IFJvbGU7XG4gICAgcHVibGljIHJlY2VpdmVUcmFuc2FjdGlvbnNMYW1iZGFSb2xlOiBJUm9sZTtcbiAgICBwdWJsaWMgbmV3VHJhbnNhY3Rpb25MYW1iZGFSb2xlOiBJUm9sZTtcblxuICAgIGNvbnN0cnVjdG9yKHNjb3BlOiBDb25zdHJ1Y3QsIGlkOiBzdHJpbmcsIHByb3BzOiBUcmFuc2FjdGlvbkxhbWJkYVJvbGVzUHJvcHMpIHtcbiAgICAgICAgc3VwZXIoc2NvcGUsIGlkKTtcblxuICAgICAgICB0aGlzLmdldFRyYW5zYWN0aW9uc0xhbWJkYVJvbGUgPSBuZXcgUm9sZSh0aGlzLCAnR2V0VHJhbnNhY3Rpb25zTGFtYmRhUm9sZScsIHtcbiAgICAgICAgICAgIGFzc3VtZWRCeTogbmV3IFNlcnZpY2VQcmluY2lwYWwoJ2xhbWJkYS5hbWF6b25hd3MuY29tJyksXG4gICAgICAgICAgICBtYW5hZ2VkUG9saWNpZXM6IFtcbiAgICAgICAgICAgICAgICBNYW5hZ2VkUG9saWN5LmZyb21Bd3NNYW5hZ2VkUG9saWN5TmFtZShcInNlcnZpY2Utcm9sZS9BV1NMYW1iZGFCYXNpY0V4ZWN1dGlvblJvbGVcIilcbiAgICAgICAgICAgIF0sXG4gICAgICAgIH0pO1xuXG4gICAgICAgIHRoaXMubG9hZFRyYW5zYWN0aW9uc0xhbWJkYVJvbGUgPSBuZXcgUm9sZSh0aGlzLCAnTG9hZFRyYW5zYWN0aW9uc0xhbWJkYVJvbGUnLCB7XG4gICAgICAgICAgICBhc3N1bWVkQnk6IG5ldyBTZXJ2aWNlUHJpbmNpcGFsKCdsYW1iZGEuYW1hem9uYXdzLmNvbScpLFxuICAgICAgICAgICAgbWFuYWdlZFBvbGljaWVzOiBbXG4gICAgICAgICAgICAgICAgTWFuYWdlZFBvbGljeS5mcm9tQXdzTWFuYWdlZFBvbGljeU5hbWUoXCJzZXJ2aWNlLXJvbGUvQVdTTGFtYmRhQmFzaWNFeGVjdXRpb25Sb2xlXCIpXG4gICAgICAgICAgICBdLFxuICAgICAgICAgICAgaW5saW5lUG9saWNpZXM6IHtcbiAgICAgICAgICAgICAgICBwbGFpZFNlY3JldHNQb2xpY3k6IG5ldyBQb2xpY3lEb2N1bWVudCh7c3RhdGVtZW50czogW1xuICAgICAgICAgICAgICAgICAgICAgICAgbmV3IFBvbGljeVN0YXRlbWVudCgoe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlc291cmNlczogW1BMQUlEX1NFQ1JFVFNfQVJOXSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlZmZlY3Q6IEVmZmVjdC5BTExPVyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBhY3Rpb25zOiBbXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFwic2VjcmV0c21hbmFnZXI6R2V0U2VjcmV0VmFsdWVcIixcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgXCJzZWNyZXRzbWFuYWdlcjpEZXNjcmliZVNlY3JldFwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBcInNlY3JldHNtYW5hZ2VyOkxpc3RTZWNyZXRWZXJzaW9uSWRzXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgXVxuICAgICAgICAgICAgICAgICAgICAgICAgfSkpXG4gICAgICAgICAgICAgICAgICAgIF19IClcbiAgICAgICAgICAgIH1cbiAgICAgICAgfSk7XG4gICAgICAgIHRoaXMucmVjZWl2ZVRyYW5zYWN0aW9uc0xhbWJkYVJvbGUgPSBuZXcgUm9sZSh0aGlzLCAnUmVjZWl2ZVRyYW5zYWN0aW9uc0xhbWJkYVJvbGUnLCB7XG4gICAgICAgICAgICBhc3N1bWVkQnk6IG5ldyBTZXJ2aWNlUHJpbmNpcGFsKCdsYW1iZGEuYW1hem9uYXdzLmNvbScpLFxuICAgICAgICAgICAgbWFuYWdlZFBvbGljaWVzOiBbXG4gICAgICAgICAgICAgICAgTWFuYWdlZFBvbGljeS5mcm9tQXdzTWFuYWdlZFBvbGljeU5hbWUoXCJzZXJ2aWNlLXJvbGUvQVdTTGFtYmRhQmFzaWNFeGVjdXRpb25Sb2xlXCIpXG4gICAgICAgICAgICBdXG4gICAgICAgIH0pXG5cbiAgICAgICAgdGhpcy5yZWNlaXZlVHJhbnNhY3Rpb25zTGFtYmRhUm9sZS5hZGRUb1ByaW5jaXBhbFBvbGljeSggbmV3IFBvbGljeVN0YXRlbWVudCh7XG4gICAgICAgICAgICByZXNvdXJjZXM6IFtcIipcIl0sXG4gICAgICAgICAgICBhY3Rpb25zOiBbXCJldmVudHM6UHV0RXZlbnRzXCIsIFwiZXZlbnRzOkxpc3RSdWxlc1wiXVxuICAgICAgICB9KSlcblxuICAgICAgICB0aGlzLm5ld1RyYW5zYWN0aW9uTGFtYmRhUm9sZSA9IG5ldyBSb2xlKHRoaXMsICdOZXdUcmFuc2FjdGlvbkxhbWJkYXJvbGUnLCB7XG4gICAgICAgICAgICBhc3N1bWVkQnk6IG5ldyBTZXJ2aWNlUHJpbmNpcGFsKCdsYW1iZGEuYW1hem9uYXdzLmNvbScpLFxuICAgICAgICAgICAgbWFuYWdlZFBvbGljaWVzOiBbXG4gICAgICAgICAgICAgICAgTWFuYWdlZFBvbGljeS5mcm9tQXdzTWFuYWdlZFBvbGljeU5hbWUoXCJzZXJ2aWNlLXJvbGUvQVdTTGFtYmRhQmFzaWNFeGVjdXRpb25Sb2xlXCIpXG4gICAgICAgICAgICBdLFxuICAgICAgICB9KVxuXG4gICAgICAgIHRoaXMubmV3VHJhbnNhY3Rpb25MYW1iZGFSb2xlLmFkZFRvUHJpbmNpcGFsUG9saWN5KCBuZXcgUG9saWN5U3RhdGVtZW50KHtcbiAgICAgICAgICAgIHJlc291cmNlczogW1wiKlwiXSxcbiAgICAgICAgICAgIGFjdGlvbnM6IFtcImV2ZW50czpQdXRFdmVudHNcIiwgXCJldmVudHM6TGlzdFJ1bGVzXCJdXG4gICAgICAgIH0pKVxuXG4gICAgICAgIHByb3BzLml0ZW1zVGFibGUuZ3JhbnRSZWFkV3JpdGVEYXRhKHRoaXMubG9hZFRyYW5zYWN0aW9uc0xhbWJkYVJvbGUpO1xuICAgICAgICBwcm9wcy5pdGVtc1RhYmxlLmdyYW50UmVhZFdyaXRlRGF0YSh0aGlzLnJlY2VpdmVUcmFuc2FjdGlvbnNMYW1iZGFSb2xlKVxuICAgICAgICBwcm9wcy5pdGVtc1RhYmxlLmdyYW50UmVhZERhdGEodGhpcy5uZXdUcmFuc2FjdGlvbkxhbWJkYVJvbGUpO1xuICAgICAgICBwcm9wcy50cmFuc2FjdGlvbnNUYWJsZS5ncmFudFJlYWRXcml0ZURhdGEodGhpcy5yZWNlaXZlVHJhbnNhY3Rpb25zTGFtYmRhUm9sZSk7XG4gICAgICAgIHByb3BzLnRyYW5zYWN0aW9uc1RhYmxlLmdyYW50UmVhZERhdGEodGhpcy5nZXRUcmFuc2FjdGlvbnNMYW1iZGFSb2xlKTtcbiAgICB9XG59XG5cbmV4cG9ydCBjbGFzcyBNZXNzYWdlTGFtYmRhUm9sZXMgZXh0ZW5kcyBDb25zdHJ1Y3Qge1xuICAgIHB1YmxpYyBzZW5kTWVzc2FnZUxhbWJkYVJvbGU6IElSb2xlO1xuXG4gICAgY29uc3RydWN0b3Ioc2NvcGU6IENvbnN0cnVjdCwgaWQ6IHN0cmluZykge1xuICAgICAgICBzdXBlcihzY29wZSwgaWQpO1xuXG4gICAgICAgIHRoaXMuc2VuZE1lc3NhZ2VMYW1iZGFSb2xlID0gbmV3IFJvbGUodGhpcywgJ1NlbmRNZXNzYWdlTGFtYmRhUm9sZScsIHtcbiAgICAgICAgICAgIGFzc3VtZWRCeTogbmV3IFNlcnZpY2VQcmluY2lwYWwoJ2xhbWJkYS5hbWF6b25hd3MuY29tJyksXG4gICAgICAgICAgICBtYW5hZ2VkUG9saWNpZXM6IFtcbiAgICAgICAgICAgICAgICBNYW5hZ2VkUG9saWN5LmZyb21Bd3NNYW5hZ2VkUG9saWN5TmFtZShcInNlcnZpY2Utcm9sZS9BV1NMYW1iZGFCYXNpY0V4ZWN1dGlvblJvbGVcIilcbiAgICAgICAgICAgIF1cbiAgICAgICAgfSlcblxuICAgIH1cbn0iXX0=