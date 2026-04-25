package io.sentry;

import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpScope implements IScope {
  private static final NoOpScope instance = new NoOpScope();
  
  @NotNull
  private final SentryOptions emptyOptions = SentryOptions.empty();
  
  public static NoOpScope getInstance() {
    return instance;
  }
  
  @Nullable
  public SentryLevel getLevel() {
    return null;
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {}
  
  @Nullable
  public String getTransactionName() {
    return null;
  }
  
  public void setTransaction(@NotNull String paramString) {}
  
  @Nullable
  public ISpan getSpan() {
    return null;
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {}
  
  public void setTransaction(@Nullable ITransaction paramITransaction) {}
  
  @Nullable
  public User getUser() {
    return null;
  }
  
  public void setUser(@Nullable User paramUser) {}
  
  @Internal
  @Nullable
  public String getScreen() {
    return null;
  }
  
  @Internal
  public void setScreen(@Nullable String paramString) {}
  
  @Nullable
  public Request getRequest() {
    return null;
  }
  
  public void setRequest(@Nullable Request paramRequest) {}
  
  @Internal
  @NotNull
  public List<String> getFingerprint() {
    return new ArrayList<>();
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {}
  
  @Internal
  @NotNull
  public Queue<Breadcrumb> getBreadcrumbs() {
    return new ArrayDeque<>();
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {}
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {}
  
  public void clearBreadcrumbs() {}
  
  public void clearTransaction() {}
  
  @Nullable
  public ITransaction getTransaction() {
    return null;
  }
  
  public void clear() {}
  
  @Internal
  @NotNull
  public Map<String, String> getTags() {
    return new HashMap<>();
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeTag(@NotNull String paramString) {}
  
  @Internal
  @NotNull
  public Map<String, Object> getExtras() {
    return new HashMap<>();
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeExtra(@NotNull String paramString) {}
  
  @NotNull
  public Contexts getContexts() {
    return new Contexts();
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Object paramObject) {}
  
  public void setContexts(@NotNull String paramString, @NotNull Boolean paramBoolean) {}
  
  public void setContexts(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void setContexts(@NotNull String paramString, @NotNull Number paramNumber) {}
  
  public void setContexts(@NotNull String paramString, @NotNull Collection<?> paramCollection) {}
  
  public void setContexts(@NotNull String paramString, @NotNull Object[] paramArrayOfObject) {}
  
  public void setContexts(@NotNull String paramString, @NotNull Character paramCharacter) {}
  
  public void removeContexts(@NotNull String paramString) {}
  
  @Internal
  @NotNull
  public List<Attachment> getAttachments() {
    return new ArrayList<>();
  }
  
  public void addAttachment(@NotNull Attachment paramAttachment) {}
  
  public void clearAttachments() {}
  
  @Internal
  @NotNull
  public List<EventProcessor> getEventProcessors() {
    return new ArrayList<>();
  }
  
  @Internal
  @NotNull
  public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
    return new ArrayList<>();
  }
  
  public void addEventProcessor(@NotNull EventProcessor paramEventProcessor) {}
  
  @Internal
  @Nullable
  public Session withSession(Scope.IWithSession paramIWithSession) {
    return null;
  }
  
  @Internal
  @Nullable
  public Scope.SessionPair startSession() {
    return null;
  }
  
  @Internal
  @Nullable
  public Session endSession() {
    return null;
  }
  
  @Internal
  public void withTransaction(Scope.IWithTransaction paramIWithTransaction) {}
  
  @Internal
  @NotNull
  public SentryOptions getOptions() {
    return this.emptyOptions;
  }
  
  @Internal
  @Nullable
  public Session getSession() {
    return null;
  }
  
  @Internal
  public void clearSession() {}
  
  @Internal
  public void setPropagationContext(@NotNull PropagationContext paramPropagationContext) {}
  
  @Internal
  @NotNull
  public PropagationContext getPropagationContext() {
    return new PropagationContext();
  }
  
  @Internal
  @NotNull
  public PropagationContext withPropagationContext(Scope.IWithPropagationContext paramIWithPropagationContext) {
    return new PropagationContext();
  }
  
  public void setLastEventId(@NotNull SentryId paramSentryId) {}
  
  @NotNull
  public IScope clone() {
    return getInstance();
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return SentryId.EMPTY_ID;
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {}
  
  @NotNull
  public ISentryClient getClient() {
    return NoOpSentryClient.getInstance();
  }
  
  public void assignTraceContext(@NotNull SentryEvent paramSentryEvent) {}
  
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {}
  
  public void replaceOptions(@NotNull SentryOptions paramSentryOptions) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */