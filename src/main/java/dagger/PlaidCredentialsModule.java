package dagger;

import javax.inject.Named;

@Module
public interface PlaidCredentialsModule {

    @Provides
    @Named("CLIENT_ID")
    static String provideClientId() {
        return System.getenv("CLIENT_ID");
    }

    @Provides
    @Named("SANDBOX_SECRET")
    static String provideSandboxSecret() {
        return System.getenv("SANDBOX_SECRET");
    }

    @Provides
    @Named("DEVELOPMENT_SECRET")
    static String provideDevelopmentSecret() {
        return System.getenv("DEVELOPMENT_SECRET");
    }
}
