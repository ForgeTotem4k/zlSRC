package io.sentry;

import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.App;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.protocol.User;
import io.sentry.util.CollectionUtils;
import io.sentry.util.EventProcessorUtils;
import io.sentry.util.ExceptionUtils;
import io.sentry.util.Objects;
import io.sentry.util.Pair;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Scope implements IScope {
  @NotNull
  private volatile SentryId lastEventId;
  
  @Nullable
  private SentryLevel level;
  
  @Nullable
  private ITransaction transaction;
  
  @NotNull
  private WeakReference<ISpan> activeSpan = new WeakReference<>(null);
  
  @Nullable
  private String transactionName;
  
  @Nullable
  private User user;
  
  @Nullable
  private String screen;
  
  @Nullable
  private Request request;
  
  @NotNull
  private List<String> fingerprint = new ArrayList<>();
  
  @NotNull
  private volatile Queue<Breadcrumb> breadcrumbs;
  
  @NotNull
  private Map<String, String> tags = new ConcurrentHashMap<>();
  
  @NotNull
  private Map<String, Object> extra = new ConcurrentHashMap<>();
  
  @NotNull
  private List<EventProcessorAndOrder> eventProcessors = new CopyOnWriteArrayList<>();
  
  @NotNull
  private volatile SentryOptions options;
  
  @Nullable
  private volatile Session session;
  
  @NotNull
  private final Object sessionLock = new Object();
  
  @NotNull
  private final Object transactionLock = new Object();
  
  @NotNull
  private final Object propagationContextLock = new Object();
  
  @NotNull
  private Contexts contexts = new Contexts();
  
  @NotNull
  private List<Attachment> attachments = new CopyOnWriteArrayList<>();
  
  @NotNull
  private PropagationContext propagationContext;
  
  @NotNull
  private ISentryClient client = NoOpSentryClient.getInstance();
  
  @NotNull
  private final Map<Throwable, Pair<WeakReference<ISpan>, String>> throwableToSpan = Collections.synchronizedMap(new WeakHashMap<>());
  
  public Scope(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required.");
    this.breadcrumbs = createBreadcrumbsList(this.options.getMaxBreadcrumbs());
    this.propagationContext = new PropagationContext();
    this.lastEventId = SentryId.EMPTY_ID;
  }
  
  private Scope(@NotNull Scope paramScope) {
    this.transaction = paramScope.transaction;
    this.transactionName = paramScope.transactionName;
    this.session = paramScope.session;
    this.options = paramScope.options;
    this.level = paramScope.level;
    this.client = paramScope.client;
    this.lastEventId = paramScope.getLastEventId();
    User user = paramScope.user;
    this.user = (user != null) ? new User(user) : null;
    this.screen = paramScope.screen;
    Request request = paramScope.request;
    this.request = (request != null) ? new Request(request) : null;
    this.fingerprint = new ArrayList<>(paramScope.fingerprint);
    this.eventProcessors = new CopyOnWriteArrayList<>(paramScope.eventProcessors);
    Breadcrumb[] arrayOfBreadcrumb = (Breadcrumb[])paramScope.breadcrumbs.toArray((Object[])new Breadcrumb[0]);
    Queue<Breadcrumb> queue = createBreadcrumbsList(paramScope.options.getMaxBreadcrumbs());
    for (Breadcrumb breadcrumb1 : arrayOfBreadcrumb) {
      Breadcrumb breadcrumb2 = new Breadcrumb(breadcrumb1);
      queue.add(breadcrumb2);
    } 
    this.breadcrumbs = queue;
    Map<String, String> map = paramScope.tags;
    ConcurrentHashMap<Object, Object> concurrentHashMap1 = new ConcurrentHashMap<>();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (entry != null)
        concurrentHashMap1.put(entry.getKey(), entry.getValue()); 
    } 
    this.tags = (Map)concurrentHashMap1;
    Map<String, Object> map1 = paramScope.extra;
    ConcurrentHashMap<Object, Object> concurrentHashMap2 = new ConcurrentHashMap<>();
    for (Map.Entry<String, Object> entry : map1.entrySet()) {
      if (entry != null)
        concurrentHashMap2.put(entry.getKey(), entry.getValue()); 
    } 
    this.extra = (Map)concurrentHashMap2;
    this.contexts = new Contexts(paramScope.contexts);
    this.attachments = new CopyOnWriteArrayList<>(paramScope.attachments);
    this.propagationContext = new PropagationContext(paramScope.propagationContext);
  }
  
  @Nullable
  public SentryLevel getLevel() {
    return this.level;
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    this.level = paramSentryLevel;
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setLevel(paramSentryLevel); 
  }
  
  @Nullable
  public String getTransactionName() {
    ITransaction iTransaction = this.transaction;
    return (iTransaction != null) ? iTransaction.getName() : this.transactionName;
  }
  
  public void setTransaction(@NotNull String paramString) {
    if (paramString != null) {
      ITransaction iTransaction = this.transaction;
      if (iTransaction != null)
        iTransaction.setName(paramString, TransactionNameSource.CUSTOM); 
      this.transactionName = paramString;
      for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
        iScopeObserver.setTransaction(paramString); 
    } else {
      this.options.getLogger().log(SentryLevel.WARNING, "Transaction cannot be null", new Object[0]);
    } 
  }
  
  @Nullable
  public ISpan getSpan() {
    ISpan iSpan = this.activeSpan.get();
    if (iSpan != null)
      return iSpan; 
    ITransaction iTransaction = this.transaction;
    if (iTransaction != null) {
      ISpan iSpan1 = iTransaction.getLatestActiveSpan();
      if (iSpan1 != null)
        return iSpan1; 
    } 
    return iTransaction;
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {
    this.activeSpan = new WeakReference<>(paramISpan);
  }
  
  public void setTransaction(@Nullable ITransaction paramITransaction) {
    synchronized (this.transactionLock) {
      this.transaction = paramITransaction;
      for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
        if (paramITransaction != null) {
          iScopeObserver.setTransaction(paramITransaction.getName());
          iScopeObserver.setTrace(paramITransaction.getSpanContext());
          continue;
        } 
        iScopeObserver.setTransaction(null);
        iScopeObserver.setTrace(null);
      } 
    } 
  }
  
  @Nullable
  public User getUser() {
    return this.user;
  }
  
  public void setUser(@Nullable User paramUser) {
    this.user = paramUser;
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setUser(paramUser); 
  }
  
  @Internal
  @Nullable
  public String getScreen() {
    return this.screen;
  }
  
  @Internal
  public void setScreen(@Nullable String paramString) {
    this.screen = paramString;
    Contexts contexts = getContexts();
    App app = contexts.getApp();
    if (app == null) {
      app = new App();
      contexts.setApp(app);
    } 
    if (paramString == null) {
      app.setViewNames(null);
    } else {
      ArrayList<String> arrayList = new ArrayList(1);
      arrayList.add(paramString);
      app.setViewNames(arrayList);
    } 
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setContexts(contexts); 
  }
  
  @Nullable
  public Request getRequest() {
    return this.request;
  }
  
  public void setRequest(@Nullable Request paramRequest) {
    this.request = paramRequest;
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setRequest(paramRequest); 
  }
  
  @Internal
  @NotNull
  public List<String> getFingerprint() {
    return this.fingerprint;
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {
    if (paramList == null)
      return; 
    this.fingerprint = new ArrayList<>(paramList);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setFingerprint(paramList); 
  }
  
  @Internal
  @NotNull
  public Queue<Breadcrumb> getBreadcrumbs() {
    return this.breadcrumbs;
  }
  
  @Nullable
  private Breadcrumb executeBeforeBreadcrumb(@NotNull SentryOptions.BeforeBreadcrumbCallback paramBeforeBreadcrumbCallback, @NotNull Breadcrumb paramBreadcrumb, @NotNull Hint paramHint) {
    try {
      paramBreadcrumb = paramBeforeBreadcrumbCallback.execute(paramBreadcrumb, paramHint);
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, "The BeforeBreadcrumbCallback callback threw an exception. Exception details will be added to the breadcrumb.", throwable);
      if (throwable.getMessage() != null)
        paramBreadcrumb.setData("sentry:message", throwable.getMessage()); 
    } 
    return paramBreadcrumb;
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    if (paramBreadcrumb == null)
      return; 
    if (paramHint == null)
      paramHint = new Hint(); 
    SentryOptions.BeforeBreadcrumbCallback beforeBreadcrumbCallback = this.options.getBeforeBreadcrumb();
    if (beforeBreadcrumbCallback != null)
      paramBreadcrumb = executeBeforeBreadcrumb(beforeBreadcrumbCallback, paramBreadcrumb, paramHint); 
    if (paramBreadcrumb != null) {
      this.breadcrumbs.add(paramBreadcrumb);
      for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
        iScopeObserver.addBreadcrumb(paramBreadcrumb);
        iScopeObserver.setBreadcrumbs(this.breadcrumbs);
      } 
    } else {
      this.options.getLogger().log(SentryLevel.INFO, "Breadcrumb was dropped by beforeBreadcrumb", new Object[0]);
    } 
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    addBreadcrumb(paramBreadcrumb, null);
  }
  
  public void clearBreadcrumbs() {
    this.breadcrumbs.clear();
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setBreadcrumbs(this.breadcrumbs); 
  }
  
  public void clearTransaction() {
    synchronized (this.transactionLock) {
      this.transaction = null;
    } 
    this.transactionName = null;
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
      iScopeObserver.setTransaction(null);
      iScopeObserver.setTrace(null);
    } 
  }
  
  @Nullable
  public ITransaction getTransaction() {
    return this.transaction;
  }
  
  public void clear() {
    this.level = null;
    this.user = null;
    this.request = null;
    this.screen = null;
    this.fingerprint.clear();
    clearBreadcrumbs();
    this.tags.clear();
    this.extra.clear();
    this.eventProcessors.clear();
    clearTransaction();
    clearAttachments();
  }
  
  @Internal
  @NotNull
  public Map<String, String> getTags() {
    return CollectionUtils.newConcurrentHashMap(this.tags);
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    this.tags.put(paramString1, paramString2);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
      iScopeObserver.setTag(paramString1, paramString2);
      iScopeObserver.setTags(this.tags);
    } 
  }
  
  public void removeTag(@NotNull String paramString) {
    this.tags.remove(paramString);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
      iScopeObserver.removeTag(paramString);
      iScopeObserver.setTags(this.tags);
    } 
  }
  
  @Internal
  @NotNull
  public Map<String, Object> getExtras() {
    return this.extra;
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    this.extra.put(paramString1, paramString2);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
      iScopeObserver.setExtra(paramString1, paramString2);
      iScopeObserver.setExtras(this.extra);
    } 
  }
  
  public void removeExtra(@NotNull String paramString) {
    this.extra.remove(paramString);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers()) {
      iScopeObserver.removeExtra(paramString);
      iScopeObserver.setExtras(this.extra);
    } 
  }
  
  @NotNull
  public Contexts getContexts() {
    return this.contexts;
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Object paramObject) {
    this.contexts.put(paramString, paramObject);
    for (IScopeObserver iScopeObserver : this.options.getScopeObservers())
      iScopeObserver.setContexts(this.contexts); 
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Boolean paramBoolean) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramBoolean);
    setContexts(paramString, hashMap);
  }
  
  public void setContexts(@NotNull String paramString1, @NotNull String paramString2) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramString2);
    setContexts(paramString1, hashMap);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Number paramNumber) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramNumber);
    setContexts(paramString, hashMap);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Collection<?> paramCollection) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramCollection);
    setContexts(paramString, hashMap);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Object[] paramArrayOfObject) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramArrayOfObject);
    setContexts(paramString, hashMap);
  }
  
  public void setContexts(@NotNull String paramString, @NotNull Character paramCharacter) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("value", paramCharacter);
    setContexts(paramString, hashMap);
  }
  
  public void removeContexts(@NotNull String paramString) {
    this.contexts.remove(paramString);
  }
  
  @Internal
  @NotNull
  public List<Attachment> getAttachments() {
    return new CopyOnWriteArrayList<>(this.attachments);
  }
  
  public void addAttachment(@NotNull Attachment paramAttachment) {
    this.attachments.add(paramAttachment);
  }
  
  public void clearAttachments() {
    this.attachments.clear();
  }
  
  @NotNull
  static Queue<Breadcrumb> createBreadcrumbsList(int paramInt) {
    return SynchronizedQueue.synchronizedQueue(new CircularFifoQueue<>(paramInt));
  }
  
  @Internal
  @NotNull
  public List<EventProcessor> getEventProcessors() {
    return EventProcessorUtils.unwrap(this.eventProcessors);
  }
  
  @Internal
  @NotNull
  public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
    return this.eventProcessors;
  }
  
  public void addEventProcessor(@NotNull EventProcessor paramEventProcessor) {
    this.eventProcessors.add(new EventProcessorAndOrder(paramEventProcessor, paramEventProcessor.getOrder()));
  }
  
  @Internal
  @Nullable
  public Session withSession(@NotNull IWithSession paramIWithSession) {
    Session session = null;
    synchronized (this.sessionLock) {
      paramIWithSession.accept(this.session);
      if (this.session != null)
        session = this.session.clone(); 
    } 
    return session;
  }
  
  @Internal
  @Nullable
  public SessionPair startSession() {
    SessionPair sessionPair = null;
    synchronized (this.sessionLock) {
      if (this.session != null)
        this.session.end(); 
      Session session = this.session;
      if (this.options.getRelease() != null) {
        this.session = new Session(this.options.getDistinctId(), this.user, this.options.getEnvironment(), this.options.getRelease());
        Session session1 = (session != null) ? session.clone() : null;
        sessionPair = new SessionPair(this.session.clone(), session1);
      } else {
        this.options.getLogger().log(SentryLevel.WARNING, "Release is not set on SentryOptions. Session could not be started", new Object[0]);
      } 
    } 
    return sessionPair;
  }
  
  @Internal
  @Nullable
  public Session endSession() {
    Session session = null;
    synchronized (this.sessionLock) {
      if (this.session != null) {
        this.session.end();
        session = this.session.clone();
        this.session = null;
      } 
    } 
    return session;
  }
  
  @Internal
  public void withTransaction(@NotNull IWithTransaction paramIWithTransaction) {
    synchronized (this.transactionLock) {
      paramIWithTransaction.accept(this.transaction);
    } 
  }
  
  @Internal
  @NotNull
  public SentryOptions getOptions() {
    return this.options;
  }
  
  @Internal
  @Nullable
  public Session getSession() {
    return this.session;
  }
  
  @Internal
  public void clearSession() {
    this.session = null;
  }
  
  @Internal
  public void setPropagationContext(@NotNull PropagationContext paramPropagationContext) {
    this.propagationContext = paramPropagationContext;
  }
  
  @Internal
  @NotNull
  public PropagationContext getPropagationContext() {
    return this.propagationContext;
  }
  
  @Internal
  @NotNull
  public PropagationContext withPropagationContext(@NotNull IWithPropagationContext paramIWithPropagationContext) {
    synchronized (this.propagationContextLock) {
      paramIWithPropagationContext.accept(this.propagationContext);
      return new PropagationContext(this.propagationContext);
    } 
  }
  
  @NotNull
  public IScope clone() {
    return new Scope(this);
  }
  
  public void setLastEventId(@NotNull SentryId paramSentryId) {
    this.lastEventId = paramSentryId;
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return this.lastEventId;
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {
    this.client = paramISentryClient;
  }
  
  @NotNull
  public ISentryClient getClient() {
    return this.client;
  }
  
  @Internal
  public void assignTraceContext(@NotNull SentryEvent paramSentryEvent) {
    if (this.options.isTracingEnabled() && paramSentryEvent.getThrowable() != null) {
      Pair pair = this.throwableToSpan.get(ExceptionUtils.findRootCause(paramSentryEvent.getThrowable()));
      if (pair != null) {
        WeakReference<ISpan> weakReference = (WeakReference)pair.getFirst();
        if (paramSentryEvent.getContexts().getTrace() == null && weakReference != null) {
          ISpan iSpan = weakReference.get();
          if (iSpan != null)
            paramSentryEvent.getContexts().setTrace(iSpan.getSpanContext()); 
        } 
        String str = (String)pair.getSecond();
        if (paramSentryEvent.getTransaction() == null && str != null)
          paramSentryEvent.setTransaction(str); 
      } 
    } 
  }
  
  @Internal
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {
    Objects.requireNonNull(paramThrowable, "throwable is required");
    Objects.requireNonNull(paramISpan, "span is required");
    Objects.requireNonNull(paramString, "transactionName is required");
    Throwable throwable = ExceptionUtils.findRootCause(paramThrowable);
    if (!this.throwableToSpan.containsKey(throwable))
      this.throwableToSpan.put(throwable, new Pair(new WeakReference<>(paramISpan), paramString)); 
  }
  
  @Internal
  public void replaceOptions(@NotNull SentryOptions paramSentryOptions) {
    if (!getClient().isEnabled()) {
      this.options = paramSentryOptions;
      Queue<Breadcrumb> queue = this.breadcrumbs;
      this.breadcrumbs = createBreadcrumbsList(paramSentryOptions.getMaxBreadcrumbs());
      for (Breadcrumb breadcrumb : queue)
        addBreadcrumb(breadcrumb); 
    } 
  }
  
  static interface IWithSession {
    void accept(@Nullable Session param1Session);
    
    static {
    
    }
  }
  
  static final class SessionPair {
    @Nullable
    private final Session previous;
    
    @NotNull
    private final Session current;
    
    public SessionPair(@NotNull Session param1Session1, @Nullable Session param1Session2) {
      this.current = param1Session1;
      this.previous = param1Session2;
    }
    
    @Nullable
    public Session getPrevious() {
      return this.previous;
    }
    
    @NotNull
    public Session getCurrent() {
      return this.current;
    }
  }
  
  @Internal
  public static interface IWithTransaction {
    void accept(@Nullable ITransaction param1ITransaction);
    
    static {
    
    }
  }
  
  @Internal
  public static interface IWithPropagationContext {
    void accept(@NotNull PropagationContext param1PropagationContext);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */