package plaid;

// Immutable class that represents response of item creation.
public class PlaidItem {
    private String accessToken;
    private String itemId;

    public PlaidItem(String accessToken, String itemId){
        this.accessToken = accessToken;
        this.itemId = itemId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getItemId() {
        return itemId;
    }
}
