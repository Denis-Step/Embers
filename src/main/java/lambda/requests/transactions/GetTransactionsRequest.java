package lambda.requests.transactions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// LambdaRequest for getting Transactions for User.
@Data
@NoArgsConstructor
public class GetTransactionsRequest {

    public String user;
    public String startDate;

}
