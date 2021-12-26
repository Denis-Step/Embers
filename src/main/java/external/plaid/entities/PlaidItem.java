package external.plaid.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

// Immutable class that represents response of item creation.
@Value.Immutable
@JsonSerialize
@JsonDeserialize
public interface PlaidItem {
    public String getUser();
    public String getInstitutionId();
    public String getAccessToken();
    public String getId();
    public List<String> getAvailableProducts();
    public List<String> getAccounts();
    public String getDateCreated();
    public String getMetadata(); // Remaining metadata. Rarely used.
    public boolean getWebhook();
    public Optional<String> getReceiverNumber();
}