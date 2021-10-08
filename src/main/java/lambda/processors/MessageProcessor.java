package lambda.processors;

import dynamo.PlaidTransactionDAO;
import lambda.requests.SendTransactionsMessageRequest;
import plaid.entities.PlaidItem;
import plaid.entities.Transaction;
import twilio.MessageClient;

import javax.inject.Inject;
import java.util.List;

public class MessageProcessor {

    private final MessageClient messageClient;
    private final String DEFAULT_NUMBER = "+19175478272";

    @Inject
    public MessageProcessor(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    public String sendMessage(String receiverNumber, String message) {
        return messageClient.sendMessage(receiverNumber, message).toString();
    }

    /**
     * @param request to send summary of requests.
     * @return Entry point from lambda.
     */
    public String sendMessage(SendTransactionsMessageRequest request) {
        String messageBody = new TransactionSummary(request.getTransactions()).toString();

        if (request.getReceiverNumber() != null) {
            return this.sendMessage(request.getReceiverNumber(), messageBody);
        } else {
            return this.sendMessage(DEFAULT_NUMBER, messageBody);
        }
    }

    public static class TransactionSummary{
        public Double netBalance = 0.00;
        int numTransactions = 0;

        public TransactionSummary(List<Transaction> transactionList) {
            for (Transaction transaction: transactionList) {
                netBalance += transaction.getAmount();
                numTransactions += 1;
            }
        }

        @Override
        public String toString() {
            return "TransactionSummary{" +
                    "netBalance=" + netBalance +
                    ", numTransactions=" + numTransactions +
                    '}';
        }
    }
}
