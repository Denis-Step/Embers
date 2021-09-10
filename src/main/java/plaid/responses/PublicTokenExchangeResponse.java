package plaid.responses;

import java.util.Optional;

// Immutable result of exchanging public token for new item.
// Needs to be used with information in linkToken response
// to generate PlaidItem.
public class PublicTokenExchangeResponse {
    private final String ID;
    private final String accessToken;
    private boolean webhook = false;

    public PublicTokenExchangeResponse(String ID, String accessToken, boolean webhook) {
        this.ID = ID;
        this.accessToken = accessToken;
        this.webhook = webhook;
    }

    public String getID() {
        return ID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isWebhook() { return webhook; }
}
