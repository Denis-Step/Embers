import {IRole, ManagedPolicy, PolicyStatement, Role, ServicePrincipal} from "@aws-cdk/aws-iam";
import {Construct, Stack, StackProps} from "@aws-cdk/core";
import {ITable, Table} from "@aws-cdk/aws-dynamodb";
import {PLAID_ITEMS_DDB_TABLE_ARN, TRANSACTIONS_DDB_TABLE_ARN} from "../constants";

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
            ]
        });

        this.createItemLambdaRole = new Role(this, 'CreateItemLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
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


export class TransactionLambdasRoles extends Construct {
    private readonly itemTable: ITable;
    private readonly transactionTable: ITable;
    public loadTransactionsLambdarole: Role;
    public receiveTransactionsLambdaRole: IRole;
    public newTransactionLambdaRole: IRole;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.itemTable = Table.fromTableArn(this, "PlaidItemsTable", PLAID_ITEMS_DDB_TABLE_ARN);
        this.transactionTable = Table.fromTableArn(this, "TransactionsTable", TRANSACTIONS_DDB_TABLE_ARN);

        this.loadTransactionsLambdarole = new Role(this, 'CreateLinkTokenLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        });

        this.receiveTransactionsLambdaRole = new Role(this, 'CreateItemLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ]
        })

        this.receiveTransactionsLambdaRole.addToPrincipalPolicy( new PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }))

        this.newTransactionLambdaRole = new Role(this, 'GetItemLambdaRole', {
            assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
            ],
        })

        this.newTransactionLambdaRole.addToPrincipalPolicy( new PolicyStatement({
            resources: ["*"],
            actions: ["events:PutEvents", "events:ListRules"]
        }))

        this.itemTable.grantReadWriteData(this.loadTransactionsLambdarole);
        this.itemTable.grantReadWriteData(this.receiveTransactionsLambdaRole)
        this.transactionTable.grantReadWriteData(this.receiveTransactionsLambdaRole);
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