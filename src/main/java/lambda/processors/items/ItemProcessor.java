package lambda.processors.items;

import dynamo.NewPlaidItemDAO;
import dynamo.NewPlaidItemDAO.MultipleItemsFoundException;
import dynamo.PlaidItemDAO;
import lambda.requests.items.CreateItemRequest;
import lambda.requests.items.GetItemRequest;
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

// Params: Link --> User, InstitutionId,
public class ItemProcessor {

    private final ItemCreator itemCreator;
    private final NewPlaidItemDAO plaidItemDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcessor.class);

    @Inject
    public ItemProcessor(ItemCreator itemCreator, NewPlaidItemDAO plaidItemDAO) {
        this.itemCreator = itemCreator;
        this.plaidItemDAO = plaidItemDAO;
    }

    // Calls Plaid client to request a new Item and uses info from incoming request
    // to build PlaidItem.
    public PlaidItem createPlaidItem (CreateItemRequest createItemRequest) throws IOException {
        PlaidItem item = createItem(createItemRequest);
        plaidItemDAO.save(item);
        return item;
    }

    public List<PlaidItem> getItems(String user,  String institutionIdAccessToken) {
        return plaidItemDAO.query(user, institutionIdAccessToken);
    }

    /**
     * Use when needing a specific item.
     * @param request Request
     * @return Result of DAO call.
     */
    public Optional<PlaidItem> getItem(GetItemRequest request) {
        try {
            Optional<PlaidItem> plaidItemOptional = getItem(request.getUser(), request.getInstitutionIdAccessToken());
            LOGGER.info("Plaid Item Optional: {}",
                    plaidItemOptional.isPresent() ? plaidItemOptional.toString() : "NONE");
            return plaidItemOptional;
        } catch (MultipleItemsFoundException e) {
            LOGGER.info(e.toString());
            return Optional.empty();
        }
    }

    public Optional<PlaidItem> getItem(String user, String institutionIdAccessToken) throws MultipleItemsFoundException {
        return plaidItemDAO.get(user, institutionIdAccessToken);
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
                .webhook(createItemRequest.getWebhook())
                .build();
    }

}
