package plaid.entities;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class LinkedItem {
    public abstract String user();
    public abstract String institutionId();
    public abstract String accessToken();
    public abstract String ID();
    public abstract List<String> availableProducts();
    public abstract List<String> accounts();
    public abstract String dateCreated();
    public abstract String metaData(); // Remaining metadata. Rarely used.
    public abstract boolean webhook();
}
