"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.JPTables = void 0;
const core_1 = require("@aws-cdk/core");
const aws_dynamodb_1 = require("@aws-cdk/aws-dynamodb");
class JPTables extends core_1.Construct {
    constructor(scope, id) {
        super(scope, id);
        this.transactionsTable = new aws_dynamodb_1.Table(this, 'Transactions', {
            tableName: 'Transactions',
            partitionKey: { name: 'user', type: aws_dynamodb_1.AttributeType.STRING },
            sortKey: { name: 'dateAmountTransactionId', type: aws_dynamodb_1.AttributeType.STRING },
            billingMode: aws_dynamodb_1.BillingMode.PAY_PER_REQUEST,
        });
        this.transactionsTable.addLocalSecondaryIndex({
            indexName: 'institutionNameIndex',
            sortKey: { name: 'institutionName', type: aws_dynamodb_1.AttributeType.STRING }
        });
        this.itemsTable = new aws_dynamodb_1.Table(this, 'PlaidItems', {
            tableName: 'PlaidItems',
            partitionKey: { name: 'user', type: aws_dynamodb_1.AttributeType.STRING },
            sortKey: { name: 'institutionIdAccessToken', type: aws_dynamodb_1.AttributeType.STRING },
            billingMode: aws_dynamodb_1.BillingMode.PAY_PER_REQUEST
        });
    }
}
exports.JPTables = JPTables;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoidGFibGVzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsidGFibGVzLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLHdDQUF3QztBQUN4Qyx3REFBd0U7QUFFeEUsTUFBYSxRQUFTLFNBQVEsZ0JBQVM7SUFJbkMsWUFBWSxLQUFnQixFQUFFLEVBQVU7UUFDcEMsS0FBSyxDQUFDLEtBQUssRUFBRSxFQUFFLENBQUMsQ0FBQztRQUVqQixJQUFJLENBQUMsaUJBQWlCLEdBQUcsSUFBSSxvQkFBSyxDQUFDLElBQUksRUFBRSxjQUFjLEVBQUU7WUFDckQsU0FBUyxFQUFFLGNBQWM7WUFDekIsWUFBWSxFQUFFLEVBQUMsSUFBSSxFQUFFLE1BQU0sRUFBRSxJQUFJLEVBQUUsNEJBQWEsQ0FBQyxNQUFNLEVBQUM7WUFDeEQsT0FBTyxFQUFFLEVBQUMsSUFBSSxFQUFFLHlCQUF5QixFQUFFLElBQUksRUFBRSw0QkFBYSxDQUFDLE1BQU0sRUFBQztZQUN0RSxXQUFXLEVBQUUsMEJBQVcsQ0FBQyxlQUFlO1NBQzNDLENBQUMsQ0FBQTtRQUNGLElBQUksQ0FBQyxpQkFBaUIsQ0FBQyxzQkFBc0IsQ0FBQztZQUMxQyxTQUFTLEVBQUUsc0JBQXNCO1lBQ2pDLE9BQU8sRUFBRSxFQUFDLElBQUksRUFBRSxpQkFBaUIsRUFBRSxJQUFJLEVBQUUsNEJBQWEsQ0FBQyxNQUFNLEVBQUM7U0FDakUsQ0FBQyxDQUFBO1FBRUYsSUFBSSxDQUFDLFVBQVUsR0FBRyxJQUFJLG9CQUFLLENBQUMsSUFBSSxFQUFFLFlBQVksRUFBRTtZQUM1QyxTQUFTLEVBQUUsWUFBWTtZQUN2QixZQUFZLEVBQUUsRUFBQyxJQUFJLEVBQUUsTUFBTSxFQUFFLElBQUksRUFBRSw0QkFBYSxDQUFDLE1BQU0sRUFBQztZQUN4RCxPQUFPLEVBQUUsRUFBQyxJQUFJLEVBQUUsMEJBQTBCLEVBQUUsSUFBSSxFQUFFLDRCQUFhLENBQUMsTUFBTSxFQUFDO1lBQ3ZFLFdBQVcsRUFBRSwwQkFBVyxDQUFDLGVBQWU7U0FDM0MsQ0FBQyxDQUFBO0lBQ04sQ0FBQztDQUNKO0FBekJELDRCQXlCQyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7Q29uc3RydWN0fSBmcm9tIFwiQGF3cy1jZGsvY29yZVwiO1xuaW1wb3J0IHtBdHRyaWJ1dGVUeXBlLCBCaWxsaW5nTW9kZSwgVGFibGV9IGZyb20gXCJAYXdzLWNkay9hd3MtZHluYW1vZGJcIjtcblxuZXhwb3J0IGNsYXNzIEpQVGFibGVzIGV4dGVuZHMgQ29uc3RydWN0IHtcbiAgICBwdWJsaWMgcmVhZG9ubHkgdHJhbnNhY3Rpb25zVGFibGU6IFRhYmxlO1xuICAgIHB1YmxpYyByZWFkb25seSBpdGVtc1RhYmxlOiBUYWJsZTtcblxuICAgIGNvbnN0cnVjdG9yKHNjb3BlOiBDb25zdHJ1Y3QsIGlkOiBzdHJpbmcpIHtcbiAgICAgICAgc3VwZXIoc2NvcGUsIGlkKTtcblxuICAgICAgICB0aGlzLnRyYW5zYWN0aW9uc1RhYmxlID0gbmV3IFRhYmxlKHRoaXMsICdUcmFuc2FjdGlvbnMnLCB7XG4gICAgICAgICAgICB0YWJsZU5hbWU6ICdUcmFuc2FjdGlvbnMnLFxuICAgICAgICAgICAgcGFydGl0aW9uS2V5OiB7bmFtZTogJ3VzZXInLCB0eXBlOiBBdHRyaWJ1dGVUeXBlLlNUUklOR30sXG4gICAgICAgICAgICBzb3J0S2V5OiB7bmFtZTogJ2RhdGVBbW91bnRUcmFuc2FjdGlvbklkJywgdHlwZTogQXR0cmlidXRlVHlwZS5TVFJJTkd9LFxuICAgICAgICAgICAgYmlsbGluZ01vZGU6IEJpbGxpbmdNb2RlLlBBWV9QRVJfUkVRVUVTVCxcbiAgICAgICAgfSlcbiAgICAgICAgdGhpcy50cmFuc2FjdGlvbnNUYWJsZS5hZGRMb2NhbFNlY29uZGFyeUluZGV4KHtcbiAgICAgICAgICAgIGluZGV4TmFtZTogJ2luc3RpdHV0aW9uTmFtZUluZGV4JyxcbiAgICAgICAgICAgIHNvcnRLZXk6IHtuYW1lOiAnaW5zdGl0dXRpb25OYW1lJywgdHlwZTogQXR0cmlidXRlVHlwZS5TVFJJTkd9XG4gICAgICAgIH0pXG5cbiAgICAgICAgdGhpcy5pdGVtc1RhYmxlID0gbmV3IFRhYmxlKHRoaXMsICdQbGFpZEl0ZW1zJywge1xuICAgICAgICAgICAgdGFibGVOYW1lOiAnUGxhaWRJdGVtcycsXG4gICAgICAgICAgICBwYXJ0aXRpb25LZXk6IHtuYW1lOiAndXNlcicsIHR5cGU6IEF0dHJpYnV0ZVR5cGUuU1RSSU5HfSxcbiAgICAgICAgICAgIHNvcnRLZXk6IHtuYW1lOiAnaW5zdGl0dXRpb25JZEFjY2Vzc1Rva2VuJywgdHlwZTogQXR0cmlidXRlVHlwZS5TVFJJTkd9LFxuICAgICAgICAgICAgYmlsbGluZ01vZGU6IEJpbGxpbmdNb2RlLlBBWV9QRVJfUkVRVUVTVFxuICAgICAgICB9KVxuICAgIH1cbn1cbiJdfQ==