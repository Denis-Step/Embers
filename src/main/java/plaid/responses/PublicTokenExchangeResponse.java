package plaid.responses;

// Immutable result of exchanging public token for new item.
// Needs to be used with information in linkToken response
// to generate PlaidItem.
public class PublicTokenExchangeResponse {
    private final String ID;
    private final String accessToken;

    public PublicTokenExchangeResponse(String ID, String accessToken) {
        this.ID = ID;
        this.accessToken = accessToken;
    }

    public String getID() {
        return ID;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
