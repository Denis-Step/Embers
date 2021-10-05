package lambda.requests;

import dynamo.PlaidTransactionDAO;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class SendTransactionsMessageRequest {

    public List<PlaidTransactionDAO> transactions;

    // Nullable
    public String receiverNumber;

}
