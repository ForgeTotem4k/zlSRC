package io.sentry.cache;

import io.sentry.IOptionsObserver;
import io.sentry.JsonDeserializer;
import io.sentry.SentryOptions;
import io.sentry.protocol.SdkVersion;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingOptionsObserver implements IOptionsObserver {
  public static final String OPTIONS_CACHE = ".options-cache";
  
  public static final String RELEASE_FILENAME = "release.json";
  
  public static final String PROGUARD_UUID_FILENAME = "proguard-uuid.json";
  
  public static final String SDK_VERSION_FILENAME = "sdk-version.json";
  
  public static final String ENVIRONMENT_FILENAME = "environment.json";
  
  public static final String DIST_FILENAME = "dist.json";
  
  public static final String TAGS_FILENAME = "tags.json";
  
  @NotNull
  private final SentryOptions options;
  
  public PersistingOptionsObserver(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
  }
  
  public void setRelease(@Nullable String paramString) {
    if (paramString == null) {
      delete("release.json");
    } else {
      store(paramString, "release.json");
    } 
  }
  
  public void setProguardUuid(@Nullable String paramString) {
    if (paramString == null) {
      delete("proguard-uuid.json");
    } else {
      store(paramString, "proguard-uuid.json");
    } 
  }
  
  public void setSdkVersion(@Nullable SdkVersion paramSdkVersion) {
    if (paramSdkVersion == null) {
      delete("sdk-version.json");
    } else {
      store(paramSdkVersion, "sdk-version.json");
    } 
  }
  
  public void setDist(@Nullable String paramString) {
    if (paramString == null) {
      delete("dist.json");
    } else {
      store(paramString, "dist.json");
    } 
  }
  
  public void setEnvironment(@Nullable String paramString) {
    if (paramString == null) {
      delete("environment.json");
    } else {
      store(paramString, "environment.json");
    } 
  }
  
  public void setTags(@NotNull Map<String, String> paramMap) {
    store(paramMap, "tags.json");
  }
  
  private <T> void store(@NotNull T paramT, @NotNull String paramString) {
    CacheUtils.store(this.options, paramT, ".options-cache", paramString);
  }
  
  private void delete(@NotNull String paramString) {
    CacheUtils.delete(this.options, ".options-cache", paramString);
  }
  
  @Nullable
  public static <T> T read(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, @NotNull Class<T> paramClass) {
    return read(paramSentryOptions, paramString, paramClass, null);
  }
  
  @Nullable
  public static <T, R> T read(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, @NotNull Class<T> paramClass, @Nullable JsonDeserializer<R> paramJsonDeserializer) {
    return CacheUtils.read(paramSentryOptions, ".options-cache", paramString, paramClass, paramJsonDeserializer);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\PersistingOptionsObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */