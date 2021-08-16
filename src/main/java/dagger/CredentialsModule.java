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

    @Provides
    @Named("DEVELOPMENT_SECRET")
    static String provideDevelopmentSecret() {
        return "60ea81ee4fa5b9ff9b3c07f72f56da";
    }
}
