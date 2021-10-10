package lambda.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMessageRequest {

    public String message;

    // Nullable
    public String receiverNumber;

}
