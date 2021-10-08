package lambda.processors;

import dynamo.PlaidItemDAO;
import lambda.requests.items.CreateItemRequest;
import lambda.requests.items.GetItemRequest;
import plaid.clients.ItemGrabber;
import plaid.entities.PlaidItem;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

// Params: Link --> User, InstitutionId,
public class ItemProcessor {

    private final ItemGrabber itemGrabber;
    private final PlaidItemDAO plaidItemDAO;

    @Inject
    public ItemProcessor(ItemGrabber itemGrabber, PlaidItemDAO plaidItemDAO) {
        this.itemGrabber = itemGrabber;
        this.plaidItemDAO = plaidItemDAO;
    }

    // Calls Plaid client to request a new Item and uses info from incoming request
    // to build PlaidItem.
    public PlaidItem createPlaidItem (CreateItemRequest createItemRequest) throws IOException {
        PlaidItem item = itemGrabber.createItem(createItemRequest);
        plaidItemDAO.save(item);
        return item;
    }

    public List<PlaidItem> getItems(GetItemRequest itemRequest) {
        if (itemRequest.getInstitution() == null) {
            return plaidItemDAO.query(itemRequest.getUser());
        } else {
            return plaidItemDAO.query(itemRequest.getUser(), itemRequest.getInstitution());
        }
    }

    // For when a specific Item is required, avoids having to check size farther up the stack.
    // Requires institutionId to make this possible.
    public PlaidItem getItem(GetItemRequest request) throws ItemException {
        List<PlaidItem> plaidItems = plaidItemDAO.query(request.getUser(), request.getInstitution());

        if (plaidItems.size() > 1) {
            throw new MultipleItemsFoundException("Found " +
                    plaidItems.size() +
                    " items:" +
                    plaidItems.toString());
        }
        if (plaidItems.size() < 1) {
            throw new ItemNotFoundException("Item Not Found for User:" +
                    request.getUser() +
                    "And institution:" +
                    request.getInstitution());
        } else {
            return plaidItems.get(0);
        }
    }

    public PlaidItem getItem(String user, String institution) throws ItemException {
        List<PlaidItem> plaidItems = plaidItemDAO.query(user, institution);

        if (plaidItems.size() > 1) {
            throw new MultipleItemsFoundException("Found " +
                    plaidItems.size() +
                    " items:" +
                    plaidItems.toString());
        }
        if (plaidItems.size() < 1) {
            throw new ItemNotFoundException("Item Not Found for User:" +
                    user +
                    "And institution:" +
                    institution);
        } else {
            return plaidItems.get(0);
        }
    }

    // Exceptions
    public static class ItemException extends Exception {
        public ItemException(String errorMessage) {super(errorMessage);}
    }

    public static class ItemNotFoundException extends ItemException {
        public ItemNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class MultipleItemsFoundException extends ItemException {
        public MultipleItemsFoundException(String errorMessage) {super(errorMessage);}
    }
}
