package dagger;

import com.fasterxml.jackson.databind.JsonNode;
import lambda.processors.items.CreateLinkTokenProcessor;
import lambda.processors.items.ItemProcessor;
import lambda.processors.transactions.QueryTransactionsProcessor;
import lambda.processors.transactions.LoadTransactionsProcessor;
import lambda.processors.transactions.NewTransactionProcessor;
import lambda.processors.transactions.ReceiveTransactionsProcessor;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AwsClientModule.class,
        PlaidClientModule.class,
        ProcessorModule.class})
public interface ProcessorComponent {
    @Named("PLAID_SECRETS_JSON") JsonNode buildJsonNode();

    CreateLinkTokenProcessor buildLinkTokenProcessor();
    ItemProcessor buildItemProcessor();
    QueryTransactionsProcessor buildGetTransactionsProcessor();
    LoadTransactionsProcessor buildLoadTransactionsProcessor();
    NewTransactionProcessor buildNewTransactionProcessor();
    ReceiveTransactionsProcessor buildReceiveTransactionsProcessor();

}
