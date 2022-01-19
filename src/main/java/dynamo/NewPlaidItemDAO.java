package dynamo;

import external.plaid.entities.PlaidItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class NewPlaidItemDAO {

    private final DynamoDbTable<PlaidItem> table;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewPlaidItemDAO.class);

    @Inject
    public NewPlaidItemDAO(DynamoDbTable<PlaidItem> table) {
        this.table = table;
    }

    /**
     * @param user partition key
     * @param institutionIdAccessToken {INSTITUTION_ID#ACCESS_TOKEN} Prefer this query.
     * @return List of PlaidItems.
     */
    public List<PlaidItem> query(String user, String institutionIdAccessToken) {
        PageIterable<PlaidItem> plaidItemPages = this.paginatedQuery(user, institutionIdAccessToken);

        return plaidItemPages.items().stream().collect(Collectors.toList());
    }

    private PageIterable<PlaidItem> paginatedQuery(String user, String institutionIdAccessToken) {
        return table.query(r -> r.queryConditional(QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(user)
                        .sortValue(institutionIdAccessToken)
                        .build())));
    }

}
