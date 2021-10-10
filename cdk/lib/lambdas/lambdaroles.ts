import {IRole, ManagedPolicy, Role, ServicePrincipal} from "@aws-cdk/aws-iam";
import {Construct, Stack, StackProps} from "@aws-cdk/core";
import {ITable, Table} from "@aws-cdk/aws-dynamodb";
import {PLAID_ITEMS_DDB_TABLE_ARN} from "../constants";

export class ItemLambdaRoles extends Stack {
    private readonly itemTable: ITable;
    public createLinkTokenLambdaRole: Role;
    public createItemLambdaRole: IRole;
    public getItemLambdaRole: IRole;

    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);
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