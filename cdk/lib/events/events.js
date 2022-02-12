"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MessageEvents = exports.NewTransactionEvents = void 0;
const core_1 = require("@aws-cdk/core");
const aws_events_1 = require("@aws-cdk/aws-events");
const aws_events_targets_1 = require("@aws-cdk/aws-events-targets");
const constants_1 = require("../constants");
class NewTransactionEvents extends core_1.Construct {
    constructor(scope, id, props) {
        super(scope, id);
        this.transactionsBus = new aws_events_1.EventBus(this, 'TransactionsBus', {
            eventBusName: 'TransactionsBus'
        });
        this.newTransactionRule = new aws_events_1.Rule(this, 'NewTransactionRule', {
            eventBus: this.transactionsBus,
            description: "Invokes NewTransactionLambda with new Transaction info",
            ruleName: 'NewTransactionRule',
            targets: [new aws_events_targets_1.LambdaFunction(props.newTransactionLambda, {
                    event: aws_events_1.RuleTargetInput.fromEventPath("$.detail")
                })],
            eventPattern: {
                source: [constants_1.RECEIVE_TRANSACTIONS_SOURCE],
                detailType: [constants_1.NEW_TRANSACTION_DETAIL_TYPE]
            },
        });
    }
}
exports.NewTransactionEvents = NewTransactionEvents;
class MessageEvents extends core_1.Construct {
    constructor(scope, id, props) {
        super(scope, id);
        this.messagesBus = new aws_events_1.EventBus(this, 'TxBus', {
            eventBusName: 'SmsBus'
        });
        this.newTransactionRule = new aws_events_1.Rule(this, 'NewMessageRule', {
            eventBus: this.messagesBus,
            description: "Invokes NewTransactionLambda with new Transaction info",
            ruleName: 'NewTransactionRule',
            targets: [new aws_events_targets_1.LambdaFunction(props.sendMessageLambda, {
                    event: aws_events_1.RuleTargetInput.fromEventPath("$.detail")
                })],
            eventPattern: {
                source: [constants_1.NEW_TRANSACTION_SOURCE],
                detailType: [constants_1.NEW_MESSAGE_DETAIL_TYPE]
            },
        });
    }
}
exports.MessageEvents = MessageEvents;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiZXZlbnRzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiZXZlbnRzLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLHdDQUF3QztBQUN4QyxvREFBb0U7QUFDcEUsb0VBQTJEO0FBRTNELDRDQUtzQjtBQU10QixNQUFhLG9CQUFxQixTQUFRLGdCQUFTO0lBSS9DLFlBQVksS0FBZ0IsRUFBRSxFQUFVLEVBQUUsS0FBZ0M7UUFDdEUsS0FBSyxDQUFDLEtBQUssRUFBRSxFQUFFLENBQUMsQ0FBQztRQUVqQixJQUFJLENBQUMsZUFBZSxHQUFHLElBQUkscUJBQVEsQ0FBQyxJQUFJLEVBQUUsaUJBQWlCLEVBQUU7WUFDekQsWUFBWSxFQUFFLGlCQUFpQjtTQUNsQyxDQUFDLENBQUE7UUFFRixJQUFJLENBQUMsa0JBQWtCLEdBQUcsSUFBSSxpQkFBSSxDQUFDLElBQUksRUFBRSxvQkFBb0IsRUFBRTtZQUMzRCxRQUFRLEVBQUUsSUFBSSxDQUFDLGVBQWU7WUFDOUIsV0FBVyxFQUFFLHdEQUF3RDtZQUNyRSxRQUFRLEVBQUUsb0JBQW9CO1lBQzlCLE9BQU8sRUFBRSxDQUFDLElBQUksbUNBQWMsQ0FBQyxLQUFLLENBQUMsb0JBQW9CLEVBQUU7b0JBQ3JELEtBQUssRUFBRSw0QkFBZSxDQUFDLGFBQWEsQ0FBQyxVQUFVLENBQUM7aUJBQ25ELENBQUMsQ0FBQztZQUNILFlBQVksRUFBRTtnQkFDVixNQUFNLEVBQUUsQ0FBQyx1Q0FBMkIsQ0FBQztnQkFDckMsVUFBVSxFQUFFLENBQUMsdUNBQTJCLENBQUM7YUFDNUM7U0FFSixDQUFDLENBQUE7SUFDTixDQUFDO0NBQ0o7QUF6QkQsb0RBeUJDO0FBTUQsTUFBYSxhQUFjLFNBQVEsZ0JBQVM7SUFJeEMsWUFBWSxLQUFnQixFQUFFLEVBQVUsRUFBRSxLQUF5QjtRQUMvRCxLQUFLLENBQUMsS0FBSyxFQUFFLEVBQUUsQ0FBQyxDQUFDO1FBRWpCLElBQUksQ0FBQyxXQUFXLEdBQUcsSUFBSSxxQkFBUSxDQUFDLElBQUksRUFBRSxPQUFPLEVBQUU7WUFDM0MsWUFBWSxFQUFFLFFBQVE7U0FDekIsQ0FBQyxDQUFBO1FBRUYsSUFBSSxDQUFDLGtCQUFrQixHQUFHLElBQUksaUJBQUksQ0FBQyxJQUFJLEVBQUUsZ0JBQWdCLEVBQUU7WUFDdkQsUUFBUSxFQUFFLElBQUksQ0FBQyxXQUFXO1lBQzFCLFdBQVcsRUFBRSx3REFBd0Q7WUFDckUsUUFBUSxFQUFFLG9CQUFvQjtZQUM5QixPQUFPLEVBQUUsQ0FBQyxJQUFJLG1DQUFjLENBQUMsS0FBSyxDQUFDLGlCQUFpQixFQUFFO29CQUNsRCxLQUFLLEVBQUUsNEJBQWUsQ0FBQyxhQUFhLENBQUMsVUFBVSxDQUFDO2lCQUNuRCxDQUFDLENBQUM7WUFDSCxZQUFZLEVBQUU7Z0JBQ1YsTUFBTSxFQUFFLENBQUMsa0NBQXNCLENBQUM7Z0JBQ2hDLFVBQVUsRUFBRSxDQUFDLG1DQUF1QixDQUFDO2FBQ3hDO1NBRUosQ0FBQyxDQUFBO0lBQ04sQ0FBQztDQUNKO0FBekJELHNDQXlCQyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7Q29uc3RydWN0fSBmcm9tIFwiQGF3cy1jZGsvY29yZVwiO1xuaW1wb3J0IHtFdmVudEJ1cywgUnVsZSwgUnVsZVRhcmdldElucHV0fSBmcm9tIFwiQGF3cy1jZGsvYXdzLWV2ZW50c1wiO1xuaW1wb3J0IHtMYW1iZGFGdW5jdGlvbn0gZnJvbSBcIkBhd3MtY2RrL2F3cy1ldmVudHMtdGFyZ2V0c1wiO1xuaW1wb3J0IHtGdW5jdGlvbn0gZnJvbSBcIkBhd3MtY2RrL2F3cy1sYW1iZGFcIjtcbmltcG9ydCB7XG4gICAgTkVXX01FU1NBR0VfREVUQUlMX1RZUEUsXG4gICAgTkVXX1RSQU5TQUNUSU9OX0RFVEFJTF9UWVBFLFxuICAgIE5FV19UUkFOU0FDVElPTl9TT1VSQ0UsXG4gICAgUkVDRUlWRV9UUkFOU0FDVElPTlNfU09VUkNFXG59IGZyb20gXCIuLi9jb25zdGFudHNcIjtcblxuZXhwb3J0IGludGVyZmFjZSBOZXdUcmFuc2FjdGlvbkV2ZW50c1Byb3BzIHtcbiAgICBuZXdUcmFuc2FjdGlvbkxhbWJkYTogRnVuY3Rpb247XG59XG5cbmV4cG9ydCBjbGFzcyBOZXdUcmFuc2FjdGlvbkV2ZW50cyBleHRlbmRzIENvbnN0cnVjdCB7XG4gICAgcHVibGljIHJlYWRvbmx5IHRyYW5zYWN0aW9uc0J1czogRXZlbnRCdXM7XG4gICAgcHVibGljIHJlYWRvbmx5IG5ld1RyYW5zYWN0aW9uUnVsZTogUnVsZTtcblxuICAgIGNvbnN0cnVjdG9yKHNjb3BlOiBDb25zdHJ1Y3QsIGlkOiBzdHJpbmcsIHByb3BzOiBOZXdUcmFuc2FjdGlvbkV2ZW50c1Byb3BzKSB7XG4gICAgICAgIHN1cGVyKHNjb3BlLCBpZCk7XG5cbiAgICAgICAgdGhpcy50cmFuc2FjdGlvbnNCdXMgPSBuZXcgRXZlbnRCdXModGhpcywgJ1RyYW5zYWN0aW9uc0J1cycsIHtcbiAgICAgICAgICAgIGV2ZW50QnVzTmFtZTogJ1RyYW5zYWN0aW9uc0J1cydcbiAgICAgICAgfSlcblxuICAgICAgICB0aGlzLm5ld1RyYW5zYWN0aW9uUnVsZSA9IG5ldyBSdWxlKHRoaXMsICdOZXdUcmFuc2FjdGlvblJ1bGUnLCB7XG4gICAgICAgICAgICBldmVudEJ1czogdGhpcy50cmFuc2FjdGlvbnNCdXMsXG4gICAgICAgICAgICBkZXNjcmlwdGlvbjogXCJJbnZva2VzIE5ld1RyYW5zYWN0aW9uTGFtYmRhIHdpdGggbmV3IFRyYW5zYWN0aW9uIGluZm9cIixcbiAgICAgICAgICAgIHJ1bGVOYW1lOiAnTmV3VHJhbnNhY3Rpb25SdWxlJyxcbiAgICAgICAgICAgIHRhcmdldHM6IFtuZXcgTGFtYmRhRnVuY3Rpb24ocHJvcHMubmV3VHJhbnNhY3Rpb25MYW1iZGEsIHtcbiAgICAgICAgICAgICAgICBldmVudDogUnVsZVRhcmdldElucHV0LmZyb21FdmVudFBhdGgoXCIkLmRldGFpbFwiKVxuICAgICAgICAgICAgfSldLFxuICAgICAgICAgICAgZXZlbnRQYXR0ZXJuOiB7XG4gICAgICAgICAgICAgICAgc291cmNlOiBbUkVDRUlWRV9UUkFOU0FDVElPTlNfU09VUkNFXSxcbiAgICAgICAgICAgICAgICBkZXRhaWxUeXBlOiBbTkVXX1RSQU5TQUNUSU9OX0RFVEFJTF9UWVBFXVxuICAgICAgICAgICAgfSxcblxuICAgICAgICB9KVxuICAgIH1cbn1cblxuZXhwb3J0IGludGVyZmFjZSBNZXNzYWdlRXZlbnRzUHJvcHMge1xuICAgIHNlbmRNZXNzYWdlTGFtYmRhOiBGdW5jdGlvbjtcbn1cblxuZXhwb3J0IGNsYXNzIE1lc3NhZ2VFdmVudHMgZXh0ZW5kcyBDb25zdHJ1Y3Qge1xuICAgIHB1YmxpYyByZWFkb25seSBtZXNzYWdlc0J1czogRXZlbnRCdXM7XG4gICAgcHVibGljIHJlYWRvbmx5IG5ld1RyYW5zYWN0aW9uUnVsZTogUnVsZTtcblxuICAgIGNvbnN0cnVjdG9yKHNjb3BlOiBDb25zdHJ1Y3QsIGlkOiBzdHJpbmcsIHByb3BzOiBNZXNzYWdlRXZlbnRzUHJvcHMpIHtcbiAgICAgICAgc3VwZXIoc2NvcGUsIGlkKTtcblxuICAgICAgICB0aGlzLm1lc3NhZ2VzQnVzID0gbmV3IEV2ZW50QnVzKHRoaXMsICdUeEJ1cycsIHtcbiAgICAgICAgICAgIGV2ZW50QnVzTmFtZTogJ1Ntc0J1cydcbiAgICAgICAgfSlcblxuICAgICAgICB0aGlzLm5ld1RyYW5zYWN0aW9uUnVsZSA9IG5ldyBSdWxlKHRoaXMsICdOZXdNZXNzYWdlUnVsZScsIHtcbiAgICAgICAgICAgIGV2ZW50QnVzOiB0aGlzLm1lc3NhZ2VzQnVzLFxuICAgICAgICAgICAgZGVzY3JpcHRpb246IFwiSW52b2tlcyBOZXdUcmFuc2FjdGlvbkxhbWJkYSB3aXRoIG5ldyBUcmFuc2FjdGlvbiBpbmZvXCIsXG4gICAgICAgICAgICBydWxlTmFtZTogJ05ld1RyYW5zYWN0aW9uUnVsZScsXG4gICAgICAgICAgICB0YXJnZXRzOiBbbmV3IExhbWJkYUZ1bmN0aW9uKHByb3BzLnNlbmRNZXNzYWdlTGFtYmRhLCB7XG4gICAgICAgICAgICAgICAgZXZlbnQ6IFJ1bGVUYXJnZXRJbnB1dC5mcm9tRXZlbnRQYXRoKFwiJC5kZXRhaWxcIilcbiAgICAgICAgICAgIH0pXSxcbiAgICAgICAgICAgIGV2ZW50UGF0dGVybjoge1xuICAgICAgICAgICAgICAgIHNvdXJjZTogW05FV19UUkFOU0FDVElPTl9TT1VSQ0VdLFxuICAgICAgICAgICAgICAgIGRldGFpbFR5cGU6IFtORVdfTUVTU0FHRV9ERVRBSUxfVFlQRV1cbiAgICAgICAgICAgIH0sXG5cbiAgICAgICAgfSlcbiAgICB9XG59Il19