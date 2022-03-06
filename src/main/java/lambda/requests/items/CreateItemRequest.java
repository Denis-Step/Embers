package lambda.requests.items;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import external.plaid.entities.ImmutablePlaidItem;
import org.immutables.value.Value;

import java.util.List;

/**
 * Lambda Request Type.
 */
@Value.Immutable
public interface CreateItemRequest {

     String getUser();
     String getPublicToken();
    // # {INST_NAME}{INST_ID}
     String getInstitutionId();
     List<String> getAvailableProducts();
     List<String> getAccounts();
     String getDateCreated();
     String getMetadata(); // Remaining metadata. Rarely used.
     boolean getWebhook();
}
