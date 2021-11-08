package lambda.auth;

/**
 * Interface for checking user authorization.
 */
@FunctionalInterface
public interface AuthzCheck {

    boolean userActionIsAllowed(String user, AuthzClient client);
}
