package io.sentry;

import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScope {
  @Nullable
  SentryLevel getLevel();
  
  void setLevel(@Nullable SentryLevel paramSentryLevel);
  
  @Nullable
  String getTransactionName();
  
  void setTransaction(@NotNull String paramString);
  
  @Nullable
  ISpan getSpan();
  
  @Internal
  void setActiveSpan(@Nullable ISpan paramISpan);
  
  void setTransaction(@Nullable ITransaction paramITransaction);
  
  @Nullable
  User getUser();
  
  void setUser(@Nullable User paramUser);
  
  @Internal
  @Nullable
  String getScreen();
  
  @Internal
  void setScreen(@Nullable String paramString);
  
  @Nullable
  Request getRequest();
  
  void setRequest(@Nullable Request paramRequest);
  
  @Internal
  @NotNull
  List<String> getFingerprint();
  
  void setFingerprint(@NotNull List<String> paramList);
  
  @Internal
  @NotNull
  Queue<Breadcrumb> getBreadcrumbs();
  
  void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint);
  
  void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb);
  
  void clearBreadcrumbs();
  
  void clearTransaction();
  
  @Nullable
  ITransaction getTransaction();
  
  void clear();
  
  @Internal
  @NotNull
  Map<String, String> getTags();
  
  void setTag(@NotNull String paramString1, @NotNull String paramString2);
  
  void removeTag(@NotNull String paramString);
  
  @Internal
  @NotNull
  Map<String, Object> getExtras();
  
  void setExtra(@NotNull String paramString1, @NotNull String paramString2);
  
  void removeExtra(@NotNull String paramString);
  
  @NotNull
  Contexts getContexts();
  
  void setContexts(@NotNull String paramString, @NotNull Object paramObject);
  
  void setContexts(@NotNull String paramString, @NotNull Boolean paramBoolean);
  
  void setContexts(@NotNull String paramString1, @NotNull String paramString2);
  
  void setContexts(@NotNull String paramString, @NotNull Number paramNumber);
  
  void setContexts(@NotNull String paramString, @NotNull Collection<?> paramCollection);
  
  void setContexts(@NotNull String paramString, @NotNull Object[] paramArrayOfObject);
  
  void setContexts(@NotNull String paramString, @NotNull Character paramCharacter);
  
  void removeContexts(@NotNull String paramString);
  
  @NotNull
  List<Attachment> getAttachments();
  
  void addAttachment(@NotNull Attachment paramAttachment);
  
  void clearAttachments();
  
  @Internal
  @NotNull
  List<EventProcessor> getEventProcessors();
  
  @Internal
  @NotNull
  List<EventProcessorAndOrder> getEventProcessorsWithOrder();
  
  void addEventProcessor(@NotNull EventProcessor paramEventProcessor);
  
  @Nullable
  Session withSession(@NotNull Scope.IWithSession paramIWithSession);
  
  @Nullable
  Scope.SessionPair startSession();
  
  @Nullable
  Session endSession();
  
  @Internal
  void withTransaction(@NotNull Scope.IWithTransaction paramIWithTransaction);
  
  @NotNull
  SentryOptions getOptions();
  
  @Internal
  @Nullable
  Session getSession();
  
  @Internal
  void clearSession();
  
  @Internal
  void setPropagationContext(@NotNull PropagationContext paramPropagationContext);
  
  @Internal
  @NotNull
  PropagationContext getPropagationContext();
  
  @Internal
  @NotNull
  PropagationContext withPropagationContext(@NotNull Scope.IWithPropagationContext paramIWithPropagationContext);
  
  @NotNull
  IScope clone();
  
  void setLastEventId(@NotNull SentryId paramSentryId);
  
  @NotNull
  SentryId getLastEventId();
  
  void bindClient(@NotNull ISentryClient paramISentryClient);
  
  @NotNull
  ISentryClient getClient();
  
  @Internal
  void assignTraceContext(@NotNull SentryEvent paramSentryEvent);
  
  @Internal
  void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString);
  
  @Internal
  void replaceOptions(@NotNull SentryOptions paramSentryOptions);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */