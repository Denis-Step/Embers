package lambda.requests.items;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.immutables.value.Value;

import java.util.List;

// Immutable class that represents request for LinkToken
@Value.Immutable
@JsonSerialize(as= ImmutableCreateLinkTokenRequest.class)
@JsonDeserialize(as= ImmutableCreateLinkTokenRequest.class)
public interface CreateLinkTokenRequest {

    String getUser();
    List<String> getProducts();
    boolean getWebhookEnabled();
}
