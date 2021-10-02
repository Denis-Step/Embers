package lambda.requests;

import org.immutables.value.Value;
import java.util.List;

@Value.Immutable
public abstract class LinkRequest {

    public abstract String user();
    public abstract List<String> products();
}
