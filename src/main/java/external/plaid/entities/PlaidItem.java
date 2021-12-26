package external.plaid.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
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
    @JsonGetter
    public String user();

    @JsonGetter
    public String institutionId();

    @JsonGetter
    public String accessToken();

    @JsonGetter
    public String ID();

    @JsonGetter
    public List<String> availableProducts();
    @JsonGetter
    public List<String> accounts();

    @JsonGetter
    public String dateCreated();

    @JsonGetter
    public String metaData(); // Re

    @JsonGetter// Remaining metadata. Rarely used.
    public boolean webhook();

    @JsonGetter
    public Optional<String> receiverNumber();
}