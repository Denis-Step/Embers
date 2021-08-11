package dagger;

import javax.inject.Named;

@Module
public interface CredentialsModule {

    @Provides
    @Named("CLIENT_ID")
    static String provideClientId() {
        return "5eb13e97fd0ed40013cc0438";
    }

    @Provides
    @Named("SANDBOX_SECRET")
    static String provideSandboxSecret() {
        return "68134865febfc98c05f21563bd8b99";
    }
}
