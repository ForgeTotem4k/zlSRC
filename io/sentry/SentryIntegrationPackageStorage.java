package io.sentry;

import io.sentry.protocol.SentryPackage;
import io.sentry.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryIntegrationPackageStorage {
  @Nullable
  private static volatile SentryIntegrationPackageStorage INSTANCE;
  
  private final Set<String> integrations = new CopyOnWriteArraySet<>();
  
  private final Set<SentryPackage> packages = new CopyOnWriteArraySet<>();
  
  @NotNull
  public static SentryIntegrationPackageStorage getInstance() {
    if (INSTANCE == null)
      synchronized (SentryIntegrationPackageStorage.class) {
        if (INSTANCE == null)
          INSTANCE = new SentryIntegrationPackageStorage(); 
      }  
    return INSTANCE;
  }
  
  public void addIntegration(@NotNull String paramString) {
    Objects.requireNonNull(paramString, "integration is required.");
    this.integrations.add(paramString);
  }
  
  @NotNull
  public Set<String> getIntegrations() {
    return this.integrations;
  }
  
  public void addPackage(@NotNull String paramString1, @NotNull String paramString2) {
    Objects.requireNonNull(paramString1, "name is required.");
    Objects.requireNonNull(paramString2, "version is required.");
    SentryPackage sentryPackage = new SentryPackage(paramString1, paramString2);
    this.packages.add(sentryPackage);
  }
  
  @NotNull
  public Set<SentryPackage> getPackages() {
    return this.packages;
  }
  
  @TestOnly
  public void clearStorage() {
    this.integrations.clear();
    this.packages.clear();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryIntegrationPackageStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */