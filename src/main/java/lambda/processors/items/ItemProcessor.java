package lambda.processors.items;

import dynamo.NewPlaidItemDAO;
import dynamo.NewPlaidItemDAO.MultipleItemsFoundException;
import lambda.requests.items.CreateItemRequest;
import external.plaid.clients.ItemCreator;
import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;
import external.plaid.responses.PublicTokenExchangeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Item-related functionality wrapping Plaid clients & Data Acces. Create Items with Link Tokens
 * & Query for existing Items.
 */
// Params: Link --> User, InstitutionId,
public class ItemProcessor {

    private final ItemCreator itemCreator;
    private final NewPlaidItemDAO plaidItemDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcessor.class);

    /**
     * Takes Plaid ItemCreator client & Item DAO in constructor.
     * @param itemCreator Plaid client to create items
     * @param plaidItemDAO DAO
     */
    @Inject
    public ItemProcessor(ItemCreator itemCreator, NewPlaidItemDAO plaidItemDAO) {
        this.itemCreator = itemCreator;
        this.plaidItemDAO = plaidItemDAO;
    }

    /**
     * Calls Plaid client to request a new Item and uses info from incoming request
     * to build PlaidItem.
     * @param createItemRequest incoming request
     * @return built & saved PlaidItem
     * @throws IOException
     */
    public PlaidItem createPlaidItem (CreateItemRequest createItemRequest) throws IOException {
        PlaidItem item = createItem(createItemRequest);
        plaidItemDAO.save(item);
        return item;
    }

    /**
     * Get all items for a user.
     * @param user the user associated with this item
     * @param institutionIdAccessToken sort key
     * @return
     */
    public List<PlaidItem> getItems(String user,  String institutionIdAccessToken) {
        return plaidItemDAO.query(user, institutionIdAccessToken);
    }

    /**
     * Finds a specific item. If and only if one matching item is found, it returns a full Optional.
     * Otherwise, it will return an empty Optional.
     * @param user
     * @param institutionIdAccessToken
     * @return
     * @throws MultipleItemsFoundException
     */
    public Optional<PlaidItem> getItem(String user, String institutionIdAccessToken) throws MultipleItemsFoundException {
        Optional<PlaidItem> plaidItemOptional = plaidItemDAO.get(user, institutionIdAccessToken);
        LOGGER.info("Plaid Item Optional: {}",
                plaidItemOptional.isPresent() ? plaidItemOptional.toString() : "NONE");
        return plaidItemOptional;
    }

    private PlaidItem createItem(CreateItemRequest createItemRequest) {
        PublicTokenExchangeResponse itemInfo = this.itemCreator.requestItem(createItemRequest.getPublicToken());

        return ImmutablePlaidItem.builder()
                .id(itemInfo.getID())
                .accessToken(itemInfo.getAccessToken())
                .user(createItemRequest.getUser())
                .dateCreated(createItemRequest.getDateCreated())
                .availableProducts(createItemRequest.getAvailableProducts())
                .accounts(createItemRequest.getAccounts())
                .institutionId(createItemRequest.getInstitutionId())
                .metadata(createItemRequest.getMetadata())
                .webhook(createItemRequest.getWebhookEnabled())
                .build();
    }

}
