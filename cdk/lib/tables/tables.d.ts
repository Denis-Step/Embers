import { Construct } from "@aws-cdk/core";
import { Table } from "@aws-cdk/aws-dynamodb";
export declare class JPTables extends Construct {
    readonly transactionsTable: Table;
    readonly itemsTable: Table;
    constructor(scope: Construct, id: string);
}
