import * as lambda from "@aws-cdk/aws-lambda";
import { Construct } from "@aws-cdk/core";
import { ItemLambdaRoles } from "./lambdaroles";
import { Table } from "@aws-cdk/aws-dynamodb";
export declare class ItemLambdasProps {
    itemsTable: Table;
}
export declare class ItemLambdas extends Construct {
    readonly createLinkTokenLambda: lambda.Function;
    readonly createItemLambda: lambda.Function;
    readonly getItemLambda: lambda.Function;
    roles: ItemLambdaRoles;
    constructor(scope: Construct, id: string, props: ItemLambdasProps);
}
