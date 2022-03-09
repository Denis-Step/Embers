package external.plaid.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/**
 * Immutable class that represents response of item creation.
 * Uses get* format for out-the-box JSON serialization in AWS Lambda handler request types.
 */
@Value.Immutable
public interface PlaidItem {
    String getUser();
    String getInstitutionId();
    String getAccessToken();
    String getId();
    List<String> getAvailableProducts();
    List<String> getAccounts();
    String getDateCreated();
    String getMetadata(); // Remaining metadata. Rarely used.
    boolean getWebhook();
    Optional<String> getReceiverNumber();
}