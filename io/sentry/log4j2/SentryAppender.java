package io.sentry.log4j2;

import io.sentry.Breadcrumb;
import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.IScopes;
import io.sentry.ITransportFactory;
import io.sentry.ScopesAdapter;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.Message;
import io.sentry.protocol.SdkVersion;
import io.sentry.util.CollectionUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Plugin(name = "Sentry", category = "Core", elementType = "appender", printObject = true)
public class SentryAppender extends AbstractAppender {
  public static final String MECHANISM_TYPE = "Log4j2SentryAppender";
  
  @Nullable
  private final String dsn;
  
  @Nullable
  private final ITransportFactory transportFactory;
  
  @NotNull
  private Level minimumBreadcrumbLevel = Level.INFO;
  
  @NotNull
  private Level minimumEventLevel = Level.ERROR;
  
  @Nullable
  private final Boolean debug;
  
  @NotNull
  private final IScopes scopes;
  
  @Nullable
  private final List<String> contextTags;
  
  public SentryAppender(@NotNull String paramString1, @Nullable Filter paramFilter, @Nullable String paramString2, @Nullable Level paramLevel1, @Nullable Level paramLevel2, @Nullable Boolean paramBoolean, @Nullable ITransportFactory paramITransportFactory, @NotNull IScopes paramIScopes, @Nullable String[] paramArrayOfString) {
    super(paramString1, paramFilter, null, true, null);
    this.dsn = paramString2;
    if (paramLevel1 != null)
      this.minimumBreadcrumbLevel = paramLevel1; 
    if (paramLevel2 != null)
      this.minimumEventLevel = paramLevel2; 
    this.debug = paramBoolean;
    this.transportFactory = paramITransportFactory;
    this.scopes = paramIScopes;
    this.contextTags = (paramArrayOfString != null) ? Arrays.<String>asList(paramArrayOfString) : null;
  }
  
  @PluginFactory
  @Nullable
  public static SentryAppender createAppender(@PluginAttribute("name") @Nullable String paramString1, @PluginAttribute("minimumBreadcrumbLevel") @Nullable Level paramLevel1, @PluginAttribute("minimumEventLevel") @Nullable Level paramLevel2, @PluginAttribute("dsn") @Nullable String paramString2, @PluginAttribute("debug") @Nullable Boolean paramBoolean, @PluginElement("filter") @Nullable Filter paramFilter, @PluginAttribute("contextTags") @Nullable String paramString3) {
    if (paramString1 == null) {
      LOGGER.error("No name provided for SentryAppender");
      return null;
    } 
    return new SentryAppender(paramString1, paramFilter, paramString2, paramLevel1, paramLevel2, paramBoolean, null, (IScopes)ScopesAdapter.getInstance(), (paramString3 != null) ? paramString3.split(",") : null);
  }
  
  public void start() {
    if (!Sentry.isEnabled())
      try {
        Sentry.init(paramSentryOptions -> {
              paramSentryOptions.setEnableExternalConfiguration(true);
              paramSentryOptions.setDsn(this.dsn);
              if (this.debug != null)
                paramSentryOptions.setDebug(this.debug.booleanValue()); 
              paramSentryOptions.setSentryClientName("sentry.java.log4j2/8.0.0-alpha.4");
              paramSentryOptions.setSdkVersion(createSdkVersion(paramSentryOptions));
              if (this.contextTags != null)
                for (String str : this.contextTags)
                  paramSentryOptions.addContextTag(str);  
              Objects.requireNonNull(paramSentryOptions);
              Optional.<ITransportFactory>ofNullable(this.transportFactory).ifPresent(paramSentryOptions::setTransportFactory);
            });
      } catch (IllegalArgumentException illegalArgumentException) {
        LOGGER.warn("Failed to init Sentry during appender initialization: " + illegalArgumentException.getMessage());
      }  
    addPackageAndIntegrationInfo();
    super.start();
  }
  
  public void append(@NotNull LogEvent paramLogEvent) {
    if (paramLogEvent.getLevel().isMoreSpecificThan(this.minimumEventLevel)) {
      Hint hint = new Hint();
      hint.set("syntheticException", paramLogEvent);
      this.scopes.captureEvent(createEvent(paramLogEvent), hint);
    } 
    if (paramLogEvent.getLevel().isMoreSpecificThan(this.minimumBreadcrumbLevel)) {
      Hint hint = new Hint();
      hint.set("log4j:logEvent", paramLogEvent);
      this.scopes.addBreadcrumb(createBreadcrumb(paramLogEvent), hint);
    } 
  }
  
  @NotNull
  protected SentryEvent createEvent(@NotNull LogEvent paramLogEvent) {
    SentryEvent sentryEvent = new SentryEvent(DateUtils.getDateTime(paramLogEvent.getTimeMillis()));
    Message message = new Message();
    message.setMessage(paramLogEvent.getMessage().getFormat());
    message.setFormatted(paramLogEvent.getMessage().getFormattedMessage());
    message.setParams(toParams(paramLogEvent.getMessage().getParameters()));
    sentryEvent.setMessage(message);
    sentryEvent.setLogger(paramLogEvent.getLoggerName());
    sentryEvent.setLevel(formatLevel(paramLogEvent.getLevel()));
    ThrowableProxy throwableProxy = paramLogEvent.getThrownProxy();
    if (throwableProxy != null) {
      Mechanism mechanism = new Mechanism();
      mechanism.setType("Log4j2SentryAppender");
      ExceptionMechanismException exceptionMechanismException = new ExceptionMechanismException(mechanism, throwableProxy.getThrowable(), Thread.currentThread());
      sentryEvent.setThrowable((Throwable)exceptionMechanismException);
    } 
    if (paramLogEvent.getThreadName() != null)
      sentryEvent.setExtra("thread_name", paramLogEvent.getThreadName()); 
    if (paramLogEvent.getMarker() != null)
      sentryEvent.setExtra("marker", paramLogEvent.getMarker().toString()); 
    Map map = CollectionUtils.filterMapEntries(paramLogEvent.getContextData().toMap(), paramEntry -> (paramEntry.getValue() != null));
    if (!map.isEmpty()) {
      List list = this.scopes.getOptions().getContextTags();
      if (list != null && !list.isEmpty())
        for (String str : list) {
          if (map.containsKey(str)) {
            sentryEvent.setTag(str, (String)map.get(str));
            map.remove(str);
          } 
        }  
      if (!map.isEmpty())
        sentryEvent.getContexts().put("Context Data", map); 
    } 
    return sentryEvent;
  }
  
  @NotNull
  private List<String> toParams(@Nullable Object[] paramArrayOfObject) {
    return (paramArrayOfObject != null) ? (List<String>)Arrays.<Object>stream(paramArrayOfObject).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList()) : Collections.emptyList();
  }
  
  @NotNull
  protected Breadcrumb createBreadcrumb(@NotNull LogEvent paramLogEvent) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setLevel(formatLevel(paramLogEvent.getLevel()));
    breadcrumb.setCategory(paramLogEvent.getLoggerName());
    breadcrumb.setMessage(paramLogEvent.getMessage().getFormattedMessage());
    return breadcrumb;
  }
  
  @NotNull
  private static SentryLevel formatLevel(@NotNull Level paramLevel) {
    return paramLevel.isMoreSpecificThan(Level.FATAL) ? SentryLevel.FATAL : (paramLevel.isMoreSpecificThan(Level.ERROR) ? SentryLevel.ERROR : (paramLevel.isMoreSpecificThan(Level.WARN) ? SentryLevel.WARNING : (paramLevel.isMoreSpecificThan(Level.INFO) ? SentryLevel.INFO : SentryLevel.DEBUG)));
  }
  
  @NotNull
  private SdkVersion createSdkVersion(@NotNull SentryOptions paramSentryOptions) {
    null = paramSentryOptions.getSdkVersion();
    String str1 = "sentry.java.log4j2";
    String str2 = "8.0.0-alpha.4";
    return SdkVersion.updateSdkVersion(null, "sentry.java.log4j2", "8.0.0-alpha.4");
  }
  
  private void addPackageAndIntegrationInfo() {
    SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.sentry:sentry-log4j2", "8.0.0-alpha.4");
    SentryIntegrationPackageStorage.getInstance().addIntegration("Log4j");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\log4j2\SentryAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */