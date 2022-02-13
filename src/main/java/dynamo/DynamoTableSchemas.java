package dynamo;


import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.ImmutableTransaction;
import external.plaid.entities.PlaidItem;
import external.plaid.entities.Transaction;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticImmutableTableSchema;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.*;

/**
 * Constants class to hold schemas for DynamoDB.
 */
public final class DynamoTableSchemas {

    public static TableSchema<PlaidItem> PLAID_ITEM_SCHEMA = StaticImmutableTableSchema.builder(PlaidItem.class, ImmutablePlaidItem.Builder.class)
            .newItemBuilder(ImmutablePlaidItem::builder, ImmutablePlaidItem.Builder::build)
            // PARTITION KEY
            .addAttribute(String.class, partitionKey -> partitionKey.name("user")
                    .getter(PlaidItem::getUser)
                    .setter(ImmutablePlaidItem.Builder::user)
                    .tags(primaryPartitionKey())
                    .build())
            // COMPOSITE SORT KEY {institutionId#accessToken}
            .addAttribute(String.class, sort -> sort.name("institutionIdAccessToken")
                    .getter( item -> item.getInstitutionId() + "#" + item.getAccessToken())
                    .setter( (builder, sortKey) -> {
                        String[] sortKeyInfo = sortKey.split("#");
                        System.out.println(sortKey);
                        builder.institutionId(sortKeyInfo[0]);
                        builder.accessToken(sortKeyInfo[1]); })
                    .tags(primarySortKey())
                    .build())
            // ATTRIBUTES:
            .addAttribute(String.class, a -> a.name("ID")
                    .getter(PlaidItem::getId)
                    .setter(ImmutablePlaidItem.Builder::id)
                    .build())
            .addAttribute(EnhancedType.listOf(String.class), a -> a.name("availableProducts")
                    .getter(PlaidItem::getAvailableProducts)
                    .setter(ImmutablePlaidItem.Builder::availableProducts)
                    .build())
            .addAttribute(String.class, a -> a.name("dateCreated")
                    .getter(PlaidItem::getDateCreated)
                    .setter(ImmutablePlaidItem.Builder::dateCreated)
                    .build())
            .addAttribute(String.class, a -> a.name("metaData")
                    .getter(PlaidItem::getMetadata)
                    .setter(ImmutablePlaidItem.Builder::metadata)
                    .build())
            .addAttribute(EnhancedType.listOf(String.class), a -> a.name("accounts")
                    .getter(PlaidItem::getAccounts)
                    .setter(ImmutablePlaidItem.Builder::accounts)
                    .build())
            .addAttribute(String.class, a -> a.name("receiverNumber")
                    .getter(item -> item.getReceiverNumber().orElse(null))
                    .setter(ImmutablePlaidItem.Builder::receiverNumber)
                    .build())
            .addAttribute(Boolean.class, a -> a.name("webHook")
                    .getter(PlaidItem::getWebhook)
                    .setter(ImmutablePlaidItem.Builder::webhook)
                    .build())
            .build();

    public static TableSchema<Transaction> TRANSACTION_SCHEMA = StaticImmutableTableSchema.builder(Transaction.class,
            ImmutableTransaction.Builder.class).newItemBuilder(ImmutableTransaction::builder,
            ImmutableTransaction.Builder::build)
            // PARTITION KEY
            .addAttribute(String.class, partitionKey -> partitionKey.name("user")
                    .getter(Transaction::getUser)
                    .setter(ImmutableTransaction.Builder::user)
                    .tags(primaryPartitionKey())
                    .build()
            )
            // COMPOSITE SORT KEY {date#transactionId}
            .addAttribute(String.class, sort -> sort.name("dateTransactionId")
                    .getter(tx -> tx.getDate() + "#" + tx.getTransactionId())
                    .setter( (builder, sortKey) -> {
                        String[] sortKeyInfo = sortKey.split("#");
                        builder.date(sortKeyInfo[0]);
                        builder.transactionId(sortKeyInfo[1]);
                    })
                    .tags(primarySortKey())
            )
            // ATTRIBUTES
            .addAttribute(Double.class, a -> a.name("amount")
                    .getter(Transaction::getAmount)
                    .setter(ImmutableTransaction.Builder::amount)
                    .tags(secondarySortKey("amountIndex"))
            )
            .addAttribute(String.class, a -> a.name("institutionName")
                    .getter(Transaction::getInstitutionName)
                    .setter(ImmutableTransaction.Builder::institutionName)
                    .tags(secondarySortKey("institutionIndex"))
            )
            .addAttribute(String.class, a -> a.name("accountId")
                    .getter(Transaction::getAccountId)
                    .setter(ImmutableTransaction.Builder::accountId)
                    .tags(secondaryPartitionKey("accountIndex"))
            )
            .addAttribute(String.class, a -> a.name("description")
                    .getter(Transaction::getDescription)
                    .setter(ImmutableTransaction.Builder::description)
                    .tags(secondarySortKey("descriptionIndex"))
            )
            .addAttribute(String.class, a -> a.name("originalDescription")
                    .getter(Transaction::getOriginalDescription)
                    .setter(ImmutableTransaction.Builder::originalDescription)
            )
            .addAttribute(String.class, a -> a.name("merchantName")
                    .getter(Transaction::getMerchantName)
                    .setter(ImmutableTransaction.Builder::merchantName)
                    .tags(secondarySortKey("merchantIndex"))
            )
            .addAttribute(String.class, a -> a.name("transactionId")
                    .getter(Transaction::getTransactionId)
                    .setter(ImmutableTransaction.Builder::transactionId)
                    .tags(secondaryPartitionKey("transactionIdIndex"))
            )
            .build();

    /**
     * UNINSTANTIABLE.
     */
        private DynamoTableSchemas(){}
}
