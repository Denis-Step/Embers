package external.plaid.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.annotations.JsonAdapter;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/**
 * Immutable class that represents response of item creation.
 * Uses get* format for out-the-box JSON serialization in AWS Lambda handler request types.
 */
@Value.Immutable
@JsonSerialize(as= ImmutablePlaidItem.class)
@JsonDeserialize(as= ImmutablePlaidItem.class)
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

//    static PlaidItem fromJson(String jsonString) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new Jdk8Module());
//        objectMapper.registerModule(new JavaTimeModule());
//
//        try {
//            return objectMapper.readValue(jsonString, PlaidItem.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}