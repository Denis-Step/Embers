package lambda.requests.transactions;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Load new Transactions from Plaid.
 */
@Data
@NoArgsConstructor
public class PullNewTransactionsRequest {

    public String user;
    public String institutionName;
    public String accountId; // Nullable
    public String startDate; // Nullable
    public String endDate; // Nullable

}
