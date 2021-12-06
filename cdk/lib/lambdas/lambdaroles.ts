import {
    Effect,
    IPolicy,
    IRole,
    ManagedPolicy,
    Policy,
    PolicyDocument,
    PolicyStatement,
    Role,
    ServicePrincipal
} from "@aws-cdk/aws-iam";
import {Construct} from "@aws-cdk/core";
import {ITable, Table} from "@aws-cdk/aws-dynamodb";
import {PLAID_ITEMS_DDB_TABLE_ARN, PLAID_SECRETS_ARN} from "../constants";

class PlaidSecretsPolicy extends Construct {
    public readonly policy: Policy;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.policy = new Policy(this, id, {
            statements: [
                new PolicyStatement(({
                    resources: [PLAID_SECRETS_ARN],
                    effect: Effect.ALLOW,
                    actions: [
                        "secretsmanager:GetSecretValue",
                        "secretsmanager:DescribeSecret",
                        "secretsmanager:ListSecretVersionIds",
                    ]
                }))
            ]
        })
    }
}

export class ItemLambdaRoles extends Construct {
    private readonly itemTable: ITable;
    public createLinkTokenLambdaRole: Role;
    public createItemLambdaRole: IRole;
    public getItemLambdaRole: IRole;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.itemTable = Table.fromTableArn(this, "PlaidItemsTable", PLAID_ITEMS_DDB_TABLE_ARN);

        this.createLinkTokenLambdaRole = new Role(this, 'CreateLinkTokenLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new PolicyDocument({statements: [
                        new PolicyStatement(({
                            resources: [PLAID_SECRETS_ARN],
                            effect: Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ]} )
            }
        });
        this.createItemLambdaRole = new Role(this, 'CreateItemLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new PolicyDocument({statements: [
                        new PolicyStatement(({
                            resources: [PLAID_SECRETS_ARN],
                            effect: Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ]} )
            }
        })

        this.getItemLambdaRole = new Role(this, 'GetItemLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        })

        this.itemTable.grantReadWriteData(this.createItemLambdaRole);
        this.itemTable.grantReadData(this.getItemLambdaRole);
    }
}


export interface TransactionLambdaRolesProps {
    transactionsTable: Table;
}

export class TransactionLambdasRoles extends Construct {
    private readonly itemTable: ITable;
    private readonly transactionTable: ITable;
    public getTransactionsLambdaRole: Role;
    public loadTransactionsLambdaRole: Role;
    public receiveTransactionsLambdaRole: IRole;
    public newTransactionLambdaRole: IRole;

    constructor(scope: Construct, id: string, props: TransactionLambdaRolesProps) {
        super(scope, id);

        this.itemTable = Table.fromTableArn(this, "PlaidItemsTable", PLAID_ITEMS_DDB_TABLE_ARN);
        this.transactionTable = props.transactionsTable;

        this.getTransactionsLambdaRole = new Role(this, 'GetTransactionsLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
        });

        this.loadTransactionsLambdaRole = new Role(this, 'LoadTransactionsLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
            inlinePolicies: {
                plaidSecretsPolicy: new PolicyDocument({statements: [
                        new PolicyStatement(({
                            resources: [PLAID_SECRETS_ARN],
                            effect: Effect.ALLOW,
                            actions: [
                                "secretsmanager:GetSecretValue",
                                "secretsmanager:DescribeSecret",
                                "secretsmanager:ListSecretVersionIds",
                            ]
                        }))
                    ]} )
            }
        });
        this.receiveTransactionsLambdaRole = new Role(this, 'ReceiveTransactionsLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        })

        this.receiveTransactionsLambdaRole.addToPrincipalPolicy( new PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }))

        this.newTransactionLambdaRole = new Role(this, 'NewTransactionLambdarole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
        })

        this.newTransactionLambdaRole.addToPrincipalPolicy( new PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }))

        this.itemTable.grantReadWriteData(this.loadTransactionsLambdaRole);
        this.itemTable.grantReadWriteData(this.receiveTransactionsLambdaRole)
        this.itemTable.grantReadData(this.newTransactionLambdaRole);
        this.transactionTable.grantReadWriteData(this.receiveTransactionsLambdaRole);
        this.transactionTable.grantReadData(this.getTransactionsLambdaRole);
    }
}

export class MessageLambdaRoles extends Construct {
    public sendMessageLambdaRole: IRole;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.sendMessageLambdaRole = new Role(this, 'SendMessageLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        })

    }
}