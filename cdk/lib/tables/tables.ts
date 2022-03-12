import {Construct} from "@aws-cdk/core";
import {AttributeType, BillingMode, Table, TableProps} from "@aws-cdk/aws-dynamodb";

class TransactionsTable extends Construct {
    public readonly underlyingTable: Table;

    constructor(scope: Construct, id: string, props?: TableProps) {
        super(scope, id);

        this.underlyingTable =  new Table(scope, 'TransactionsT', {
            tableName: 'Transactions',
            partitionKey: {name: 'user', type: AttributeType.STRING},
            sortKey: {name: 'dateTransactionId', type: AttributeType.STRING},
            billingMode: BillingMode.PAY_PER_REQUEST
        })

        this.underlyingTable.addLocalSecondaryIndex({
            indexName: "amountIndex",
            sortKey: {name: "amount", type: AttributeType.NUMBER}
        })

        this.underlyingTable.addLocalSecondaryIndex({
            indexName: "descriptionIndex",
            sortKey: {name: "description", type: AttributeType.STRING}
        })

        this.underlyingTable.addLocalSecondaryIndex({
            indexName: 'institutionNameIndex',
            sortKey: {name: 'institutionName', type: AttributeType.STRING}
        })

        this.underlyingTable.addGlobalSecondaryIndex({
            indexName: "accountIdIndex",
            partitionKey: {name: "accountIdIndex", type: AttributeType.STRING},
            sortKey: {name: "dateTransactionId", type: AttributeType.STRING}
        })
    }
}

class PlaidItemsTable extends Construct {
    public readonly underlyingTable: Table;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.underlyingTable = new Table(scope, 'PlaidItems', {
            tableName: 'PlaidItems',
            partitionKey: {name: 'user', type: AttributeType.STRING},
            sortKey: {name: 'institutionIdAccessToken', type: AttributeType.STRING},
            billingMode: BillingMode.PAY_PER_REQUEST
        })
    }
}

export class JPTables extends Construct {
    public readonly transactionsTable: Table;
    public readonly itemsTable: Table;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        this.transactionsTable = new TransactionsTable(this, "TransactionTable2").underlyingTable;
        this.itemsTable = new PlaidItemsTable(this, "ItemTable2").underlyingTable;
    }
}
