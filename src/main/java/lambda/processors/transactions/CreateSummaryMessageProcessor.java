package lambda.processors.transactions;

import plaid.entities.Transaction;

import javax.inject.Inject;
import java.util.List;

public class CreateSummaryMessageProcessor {

    @Inject
    public CreateSummaryMessageProcessor() {}

    public TransactionSummary getSummary(List<Transaction> transactions) {
        return new TransactionSummary(transactions);
    }

    public static class TransactionSummary{
        private final Double netBalance;
        private final int numTransactions;

        public TransactionSummary(List<Transaction> transactionList) {
            netBalance = transactionList.stream()
                    .mapToDouble(tx -> tx.amount)
                    .reduce(0, (subtotal, amount) -> subtotal + amount);
            numTransactions = transactionList.size();
        }

        @Override
        public String toString() {
            return String.format("%o transactions for a total of %f", numTransactions, netBalance);
        }
    }
}
