package lambda.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// LambdaRequest
@Data
@NoArgsConstructor
public class CreateLinkTokenRequest {

    public String user;
    public List<String> products;

    // Nullable
    public boolean webhook;
}
