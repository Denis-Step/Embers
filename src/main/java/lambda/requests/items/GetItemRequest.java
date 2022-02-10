package lambda.requests.items;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

// Immutable class that represents request for Item
@Value.Immutable
@JsonSerialize(as= ImmutableGetItemRequest.class)
@JsonDeserialize(as= ImmutableGetItemRequest.class)
public interface GetItemRequest {

    String getUser();
    @Nullable String getInstitutionIdAccessToken();
}
