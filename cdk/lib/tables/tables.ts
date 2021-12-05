import {Construct} from "@aws-cdk/core";
import {AttributeType, BillingMode, Table} from "@aws-cdk/aws-dynamodb";

export class JPTables extends Construct {
    public readonly transactionsTable: Table;
    public readonly itemsTable: Table;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.transactionsTable = new Table(this, 'Transactions', {
            tableName: 'Transactions',
            partitionKey: {name: 'user', type: AttributeType.STRING},
            sortKey: {name: 'dateAmountTransactionId', type: AttributeType.STRING},
            billingMode: BillingMode.PAY_PER_REQUEST,
        })
        this.transactionsTable.addLocalSecondaryIndex({
            indexName: 'institutionNameIndex',
            sortKey: {name: 'institutionName', type: AttributeType.STRING}
        })

        this.itemsTable = new Table(this, 'PlaidItems', {
            tableName: 'PlaidItems',
            partitionKey: {name: 'user', type: AttributeType.STRING},
            sortKey: {name: 'institutionIdAccessToken', type: AttributeType.STRING},
            billingMode: BillingMode.PAY_PER_REQUEST
        })
    }
}
