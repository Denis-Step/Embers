package lambda.requests;

import dynamo.PlaidTransactionDAO;
import plaid.entities.Transaction;

import java.util.List;

public class SendTransactionsMessageRequest {
    private List<PlaidTransactionDAO> transactions;
    private String receiverNumber;

    public List<PlaidTransactionDAO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<PlaidTransactionDAO> transactions) {
        this.transactions = transactions;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }
}
