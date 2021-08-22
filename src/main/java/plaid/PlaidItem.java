package plaid;

import java.util.List;
import java.util.Map;

// Immutable class that represents response of item creation.
public class PlaidItem {
    private final String user;
    private final String institutionId;
    private final String accessToken;
    private final String ID;
    private final List<String> availableProducts;
    private final List<String> accounts;
    private final String dateCreated;
    private final Map<String, String> metaData; // Remaining metadata. Rarely used.

    public PlaidItem (Builder builder) {
        this.user = builder.getUser();
        this.institutionId = builder.getInstitutionId();
        this.accessToken = builder.getAccessToken();
        this.ID = builder.getID();
        this.availableProducts = builder.getAvailableProducts();
        this.accounts = builder.getAccounts();
        this.dateCreated = builder.getDateCreated();
        this.metaData = builder.getMetaData();
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

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public static class Builder {
        private String user;
        private String institutionId;
        private String accessToken;
        private String ID;
        private List<String> availableProducts;
        private List<String> accounts;
        private String dateCreated;
        private Map<String, String> metaData; // Remaining metadata. Rarely used.

        public PlaidItem build() {
            validateItem();
            return new PlaidItem(this);
        }

        private void validateItem() {
            // Ignore for now.
        }

        public String getUser() {
            return user;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public String getInstitutionId() {
            return institutionId;
        }

        public Builder setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
            return this;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public String getID() {
            return ID;
        }

        public Builder setID(String ID) {
            this.ID = ID;
            return this;
        }

        public List<String> getAvailableProducts() {
            return availableProducts;
        }

        public Builder setAvailableProducts(List<String> availableProducts) {
            this.availableProducts = availableProducts;
            return this;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public Builder setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public Map<String, String> getMetaData() {
            return metaData;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.metaData = metaData;
            return this;
        }

        public List<String> getAccounts() {
            return accounts;
        }

        public Builder setAccounts(List<String> accounts) {
            this.accounts = accounts;
            return this;
        }
    }
}
