package io.sentry;

import io.sentry.config.PropertiesProvider;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExternalOptions {
  private static final String PROXY_PORT_DEFAULT = "80";
  
  @Nullable
  private String dsn;
  
  @Nullable
  private String environment;
  
  @Nullable
  private String release;
  
  @Nullable
  private String dist;
  
  @Nullable
  private String serverName;
  
  @Nullable
  private Boolean enableUncaughtExceptionHandler;
  
  @Nullable
  private Boolean debug;
  
  @Nullable
  private Boolean enableDeduplication;
  
  @Nullable
  private Boolean enableTracing;
  
  @Nullable
  private Double tracesSampleRate;
  
  @Nullable
  private Double profilesSampleRate;
  
  @Nullable
  private SentryOptions.RequestSize maxRequestBodySize;
  
  @NotNull
  private final Map<String, String> tags = new ConcurrentHashMap<>();
  
  @Nullable
  private SentryOptions.Proxy proxy;
  
  @NotNull
  private final List<String> inAppExcludes = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final List<String> inAppIncludes = new CopyOnWriteArrayList<>();
  
  @Nullable
  private List<String> tracePropagationTargets = null;
  
  @NotNull
  private final List<String> contextTags = new CopyOnWriteArrayList<>();
  
  @Nullable
  private String proguardUuid;
  
  @Nullable
  private Long idleTimeout;
  
  @NotNull
  private final Set<Class<? extends Throwable>> ignoredExceptionsForType = new CopyOnWriteArraySet<>();
  
  @Nullable
  private Boolean printUncaughtStackTrace;
  
  @Nullable
  private Boolean sendClientReports;
  
  @NotNull
  private Set<String> bundleIds = new CopyOnWriteArraySet<>();
  
  @Nullable
  private Boolean enabled;
  
  @Nullable
  private Boolean enablePrettySerializationOutput;
  
  @Nullable
  private List<String> ignoredCheckIns;
  
  @Nullable
  private Boolean sendModules;
  
  @Nullable
  private Boolean sendDefaultPii;
  
  @Nullable
  private Boolean enableBackpressureHandling;
  
  @Nullable
  private SentryOptions.Cron cron;
  
  @NotNull
  public static ExternalOptions from(@NotNull PropertiesProvider paramPropertiesProvider, @NotNull ILogger paramILogger) {
    ExternalOptions externalOptions = new ExternalOptions();
    externalOptions.setDsn(paramPropertiesProvider.getProperty("dsn"));
    externalOptions.setEnvironment(paramPropertiesProvider.getProperty("environment"));
    externalOptions.setRelease(paramPropertiesProvider.getProperty("release"));
    externalOptions.setDist(paramPropertiesProvider.getProperty("dist"));
    externalOptions.setServerName(paramPropertiesProvider.getProperty("servername"));
    externalOptions.setEnableUncaughtExceptionHandler(paramPropertiesProvider.getBooleanProperty("uncaught.handler.enabled"));
    externalOptions.setPrintUncaughtStackTrace(paramPropertiesProvider.getBooleanProperty("uncaught.handler.print-stacktrace"));
    externalOptions.setEnableTracing(paramPropertiesProvider.getBooleanProperty("enable-tracing"));
    externalOptions.setTracesSampleRate(paramPropertiesProvider.getDoubleProperty("traces-sample-rate"));
    externalOptions.setProfilesSampleRate(paramPropertiesProvider.getDoubleProperty("profiles-sample-rate"));
    externalOptions.setDebug(paramPropertiesProvider.getBooleanProperty("debug"));
    externalOptions.setEnableDeduplication(paramPropertiesProvider.getBooleanProperty("enable-deduplication"));
    externalOptions.setSendClientReports(paramPropertiesProvider.getBooleanProperty("send-client-reports"));
    String str1 = paramPropertiesProvider.getProperty("max-request-body-size");
    if (str1 != null)
      externalOptions.setMaxRequestBodySize(SentryOptions.RequestSize.valueOf(str1.toUpperCase(Locale.ROOT))); 
    Map map = paramPropertiesProvider.getMap("tags");
    for (Map.Entry entry : map.entrySet())
      externalOptions.setTag((String)entry.getKey(), (String)entry.getValue()); 
    String str2 = paramPropertiesProvider.getProperty("proxy.host");
    String str3 = paramPropertiesProvider.getProperty("proxy.user");
    String str4 = paramPropertiesProvider.getProperty("proxy.pass");
    String str5 = paramPropertiesProvider.getProperty("proxy.port", "80");
    if (str2 != null)
      externalOptions.setProxy(new SentryOptions.Proxy(str2, str5, str3, str4)); 
    for (String str : paramPropertiesProvider.getList("in-app-includes"))
      externalOptions.addInAppInclude(str); 
    for (String str : paramPropertiesProvider.getList("in-app-excludes"))
      externalOptions.addInAppExclude(str); 
    List list = null;
    if (paramPropertiesProvider.getProperty("trace-propagation-targets") != null)
      list = paramPropertiesProvider.getList("trace-propagation-targets"); 
    if (list == null && paramPropertiesProvider.getProperty("tracing-origins") != null)
      list = paramPropertiesProvider.getList("tracing-origins"); 
    if (list != null)
      for (String str : list)
        externalOptions.addTracePropagationTarget(str);  
    for (String str : paramPropertiesProvider.getList("context-tags"))
      externalOptions.addContextTag(str); 
    externalOptions.setProguardUuid(paramPropertiesProvider.getProperty("proguard-uuid"));
    for (String str : paramPropertiesProvider.getList("bundle-ids"))
      externalOptions.addBundleId(str); 
    externalOptions.setIdleTimeout(paramPropertiesProvider.getLongProperty("idle-timeout"));
    externalOptions.setEnabled(paramPropertiesProvider.getBooleanProperty("enabled"));
    externalOptions.setEnablePrettySerializationOutput(paramPropertiesProvider.getBooleanProperty("enable-pretty-serialization-output"));
    externalOptions.setSendModules(paramPropertiesProvider.getBooleanProperty("send-modules"));
    externalOptions.setSendDefaultPii(paramPropertiesProvider.getBooleanProperty("send-default-pii"));
    externalOptions.setIgnoredCheckIns(paramPropertiesProvider.getList("ignored-checkins"));
    externalOptions.setEnableBackpressureHandling(paramPropertiesProvider.getBooleanProperty("enable-backpressure-handling"));
    for (String str : paramPropertiesProvider.getList("ignored-exceptions-for-type")) {
      try {
        Class<?> clazz = Class.forName(str);
        if (Throwable.class.isAssignableFrom(clazz)) {
          externalOptions.addIgnoredExceptionForType((Class)clazz);
          continue;
        } 
        paramILogger.log(SentryLevel.WARNING, "Skipping setting %s as ignored-exception-for-type. Reason: %s does not extend Throwable", new Object[] { str, str });
      } catch (ClassNotFoundException classNotFoundException) {
        paramILogger.log(SentryLevel.WARNING, "Skipping setting %s as ignored-exception-for-type. Reason: %s class is not found", new Object[] { str, str });
      } 
    } 
    Long long_1 = paramPropertiesProvider.getLongProperty("cron.default-checkin-margin");
    Long long_2 = paramPropertiesProvider.getLongProperty("cron.default-max-runtime");
    String str6 = paramPropertiesProvider.getProperty("cron.default-timezone");
    Long long_3 = paramPropertiesProvider.getLongProperty("cron.default-failure-issue-threshold");
    Long long_4 = paramPropertiesProvider.getLongProperty("cron.default-recovery-threshold");
    if (long_1 != null || long_2 != null || str6 != null || long_3 != null || long_4 != null) {
      SentryOptions.Cron cron = new SentryOptions.Cron();
      cron.setDefaultCheckinMargin(long_1);
      cron.setDefaultMaxRuntime(long_2);
      cron.setDefaultTimezone(str6);
      cron.setDefaultFailureIssueThreshold(long_3);
      cron.setDefaultRecoveryThreshold(long_4);
      externalOptions.setCron(cron);
    } 
    return externalOptions;
  }
  
  @Nullable
  public String getDsn() {
    return this.dsn;
  }
  
  public void setDsn(@Nullable String paramString) {
    this.dsn = paramString;
  }
  
  @Nullable
  public String getEnvironment() {
    return this.environment;
  }
  
  public void setEnvironment(@Nullable String paramString) {
    this.environment = paramString;
  }
  
  @Nullable
  public String getRelease() {
    return this.release;
  }
  
  public void setRelease(@Nullable String paramString) {
    this.release = paramString;
  }
  
  @Nullable
  public String getDist() {
    return this.dist;
  }
  
  public void setDist(@Nullable String paramString) {
    this.dist = paramString;
  }
  
  @Nullable
  public String getServerName() {
    return this.serverName;
  }
  
  public void setServerName(@Nullable String paramString) {
    this.serverName = paramString;
  }
  
  @Nullable
  public Boolean getEnableUncaughtExceptionHandler() {
    return this.enableUncaughtExceptionHandler;
  }
  
  public void setEnableUncaughtExceptionHandler(@Nullable Boolean paramBoolean) {
    this.enableUncaughtExceptionHandler = paramBoolean;
  }
  
  @Deprecated
  @Nullable
  public List<String> getTracingOrigins() {
    return this.tracePropagationTargets;
  }
  
  @Nullable
  public List<String> getTracePropagationTargets() {
    return this.tracePropagationTargets;
  }
  
  @Nullable
  public Boolean getDebug() {
    return this.debug;
  }
  
  public void setDebug(@Nullable Boolean paramBoolean) {
    this.debug = paramBoolean;
  }
  
  @Nullable
  public Boolean getEnableDeduplication() {
    return this.enableDeduplication;
  }
  
  public void setEnableDeduplication(@Nullable Boolean paramBoolean) {
    this.enableDeduplication = paramBoolean;
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
    this.tracesSampleRate = paramDouble;
  }
  
  @Nullable
  public Double getProfilesSampleRate() {
    return this.profilesSampleRate;
  }
  
  public void setProfilesSampleRate(@Nullable Double paramDouble) {
    this.profilesSampleRate = paramDouble;
  }
  
  @Nullable
  public SentryOptions.RequestSize getMaxRequestBodySize() {
    return this.maxRequestBodySize;
  }
  
  public void setMaxRequestBodySize(@Nullable SentryOptions.RequestSize paramRequestSize) {
    this.maxRequestBodySize = paramRequestSize;
  }
  
  @NotNull
  public Map<String, String> getTags() {
    return this.tags;
  }
  
  @Nullable
  public SentryOptions.Proxy getProxy() {
    return this.proxy;
  }
  
  public void setProxy(@Nullable SentryOptions.Proxy paramProxy) {
    this.proxy = paramProxy;
  }
  
  @NotNull
  public List<String> getInAppExcludes() {
    return this.inAppExcludes;
  }
  
  @NotNull
  public List<String> getInAppIncludes() {
    return this.inAppIncludes;
  }
  
  @NotNull
  public List<String> getContextTags() {
    return this.contextTags;
  }
  
  @Nullable
  public String getProguardUuid() {
    return this.proguardUuid;
  }
  
  public void setProguardUuid(@Nullable String paramString) {
    this.proguardUuid = paramString;
  }
  
  @NotNull
  public Set<Class<? extends Throwable>> getIgnoredExceptionsForType() {
    return this.ignoredExceptionsForType;
  }
  
  public void addInAppInclude(@NotNull String paramString) {
    this.inAppIncludes.add(paramString);
  }
  
  public void addInAppExclude(@NotNull String paramString) {
    this.inAppExcludes.add(paramString);
  }
  
  @Deprecated
  public void addTracingOrigin(@NotNull String paramString) {
    addTracePropagationTarget(paramString);
  }
  
  public void addTracePropagationTarget(@NotNull String paramString) {
    if (this.tracePropagationTargets == null)
      this.tracePropagationTargets = new CopyOnWriteArrayList<>(); 
    if (!paramString.isEmpty())
      this.tracePropagationTargets.add(paramString); 
  }
  
  public void addContextTag(@NotNull String paramString) {
    this.contextTags.add(paramString);
  }
  
  public void addIgnoredExceptionForType(@NotNull Class<? extends Throwable> paramClass) {
    this.ignoredExceptionsForType.add(paramClass);
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    this.tags.put(paramString1, paramString2);
  }
  
  @Nullable
  public Boolean getPrintUncaughtStackTrace() {
    return this.printUncaughtStackTrace;
  }
  
  public void setPrintUncaughtStackTrace(@Nullable Boolean paramBoolean) {
    this.printUncaughtStackTrace = paramBoolean;
  }
  
  @Nullable
  public Long getIdleTimeout() {
    return this.idleTimeout;
  }
  
  public void setIdleTimeout(@Nullable Long paramLong) {
    this.idleTimeout = paramLong;
  }
  
  @Nullable
  public Boolean getSendClientReports() {
    return this.sendClientReports;
  }
  
  public void setSendClientReports(@Nullable Boolean paramBoolean) {
    this.sendClientReports = paramBoolean;
  }
  
  @NotNull
  public Set<String> getBundleIds() {
    return this.bundleIds;
  }
  
  public void addBundleId(@NotNull String paramString) {
    this.bundleIds.add(paramString);
  }
  
  @Nullable
  public Boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(@Nullable Boolean paramBoolean) {
    this.enabled = paramBoolean;
  }
  
  @Nullable
  public Boolean isEnablePrettySerializationOutput() {
    return this.enablePrettySerializationOutput;
  }
  
  public void setEnablePrettySerializationOutput(@Nullable Boolean paramBoolean) {
    this.enablePrettySerializationOutput = paramBoolean;
  }
  
  @Nullable
  public Boolean isSendModules() {
    return this.sendModules;
  }
  
  public void setSendModules(@Nullable Boolean paramBoolean) {
    this.sendModules = paramBoolean;
  }
  
  @Nullable
  public Boolean isSendDefaultPii() {
    return this.sendDefaultPii;
  }
  
  public void setSendDefaultPii(@Nullable Boolean paramBoolean) {
    this.sendDefaultPii = paramBoolean;
  }
  
  @Experimental
  public void setIgnoredCheckIns(@Nullable List<String> paramList) {
    this.ignoredCheckIns = paramList;
  }
  
  @Experimental
  @Nullable
  public List<String> getIgnoredCheckIns() {
    return this.ignoredCheckIns;
  }
  
  @Experimental
  public void setEnableBackpressureHandling(@Nullable Boolean paramBoolean) {
    this.enableBackpressureHandling = paramBoolean;
  }
  
  @Experimental
  @Nullable
  public Boolean isEnableBackpressureHandling() {
    return this.enableBackpressureHandling;
  }
  
  @Experimental
  @Nullable
  public SentryOptions.Cron getCron() {
    return this.cron;
  }
  
  @Experimental
  public void setCron(@Nullable SentryOptions.Cron paramCron) {
    this.cron = paramCron;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ExternalOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */