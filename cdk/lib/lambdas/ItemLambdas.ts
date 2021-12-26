import * as lambda from "@aws-cdk/aws-lambda";
import {Construct, Duration, Stack, StackProps} from "@aws-cdk/core";
import * as path from "path";
import {ItemLambdaRoles} from "./lambdaroles";
import {Table} from "@aws-cdk/aws-dynamodb";

export class ItemLambdasProps {
    itemsTable: Table
}

export class ItemLambdas extends Construct {
    public readonly createLinkTokenLambda: lambda.Function;
    public readonly createItemLambda: lambda.Function;
    public readonly getItemLambda: lambda.Function;
    public roles: ItemLambdaRoles;

    constructor(scope: Construct, id: string, props: ItemLambdasProps) {
        super(scope, id);

        this.roles = new ItemLambdaRoles(scope, 'ItemLambdaRoles', {
            itemsTable: props.itemsTable
        });

        this.createLinkTokenLambda = new lambda.Function(this, 'LinkTokenLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.CreateLinkTokenHandler",

            // Code supports local build steps, S3 buckets, and inlining.
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            timeout: Duration.seconds(300),
            role: this.roles.createLinkTokenLambdaRole
        })

        this.createItemLambda = new lambda.Function(this, 'CreateItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.CreateItemHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            timeout: Duration.seconds(300),
            role: this.roles.createItemLambdaRole
        });

        this.getItemLambda = new lambda.Function(this, 'GetItemLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.GetItemsHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            timeout: Duration.seconds(300),
            role: this.roles.getItemLambdaRole
        });
    }
}