package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName="Transactions")
public class TransactionsDAO {
    private String user;
    private String plaidItem;
    private String date;
    private List<String> transactions;

    public TransactionsDAO() {
    }

    @DynamoDBHashKey(attributeName = "User")
    public String getUser() {
        return this.user;
    }

    @DynamoDBHashKey(attributeName = "PlaidItem")
    public String getPlaidItem() {
        return this.plaidItem;
    }

    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() {
        return date;
    }

    @DynamoDBAttribute(attributeName = "Transactions")
    public List<String> getTransactions() {
        return transactions;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlaidItem(String plaidItem) {
        this.plaidItem = plaidItem;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }
}
