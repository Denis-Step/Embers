package lambda.processors.items;

import dynamo.PlaidItemDAO;
import lambda.requests.items.CreateItemRequest;
import lambda.requests.items.GetItemRequest;
import external.plaid.clients.ItemCreator;
import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;
import external.plaid.responses.PublicTokenExchangeResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

// Params: Link --> User, InstitutionId,
public class ItemProcessor {

    private final ItemCreator itemCreator;
    private final PlaidItemDAO plaidItemDAO;

    @Inject
    public ItemProcessor(ItemCreator itemCreator, PlaidItemDAO plaidItemDAO) {
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

    public List<PlaidItem> getItems(String user,  String institution) {
        return plaidItemDAO.query(user, institution);
    }

    // For when a specific Item is required, avoids having to check size farther up the stack.
    // Requires institutionId to make this possible.
    public PlaidItem getItem(GetItemRequest request) throws PlaidItemDAO.ItemException {
        List<PlaidItem> plaidItems = plaidItemDAO.query(request.getUser(), request.getInstitution());

        if (plaidItems.size() > 1) {
            throw new PlaidItemDAO.MultipleItemsFoundException("Found " +
                    plaidItems.size() +
                    " items:" +
                    plaidItems.toString());
        }
        if (plaidItems.size() < 1) {
            throw new PlaidItemDAO.ItemNotFoundException("Item Not Found for User:" +
                    request.getUser() +
                    "And institution:" +
                    request.getInstitution());
        } else {
            return plaidItems.get(0);
        }
    }

    public PlaidItem getItem(String user, String institution) throws PlaidItemDAO.ItemException {
        List<PlaidItem> plaidItems = plaidItemDAO.query(user, institution);

        if (plaidItems.size() > 1) {
            throw new PlaidItemDAO.MultipleItemsFoundException("Found " +
                    plaidItems.size() +
                    " items:" +
                    plaidItems.toString());
        }
        if (plaidItems.size() < 1) {
            throw new PlaidItemDAO.ItemNotFoundException("Item Not Found for User:" +
                    user +
                    "And institution:" +
                    institution);
        } else {
            return plaidItems.get(0);
        }
    }

    private PlaidItem createItem(CreateItemRequest createItemRequest) {
        PublicTokenExchangeResponse itemInfo = this.itemCreator.requestItem(createItemRequest.getPublicToken());

        return ImmutablePlaidItem.builder()
                .ID(itemInfo.getID())
                .accessToken(itemInfo.getAccessToken())
                .user(createItemRequest.getUser())
                .dateCreated(createItemRequest.getDateCreated())
                .availableProducts(createItemRequest.getAvailableProducts())
                .accounts(createItemRequest.getAccounts())
                .institutionId(createItemRequest.getInstitutionId())
                .metaData(createItemRequest.getMetaData())
                .webhook(createItemRequest.isWebhook())
                .build();
    }

}
