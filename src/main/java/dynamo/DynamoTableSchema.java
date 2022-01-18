package dynamo;


import external.plaid.entities.ImmutablePlaidItem;
import external.plaid.entities.PlaidItem;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticImmutableTableSchema;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

public enum DynamoTableSchema {
    PLAID_ITEM_SCHEMA(StaticImmutableTableSchema.builder(PlaidItem.class, ImmutablePlaidItem.Builder.class)
            .newItemBuilder(ImmutablePlaidItem::builder, ImmutablePlaidItem.Builder::build)

            .addAttribute(String.class, partitionKey -> partitionKey.name("user")
                    .getter(PlaidItem::getUser)
                    .setter(ImmutablePlaidItem.Builder::user)
                    .tags(primaryPartitionKey())
                    .build()
                )

            // COMPOSITE SORT KEY {institutionId#accessToken}
            .addAttribute(String.class, sort -> sort.name("institutionIdAccessToken")
                    .getter( item -> item.getInstitutionId() + "#" + item.getAccessToken())
                    .setter( (builder, sortKey) -> {
                        String[] sortKeyInfo = sortKey.split("#");
                        builder.institutionId(sortKeyInfo[0]);
                        builder.accessToken(sortKeyInfo[1]); })
                    .tags(primarySortKey())
                    .build()
            )
            .build())
    ;

    public final StaticImmutableTableSchema<?, ?> schema;

    DynamoTableSchema(StaticImmutableTableSchema<?,?> schema) {
        this.schema =schema;
    }
}
