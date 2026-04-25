package io.sentry;

import io.sentry.backpressure.IBackpressureMonitor;
import io.sentry.backpressure.NoOpBackpressureMonitor;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.clientreport.ClientReportRecorder;
import io.sentry.clientreport.IClientReportRecorder;
import io.sentry.clientreport.NoOpClientReportRecorder;
import io.sentry.internal.debugmeta.IDebugMetaLoader;
import io.sentry.internal.debugmeta.NoOpDebugMetaLoader;
import io.sentry.internal.gestures.GestureTargetLocator;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.NoOpModulesLoader;
import io.sentry.internal.viewhierarchy.ViewHierarchyExporter;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.ITransportGate;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.transport.NoOpTransportGate;
import io.sentry.util.Platform;
import io.sentry.util.SampleRateUtils;
import io.sentry.util.StringUtils;
import io.sentry.util.thread.IMainThreadChecker;
import io.sentry.util.thread.NoOpMainThreadChecker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.net.ssl.SSLSocketFactory;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class SentryOptions {
  @Internal
  @NotNull
  public static final String DEFAULT_PROPAGATION_TARGETS = ".*";
  
  static final SentryLevel DEFAULT_DIAGNOSTIC_LEVEL = SentryLevel.DEBUG;
  
  private static final String DEFAULT_ENVIRONMENT = "production";
  
  @NotNull
  private final List<EventProcessor> eventProcessors = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final Set<Class<? extends Throwable>> ignoredExceptionsForType = new CopyOnWriteArraySet<>();
  
  @NotNull
  private final List<Integration> integrations = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final Set<String> bundleIds = new CopyOnWriteArraySet<>();
  
  @Nullable
  private String dsn;
  
  @Nullable
  private String dsnHash;
  
  private long shutdownTimeoutMillis = 2000L;
  
  private long flushTimeoutMillis = 15000L;
  
  private long sessionFlushTimeoutMillis = 15000L;
  
  private boolean debug;
  
  @NotNull
  private ILogger logger = NoOpLogger.getInstance();
  
  @NotNull
  private SentryLevel diagnosticLevel = DEFAULT_DIAGNOSTIC_LEVEL;
  
  @NotNull
  private IEnvelopeReader envelopeReader = new EnvelopeReader(new JsonSerializer(this));
  
  @NotNull
  private ISerializer serializer = new JsonSerializer(this);
  
  private int maxDepth = 100;
  
  @Nullable
  private String sentryClientName;
  
  @Nullable
  private BeforeSendCallback beforeSend;
  
  @Nullable
  private BeforeSendTransactionCallback beforeSendTransaction;
  
  @Nullable
  private BeforeBreadcrumbCallback beforeBreadcrumb;
  
  @Nullable
  private String cacheDirPath;
  
  private int maxCacheItems = 30;
  
  private int maxQueueSize = this.maxCacheItems;
  
  private int maxBreadcrumbs = 100;
  
  @Nullable
  private String release;
  
  @Nullable
  private String environment;
  
  @Nullable
  private Proxy proxy;
  
  @Nullable
  private Double sampleRate;
  
  @Nullable
  private Boolean enableTracing;
  
  @Nullable
  private Double tracesSampleRate;
  
  @Nullable
  private TracesSamplerCallback tracesSampler;
  
  @Nullable
  private volatile TracesSampler internalTracesSampler;
  
  @NotNull
  private final List<String> inAppExcludes = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final List<String> inAppIncludes = new CopyOnWriteArrayList<>();
  
  @NotNull
  private ITransportFactory transportFactory = NoOpTransportFactory.getInstance();
  
  @NotNull
  private ITransportGate transportGate = (ITransportGate)NoOpTransportGate.getInstance();
  
  @Nullable
  private String dist;
  
  private boolean attachThreads;
  
  private boolean attachStacktrace = true;
  
  private boolean enableAutoSessionTracking = true;
  
  private long sessionTrackingIntervalMillis = 30000L;
  
  @Nullable
  private String distinctId;
  
  @Nullable
  private String serverName;
  
  private boolean attachServerName = true;
  
  private boolean enableUncaughtExceptionHandler = true;
  
  private boolean printUncaughtStackTrace = false;
  
  @NotNull
  private ISentryExecutorService executorService = NoOpSentryExecutorService.getInstance();
  
  private int connectionTimeoutMillis = 5000;
  
  private int readTimeoutMillis = 5000;
  
  @NotNull
  private IEnvelopeCache envelopeDiskCache = (IEnvelopeCache)NoOpEnvelopeCache.getInstance();
  
  @Nullable
  private SdkVersion sdkVersion;
  
  private boolean sendDefaultPii = false;
  
  @Nullable
  private SSLSocketFactory sslSocketFactory;
  
  @NotNull
  private final List<IScopeObserver> observers = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final List<IOptionsObserver> optionsObservers = new CopyOnWriteArrayList<>();
  
  private boolean enableExternalConfiguration;
  
  @NotNull
  private final Map<String, String> tags = new ConcurrentHashMap<>();
  
  private long maxAttachmentSize = 20971520L;
  
  private boolean enableDeduplication = true;
  
  private int maxSpans = 1000;
  
  private boolean enableShutdownHook = true;
  
  @NotNull
  private RequestSize maxRequestBodySize = RequestSize.NONE;
  
  private boolean traceSampling = true;
  
  @Nullable
  private Double profilesSampleRate;
  
  @Nullable
  private ProfilesSamplerCallback profilesSampler;
  
  private long maxTraceFileSize = 5242880L;
  
  @NotNull
  private ITransactionProfiler transactionProfiler = NoOpTransactionProfiler.getInstance();
  
  @Nullable
  private List<String> tracePropagationTargets = null;
  
  @NotNull
  private final List<String> defaultTracePropagationTargets = Collections.singletonList(".*");
  
  @Nullable
  private String proguardUuid;
  
  @Nullable
  private Long idleTimeout = Long.valueOf(3000L);
  
  @NotNull
  private final List<String> contextTags = new CopyOnWriteArrayList<>();
  
  private boolean sendClientReports = true;
  
  @NotNull
  IClientReportRecorder clientReportRecorder = (IClientReportRecorder)new ClientReportRecorder(this);
  
  @NotNull
  private IModulesLoader modulesLoader = (IModulesLoader)NoOpModulesLoader.getInstance();
  
  @NotNull
  private IDebugMetaLoader debugMetaLoader = (IDebugMetaLoader)NoOpDebugMetaLoader.getInstance();
  
  private boolean enableUserInteractionTracing = false;
  
  private boolean enableUserInteractionBreadcrumbs = true;
  
  @NotNull
  private Instrumenter instrumenter = Instrumenter.SENTRY;
  
  @NotNull
  private final List<GestureTargetLocator> gestureTargetLocators = new ArrayList<>();
  
  @NotNull
  private final List<ViewHierarchyExporter> viewHierarchyExporters = new ArrayList<>();
  
  @NotNull
  private IMainThreadChecker mainThreadChecker = (IMainThreadChecker)NoOpMainThreadChecker.getInstance();
  
  private boolean traceOptionsRequests = true;
  
  @Internal
  @NotNull
  private SentryDateProvider dateProvider = new SentryAutoDateProvider();
  
  @NotNull
  private final List<IPerformanceCollector> performanceCollectors = new ArrayList<>();
  
  @NotNull
  private TransactionPerformanceCollector transactionPerformanceCollector = NoOpTransactionPerformanceCollector.getInstance();
  
  private boolean enableTimeToFullDisplayTracing = false;
  
  @NotNull
  private final FullyDisplayedReporter fullyDisplayedReporter = FullyDisplayedReporter.getInstance();
  
  @NotNull
  private IConnectionStatusProvider connectionStatusProvider = new NoOpConnectionStatusProvider();
  
  private boolean enabled = true;
  
  private boolean enablePrettySerializationOutput = true;
  
  private boolean sendModules = true;
  
  @Nullable
  private BeforeEnvelopeCallback beforeEnvelopeCallback;
  
  private boolean enableSpotlight = false;
  
  @Nullable
  private String spotlightConnectionUrl;
  
  private boolean enableScopePersistence = true;
  
  @Experimental
  @Nullable
  private List<String> ignoredCheckIns = null;
  
  @Experimental
  @Nullable
  private List<String> ignoredSpanOrigins = null;
  
  @Experimental
  @NotNull
  private IBackpressureMonitor backpressureMonitor = (IBackpressureMonitor)NoOpBackpressureMonitor.getInstance();
  
  private boolean enableBackpressureHandling = true;
  
  private boolean enableAppStartProfiling = false;
  
  private boolean enableMetrics = false;
  
  private boolean enableDefaultTagsForMetrics = true;
  
  private boolean enableSpanLocalMetricAggregation = true;
  
  @Nullable
  private BeforeEmitMetricCallback beforeEmitMetricCallback = null;
  
  @NotNull
  private ISpanFactory spanFactory = NoOpSpanFactory.getInstance();
  
  private int profilingTracesHz = 101;
  
  @Experimental
  @Nullable
  private Cron cron = null;
  
  @NotNull
  private ScopeType defaultScopeType = ScopeType.ISOLATION;
  
  public void addEventProcessor(@NotNull EventProcessor paramEventProcessor) {
    this.eventProcessors.add(paramEventProcessor);
  }
  
  @NotNull
  public List<EventProcessor> getEventProcessors() {
    return this.eventProcessors;
  }
  
  public void addIntegration(@NotNull Integration paramIntegration) {
    this.integrations.add(paramIntegration);
  }
  
  @NotNull
  public List<Integration> getIntegrations() {
    return this.integrations;
  }
  
  @Nullable
  public String getDsn() {
    return this.dsn;
  }
  
  public void setDsn(@Nullable String paramString) {
    this.dsn = paramString;
    this.dsnHash = StringUtils.calculateStringHash(this.dsn, this.logger);
  }
  
  public boolean isDebug() {
    return this.debug;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.debug = paramBoolean;
  }
  
  @NotNull
  public ILogger getLogger() {
    return this.logger;
  }
  
  public void setLogger(@Nullable ILogger paramILogger) {
    this.logger = (paramILogger == null) ? NoOpLogger.getInstance() : new DiagnosticLogger(this, paramILogger);
  }
  
  @NotNull
  public SentryLevel getDiagnosticLevel() {
    return this.diagnosticLevel;
  }
  
  public void setDiagnosticLevel(@Nullable SentryLevel paramSentryLevel) {
    this.diagnosticLevel = (paramSentryLevel != null) ? paramSentryLevel : DEFAULT_DIAGNOSTIC_LEVEL;
  }
  
  @NotNull
  public ISerializer getSerializer() {
    return this.serializer;
  }
  
  public void setSerializer(@Nullable ISerializer paramISerializer) {
    this.serializer = (paramISerializer != null) ? paramISerializer : NoOpSerializer.getInstance();
  }
  
  public int getMaxDepth() {
    return this.maxDepth;
  }
  
  public void setMaxDepth(int paramInt) {
    this.maxDepth = paramInt;
  }
  
  @NotNull
  public IEnvelopeReader getEnvelopeReader() {
    return this.envelopeReader;
  }
  
  public void setEnvelopeReader(@Nullable IEnvelopeReader paramIEnvelopeReader) {
    this.envelopeReader = (paramIEnvelopeReader != null) ? paramIEnvelopeReader : NoOpEnvelopeReader.getInstance();
  }
  
  @Deprecated
  @ScheduledForRemoval
  public long getShutdownTimeout() {
    return this.shutdownTimeoutMillis;
  }
  
  public long getShutdownTimeoutMillis() {
    return this.shutdownTimeoutMillis;
  }
  
  @Deprecated
  @ScheduledForRemoval
  public void setShutdownTimeout(long paramLong) {
    this.shutdownTimeoutMillis = paramLong;
  }
  
  public void setShutdownTimeoutMillis(long paramLong) {
    this.shutdownTimeoutMillis = paramLong;
  }
  
  @Nullable
  public String getSentryClientName() {
    return this.sentryClientName;
  }
  
  public void setSentryClientName(@Nullable String paramString) {
    this.sentryClientName = paramString;
  }
  
  @Nullable
  public BeforeSendCallback getBeforeSend() {
    return this.beforeSend;
  }
  
  public void setBeforeSend(@Nullable BeforeSendCallback paramBeforeSendCallback) {
    this.beforeSend = paramBeforeSendCallback;
  }
  
  @Nullable
  public BeforeSendTransactionCallback getBeforeSendTransaction() {
    return this.beforeSendTransaction;
  }
  
  public void setBeforeSendTransaction(@Nullable BeforeSendTransactionCallback paramBeforeSendTransactionCallback) {
    this.beforeSendTransaction = paramBeforeSendTransactionCallback;
  }
  
  @Nullable
  public BeforeBreadcrumbCallback getBeforeBreadcrumb() {
    return this.beforeBreadcrumb;
  }
  
  public void setBeforeBreadcrumb(@Nullable BeforeBreadcrumbCallback paramBeforeBreadcrumbCallback) {
    this.beforeBreadcrumb = paramBeforeBreadcrumbCallback;
  }
  
  @Nullable
  public String getCacheDirPath() {
    return (this.cacheDirPath == null || this.cacheDirPath.isEmpty()) ? null : ((this.dsnHash != null) ? (new File(this.cacheDirPath, this.dsnHash)).getAbsolutePath() : this.cacheDirPath);
  }
  
  @Nullable
  String getCacheDirPathWithoutDsn() {
    return (this.cacheDirPath == null || this.cacheDirPath.isEmpty()) ? null : this.cacheDirPath;
  }
  
  @Nullable
  public String getOutboxPath() {
    String str = getCacheDirPath();
    return (str == null) ? null : (new File(str, "outbox")).getAbsolutePath();
  }
  
  public void setCacheDirPath(@Nullable String paramString) {
    this.cacheDirPath = paramString;
  }
  
  public int getMaxBreadcrumbs() {
    return this.maxBreadcrumbs;
  }
  
  public void setMaxBreadcrumbs(int paramInt) {
    this.maxBreadcrumbs = paramInt;
  }
  
  @Nullable
  public String getRelease() {
    return this.release;
  }
  
  public void setRelease(@Nullable String paramString) {
    this.release = paramString;
  }
  
  @Nullable
  public String getEnvironment() {
    return (this.environment != null) ? this.environment : "production";
  }
  
  public void setEnvironment(@Nullable String paramString) {
    this.environment = paramString;
  }
  
  @Nullable
  public Proxy getProxy() {
    return this.proxy;
  }
  
  public void setProxy(@Nullable Proxy paramProxy) {
    this.proxy = paramProxy;
  }
  
  @Nullable
  public Double getSampleRate() {
    return this.sampleRate;
  }
  
  public void setSampleRate(Double paramDouble) {
    if (!SampleRateUtils.isValidSampleRate(paramDouble))
      throw new IllegalArgumentException("The value " + paramDouble + " is not valid. Use null to disable or values >= 0.0 and <= 1.0."); 
    this.sampleRate = paramDouble;
  }
  
  @Nullable
  public Boolean getEnableTracing() {
    return this.enableTracing;
  }
  
  public void setEnableTracing(@Nullable Boolean paramBoolean) {
    this.enableTracing = paramBoolean;
  }
  
  @Nullable
  public Double getTracesSampleRate() {
    return this.tracesSampleRate;
  }
  
  public void setTracesSampleRate(@Nullable Double paramDouble) {
    if (!SampleRateUtils.isValidTracesSampleRate(paramDouble))
      throw new IllegalArgumentException("The value " + paramDouble + " is not valid. Use null to disable or values between 0.0 and 1.0."); 
    this.tracesSampleRate = paramDouble;
  }
  
  @Nullable
  public TracesSamplerCallback getTracesSampler() {
    return this.tracesSampler;
  }
  
  public void setTracesSampler(@Nullable TracesSamplerCallback paramTracesSamplerCallback) {
    this.tracesSampler = paramTracesSamplerCallback;
  }
  
  @Internal
  @NotNull
  public TracesSampler getInternalTracesSampler() {
    if (this.internalTracesSampler == null)
      synchronized (this) {
        if (this.internalTracesSampler == null)
          this.internalTracesSampler = new TracesSampler(this); 
      }  
    return this.internalTracesSampler;
  }
  
  @NotNull
  public List<String> getInAppExcludes() {
    return this.inAppExcludes;
  }
  
  public void addInAppExclude(@NotNull String paramString) {
    this.inAppExcludes.add(paramString);
  }
  
  @NotNull
  public List<String> getInAppIncludes() {
    return this.inAppIncludes;
  }
  
  public void addInAppInclude(@NotNull String paramString) {
    this.inAppIncludes.add(paramString);
  }
  
  @NotNull
  public ITransportFactory getTransportFactory() {
    return this.transportFactory;
  }
  
  public void setTransportFactory(@Nullable ITransportFactory paramITransportFactory) {
    this.transportFactory = (paramITransportFactory != null) ? paramITransportFactory : NoOpTransportFactory.getInstance();
  }
  
  @Nullable
  public String getDist() {
    return this.dist;
  }
  
  public void setDist(@Nullable String paramString) {
    this.dist = paramString;
  }
  
  @NotNull
  public ITransportGate getTransportGate() {
    return this.transportGate;
  }
  
  public void setTransportGate(@Nullable ITransportGate paramITransportGate) {
    this.transportGate = (paramITransportGate != null) ? paramITransportGate : (ITransportGate)NoOpTransportGate.getInstance();
  }
  
  public boolean isAttachStacktrace() {
    return this.attachStacktrace;
  }
  
  public void setAttachStacktrace(boolean paramBoolean) {
    this.attachStacktrace = paramBoolean;
  }
  
  public boolean isAttachThreads() {
    return this.attachThreads;
  }
  
  public void setAttachThreads(boolean paramBoolean) {
    this.attachThreads = paramBoolean;
  }
  
  public boolean isEnableAutoSessionTracking() {
    return this.enableAutoSessionTracking;
  }
  
  public void setEnableAutoSessionTracking(boolean paramBoolean) {
    this.enableAutoSessionTracking = paramBoolean;
  }
  
  @Nullable
  public String getServerName() {
    return this.serverName;
  }
  
  public void setServerName(@Nullable String paramString) {
    this.serverName = paramString;
  }
  
  public boolean isAttachServerName() {
    return this.attachServerName;
  }
  
  public void setAttachServerName(boolean paramBoolean) {
    this.attachServerName = paramBoolean;
  }
  
  public long getSessionTrackingIntervalMillis() {
    return this.sessionTrackingIntervalMillis;
  }
  
  public void setSessionTrackingIntervalMillis(long paramLong) {
    this.sessionTrackingIntervalMillis = paramLong;
  }
  
  @Internal
  @Nullable
  public String getDistinctId() {
    return this.distinctId;
  }
  
  @Internal
  public void setDistinctId(@Nullable String paramString) {
    this.distinctId = paramString;
  }
  
  public long getFlushTimeoutMillis() {
    return this.flushTimeoutMillis;
  }
  
  public void setFlushTimeoutMillis(long paramLong) {
    this.flushTimeoutMillis = paramLong;
  }
  
  public boolean isEnableUncaughtExceptionHandler() {
    return this.enableUncaughtExceptionHandler;
  }
  
  public void setEnableUncaughtExceptionHandler(boolean paramBoolean) {
    this.enableUncaughtExceptionHandler = paramBoolean;
  }
  
  public boolean isPrintUncaughtStackTrace() {
    return this.printUncaughtStackTrace;
  }
  
  public void setPrintUncaughtStackTrace(boolean paramBoolean) {
    this.printUncaughtStackTrace = paramBoolean;
  }
  
  @Internal
  @NotNull
  public ISentryExecutorService getExecutorService() {
    return this.executorService;
  }
  
  @Internal
  @TestOnly
  public void setExecutorService(@NotNull ISentryExecutorService paramISentryExecutorService) {
    if (paramISentryExecutorService != null)
      this.executorService = paramISentryExecutorService; 
  }
  
  public int getConnectionTimeoutMillis() {
    return this.connectionTimeoutMillis;
  }
  
  public void setConnectionTimeoutMillis(int paramInt) {
    this.connectionTimeoutMillis = paramInt;
  }
  
  public int getReadTimeoutMillis() {
    return this.readTimeoutMillis;
  }
  
  public void setReadTimeoutMillis(int paramInt) {
    this.readTimeoutMillis = paramInt;
  }
  
  @NotNull
  public IEnvelopeCache getEnvelopeDiskCache() {
    return this.envelopeDiskCache;
  }
  
  public void setEnvelopeDiskCache(@Nullable IEnvelopeCache paramIEnvelopeCache) {
    this.envelopeDiskCache = (paramIEnvelopeCache != null) ? paramIEnvelopeCache : (IEnvelopeCache)NoOpEnvelopeCache.getInstance();
  }
  
  public int getMaxQueueSize() {
    return this.maxQueueSize;
  }
  
  public void setMaxQueueSize(int paramInt) {
    if (paramInt > 0)
      this.maxQueueSize = paramInt; 
  }
  
  @Nullable
  public SdkVersion getSdkVersion() {
    return this.sdkVersion;
  }
  
  @Nullable
  public SSLSocketFactory getSslSocketFactory() {
    return this.sslSocketFactory;
  }
  
  public void setSslSocketFactory(@Nullable SSLSocketFactory paramSSLSocketFactory) {
    this.sslSocketFactory = paramSSLSocketFactory;
  }
  
  @Internal
  public void setSdkVersion(@Nullable SdkVersion paramSdkVersion) {
    this.sdkVersion = paramSdkVersion;
  }
  
  public boolean isSendDefaultPii() {
    return this.sendDefaultPii;
  }
  
  public void setSendDefaultPii(boolean paramBoolean) {
    this.sendDefaultPii = paramBoolean;
  }
  
  public void addScopeObserver(@NotNull IScopeObserver paramIScopeObserver) {
    this.observers.add(paramIScopeObserver);
  }
  
  @NotNull
  public List<IScopeObserver> getScopeObservers() {
    return this.observers;
  }
  
  public void addOptionsObserver(@NotNull IOptionsObserver paramIOptionsObserver) {
    this.optionsObservers.add(paramIOptionsObserver);
  }
  
  @NotNull
  public List<IOptionsObserver> getOptionsObservers() {
    return this.optionsObservers;
  }
  
  public boolean isEnableExternalConfiguration() {
    return this.enableExternalConfiguration;
  }
  
  public void setEnableExternalConfiguration(boolean paramBoolean) {
    this.enableExternalConfiguration = paramBoolean;
  }
  
  @NotNull
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    this.tags.put(paramString1, paramString2);
  }
  
  public long getMaxAttachmentSize() {
    return this.maxAttachmentSize;
  }
  
  public void setMaxAttachmentSize(long paramLong) {
    this.maxAttachmentSize = paramLong;
  }
  
  public boolean isEnableDeduplication() {
    return this.enableDeduplication;
  }
  
  public void setEnableDeduplication(boolean paramBoolean) {
    this.enableDeduplication = paramBoolean;
  }
  
  public boolean isTracingEnabled() {
    return (this.enableTracing != null) ? this.enableTracing.booleanValue() : ((getTracesSampleRate() != null || getTracesSampler() != null));
  }
  
  @NotNull
  public Set<Class<? extends Throwable>> getIgnoredExceptionsForType() {
    return this.ignoredExceptionsForType;
  }
  
  public void addIgnoredExceptionForType(@NotNull Class<? extends Throwable> paramClass) {
    this.ignoredExceptionsForType.add(paramClass);
  }
  
  boolean containsIgnoredExceptionForType(@NotNull Throwable paramThrowable) {
    return this.ignoredExceptionsForType.contains(paramThrowable.getClass());
  }
  
  @Experimental
  public int getMaxSpans() {
    return this.maxSpans;
  }
  
  @Experimental
  public void setMaxSpans(int paramInt) {
    this.maxSpans = paramInt;
  }
  
  public boolean isEnableShutdownHook() {
    return this.enableShutdownHook;
  }
  
  public void setEnableShutdownHook(boolean paramBoolean) {
    this.enableShutdownHook = paramBoolean;
  }
  
  public int getMaxCacheItems() {
    return this.maxCacheItems;
  }
  
  public void setMaxCacheItems(int paramInt) {
    this.maxCacheItems = paramInt;
  }
  
  @NotNull
  public RequestSize getMaxRequestBodySize() {
    return this.maxRequestBodySize;
  }
  
  public void setMaxRequestBodySize(@NotNull RequestSize paramRequestSize) {
    this.maxRequestBodySize = paramRequestSize;
  }
  
  @Experimental
  public boolean isTraceSampling() {
    return this.traceSampling;
  }
  
  @Deprecated
  public void setTraceSampling(boolean paramBoolean) {
    this.traceSampling = paramBoolean;
  }
  
  public long getMaxTraceFileSize() {
    return this.maxTraceFileSize;
  }
  
  public void setMaxTraceFileSize(long paramLong) {
    this.maxTraceFileSize = paramLong;
  }
  
  @NotNull
  public ITransactionProfiler getTransactionProfiler() {
    return this.transactionProfiler;
  }
  
  public void setTransactionProfiler(@Nullable ITransactionProfiler paramITransactionProfiler) {
    if (this.transactionProfiler == NoOpTransactionProfiler.getInstance() && paramITransactionProfiler != null)
      this.transactionProfiler = paramITransactionProfiler; 
  }
  
  public boolean isProfilingEnabled() {
    return ((getProfilesSampleRate() != null && getProfilesSampleRate().doubleValue() > 0.0D) || getProfilesSampler() != null);
  }
  
  @Deprecated
  public void setProfilingEnabled(boolean paramBoolean) {
    if (getProfilesSampleRate() == null)
      setProfilesSampleRate(paramBoolean ? Double.valueOf(1.0D) : null); 
  }
  
  @Nullable
  public ProfilesSamplerCallback getProfilesSampler() {
    return this.profilesSampler;
  }
  
  public void setProfilesSampler(@Nullable ProfilesSamplerCallback paramProfilesSamplerCallback) {
    this.profilesSampler = paramProfilesSamplerCallback;
  }
  
  @Nullable
  public Double getProfilesSampleRate() {
    return this.profilesSampleRate;
  }
  
  public void setProfilesSampleRate(@Nullable Double paramDouble) {
    if (!SampleRateUtils.isValidProfilesSampleRate(paramDouble))
      throw new IllegalArgumentException("The value " + paramDouble + " is not valid. Use null to disable or values between 0.0 and 1.0."); 
    this.profilesSampleRate = paramDouble;
  }
  
  @Nullable
  public String getProfilingTracesDirPath() {
    String str = getCacheDirPath();
    return (str == null) ? null : (new File(str, "profiling_traces")).getAbsolutePath();
  }
  
  @Deprecated
  @NotNull
  public List<String> getTracingOrigins() {
    return getTracePropagationTargets();
  }
  
  @Deprecated
  public void addTracingOrigin(@NotNull String paramString) {
    if (this.tracePropagationTargets == null)
      this.tracePropagationTargets = new CopyOnWriteArrayList<>(); 
    if (!paramString.isEmpty())
      this.tracePropagationTargets.add(paramString); 
  }
  
  @Deprecated
  @Internal
  public void setTracingOrigins(@Nullable List<String> paramList) {
    setTracePropagationTargets(paramList);
  }
  
  @NotNull
  public List<String> getTracePropagationTargets() {
    return (this.tracePropagationTargets == null) ? this.defaultTracePropagationTargets : this.tracePropagationTargets;
  }
  
  @Internal
  public void setTracePropagationTargets(@Nullable List<String> paramList) {
    if (paramList == null) {
      this.tracePropagationTargets = null;
    } else {
      ArrayList<String> arrayList = new ArrayList();
      for (String str : paramList) {
        if (!str.isEmpty())
          arrayList.add(str); 
      } 
      this.tracePropagationTargets = arrayList;
    } 
  }
  
  @Nullable
  public String getProguardUuid() {
    return this.proguardUuid;
  }
  
  public void setProguardUuid(@Nullable String paramString) {
    this.proguardUuid = paramString;
  }
  
  public void addBundleId(@Nullable String paramString) {
    if (paramString != null) {
      String str = paramString.trim();
      if (!str.isEmpty())
        this.bundleIds.add(str); 
    } 
  }
  
  @NotNull
  public Set<String> getBundleIds() {
    return this.bundleIds;
  }
  
  @NotNull
  public List<String> getContextTags() {
    return this.contextTags;
  }
  
  public void addContextTag(@NotNull String paramString) {
    this.contextTags.add(paramString);
  }
  
  @Nullable
  public Long getIdleTimeout() {
    return this.idleTimeout;
  }
  
  public void setIdleTimeout(@Nullable Long paramLong) {
    this.idleTimeout = paramLong;
  }
  
  public boolean isSendClientReports() {
    return this.sendClientReports;
  }
  
  public void setSendClientReports(boolean paramBoolean) {
    this.sendClientReports = paramBoolean;
    if (paramBoolean) {
      this.clientReportRecorder = (IClientReportRecorder)new ClientReportRecorder(this);
    } else {
      this.clientReportRecorder = (IClientReportRecorder)new NoOpClientReportRecorder();
    } 
  }
  
  public boolean isEnableUserInteractionTracing() {
    return this.enableUserInteractionTracing;
  }
  
  public void setEnableUserInteractionTracing(boolean paramBoolean) {
    this.enableUserInteractionTracing = paramBoolean;
  }
  
  public boolean isEnableUserInteractionBreadcrumbs() {
    return this.enableUserInteractionBreadcrumbs;
  }
  
  public void setEnableUserInteractionBreadcrumbs(boolean paramBoolean) {
    this.enableUserInteractionBreadcrumbs = paramBoolean;
  }
  
  @Deprecated
  public void setInstrumenter(@NotNull Instrumenter paramInstrumenter) {
    this.instrumenter = paramInstrumenter;
  }
  
  @NotNull
  public Instrumenter getInstrumenter() {
    return this.instrumenter;
  }
  
  @Internal
  @NotNull
  public IClientReportRecorder getClientReportRecorder() {
    return this.clientReportRecorder;
  }
  
  @Internal
  @NotNull
  public IModulesLoader getModulesLoader() {
    return this.modulesLoader;
  }
  
  @Internal
  public void setModulesLoader(@Nullable IModulesLoader paramIModulesLoader) {
    this.modulesLoader = (paramIModulesLoader != null) ? paramIModulesLoader : (IModulesLoader)NoOpModulesLoader.getInstance();
  }
  
  @Internal
  @NotNull
  public IDebugMetaLoader getDebugMetaLoader() {
    return this.debugMetaLoader;
  }
  
  @Internal
  public void setDebugMetaLoader(@Nullable IDebugMetaLoader paramIDebugMetaLoader) {
    this.debugMetaLoader = (paramIDebugMetaLoader != null) ? paramIDebugMetaLoader : (IDebugMetaLoader)NoOpDebugMetaLoader.getInstance();
  }
  
  public List<GestureTargetLocator> getGestureTargetLocators() {
    return this.gestureTargetLocators;
  }
  
  public void setGestureTargetLocators(@NotNull List<GestureTargetLocator> paramList) {
    this.gestureTargetLocators.clear();
    this.gestureTargetLocators.addAll(paramList);
  }
  
  @NotNull
  public final List<ViewHierarchyExporter> getViewHierarchyExporters() {
    return this.viewHierarchyExporters;
  }
  
  public void setViewHierarchyExporters(@NotNull List<ViewHierarchyExporter> paramList) {
    this.viewHierarchyExporters.clear();
    this.viewHierarchyExporters.addAll(paramList);
  }
  
  @NotNull
  public IMainThreadChecker getMainThreadChecker() {
    return this.mainThreadChecker;
  }
  
  public void setMainThreadChecker(@NotNull IMainThreadChecker paramIMainThreadChecker) {
    this.mainThreadChecker = paramIMainThreadChecker;
  }
  
  @Internal
  @NotNull
  public TransactionPerformanceCollector getTransactionPerformanceCollector() {
    return this.transactionPerformanceCollector;
  }
  
  @Internal
  public void setTransactionPerformanceCollector(@NotNull TransactionPerformanceCollector paramTransactionPerformanceCollector) {
    this.transactionPerformanceCollector = paramTransactionPerformanceCollector;
  }
  
  public boolean isEnableTimeToFullDisplayTracing() {
    return this.enableTimeToFullDisplayTracing;
  }
  
  public void setEnableTimeToFullDisplayTracing(boolean paramBoolean) {
    this.enableTimeToFullDisplayTracing = paramBoolean;
  }
  
  @Internal
  @NotNull
  public FullyDisplayedReporter getFullyDisplayedReporter() {
    return this.fullyDisplayedReporter;
  }
  
  public boolean isTraceOptionsRequests() {
    return this.traceOptionsRequests;
  }
  
  public void setTraceOptionsRequests(boolean paramBoolean) {
    this.traceOptionsRequests = paramBoolean;
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(boolean paramBoolean) {
    this.enabled = paramBoolean;
  }
  
  public boolean isEnablePrettySerializationOutput() {
    return this.enablePrettySerializationOutput;
  }
  
  public boolean isSendModules() {
    return this.sendModules;
  }
  
  public void setEnablePrettySerializationOutput(boolean paramBoolean) {
    this.enablePrettySerializationOutput = paramBoolean;
  }
  
  public boolean isEnableAppStartProfiling() {
    return (isProfilingEnabled() && this.enableAppStartProfiling);
  }
  
  public void setEnableAppStartProfiling(boolean paramBoolean) {
    this.enableAppStartProfiling = paramBoolean;
  }
  
  public void setSendModules(boolean paramBoolean) {
    this.sendModules = paramBoolean;
  }
  
  @Experimental
  public void setIgnoredCheckIns(@Nullable List<String> paramList) {
    if (paramList == null) {
      this.ignoredCheckIns = null;
    } else {
      ArrayList<String> arrayList = new ArrayList();
      for (String str : paramList) {
        if (!str.isEmpty())
          arrayList.add(str); 
      } 
      this.ignoredCheckIns = arrayList;
    } 
  }
  
  @Experimental
  @Nullable
  public List<String> getIgnoredSpanOrigins() {
    return this.ignoredSpanOrigins;
  }
  
  @Experimental
  public void setIgnoredSpanOrigins(@Nullable List<String> paramList) {
    if (paramList == null) {
      this.ignoredSpanOrigins = null;
    } else {
      ArrayList<String> arrayList = new ArrayList();
      for (String str : paramList) {
        if (str != null && !str.isEmpty())
          arrayList.add(str); 
      } 
      this.ignoredSpanOrigins = arrayList;
    } 
  }
  
  @Experimental
  @Nullable
  public List<String> getIgnoredCheckIns() {
    return this.ignoredCheckIns;
  }
  
  @Internal
  @NotNull
  public SentryDateProvider getDateProvider() {
    return this.dateProvider;
  }
  
  @Internal
  public void setDateProvider(@NotNull SentryDateProvider paramSentryDateProvider) {
    this.dateProvider = paramSentryDateProvider;
  }
  
  @Internal
  public void addPerformanceCollector(@NotNull IPerformanceCollector paramIPerformanceCollector) {
    this.performanceCollectors.add(paramIPerformanceCollector);
  }
  
  @Internal
  @NotNull
  public List<IPerformanceCollector> getPerformanceCollectors() {
    return this.performanceCollectors;
  }
  
  @NotNull
  public IConnectionStatusProvider getConnectionStatusProvider() {
    return this.connectionStatusProvider;
  }
  
  public void setConnectionStatusProvider(@NotNull IConnectionStatusProvider paramIConnectionStatusProvider) {
    this.connectionStatusProvider = paramIConnectionStatusProvider;
  }
  
  @Internal
  @NotNull
  public IBackpressureMonitor getBackpressureMonitor() {
    return this.backpressureMonitor;
  }
  
  @Internal
  public void setBackpressureMonitor(@NotNull IBackpressureMonitor paramIBackpressureMonitor) {
    this.backpressureMonitor = paramIBackpressureMonitor;
  }
  
  @Experimental
  public void setEnableBackpressureHandling(boolean paramBoolean) {
    this.enableBackpressureHandling = paramBoolean;
  }
  
  @Internal
  public int getProfilingTracesHz() {
    return this.profilingTracesHz;
  }
  
  @Internal
  public void setProfilingTracesHz(int paramInt) {
    this.profilingTracesHz = paramInt;
  }
  
  @Experimental
  public boolean isEnableBackpressureHandling() {
    return this.enableBackpressureHandling;
  }
  
  @Internal
  public long getSessionFlushTimeoutMillis() {
    return this.sessionFlushTimeoutMillis;
  }
  
  @Internal
  public void setSessionFlushTimeoutMillis(long paramLong) {
    this.sessionFlushTimeoutMillis = paramLong;
  }
  
  @Internal
  @Nullable
  public BeforeEnvelopeCallback getBeforeEnvelopeCallback() {
    return this.beforeEnvelopeCallback;
  }
  
  @Internal
  public void setBeforeEnvelopeCallback(@Nullable BeforeEnvelopeCallback paramBeforeEnvelopeCallback) {
    this.beforeEnvelopeCallback = paramBeforeEnvelopeCallback;
  }
  
  @Experimental
  @Nullable
  public String getSpotlightConnectionUrl() {
    return this.spotlightConnectionUrl;
  }
  
  @Experimental
  public void setSpotlightConnectionUrl(@Nullable String paramString) {
    this.spotlightConnectionUrl = paramString;
  }
  
  @Experimental
  public boolean isEnableSpotlight() {
    return this.enableSpotlight;
  }
  
  @Experimental
  public void setEnableSpotlight(boolean paramBoolean) {
    this.enableSpotlight = paramBoolean;
  }
  
  public boolean isEnableScopePersistence() {
    return this.enableScopePersistence;
  }
  
  public void setEnableScopePersistence(boolean paramBoolean) {
    this.enableScopePersistence = paramBoolean;
  }
  
  @Experimental
  public boolean isEnableMetrics() {
    return this.enableMetrics;
  }
  
  @Experimental
  public void setEnableMetrics(boolean paramBoolean) {
    this.enableMetrics = paramBoolean;
  }
  
  @Experimental
  public boolean isEnableSpanLocalMetricAggregation() {
    return (isEnableMetrics() && this.enableSpanLocalMetricAggregation);
  }
  
  @Experimental
  public void setEnableSpanLocalMetricAggregation(boolean paramBoolean) {
    this.enableSpanLocalMetricAggregation = paramBoolean;
  }
  
  @Experimental
  public boolean isEnableDefaultTagsForMetrics() {
    return (isEnableMetrics() && this.enableDefaultTagsForMetrics);
  }
  
  @Experimental
  public void setEnableDefaultTagsForMetrics(boolean paramBoolean) {
    this.enableDefaultTagsForMetrics = paramBoolean;
  }
  
  @Experimental
  @Nullable
  public BeforeEmitMetricCallback getBeforeEmitMetricCallback() {
    return this.beforeEmitMetricCallback;
  }
  
  @Experimental
  public void setBeforeEmitMetricCallback(@Nullable BeforeEmitMetricCallback paramBeforeEmitMetricCallback) {
    this.beforeEmitMetricCallback = paramBeforeEmitMetricCallback;
  }
  
  @Nullable
  public Cron getCron() {
    return this.cron;
  }
  
  @Experimental
  public void setCron(@Nullable Cron paramCron) {
    this.cron = paramCron;
  }
  
  public void setDefaultScopeType(@NotNull ScopeType paramScopeType) {
    this.defaultScopeType = paramScopeType;
  }
  
  @NotNull
  public ScopeType getDefaultScopeType() {
    return this.defaultScopeType;
  }
  
  @Internal
  @NotNull
  public static SentryOptions empty() {
    return new SentryOptions(true);
  }
  
  public SentryOptions() {
    this(false);
  }
  
  private SentryOptions(boolean paramBoolean) {
    if (!paramBoolean) {
      setSpanFactory(new DefaultSpanFactory());
      this.executorService = new SentryExecutorService();
      this.integrations.add(new UncaughtExceptionHandlerIntegration());
      this.integrations.add(new ShutdownHookIntegration());
      this.integrations.add(new SpotlightIntegration());
      this.eventProcessors.add(new MainEventProcessor(this));
      this.eventProcessors.add(new DuplicateEventDetectionEventProcessor(this));
      if (Platform.isJvm())
        this.eventProcessors.add(new SentryRuntimeEventProcessor()); 
      setSentryClientName("sentry.java/8.0.0-alpha.4");
      setSdkVersion(createSdkVersion());
      addPackageInfo();
    } 
  }
  
  public void merge(@NotNull ExternalOptions paramExternalOptions) {
    if (paramExternalOptions.getDsn() != null)
      setDsn(paramExternalOptions.getDsn()); 
    if (paramExternalOptions.getEnvironment() != null)
      setEnvironment(paramExternalOptions.getEnvironment()); 
    if (paramExternalOptions.getRelease() != null)
      setRelease(paramExternalOptions.getRelease()); 
    if (paramExternalOptions.getDist() != null)
      setDist(paramExternalOptions.getDist()); 
    if (paramExternalOptions.getServerName() != null)
      setServerName(paramExternalOptions.getServerName()); 
    if (paramExternalOptions.getProxy() != null)
      setProxy(paramExternalOptions.getProxy()); 
    if (paramExternalOptions.getEnableUncaughtExceptionHandler() != null)
      setEnableUncaughtExceptionHandler(paramExternalOptions.getEnableUncaughtExceptionHandler().booleanValue()); 
    if (paramExternalOptions.getPrintUncaughtStackTrace() != null)
      setPrintUncaughtStackTrace(paramExternalOptions.getPrintUncaughtStackTrace().booleanValue()); 
    if (paramExternalOptions.getEnableTracing() != null)
      setEnableTracing(paramExternalOptions.getEnableTracing()); 
    if (paramExternalOptions.getTracesSampleRate() != null)
      setTracesSampleRate(paramExternalOptions.getTracesSampleRate()); 
    if (paramExternalOptions.getProfilesSampleRate() != null)
      setProfilesSampleRate(paramExternalOptions.getProfilesSampleRate()); 
    if (paramExternalOptions.getDebug() != null)
      setDebug(paramExternalOptions.getDebug().booleanValue()); 
    if (paramExternalOptions.getEnableDeduplication() != null)
      setEnableDeduplication(paramExternalOptions.getEnableDeduplication().booleanValue()); 
    if (paramExternalOptions.getSendClientReports() != null)
      setSendClientReports(paramExternalOptions.getSendClientReports().booleanValue()); 
    HashMap<String, String> hashMap = new HashMap<>(paramExternalOptions.getTags());
    for (Map.Entry<String, String> entry : hashMap.entrySet())
      this.tags.put((String)entry.getKey(), (String)entry.getValue()); 
    ArrayList<String> arrayList1 = new ArrayList<>(paramExternalOptions.getInAppIncludes());
    for (String str : arrayList1)
      addInAppInclude(str); 
    ArrayList<String> arrayList2 = new ArrayList<>(paramExternalOptions.getInAppExcludes());
    for (String str : arrayList2)
      addInAppExclude(str); 
    for (Class<? extends Throwable> clazz : (Iterable<Class<? extends Throwable>>)new HashSet(paramExternalOptions.getIgnoredExceptionsForType()))
      addIgnoredExceptionForType(clazz); 
    if (paramExternalOptions.getTracePropagationTargets() != null) {
      ArrayList<String> arrayList = new ArrayList<>(paramExternalOptions.getTracePropagationTargets());
      setTracePropagationTargets(arrayList);
    } 
    ArrayList<String> arrayList3 = new ArrayList<>(paramExternalOptions.getContextTags());
    for (String str : arrayList3)
      addContextTag(str); 
    if (paramExternalOptions.getProguardUuid() != null)
      setProguardUuid(paramExternalOptions.getProguardUuid()); 
    if (paramExternalOptions.getIdleTimeout() != null)
      setIdleTimeout(paramExternalOptions.getIdleTimeout()); 
    for (String str : paramExternalOptions.getBundleIds())
      addBundleId(str); 
    if (paramExternalOptions.isEnabled() != null)
      setEnabled(paramExternalOptions.isEnabled().booleanValue()); 
    if (paramExternalOptions.isEnablePrettySerializationOutput() != null)
      setEnablePrettySerializationOutput(paramExternalOptions.isEnablePrettySerializationOutput().booleanValue()); 
    if (paramExternalOptions.isSendModules() != null)
      setSendModules(paramExternalOptions.isSendModules().booleanValue()); 
    if (paramExternalOptions.getIgnoredCheckIns() != null) {
      ArrayList<String> arrayList = new ArrayList<>(paramExternalOptions.getIgnoredCheckIns());
      setIgnoredCheckIns(arrayList);
    } 
    if (paramExternalOptions.isEnableBackpressureHandling() != null)
      setEnableBackpressureHandling(paramExternalOptions.isEnableBackpressureHandling().booleanValue()); 
    if (paramExternalOptions.getMaxRequestBodySize() != null)
      setMaxRequestBodySize(paramExternalOptions.getMaxRequestBodySize()); 
    if (paramExternalOptions.isSendDefaultPii() != null)
      setSendDefaultPii(paramExternalOptions.isSendDefaultPii().booleanValue()); 
    if (paramExternalOptions.getCron() != null)
      if (getCron() == null) {
        setCron(paramExternalOptions.getCron());
      } else {
        if (paramExternalOptions.getCron().getDefaultCheckinMargin() != null)
          getCron().setDefaultCheckinMargin(paramExternalOptions.getCron().getDefaultCheckinMargin()); 
        if (paramExternalOptions.getCron().getDefaultMaxRuntime() != null)
          getCron().setDefaultMaxRuntime(paramExternalOptions.getCron().getDefaultMaxRuntime()); 
        if (paramExternalOptions.getCron().getDefaultTimezone() != null)
          getCron().setDefaultTimezone(paramExternalOptions.getCron().getDefaultTimezone()); 
        if (paramExternalOptions.getCron().getDefaultFailureIssueThreshold() != null)
          getCron().setDefaultFailureIssueThreshold(paramExternalOptions.getCron().getDefaultFailureIssueThreshold()); 
        if (paramExternalOptions.getCron().getDefaultRecoveryThreshold() != null)
          getCron().setDefaultRecoveryThreshold(paramExternalOptions.getCron().getDefaultRecoveryThreshold()); 
      }  
  }
  
  @NotNull
  private SdkVersion createSdkVersion() {
    String str = "8.0.0-alpha.4";
    SdkVersion sdkVersion = new SdkVersion("sentry.java", "8.0.0-alpha.4");
    sdkVersion.setVersion("8.0.0-alpha.4");
    return sdkVersion;
  }
  
  private void addPackageInfo() {
    SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.sentry:sentry", "8.0.0-alpha.4");
  }
  
  @Internal
  @NotNull
  public ISpanFactory getSpanFactory() {
    return this.spanFactory;
  }
  
  @Internal
  public void setSpanFactory(@NotNull ISpanFactory paramISpanFactory) {
    this.spanFactory = paramISpanFactory;
  }
  
  public static interface BeforeSendCallback {
    @Nullable
    SentryEvent execute(@NotNull SentryEvent param1SentryEvent, @NotNull Hint param1Hint);
    
    static {
    
    }
  }
  
  public static interface BeforeSendTransactionCallback {
    @Nullable
    SentryTransaction execute(@NotNull SentryTransaction param1SentryTransaction, @NotNull Hint param1Hint);
    
    static {
    
    }
  }
  
  public static interface BeforeBreadcrumbCallback {
    @Nullable
    Breadcrumb execute(@NotNull Breadcrumb param1Breadcrumb, @NotNull Hint param1Hint);
    
    static {
    
    }
  }
  
  public static final class Proxy {
    @Nullable
    private String host;
    
    @Nullable
    private String port;
    
    @Nullable
    private String user;
    
    @Nullable
    private String pass;
    
    @Nullable
    private java.net.Proxy.Type type;
    
    public Proxy() {
      this(null, null, null, null, null);
    }
    
    public Proxy(@Nullable String param1String1, @Nullable String param1String2) {
      this(param1String1, param1String2, null, null, null);
    }
    
    public Proxy(@Nullable String param1String1, @Nullable String param1String2, @Nullable java.net.Proxy.Type param1Type) {
      this(param1String1, param1String2, param1Type, null, null);
    }
    
    public Proxy(@Nullable String param1String1, @Nullable String param1String2, @Nullable String param1String3, @Nullable String param1String4) {
      this(param1String1, param1String2, null, param1String3, param1String4);
    }
    
    public Proxy(@Nullable String param1String1, @Nullable String param1String2, @Nullable java.net.Proxy.Type param1Type, @Nullable String param1String3, @Nullable String param1String4) {
      this.host = param1String1;
      this.port = param1String2;
      this.type = param1Type;
      this.user = param1String3;
      this.pass = param1String4;
    }
    
    @Nullable
    public String getHost() {
      return this.host;
    }
    
    public void setHost(@Nullable String param1String) {
      this.host = param1String;
    }
    
    @Nullable
    public String getPort() {
      return this.port;
    }
    
    public void setPort(@Nullable String param1String) {
      this.port = param1String;
    }
    
    @Nullable
    public String getUser() {
      return this.user;
    }
    
    public void setUser(@Nullable String param1String) {
      this.user = param1String;
    }
    
    @Nullable
    public String getPass() {
      return this.pass;
    }
    
    public void setPass(@Nullable String param1String) {
      this.pass = param1String;
    }
    
    @Nullable
    public java.net.Proxy.Type getType() {
      return this.type;
    }
    
    public void setType(@Nullable java.net.Proxy.Type param1Type) {
      this.type = param1Type;
    }
  }
  
  public static interface TracesSamplerCallback {
    @Nullable
    Double sample(@NotNull SamplingContext param1SamplingContext);
    
    static {
    
    }
  }
  
  public enum RequestSize {
    NONE, SMALL, MEDIUM, ALWAYS;
  }
  
  public static interface ProfilesSamplerCallback {
    @Nullable
    Double sample(@NotNull SamplingContext param1SamplingContext);
    
    static {
    
    }
  }
  
  @Internal
  public static interface BeforeEnvelopeCallback {
    void execute(@NotNull SentryEnvelope param1SentryEnvelope, @Nullable Hint param1Hint);
    
    static {
    
    }
  }
  
  @Experimental
  public static interface BeforeEmitMetricCallback {
    boolean execute(@NotNull String param1String, @Nullable Map<String, String> param1Map);
    
    static {
    
    }
  }
  
  public static final class Cron {
    @Nullable
    private Long defaultCheckinMargin;
    
    @Nullable
    private Long defaultMaxRuntime;
    
    @Nullable
    private String defaultTimezone;
    
    @Nullable
    private Long defaultFailureIssueThreshold;
    
    @Nullable
    private Long defaultRecoveryThreshold;
    
    @Nullable
    public Long getDefaultCheckinMargin() {
      return this.defaultCheckinMargin;
    }
    
    public void setDefaultCheckinMargin(@Nullable Long param1Long) {
      this.defaultCheckinMargin = param1Long;
    }
    
    @Nullable
    public Long getDefaultMaxRuntime() {
      return this.defaultMaxRuntime;
    }
    
    public void setDefaultMaxRuntime(@Nullable Long param1Long) {
      this.defaultMaxRuntime = param1Long;
    }
    
    @Nullable
    public String getDefaultTimezone() {
      return this.defaultTimezone;
    }
    
    public void setDefaultTimezone(@Nullable String param1String) {
      this.defaultTimezone = param1String;
    }
    
    @Nullable
    public Long getDefaultFailureIssueThreshold() {
      return this.defaultFailureIssueThreshold;
    }
    
    public void setDefaultFailureIssueThreshold(@Nullable Long param1Long) {
      this.defaultFailureIssueThreshold = param1Long;
    }
    
    @Nullable
    public Long getDefaultRecoveryThreshold() {
      return this.defaultRecoveryThreshold;
    }
    
    public void setDefaultRecoveryThreshold(@Nullable Long param1Long) {
      this.defaultRecoveryThreshold = param1Long;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */