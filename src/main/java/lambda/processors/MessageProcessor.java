package lambda.processors;

import lambda.requests.SendTransactionsMessageRequest;
import plaid.entities.Transaction;
import twilio.MessageClient;

import javax.inject.Inject;
import java.util.List;

public class MessageProcessor {

    private final MessageClient messageClient;

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
        return this.sendMessage(request.getReceiverNumber(), messageBody);
    }

    public static class TransactionSummary{
        Double netBalance;
        int numTransactions;

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
