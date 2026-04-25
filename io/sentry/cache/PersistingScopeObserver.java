package io.sentry.cache;

import io.sentry.Breadcrumb;
import io.sentry.JsonDeserializer;
import io.sentry.ScopeObserverAdapter;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanContext;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingScopeObserver extends ScopeObserverAdapter {
  public static final String SCOPE_CACHE = ".scope-cache";
  
  public static final String USER_FILENAME = "user.json";
  
  public static final String BREADCRUMBS_FILENAME = "breadcrumbs.json";
  
  public static final String TAGS_FILENAME = "tags.json";
  
  public static final String EXTRAS_FILENAME = "extras.json";
  
  public static final String CONTEXTS_FILENAME = "contexts.json";
  
  public static final String REQUEST_FILENAME = "request.json";
  
  public static final String LEVEL_FILENAME = "level.json";
  
  public static final String FINGERPRINT_FILENAME = "fingerprint.json";
  
  public static final String TRANSACTION_FILENAME = "transaction.json";
  
  public static final String TRACE_FILENAME = "trace.json";
  
  @NotNull
  private final SentryOptions options;
  
  public PersistingScopeObserver(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
  }
  
  public void setUser(@Nullable User paramUser) {
    serializeToDisk(() -> {
          if (paramUser == null) {
            delete("user.json");
          } else {
            store(paramUser, "user.json");
          } 
        });
  }
  
  public void setBreadcrumbs(@NotNull Collection<Breadcrumb> paramCollection) {
    serializeToDisk(() -> store(paramCollection, "breadcrumbs.json"));
  }
  
  public void setTags(@NotNull Map<String, String> paramMap) {
    serializeToDisk(() -> store(paramMap, "tags.json"));
  }
  
  public void setExtras(@NotNull Map<String, Object> paramMap) {
    serializeToDisk(() -> store(paramMap, "extras.json"));
  }
  
  public void setRequest(@Nullable Request paramRequest) {
    serializeToDisk(() -> {
          if (paramRequest == null) {
            delete("request.json");
          } else {
            store(paramRequest, "request.json");
          } 
        });
  }
  
  public void setFingerprint(@NotNull Collection<String> paramCollection) {
    serializeToDisk(() -> store(paramCollection, "fingerprint.json"));
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    serializeToDisk(() -> {
          if (paramSentryLevel == null) {
            delete("level.json");
          } else {
            store(paramSentryLevel, "level.json");
          } 
        });
  }
  
  public void setTransaction(@Nullable String paramString) {
    serializeToDisk(() -> {
          if (paramString == null) {
            delete("transaction.json");
          } else {
            store(paramString, "transaction.json");
          } 
        });
  }
  
  public void setTrace(@Nullable SpanContext paramSpanContext) {
    serializeToDisk(() -> {
          if (paramSpanContext == null) {
            delete("trace.json");
          } else {
            store(paramSpanContext, "trace.json");
          } 
        });
  }
  
  public void setContexts(@NotNull Contexts paramContexts) {
    serializeToDisk(() -> store(paramContexts, "contexts.json"));
  }
  
  private void serializeToDisk(@NotNull Runnable paramRunnable) {
    try {
      this.options.getExecutorService().submit(() -> {
            try {
              paramRunnable.run();
            } catch (Throwable throwable) {
              this.options.getLogger().log(SentryLevel.ERROR, "Serialization task failed", throwable);
            } 
          });
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, "Serialization task could not be scheduled", throwable);
    } 
  }
  
  private <T> void store(@NotNull T paramT, @NotNull String paramString) {
    CacheUtils.store(this.options, paramT, ".scope-cache", paramString);
  }
  
  private void delete(@NotNull String paramString) {
    CacheUtils.delete(this.options, ".scope-cache", paramString);
  }
  
  @Nullable
  public static <T> T read(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, @NotNull Class<T> paramClass) {
    return read(paramSentryOptions, paramString, paramClass, null);
  }
  
  @Nullable
  public static <T, R> T read(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, @NotNull Class<T> paramClass, @Nullable JsonDeserializer<R> paramJsonDeserializer) {
    return CacheUtils.read(paramSentryOptions, ".scope-cache", paramString, paramClass, paramJsonDeserializer);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\PersistingScopeObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */