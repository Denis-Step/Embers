import { IRole, Role } from "@aws-cdk/aws-iam";
import { Construct } from "@aws-cdk/core";
import { Table } from "@aws-cdk/aws-dynamodb";
export interface ItemLambdaRolesProps {
    itemsTable: Table;
}
export declare class ItemLambdaRoles extends Construct {
    createLinkTokenLambdaRole: Role;
    createItemLambdaRole: IRole;
    getItemLambdaRole: IRole;
    constructor(scope: Construct, id: string, props: ItemLambdaRolesProps);
}
export interface TransactionLambdaRolesProps {
    transactionsTable: Table;
    itemsTable: Table;
}
export declare class TransactionLambdasRoles extends Construct {
    getTransactionsLambdaRole: Role;
    loadTransactionsLambdaRole: Role;
    receiveTransactionsLambdaRole: IRole;
    newTransactionLambdaRole: IRole;
    constructor(scope: Construct, id: string, props: TransactionLambdaRolesProps);
}
export declare class MessageLambdaRoles extends Construct {
    sendMessageLambdaRole: IRole;
    constructor(scope: Construct, id: string);
}
