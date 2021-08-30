package plaid.entities;

import java.util.Optional;

// Wrapper on Plaid Transactions. Removes unnecessary info.
public class Transaction {
    private final String user;
    private final String institutionName;
    private final String accountId;
    private final Double amount;

    // This maps to Plaid "name"
    private final String description;
    private final Optional<String> originalDescription;
    private final String merchantName;
    private final String date;

    private final String transactionId;


    /**
     * Transactions are instantiated here.
     */
    public Transaction (Builder builder) {
        this.user = builder.user;
        this.institutionName = builder.institutionName;
        this.amount = builder.amount;
        this.description = builder.description;
        this.originalDescription = Optional.ofNullable(builder.originalDescription);
        this.merchantName = builder.merchantName;
        this.date = builder.date;
        this.accountId = builder.accountId;
        this.transactionId = builder.transactionId;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public String getUser() { return user; }

    public String getInstitutionName() { return institutionName; }

    public Double getAmount() {
        return this.amount;
    }

    public Optional<String> getOriginalDescription() {
        return originalDescription;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getDate() {
        return date;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", originalDescription='" + originalDescription.orElse("no original description") + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", date='" + date + '\'' +
                ", accountId='" + accountId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }

    // Builder helps maintain immutability of Transaction.
    // A Transaction should NEVER be modified after creation.
    public static class Builder{

        private String user;
        private String institutionName;
        private Double amount;
        private String description;
        private String originalDescription;
        private String merchantName;
        private String date;
        private String accountId;
        private String transactionId;

        public Builder() {}

        public Transaction build() {
            validateTransaction();
            return new Transaction(this);
        }

        private void validateTransaction() {
            // Leave empty for now.
        }

        public String getUser() {
            return user;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public String getInstitutionName() {
            return institutionName;
        }

        public Builder setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
            return this;
        }

        public Double getAmount() {
            return amount;
        }

        public Builder setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getOriginalDescription() {
            return originalDescription;
        }

        public Builder setOriginalDescription(String originalDescription) {
            this.originalDescription = originalDescription;
            return this;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public Builder setMerchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public String getDate() {
            return date;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public String getAccountId() {
            return accountId;
        }

        public Builder setAccountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }
    }
}
