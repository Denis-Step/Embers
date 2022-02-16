package lambda.requests.transactions;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import external.plaid.entities.PlaidItem;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

/**
 * Poll for new transactions.
 */
@Value.Immutable
@JsonSerialize(as= ImmutablePollTransactionsRequest.class)
@JsonDeserialize(as= ImmutablePollTransactionsRequest.class)
public interface PollTransactionsRequest {

    PlaidItem getPlaidItem();
    @Nullable String getAccountId(); // Nullable
    @Nullable String getStartDate(); // Nullable
    @Nullable String getEndDate(); // Nullable

}
