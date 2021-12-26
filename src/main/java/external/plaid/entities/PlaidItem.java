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
    public String user();
    public String institutionId();
    public String accessToken();
    public String ID();
    public List<String> availableProducts();
    public List<String> accounts();
    public String dateCreated();
    public String metaData(); // Remaining metadata. Rarely used.
    public boolean webhook();
    public Optional<String> receiverNumber();
}