package plaid;

// Wrapper on Plaid Transactions. Removes unnecessary info.
// @TODO: Make this class immutable and add builder.
public class Transaction {
    public Double amount;

    // This maps to Plaid "name"
    public String description;
    public String originalDescription;
    public String merchantName;
    public String date;

    public String accountId;
    public String transactionId;

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", originalDescription='" + originalDescription + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", date='" + date + '\'' +
                ", accountId='" + accountId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
