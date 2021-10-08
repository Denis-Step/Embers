package lambda.requests;

import dynamo.PlaidTransactionDAO;
import lombok.Data;
import lombok.NoArgsConstructor;
import plaid.entities.Transaction;

import java.util.List;

@Data
@NoArgsConstructor
public class SendTransactionsMessageRequest {

    public List<Transaction> transactions;

    // Nullable
    public String receiverNumber;

}
