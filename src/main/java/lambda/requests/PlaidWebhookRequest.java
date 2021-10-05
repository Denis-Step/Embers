package lambda.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaidWebhookRequest {
    public String webhook_type;
    public String webhook_code;
    public String item_id;
    public int new_transactions;

    //Nullable
    public Object error;
}
