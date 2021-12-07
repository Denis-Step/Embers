package lambda.requests.items;

import java.util.List;

// LambdaRequest with result of calling link flow.
public class CreateItemRequest {

    private String user;
    private String publicToken;
    // # {INST_NAME}{INST_ID}
    private String institutionId;
    private List<String> availableProducts;
    private List<String> accounts;
    private String dateCreated;
    private String metaData; // Remaining metadata. Rarely used.
    private boolean webhook;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public List<String> getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(List<String> availableProducts) {
        this.availableProducts = availableProducts;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public boolean isWebhook() {
        return webhook;
    }

    public void setWebhook(boolean webhook) {
        this.webhook = webhook;
    }

    @Override
    public String toString() {
        return "CreateItemRequest{" +
                "user='" + user + '\'' +
                ", publicToken='" + publicToken + '\'' +
                ", institutionId='" + institutionId + '\'' +
                ", availableProducts=" + availableProducts +
                ", accounts=" + accounts +
                ", dateCreated='" + dateCreated + '\'' +
                ", metaData='" + metaData + '\'' +
                ", webhook=" + webhook +
                '}';
    }
}
