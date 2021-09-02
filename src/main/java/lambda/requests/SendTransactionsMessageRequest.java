package lambda.requests;

import plaid.entities.Transaction;

import java.util.List;

public class SendTransactionsMessageRequest {
    private List<Transaction> transactions;
    private String receiverNumber;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }
}
