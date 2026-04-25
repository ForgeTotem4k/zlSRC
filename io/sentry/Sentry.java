package io.sentry;

import io.sentry.backpressure.BackpressureMonitor;
import io.sentry.backpressure.IBackpressureMonitor;
import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.config.PropertiesProviderFactory;
import io.sentry.internal.debugmeta.IDebugMetaLoader;
import io.sentry.internal.debugmeta.ResourcesDebugMetaLoader;
import io.sentry.internal.modules.CompositeModulesLoader;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.ManifestModulesLoader;
import io.sentry.internal.modules.NoOpModulesLoader;
import io.sentry.internal.modules.ResourcesModulesLoader;
import io.sentry.metrics.MetricsApi;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.DebugMetaPropertiesApplier;
import io.sentry.util.FileUtils;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.thread.IMainThreadChecker;
import io.sentry.util.thread.MainThreadChecker;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Sentry {
  @NotNull
  private static volatile IScopesStorage scopesStorage = ScopesStorageFactory.create(new LoadClass(), NoOpLogger.getInstance());
  
  @NotNull
  private static volatile IScopes rootScopes = NoOpScopes.getInstance();
  
  @NotNull
  private static final IScope globalScope = new Scope(SentryOptions.empty());
  
  private static final boolean GLOBAL_HUB_DEFAULT_MODE = false;
  
  private static volatile boolean globalHubMode = false;
  
  @Internal
  @NotNull
  public static final String APP_START_PROFILING_CONFIG_FILE_NAME = "app_start_profiling_config";
  
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  private static final long classCreationTimestamp = System.currentTimeMillis();
  
  @Deprecated
  @Internal
  @NotNull
  public static IHub getCurrentHub() {
    return new HubScopesWrapper(getCurrentScopes());
  }
  
  @Internal
  @NotNull
  public static IScopes getCurrentScopes() {
    if (globalHubMode)
      return rootScopes; 
    IScopes iScopes = getScopesStorage().get();
    if (iScopes == null || iScopes.isNoOp()) {
      iScopes = rootScopes.forkedScopes("getCurrentScopes");
      getScopesStorage().set(iScopes);
    } 
    return iScopes;
  }
  
  @NotNull
  private static IScopesStorage getScopesStorage() {
    return scopesStorage;
  }
  
  @Internal
  @NotNull
  public static IScopes forkedRootScopes(@NotNull String paramString) {
    return globalHubMode ? rootScopes : rootScopes.forkedScopes(paramString);
  }
  
  @NotNull
  public static IScopes forkedScopes(@NotNull String paramString) {
    return getCurrentScopes().forkedScopes(paramString);
  }
  
  @NotNull
  public static IScopes forkedCurrentScope(@NotNull String paramString) {
    return getCurrentScopes().forkedCurrentScope(paramString);
  }
  
  @Deprecated
  @Internal
  @NotNull
  public static ISentryLifecycleToken setCurrentHub(@NotNull IHub paramIHub) {
    return setCurrentScopes(paramIHub);
  }
  
  @Internal
  @NotNull
  public static ISentryLifecycleToken setCurrentScopes(@NotNull IScopes paramIScopes) {
    return getScopesStorage().set(paramIScopes);
  }
  
  @NotNull
  public static IScope getGlobalScope() {
    return globalScope;
  }
  
  public static boolean isEnabled() {
    return getCurrentScopes().isEnabled();
  }
  
  public static void init() {
    init(paramSentryOptions -> paramSentryOptions.setEnableExternalConfiguration(true), false);
  }
  
  public static void init(@NotNull String paramString) {
    init(paramSentryOptions -> paramSentryOptions.setDsn(paramString));
  }
  
  public static <T extends SentryOptions> void init(@NotNull OptionsContainer<T> paramOptionsContainer, @NotNull OptionsConfiguration<T> paramOptionsConfiguration) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    init(paramOptionsContainer, paramOptionsConfiguration, false);
  }
  
  public static <T extends SentryOptions> void init(@NotNull OptionsContainer<T> paramOptionsContainer, @NotNull OptionsConfiguration<T> paramOptionsConfiguration, boolean paramBoolean) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    SentryOptions sentryOptions = (SentryOptions)paramOptionsContainer.createInstance();
    applyOptionsConfiguration(paramOptionsConfiguration, (T)sentryOptions);
    init(sentryOptions, paramBoolean);
  }
  
  public static void init(@NotNull OptionsConfiguration<SentryOptions> paramOptionsConfiguration) {
    init(paramOptionsConfiguration, false);
  }
  
  public static void init(@NotNull OptionsConfiguration<SentryOptions> paramOptionsConfiguration, boolean paramBoolean) {
    SentryOptions sentryOptions = new SentryOptions();
    applyOptionsConfiguration(paramOptionsConfiguration, sentryOptions);
    init(sentryOptions, paramBoolean);
  }
  
  private static <T extends SentryOptions> void applyOptionsConfiguration(OptionsConfiguration<T> paramOptionsConfiguration, T paramT) {
    try {
      paramOptionsConfiguration.configure(paramT);
    } catch (Throwable throwable) {
      paramT.getLogger().log(SentryLevel.ERROR, "Error in the 'OptionsConfiguration.configure' callback.", throwable);
    } 
  }
  
  @Internal
  public static void init(@NotNull SentryOptions paramSentryOptions) {
    init(paramSentryOptions, false);
  }
  
  private static synchronized void init(@NotNull SentryOptions paramSentryOptions, boolean paramBoolean) {
    if (isEnabled())
      paramSentryOptions.getLogger().log(SentryLevel.WARNING, "Sentry has been already initialized. Previous configuration will be overwritten.", new Object[0]); 
    if (!initConfigurations(paramSentryOptions))
      return; 
    paramSentryOptions.getLogger().log(SentryLevel.INFO, "GlobalHubMode: '%s'", new Object[] { String.valueOf(paramBoolean) });
    globalHubMode = paramBoolean;
    globalScope.replaceOptions(paramSentryOptions);
    IScopes iScopes = getCurrentScopes();
    Scope scope1 = new Scope(paramSentryOptions);
    Scope scope2 = new Scope(paramSentryOptions);
    rootScopes = new Scopes(scope1, scope2, globalScope, "Sentry.init");
    getScopesStorage().set(rootScopes);
    iScopes.close(true);
    globalScope.bindClient(new SentryClient(rootScopes.getOptions()));
    if (paramSentryOptions.getExecutorService().isClosed())
      paramSentryOptions.setExecutorService(new SentryExecutorService()); 
    for (Integration integration : paramSentryOptions.getIntegrations())
      integration.register(ScopesAdapter.getInstance(), paramSentryOptions); 
    notifyOptionsObservers(paramSentryOptions);
    finalizePreviousSession(paramSentryOptions, ScopesAdapter.getInstance());
    handleAppStartProfilingConfig(paramSentryOptions, paramSentryOptions.getExecutorService());
  }
  
  private static void handleAppStartProfilingConfig(@NotNull SentryOptions paramSentryOptions, @NotNull ISentryExecutorService paramISentryExecutorService) {
    try {
      paramISentryExecutorService.submit(() -> {
            String str = paramSentryOptions.getCacheDirPathWithoutDsn();
            if (str != null) {
              File file = new File(str, "app_start_profiling_config");
              try {
                FileUtils.deleteRecursively(file);
                if (!paramSentryOptions.isEnableAppStartProfiling())
                  return; 
                if (!paramSentryOptions.isTracingEnabled()) {
                  paramSentryOptions.getLogger().log(SentryLevel.INFO, "Tracing is disabled and app start profiling will not start.", new Object[0]);
                  return;
                } 
                if (file.createNewFile()) {
                  TracesSamplingDecision tracesSamplingDecision = sampleAppStartProfiling(paramSentryOptions);
                  SentryAppStartProfilingOptions sentryAppStartProfilingOptions = new SentryAppStartProfilingOptions(paramSentryOptions, tracesSamplingDecision);
                  FileOutputStream fileOutputStream = new FileOutputStream(file);
                  try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF_8));
                    try {
                      paramSentryOptions.getSerializer().serialize(sentryAppStartProfilingOptions, bufferedWriter);
                      bufferedWriter.close();
                    } catch (Throwable throwable) {
                      try {
                        bufferedWriter.close();
                      } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                      } 
                      throw throwable;
                    } 
                    fileOutputStream.close();
                  } catch (Throwable throwable) {
                    try {
                      fileOutputStream.close();
                    } catch (Throwable throwable1) {
                      throwable.addSuppressed(throwable1);
                    } 
                    throw throwable;
                  } 
                } 
              } catch (Throwable throwable) {
                paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Unable to create app start profiling config file. ", throwable);
              } 
            } 
          });
    } catch (Throwable throwable) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. App start profiling config will not be changed. Did you call Sentry.close()?", throwable);
    } 
  }
  
  @NotNull
  private static TracesSamplingDecision sampleAppStartProfiling(@NotNull SentryOptions paramSentryOptions) {
    TransactionContext transactionContext = new TransactionContext("app.launch", "profile");
    transactionContext.setForNextAppStart(true);
    SamplingContext samplingContext = new SamplingContext(transactionContext, null);
    return paramSentryOptions.getInternalTracesSampler().sample(samplingContext);
  }
  
  private static void finalizePreviousSession(@NotNull SentryOptions paramSentryOptions, @NotNull IScopes paramIScopes) {
    try {
      paramSentryOptions.getExecutorService().submit(new PreviousSessionFinalizer(paramSentryOptions, paramIScopes));
    } catch (Throwable throwable) {
      paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Failed to finalize previous session.", throwable);
    } 
  }
  
  private static void notifyOptionsObservers(@NotNull SentryOptions paramSentryOptions) {
    try {
      paramSentryOptions.getExecutorService().submit(() -> {
            for (IOptionsObserver iOptionsObserver : paramSentryOptions.getOptionsObservers()) {
              iOptionsObserver.setRelease(paramSentryOptions.getRelease());
              iOptionsObserver.setProguardUuid(paramSentryOptions.getProguardUuid());
              iOptionsObserver.setSdkVersion(paramSentryOptions.getSdkVersion());
              iOptionsObserver.setDist(paramSentryOptions.getDist());
              iOptionsObserver.setEnvironment(paramSentryOptions.getEnvironment());
              iOptionsObserver.setTags(paramSentryOptions.getTags());
            } 
          });
    } catch (Throwable throwable) {
      paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Failed to notify options observers.", throwable);
    } 
  }
  
  private static boolean initConfigurations(@NotNull SentryOptions paramSentryOptions) {
    if (paramSentryOptions.isEnableExternalConfiguration())
      paramSentryOptions.merge(ExternalOptions.from(PropertiesProviderFactory.create(), paramSentryOptions.getLogger())); 
    String str1 = paramSentryOptions.getDsn();
    if (!paramSentryOptions.isEnabled() || (str1 != null && str1.isEmpty())) {
      close();
      return false;
    } 
    if (str1 == null)
      throw new IllegalArgumentException("DSN is required. Use empty string or set enabled to false in SentryOptions to disable SDK."); 
    Dsn dsn = new Dsn(str1);
    ILogger iLogger = paramSentryOptions.getLogger();
    if (paramSentryOptions.isDebug() && iLogger instanceof NoOpLogger) {
      paramSentryOptions.setLogger(new SystemOutLogger());
      iLogger = paramSentryOptions.getLogger();
    } 
    iLogger.log(SentryLevel.INFO, "Initializing SDK with DSN: '%s'", new Object[] { paramSentryOptions.getDsn() });
    String str2 = paramSentryOptions.getOutboxPath();
    if (str2 != null) {
      File file = new File(str2);
      file.mkdirs();
    } else {
      iLogger.log(SentryLevel.INFO, "No outbox dir path is defined in options.", new Object[0]);
    } 
    String str3 = paramSentryOptions.getCacheDirPath();
    if (str3 != null) {
      File file = new File(str3);
      file.mkdirs();
      IEnvelopeCache iEnvelopeCache = paramSentryOptions.getEnvelopeDiskCache();
      if (iEnvelopeCache instanceof io.sentry.transport.NoOpEnvelopeCache)
        paramSentryOptions.setEnvelopeDiskCache(EnvelopeCache.create(paramSentryOptions)); 
    } 
    String str4 = paramSentryOptions.getProfilingTracesDirPath();
    if (paramSentryOptions.isProfilingEnabled() && str4 != null) {
      File file = new File(str4);
      file.mkdirs();
      try {
        paramSentryOptions.getExecutorService().submit(() -> {
              File[] arrayOfFile = paramFile.listFiles();
              if (arrayOfFile == null)
                return; 
              for (File file : arrayOfFile) {
                if (file.lastModified() < classCreationTimestamp - TimeUnit.MINUTES.toMillis(5L))
                  FileUtils.deleteRecursively(file); 
              } 
            });
      } catch (RejectedExecutionException rejectedExecutionException) {
        paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Old profiles will not be deleted. Did you call Sentry.close()?", rejectedExecutionException);
      } 
    } 
    IModulesLoader iModulesLoader = paramSentryOptions.getModulesLoader();
    if (!paramSentryOptions.isSendModules()) {
      paramSentryOptions.setModulesLoader((IModulesLoader)NoOpModulesLoader.getInstance());
    } else if (iModulesLoader instanceof NoOpModulesLoader) {
      paramSentryOptions.setModulesLoader((IModulesLoader)new CompositeModulesLoader(Arrays.asList(new IModulesLoader[] { (IModulesLoader)new ManifestModulesLoader(paramSentryOptions.getLogger()), (IModulesLoader)new ResourcesModulesLoader(paramSentryOptions.getLogger()) }, ), paramSentryOptions.getLogger()));
    } 
    if (paramSentryOptions.getDebugMetaLoader() instanceof io.sentry.internal.debugmeta.NoOpDebugMetaLoader)
      paramSentryOptions.setDebugMetaLoader((IDebugMetaLoader)new ResourcesDebugMetaLoader(paramSentryOptions.getLogger())); 
    List list = paramSentryOptions.getDebugMetaLoader().loadDebugMeta();
    DebugMetaPropertiesApplier.applyToOptions(paramSentryOptions, list);
    IMainThreadChecker iMainThreadChecker = paramSentryOptions.getMainThreadChecker();
    if (iMainThreadChecker instanceof io.sentry.util.thread.NoOpMainThreadChecker)
      paramSentryOptions.setMainThreadChecker((IMainThreadChecker)MainThreadChecker.getInstance()); 
    if (paramSentryOptions.getPerformanceCollectors().isEmpty())
      paramSentryOptions.addPerformanceCollector(new JavaMemoryCollector()); 
    if (paramSentryOptions.isEnableBackpressureHandling() && Platform.isJvm()) {
      paramSentryOptions.setBackpressureMonitor((IBackpressureMonitor)new BackpressureMonitor(paramSentryOptions, ScopesAdapter.getInstance()));
      paramSentryOptions.getBackpressureMonitor().start();
    } 
    return true;
  }
  
  public static synchronized void close() {
    IScopes iScopes = getCurrentScopes();
    rootScopes = NoOpScopes.getInstance();
    getScopesStorage().close();
    iScopes.close(false);
  }
  
  @NotNull
  public static SentryId captureEvent(@NotNull SentryEvent paramSentryEvent) {
    return getCurrentScopes().captureEvent(paramSentryEvent);
  }
  
  @NotNull
  public static SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureEvent(paramSentryEvent, paramScopeCallback);
  }
  
  @NotNull
  public static SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return getCurrentScopes().captureEvent(paramSentryEvent, paramHint);
  }
  
  @NotNull
  public static SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureEvent(paramSentryEvent, paramHint, paramScopeCallback);
  }
  
  @NotNull
  public static SentryId captureMessage(@NotNull String paramString) {
    return getCurrentScopes().captureMessage(paramString);
  }
  
  @NotNull
  public static SentryId captureMessage(@NotNull String paramString, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureMessage(paramString, paramScopeCallback);
  }
  
  @NotNull
  public static SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return getCurrentScopes().captureMessage(paramString, paramSentryLevel);
  }
  
  @NotNull
  public static SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureMessage(paramString, paramSentryLevel, paramScopeCallback);
  }
  
  @NotNull
  public static SentryId captureException(@NotNull Throwable paramThrowable) {
    return getCurrentScopes().captureException(paramThrowable);
  }
  
  @NotNull
  public static SentryId captureException(@NotNull Throwable paramThrowable, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureException(paramThrowable, paramScopeCallback);
  }
  
  @NotNull
  public static SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return getCurrentScopes().captureException(paramThrowable, paramHint);
  }
  
  @NotNull
  public static SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return getCurrentScopes().captureException(paramThrowable, paramHint, paramScopeCallback);
  }
  
  public static void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {
    getCurrentScopes().captureUserFeedback(paramUserFeedback);
  }
  
  public static void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    getCurrentScopes().addBreadcrumb(paramBreadcrumb, paramHint);
  }
  
  public static void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    getCurrentScopes().addBreadcrumb(paramBreadcrumb);
  }
  
  public static void addBreadcrumb(@NotNull String paramString) {
    getCurrentScopes().addBreadcrumb(paramString);
  }
  
  public static void addBreadcrumb(@NotNull String paramString1, @NotNull String paramString2) {
    getCurrentScopes().addBreadcrumb(paramString1, paramString2);
  }
  
  public static void setLevel(@Nullable SentryLevel paramSentryLevel) {
    getCurrentScopes().setLevel(paramSentryLevel);
  }
  
  public static void setTransaction(@Nullable String paramString) {
    getCurrentScopes().setTransaction(paramString);
  }
  
  public static void setUser(@Nullable User paramUser) {
    getCurrentScopes().setUser(paramUser);
  }
  
  public static void setFingerprint(@NotNull List<String> paramList) {
    getCurrentScopes().setFingerprint(paramList);
  }
  
  public static void clearBreadcrumbs() {
    getCurrentScopes().clearBreadcrumbs();
  }
  
  public static void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    getCurrentScopes().setTag(paramString1, paramString2);
  }
  
  public static void removeTag(@NotNull String paramString) {
    getCurrentScopes().removeTag(paramString);
  }
  
  public static void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    getCurrentScopes().setExtra(paramString1, paramString2);
  }
  
  public static void removeExtra(@NotNull String paramString) {
    getCurrentScopes().removeExtra(paramString);
  }
  
  @NotNull
  public static SentryId getLastEventId() {
    return getCurrentScopes().getLastEventId();
  }
  
  @NotNull
  public static ISentryLifecycleToken pushScope() {
    return !globalHubMode ? getCurrentScopes().pushScope() : NoOpScopesLifecycleToken.getInstance();
  }
  
  @NotNull
  public static ISentryLifecycleToken pushIsolationScope() {
    return !globalHubMode ? getCurrentScopes().pushIsolationScope() : NoOpScopesLifecycleToken.getInstance();
  }
  
  @Deprecated
  public static void popScope() {
    if (!globalHubMode)
      getCurrentScopes().popScope(); 
  }
  
  public static void withScope(@NotNull ScopeCallback paramScopeCallback) {
    getCurrentScopes().withScope(paramScopeCallback);
  }
  
  public static void withIsolationScope(@NotNull ScopeCallback paramScopeCallback) {
    getCurrentScopes().withIsolationScope(paramScopeCallback);
  }
  
  public static void configureScope(@NotNull ScopeCallback paramScopeCallback) {
    configureScope(null, paramScopeCallback);
  }
  
  public static void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback) {
    getCurrentScopes().configureScope(paramScopeType, paramScopeCallback);
  }
  
  public static void bindClient(@NotNull ISentryClient paramISentryClient) {
    getCurrentScopes().bindClient(paramISentryClient);
  }
  
  public static boolean isHealthy() {
    return getCurrentScopes().isHealthy();
  }
  
  public static void flush(long paramLong) {
    getCurrentScopes().flush(paramLong);
  }
  
  public static void startSession() {
    getCurrentScopes().startSession();
  }
  
  public static void endSession() {
    getCurrentScopes().endSession();
  }
  
  @NotNull
  public static ITransaction startTransaction(@NotNull String paramString1, @NotNull String paramString2) {
    return getCurrentScopes().startTransaction(paramString1, paramString2);
  }
  
  @NotNull
  public static ITransaction startTransaction(@NotNull String paramString1, @NotNull String paramString2, @NotNull TransactionOptions paramTransactionOptions) {
    return getCurrentScopes().startTransaction(paramString1, paramString2, paramTransactionOptions);
  }
  
  @NotNull
  public static ITransaction startTransaction(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3, @NotNull TransactionOptions paramTransactionOptions) {
    ITransaction iTransaction = getCurrentScopes().startTransaction(paramString1, paramString2, paramTransactionOptions);
    iTransaction.setDescription(paramString3);
    return iTransaction;
  }
  
  @NotNull
  public static ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext) {
    return getCurrentScopes().startTransaction(paramTransactionContext);
  }
  
  @NotNull
  public static ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    return getCurrentScopes().startTransaction(paramTransactionContext, paramTransactionOptions);
  }
  
  @Deprecated
  @Nullable
  public static SentryTraceHeader traceHeaders() {
    return getCurrentScopes().traceHeaders();
  }
  
  @Nullable
  public static ISpan getSpan() {
    return (globalHubMode && Platform.isAndroid()) ? getCurrentScopes().getTransaction() : getCurrentScopes().getOptions().getSpanFactory().retrieveCurrentSpan(getCurrentScopes());
  }
  
  @Nullable
  public static Boolean isCrashedLastRun() {
    return getCurrentScopes().isCrashedLastRun();
  }
  
  public static void reportFullyDisplayed() {
    getCurrentScopes().reportFullyDisplayed();
  }
  
  @Deprecated
  public static void reportFullDisplayed() {
    reportFullyDisplayed();
  }
  
  @NotNull
  @Experimental
  public static MetricsApi metrics() {
    return getCurrentScopes().metrics();
  }
  
  @Nullable
  public static TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList) {
    return getCurrentScopes().continueTrace(paramString, paramList);
  }
  
  @Nullable
  public static SentryTraceHeader getTraceparent() {
    return getCurrentScopes().getTraceparent();
  }
  
  @Nullable
  public static BaggageHeader getBaggage() {
    return getCurrentScopes().getBaggage();
  }
  
  @Experimental
  @NotNull
  public static SentryId captureCheckIn(@NotNull CheckIn paramCheckIn) {
    return getCurrentScopes().captureCheckIn(paramCheckIn);
  }
  
  public static interface OptionsConfiguration<T extends SentryOptions> {
    void configure(@NotNull T param1T);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Sentry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */