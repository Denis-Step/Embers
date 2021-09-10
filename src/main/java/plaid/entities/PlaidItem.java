package plaid.entities;

import java.util.List;

// Immutable class that represents response of item creation.
public class PlaidItem {
    private final String user;
    private final String institutionId;
    private final String accessToken;
    private final String ID;
    private final List<String> availableProducts;
    private final List<String> accounts;
    private final String dateCreated;
    private final String metaData; // Remaining metadata. Rarely used.
    private final boolean webhook;

    public PlaidItem (Builder builder) {
        this.user = builder.user;
        this.institutionId = builder.institutionId;
        this.accessToken = builder.accessToken;
        this.ID = builder.ID;
        this.availableProducts = builder.availableProducts;
        this.accounts = builder.accounts;
        this.dateCreated = builder.dateCreated;
        this.metaData = builder.metaData;
        this.webhook = builder.webhook;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "PlaidItem{" +
                "user='" + user + '\'' +
                ", institutionId='" + institutionId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", ID='" + ID + '\'' +
                ", availableProducts=" + availableProducts +
                ", accounts=" + accounts + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", metaData=" + metaData +
                '}';
    }

    public String getUser() {
        return user;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getID() {
        return ID;
    }

    public List<String> getAvailableProducts() {
        return availableProducts;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getMetaData() {
        return metaData;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public boolean getWebhook() { return webhook; }

    public static class Builder {
        private String user;
        private String institutionId;
        private String accessToken;
        private String ID;
        private List<String> availableProducts;
        private List<String> accounts;
        private String dateCreated;
        private String metaData; // Remaining metadata. Rarely used.
        private boolean webhook;

        public PlaidItem build() {
            validateItem();
            return new PlaidItem(this);
        }

        private void validateItem() {
            // Ignore for now.
        }
        public Builder setUser(String user) {
            this.user = user;
            return this;
        }
        public Builder setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
            return this;
        }
        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        public Builder setID(String ID) {
            this.ID = ID;
            return this;
        }

        public Builder setAvailableProducts(List<String> availableProducts) {
            this.availableProducts = availableProducts;
            return this;
        }

        public Builder setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public Builder setMetaData(String metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder setAccounts(List<String> accounts) {
            this.accounts = accounts;
            return this;
        }

        public void setWebhook(boolean webhook) {
            this.webhook = webhook;
        }
    }
}
