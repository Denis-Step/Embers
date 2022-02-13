package external.plaid.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Wrapper on Plaid Transactions. Removes unnecessary info.
 * Uses get* format for out-the-box JSON serialization in AWS Lambda handler request types.
 */
@Value.Immutable
@JsonSerialize(as= ImmutableTransaction.class)
@JsonDeserialize(as= ImmutableTransaction.class)
public interface Transaction {
    String getUser();
    String getDate();
    String getTransactionId();
    String getInstitutionName();
    String getAccountId();
    double getAmount();

    /**
     * This maps to Plaid name.
     * @return description of transaction
     */
    String getDescription();
    String getOriginalDescription();
    String getMerchantName();
}
