import * as lambda from "@aws-cdk/aws-lambda";
import * as path from "path";
import {Function} from "@aws-cdk/aws-lambda"
import {Construct, Duration} from "@aws-cdk/core";
import {MessageLambdaRoles} from "./lambdaroles";


export class MessageLambdas extends Construct {
    public readonly sendMessageLambda: Function

    private readonly roles: MessageLambdaRoles;

    constructor(scope: Construct, id: string) {
        super(scope, id);
        this.roles = new MessageLambdaRoles(this, 'MessageLambdaRoles');

        this.sendMessageLambda = new lambda.Function(this, 'SendMessageLambda', {
            runtime: lambda.Runtime.JAVA_8_CORRETTO,
            handler: "lambda.handlers.SendMessageHandler",
            code: lambda.Code.fromAsset(path.join(__dirname, 'JavaPlaid-1.0.zip')),
            memorySize: 512,
            timeout: Duration.seconds(300)
        });
    }
}