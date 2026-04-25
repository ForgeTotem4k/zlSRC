package io.sentry;

import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.EventProcessorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombinedScopeView implements IScope {
  private final IScope globalScope;
  
  private final IScope isolationScope;
  
  private final IScope scope;
  
  public CombinedScopeView(@NotNull IScope paramIScope1, @NotNull IScope paramIScope2, @NotNull IScope paramIScope3) {
    this.globalScope = paramIScope1;
    this.isolationScope = paramIScope2;
    this.scope = paramIScope3;
  }
  
  @Nullable
  public SentryLevel getLevel() {
    SentryLevel sentryLevel1 = this.scope.getLevel();
    if (sentryLevel1 != null)
      return sentryLevel1; 
    SentryLevel sentryLevel2 = this.isolationScope.getLevel();
    return (sentryLevel2 != null) ? sentryLevel2 : this.globalScope.getLevel();
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    getDefaultWriteScope().setLevel(paramSentryLevel);
  }
  
  @Nullable
  public String getTransactionName() {
    String str1 = this.scope.getTransactionName();
    if (str1 != null)
      return str1; 
    String str2 = this.isolationScope.getTransactionName();
    return (str2 != null) ? str2 : this.globalScope.getTransactionName();
  }
  
  public void setTransaction(@NotNull String paramString) {
    getDefaultWriteScope().setTransaction(paramString);
  }
  
  @Nullable
  public ISpan getSpan() {
    ISpan iSpan1 = this.scope.getSpan();
    if (iSpan1 != null)
      return iSpan1; 
    ISpan iSpan2 = this.isolationScope.getSpan();
    return (iSpan2 != null) ? iSpan2 : this.globalScope.getSpan();
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {
    this.scope.setActiveSpan(paramISpan);
  }
  
  public void setTransaction(@Nullable ITransaction paramITransaction) {
    getDefaultWriteScope().setTransaction(paramITransaction);
  }
  
  @Nullable
  public User getUser() {
    User user1 = this.scope.getUser();
    if (user1 != null)
      return user1; 
    User user2 = this.isolationScope.getUser();
    return (user2 != null) ? user2 : this.globalScope.getUser();
  }
  
  public void setUser(@Nullable User paramUser) {
    getDefaultWriteScope().setUser(paramUser);
  }
  
  @Nullable
  public String getScreen() {
    String str1 = this.scope.getScreen();
    if (str1 != null)
      return str1; 
    String str2 = this.isolationScope.getScreen();
    return (str2 != null) ? str2 : this.globalScope.getScreen();
  }
  
  public void setScreen(@Nullable String paramString) {
    getDefaultWriteScope().setScreen(paramString);
  }
  
  @Nullable
  public Request getRequest() {
    Request request1 = this.scope.getRequest();
    if (request1 != null)
      return request1; 
    Request request2 = this.isolationScope.getRequest();
    return (request2 != null) ? request2 : this.globalScope.getRequest();
  }
  
  public void setRequest(@Nullable Request paramRequest) {
    getDefaultWriteScope().setRequest(paramRequest);
  }
  
  @NotNull
  public List<String> getFingerprint() {
    List<String> list1 = this.scope.getFingerprint();
    if (!list1.isEmpty())
      return list1; 
    List<String> list2 = this.isolationScope.getFingerprint();
    return !list2.isEmpty() ? list2 : this.globalScope.getFingerprint();
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {
    getDefaultWriteScope().setFingerprint(paramList);
  }
  
  @NotNull
  public Queue<Breadcrumb> getBreadcrumbs() {
    ArrayList<Breadcrumb> arrayList = new ArrayList();
    arrayList.addAll(this.globalScope.getBreadcrumbs());
    arrayList.addAll(this.isolationScope.getBreadcrumbs());
    arrayList.addAll(this.scope.getBreadcrumbs());
    Collections.sort(arrayList);
    Queue<Breadcrumb> queue = Scope.createBreadcrumbsList(this.scope.getOptions().getMaxBreadcrumbs());
    queue.addAll(arrayList);
    return queue;
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    getDefaultWriteScope().addBreadcrumb(paramBreadcrumb, paramHint);
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    getDefaultWriteScope().addBreadcrumb(paramBreadcrumb);
  }
  
  public void clearBreadcrumbs() {
    getDefaultWriteScope().clearBreadcrumbs();
  }
  
  public void clearTransaction() {
    getDefaultWriteScope().clearTransaction();
  }
  
  @Nullable
  public ITransaction getTransaction() {
    ITransaction iTransaction1 = this.scope.getTransaction();
    if (iTransaction1 != null)
      return iTransaction1; 
    ITransaction iTransaction2 = this.isolationScope.getTransaction();
    return (iTransaction2 != null) ? iTransaction2 : this.globalScope.getTransaction();
  }
  
  public void clear() {
    getDefaultWriteScope().clear();
  }
  
  @NotNull
  public Map<String, String> getTags() {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.putAll(this.globalScope.getTags());
    concurrentHashMap.putAll(this.isolationScope.getTags());
    concurrentHashMap.putAll(this.scope.getTags());
    return (Map)concurrentHashMap;
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    getDefaultWriteScope().setTag(paramString1, paramString2);
  }
  
  public void removeTag(@NotNull String paramString) {
    getDefaultWriteScope().removeTag(paramString);
  }
  
  @NotNull
  public Map<String, Object> getExtras() {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.putAll(this.globalScope.getExtras());
    concurrentHashMap.putAll(this.isolationScope.getExtras());
    concurrentHashMap.putAll(this.scope.getExtras());
    return (Map)concurrentHashMap;
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    getDefaultWriteScope().setExtra(paramString1, paramString2);
  }
  
  public void removeExtra(@NotNull String paramString) {
    getDefaultWriteScope().removeExtra(paramString);
  }
  
  @NotNull
  public Contexts getContexts() {
    return new CombinedContextsView(this.globalScope.getContexts(), this.isolationScope.getContexts(), this.scope.getContexts(), getOptions().getDefaultScopeType());
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Object paramObject) {
    getDefaultWriteScope().setContexts(paramString, paramObject);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Boolean paramBoolean) {
    getDefaultWriteScope().setContexts(paramString, paramBoolean);
  }
  
  public void setContexts(@NotNull String paramString1, @NotNull String paramString2) {
    getDefaultWriteScope().setContexts(paramString1, paramString2);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Number paramNumber) {
    getDefaultWriteScope().setContexts(paramString, paramNumber);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Collection<?> paramCollection) {
    getDefaultWriteScope().setContexts(paramString, paramCollection);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Object[] paramArrayOfObject) {
    getDefaultWriteScope().setContexts(paramString, paramArrayOfObject);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Character paramCharacter) {
    getDefaultWriteScope().setContexts(paramString, paramCharacter);
  }
  
  public void removeContexts(@NotNull String paramString) {
    getDefaultWriteScope().removeContexts(paramString);
  }
  
  @NotNull
  private IScope getDefaultWriteScope() {
    return getSpecificScope(null);
  }
  
  IScope getSpecificScope(@Nullable ScopeType paramScopeType) {
    if (paramScopeType != null)
      switch (paramScopeType) {
        case CURRENT:
          return this.scope;
        case ISOLATION:
          return this.isolationScope;
        case GLOBAL:
          return this.globalScope;
        case COMBINED:
          return this;
      }  
    switch (getOptions().getDefaultScopeType()) {
      case CURRENT:
        return this.scope;
      case ISOLATION:
        return this.isolationScope;
      case GLOBAL:
        return this.globalScope;
    } 
    return this.scope;
  }
  
  @NotNull
  public List<Attachment> getAttachments() {
    CopyOnWriteArrayList<Attachment> copyOnWriteArrayList = new CopyOnWriteArrayList();
    copyOnWriteArrayList.addAll(this.globalScope.getAttachments());
    copyOnWriteArrayList.addAll(this.isolationScope.getAttachments());
    copyOnWriteArrayList.addAll(this.scope.getAttachments());
    return copyOnWriteArrayList;
  }
  
  public void addAttachment(@NotNull Attachment paramAttachment) {
    getDefaultWriteScope().addAttachment(paramAttachment);
  }
  
  public void clearAttachments() {
    getDefaultWriteScope().clearAttachments();
  }
  
  @NotNull
  public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
    CopyOnWriteArrayList<EventProcessorAndOrder> copyOnWriteArrayList = new CopyOnWriteArrayList();
    copyOnWriteArrayList.addAll(this.globalScope.getEventProcessorsWithOrder());
    copyOnWriteArrayList.addAll(this.isolationScope.getEventProcessorsWithOrder());
    copyOnWriteArrayList.addAll(this.scope.getEventProcessorsWithOrder());
    Collections.sort(copyOnWriteArrayList);
    return copyOnWriteArrayList;
  }
  
  @NotNull
  public List<EventProcessor> getEventProcessors() {
    return EventProcessorUtils.unwrap(getEventProcessorsWithOrder());
  }
  
  public void addEventProcessor(@NotNull EventProcessor paramEventProcessor) {
    getDefaultWriteScope().addEventProcessor(paramEventProcessor);
  }
  
  @Nullable
  public Session withSession(Scope.IWithSession paramIWithSession) {
    return getDefaultWriteScope().withSession(paramIWithSession);
  }
  
  @Nullable
  public Scope.SessionPair startSession() {
    return getDefaultWriteScope().startSession();
  }
  
  @Nullable
  public Session endSession() {
    return getDefaultWriteScope().endSession();
  }
  
  public void withTransaction(Scope.IWithTransaction paramIWithTransaction) {
    getDefaultWriteScope().withTransaction(paramIWithTransaction);
  }
  
  @NotNull
  public SentryOptions getOptions() {
    return this.globalScope.getOptions();
  }
  
  @Nullable
  public Session getSession() {
    Session session1 = this.scope.getSession();
    if (session1 != null)
      return session1; 
    Session session2 = this.isolationScope.getSession();
    return (session2 != null) ? session2 : this.globalScope.getSession();
  }
  
  public void clearSession() {
    getDefaultWriteScope().clearSession();
  }
  
  public void setPropagationContext(@NotNull PropagationContext paramPropagationContext) {
    getDefaultWriteScope().setPropagationContext(paramPropagationContext);
  }
  
  @NotNull
  public PropagationContext getPropagationContext() {
    return getDefaultWriteScope().getPropagationContext();
  }
  
  @NotNull
  public PropagationContext withPropagationContext(Scope.IWithPropagationContext paramIWithPropagationContext) {
    return getDefaultWriteScope().withPropagationContext(paramIWithPropagationContext);
  }
  
  @NotNull
  public IScope clone() {
    return new CombinedScopeView(this.globalScope, this.isolationScope.clone(), this.scope.clone());
  }
  
  public void setLastEventId(@NotNull SentryId paramSentryId) {
    this.globalScope.setLastEventId(paramSentryId);
    this.isolationScope.setLastEventId(paramSentryId);
    this.scope.setLastEventId(paramSentryId);
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return this.globalScope.getLastEventId();
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {
    getDefaultWriteScope().bindClient(paramISentryClient);
  }
  
  @NotNull
  public ISentryClient getClient() {
    ISentryClient iSentryClient1 = this.scope.getClient();
    if (!(iSentryClient1 instanceof NoOpSentryClient))
      return iSentryClient1; 
    ISentryClient iSentryClient2 = this.isolationScope.getClient();
    return !(iSentryClient2 instanceof NoOpSentryClient) ? iSentryClient2 : this.globalScope.getClient();
  }
  
  public void assignTraceContext(@NotNull SentryEvent paramSentryEvent) {
    this.globalScope.assignTraceContext(paramSentryEvent);
  }
  
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {
    this.globalScope.setSpanContext(paramThrowable, paramISpan, paramString);
  }
  
  @Internal
  public void replaceOptions(@NotNull SentryOptions paramSentryOptions) {
    this.globalScope.replaceOptions(paramSentryOptions);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CombinedScopeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */