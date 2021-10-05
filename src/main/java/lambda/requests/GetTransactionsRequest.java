package lambda.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// LambdaRequest for getting Transactions for User.
@Data
@NoArgsConstructor
public class GetTransactionsRequest {

    public String user;
    public String institutionName;
    public String accountId; //Nullable
    public String startDate;
    public String endDate;

}
