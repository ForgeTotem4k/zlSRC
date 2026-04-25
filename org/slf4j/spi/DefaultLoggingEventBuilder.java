package org.slf4j.spi;

import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;

public class DefaultLoggingEventBuilder implements LoggingEventBuilder, CallerBoundaryAware {
  static String DLEB_FQCN = DefaultLoggingEventBuilder.class.getName();
  
  protected DefaultLoggingEvent loggingEvent;
  
  protected Logger logger;
  
  public DefaultLoggingEventBuilder(Logger paramLogger, Level paramLevel) {
    this.logger = paramLogger;
    this.loggingEvent = new DefaultLoggingEvent(paramLevel, paramLogger);
  }
  
  public LoggingEventBuilder addMarker(Marker paramMarker) {
    this.loggingEvent.addMarker(paramMarker);
    return this;
  }
  
  public LoggingEventBuilder setCause(Throwable paramThrowable) {
    this.loggingEvent.setThrowable(paramThrowable);
    return this;
  }
  
  public LoggingEventBuilder addArgument(Object paramObject) {
    this.loggingEvent.addArgument(paramObject);
    return this;
  }
  
  public LoggingEventBuilder addArgument(Supplier<?> paramSupplier) {
    this.loggingEvent.addArgument(paramSupplier.get());
    return this;
  }
  
  public LoggingEventBuilder addKeyValue(String paramString, Object paramObject) {
    this.loggingEvent.addKeyValue(paramString, paramObject);
    return this;
  }
  
  public LoggingEventBuilder addKeyValue(String paramString, Supplier<Object> paramSupplier) {
    this.loggingEvent.addKeyValue(paramString, paramSupplier.get());
    return this;
  }
  
  public void setCallerBoundary(String paramString) {
    this.loggingEvent.setCallerBoundary(paramString);
  }
  
  public void log() {
    log((LoggingEvent)this.loggingEvent);
  }
  
  public LoggingEventBuilder setMessage(String paramString) {
    this.loggingEvent.setMessage(paramString);
    return this;
  }
  
  public LoggingEventBuilder setMessage(Supplier<String> paramSupplier) {
    this.loggingEvent.setMessage(paramSupplier.get());
    return this;
  }
  
  public void log(String paramString) {
    this.loggingEvent.setMessage(paramString);
    log((LoggingEvent)this.loggingEvent);
  }
  
  public void log(String paramString, Object paramObject) {
    this.loggingEvent.setMessage(paramString);
    this.loggingEvent.addArgument(paramObject);
    log((LoggingEvent)this.loggingEvent);
  }
  
  public void log(String paramString, Object paramObject1, Object paramObject2) {
    this.loggingEvent.setMessage(paramString);
    this.loggingEvent.addArgument(paramObject1);
    this.loggingEvent.addArgument(paramObject2);
    log((LoggingEvent)this.loggingEvent);
  }
  
  public void log(String paramString, Object... paramVarArgs) {
    this.loggingEvent.setMessage(paramString);
    this.loggingEvent.addArguments(paramVarArgs);
    log((LoggingEvent)this.loggingEvent);
  }
  
  public void log(Supplier<String> paramSupplier) {
    if (paramSupplier == null) {
      log((String)null);
    } else {
      log(paramSupplier.get());
    } 
  }
  
  protected void log(LoggingEvent paramLoggingEvent) {
    if (paramLoggingEvent.getCallerBoundary() == null)
      setCallerBoundary(DLEB_FQCN); 
    if (this.logger instanceof LoggingEventAware) {
      ((LoggingEventAware)this.logger).log(paramLoggingEvent);
    } else if (this.logger instanceof LocationAwareLogger) {
      logViaLocationAwareLoggerAPI((LocationAwareLogger)this.logger, paramLoggingEvent);
    } else {
      logViaPublicSLF4JLoggerAPI(paramLoggingEvent);
    } 
  }
  
  private void logViaLocationAwareLoggerAPI(LocationAwareLogger paramLocationAwareLogger, LoggingEvent paramLoggingEvent) {
    String str1 = paramLoggingEvent.getMessage();
    List list = paramLoggingEvent.getMarkers();
    String str2 = mergeMarkersAndKeyValuePairsAndMessage(paramLoggingEvent);
    paramLocationAwareLogger.log(null, paramLoggingEvent.getCallerBoundary(), paramLoggingEvent.getLevel().toInt(), str2, paramLoggingEvent.getArgumentArray(), paramLoggingEvent.getThrowable());
  }
  
  private void logViaPublicSLF4JLoggerAPI(LoggingEvent paramLoggingEvent) {
    Object[] arrayOfObject1 = paramLoggingEvent.getArgumentArray();
    byte b1 = (arrayOfObject1 == null) ? 0 : arrayOfObject1.length;
    Throwable throwable = paramLoggingEvent.getThrowable();
    byte b2 = (throwable == null) ? 0 : 1;
    Object[] arrayOfObject2 = new Object[b1 + b2];
    if (arrayOfObject1 != null)
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, b1); 
    if (throwable != null)
      arrayOfObject2[b1] = throwable; 
    String str = mergeMarkersAndKeyValuePairsAndMessage(paramLoggingEvent);
    switch (paramLoggingEvent.getLevel()) {
      case TRACE:
        this.logger.trace(str, arrayOfObject2);
        break;
      case DEBUG:
        this.logger.debug(str, arrayOfObject2);
        break;
      case INFO:
        this.logger.info(str, arrayOfObject2);
        break;
      case WARN:
        this.logger.warn(str, arrayOfObject2);
        break;
      case ERROR:
        this.logger.error(str, arrayOfObject2);
        break;
    } 
  }
  
  private String mergeMarkersAndKeyValuePairsAndMessage(LoggingEvent paramLoggingEvent) {
    StringBuilder stringBuilder = mergeMarkers(paramLoggingEvent.getMarkers(), null);
    stringBuilder = mergeKeyValuePairs(paramLoggingEvent.getKeyValuePairs(), stringBuilder);
    return mergeMessage(paramLoggingEvent.getMessage(), stringBuilder);
  }
  
  private StringBuilder mergeMarkers(List<Marker> paramList, StringBuilder paramStringBuilder) {
    if (paramList == null || paramList.isEmpty())
      return paramStringBuilder; 
    if (paramStringBuilder == null)
      paramStringBuilder = new StringBuilder(); 
    for (Marker marker : paramList) {
      paramStringBuilder.append(marker);
      paramStringBuilder.append(' ');
    } 
    return paramStringBuilder;
  }
  
  private StringBuilder mergeKeyValuePairs(List<KeyValuePair> paramList, StringBuilder paramStringBuilder) {
    if (paramList == null || paramList.isEmpty())
      return paramStringBuilder; 
    if (paramStringBuilder == null)
      paramStringBuilder = new StringBuilder(); 
    for (KeyValuePair keyValuePair : paramList) {
      paramStringBuilder.append(keyValuePair.key);
      paramStringBuilder.append('=');
      paramStringBuilder.append(keyValuePair.value);
      paramStringBuilder.append(' ');
    } 
    return paramStringBuilder;
  }
  
  private String mergeMessage(String paramString, StringBuilder paramStringBuilder) {
    if (paramStringBuilder != null) {
      paramStringBuilder.append(paramString);
      return paramStringBuilder.toString();
    } 
    return paramString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\DefaultLoggingEventBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */