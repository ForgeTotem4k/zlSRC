package org.slf4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.EventRecordingLogger;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.spi.LoggingEventBuilder;

public class SubstituteLogger implements Logger {
  private final String name;
  
  private volatile Logger _delegate;
  
  private Boolean delegateEventAware;
  
  private Method logMethodCache;
  
  private EventRecordingLogger eventRecordingLogger;
  
  private final Queue<SubstituteLoggingEvent> eventQueue;
  
  public final boolean createdPostInitialization;
  
  public SubstituteLogger(String paramString, Queue<SubstituteLoggingEvent> paramQueue, boolean paramBoolean) {
    this.name = paramString;
    this.eventQueue = paramQueue;
    this.createdPostInitialization = paramBoolean;
  }
  
  public String getName() {
    return this.name;
  }
  
  public LoggingEventBuilder makeLoggingEventBuilder(Level paramLevel) {
    return delegate().makeLoggingEventBuilder(paramLevel);
  }
  
  public LoggingEventBuilder atLevel(Level paramLevel) {
    return delegate().atLevel(paramLevel);
  }
  
  public boolean isEnabledForLevel(Level paramLevel) {
    return delegate().isEnabledForLevel(paramLevel);
  }
  
  public boolean isTraceEnabled() {
    return delegate().isTraceEnabled();
  }
  
  public void trace(String paramString) {
    delegate().trace(paramString);
  }
  
  public void trace(String paramString, Object paramObject) {
    delegate().trace(paramString, paramObject);
  }
  
  public void trace(String paramString, Object paramObject1, Object paramObject2) {
    delegate().trace(paramString, paramObject1, paramObject2);
  }
  
  public void trace(String paramString, Object... paramVarArgs) {
    delegate().trace(paramString, paramVarArgs);
  }
  
  public void trace(String paramString, Throwable paramThrowable) {
    delegate().trace(paramString, paramThrowable);
  }
  
  public boolean isTraceEnabled(Marker paramMarker) {
    return delegate().isTraceEnabled(paramMarker);
  }
  
  public void trace(Marker paramMarker, String paramString) {
    delegate().trace(paramMarker, paramString);
  }
  
  public void trace(Marker paramMarker, String paramString, Object paramObject) {
    delegate().trace(paramMarker, paramString, paramObject);
  }
  
  public void trace(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    delegate().trace(paramMarker, paramString, paramObject1, paramObject2);
  }
  
  public void trace(Marker paramMarker, String paramString, Object... paramVarArgs) {
    delegate().trace(paramMarker, paramString, paramVarArgs);
  }
  
  public void trace(Marker paramMarker, String paramString, Throwable paramThrowable) {
    delegate().trace(paramMarker, paramString, paramThrowable);
  }
  
  public LoggingEventBuilder atTrace() {
    return delegate().atTrace();
  }
  
  public boolean isDebugEnabled() {
    return delegate().isDebugEnabled();
  }
  
  public void debug(String paramString) {
    delegate().debug(paramString);
  }
  
  public void debug(String paramString, Object paramObject) {
    delegate().debug(paramString, paramObject);
  }
  
  public void debug(String paramString, Object paramObject1, Object paramObject2) {
    delegate().debug(paramString, paramObject1, paramObject2);
  }
  
  public void debug(String paramString, Object... paramVarArgs) {
    delegate().debug(paramString, paramVarArgs);
  }
  
  public void debug(String paramString, Throwable paramThrowable) {
    delegate().debug(paramString, paramThrowable);
  }
  
  public boolean isDebugEnabled(Marker paramMarker) {
    return delegate().isDebugEnabled(paramMarker);
  }
  
  public void debug(Marker paramMarker, String paramString) {
    delegate().debug(paramMarker, paramString);
  }
  
  public void debug(Marker paramMarker, String paramString, Object paramObject) {
    delegate().debug(paramMarker, paramString, paramObject);
  }
  
  public void debug(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    delegate().debug(paramMarker, paramString, paramObject1, paramObject2);
  }
  
  public void debug(Marker paramMarker, String paramString, Object... paramVarArgs) {
    delegate().debug(paramMarker, paramString, paramVarArgs);
  }
  
  public void debug(Marker paramMarker, String paramString, Throwable paramThrowable) {
    delegate().debug(paramMarker, paramString, paramThrowable);
  }
  
  public LoggingEventBuilder atDebug() {
    return delegate().atDebug();
  }
  
  public boolean isInfoEnabled() {
    return delegate().isInfoEnabled();
  }
  
  public void info(String paramString) {
    delegate().info(paramString);
  }
  
  public void info(String paramString, Object paramObject) {
    delegate().info(paramString, paramObject);
  }
  
  public void info(String paramString, Object paramObject1, Object paramObject2) {
    delegate().info(paramString, paramObject1, paramObject2);
  }
  
  public void info(String paramString, Object... paramVarArgs) {
    delegate().info(paramString, paramVarArgs);
  }
  
  public void info(String paramString, Throwable paramThrowable) {
    delegate().info(paramString, paramThrowable);
  }
  
  public boolean isInfoEnabled(Marker paramMarker) {
    return delegate().isInfoEnabled(paramMarker);
  }
  
  public void info(Marker paramMarker, String paramString) {
    delegate().info(paramMarker, paramString);
  }
  
  public void info(Marker paramMarker, String paramString, Object paramObject) {
    delegate().info(paramMarker, paramString, paramObject);
  }
  
  public void info(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    delegate().info(paramMarker, paramString, paramObject1, paramObject2);
  }
  
  public void info(Marker paramMarker, String paramString, Object... paramVarArgs) {
    delegate().info(paramMarker, paramString, paramVarArgs);
  }
  
  public void info(Marker paramMarker, String paramString, Throwable paramThrowable) {
    delegate().info(paramMarker, paramString, paramThrowable);
  }
  
  public LoggingEventBuilder atInfo() {
    return delegate().atInfo();
  }
  
  public boolean isWarnEnabled() {
    return delegate().isWarnEnabled();
  }
  
  public void warn(String paramString) {
    delegate().warn(paramString);
  }
  
  public void warn(String paramString, Object paramObject) {
    delegate().warn(paramString, paramObject);
  }
  
  public void warn(String paramString, Object paramObject1, Object paramObject2) {
    delegate().warn(paramString, paramObject1, paramObject2);
  }
  
  public void warn(String paramString, Object... paramVarArgs) {
    delegate().warn(paramString, paramVarArgs);
  }
  
  public void warn(String paramString, Throwable paramThrowable) {
    delegate().warn(paramString, paramThrowable);
  }
  
  public boolean isWarnEnabled(Marker paramMarker) {
    return delegate().isWarnEnabled(paramMarker);
  }
  
  public void warn(Marker paramMarker, String paramString) {
    delegate().warn(paramMarker, paramString);
  }
  
  public void warn(Marker paramMarker, String paramString, Object paramObject) {
    delegate().warn(paramMarker, paramString, paramObject);
  }
  
  public void warn(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    delegate().warn(paramMarker, paramString, paramObject1, paramObject2);
  }
  
  public void warn(Marker paramMarker, String paramString, Object... paramVarArgs) {
    delegate().warn(paramMarker, paramString, paramVarArgs);
  }
  
  public void warn(Marker paramMarker, String paramString, Throwable paramThrowable) {
    delegate().warn(paramMarker, paramString, paramThrowable);
  }
  
  public LoggingEventBuilder atWarn() {
    return delegate().atWarn();
  }
  
  public boolean isErrorEnabled() {
    return delegate().isErrorEnabled();
  }
  
  public void error(String paramString) {
    delegate().error(paramString);
  }
  
  public void error(String paramString, Object paramObject) {
    delegate().error(paramString, paramObject);
  }
  
  public void error(String paramString, Object paramObject1, Object paramObject2) {
    delegate().error(paramString, paramObject1, paramObject2);
  }
  
  public void error(String paramString, Object... paramVarArgs) {
    delegate().error(paramString, paramVarArgs);
  }
  
  public void error(String paramString, Throwable paramThrowable) {
    delegate().error(paramString, paramThrowable);
  }
  
  public boolean isErrorEnabled(Marker paramMarker) {
    return delegate().isErrorEnabled(paramMarker);
  }
  
  public void error(Marker paramMarker, String paramString) {
    delegate().error(paramMarker, paramString);
  }
  
  public void error(Marker paramMarker, String paramString, Object paramObject) {
    delegate().error(paramMarker, paramString, paramObject);
  }
  
  public void error(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    delegate().error(paramMarker, paramString, paramObject1, paramObject2);
  }
  
  public void error(Marker paramMarker, String paramString, Object... paramVarArgs) {
    delegate().error(paramMarker, paramString, paramVarArgs);
  }
  
  public void error(Marker paramMarker, String paramString, Throwable paramThrowable) {
    delegate().error(paramMarker, paramString, paramThrowable);
  }
  
  public LoggingEventBuilder atError() {
    return delegate().atError();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SubstituteLogger substituteLogger = (SubstituteLogger)paramObject;
    return !!this.name.equals(substituteLogger.name);
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public Logger delegate() {
    return (this._delegate != null) ? this._delegate : (this.createdPostInitialization ? NOPLogger.NOP_LOGGER : getEventRecordingLogger());
  }
  
  private Logger getEventRecordingLogger() {
    if (this.eventRecordingLogger == null)
      this.eventRecordingLogger = new EventRecordingLogger(this, this.eventQueue); 
    return (Logger)this.eventRecordingLogger;
  }
  
  public void setDelegate(Logger paramLogger) {
    this._delegate = paramLogger;
  }
  
  public boolean isDelegateEventAware() {
    if (this.delegateEventAware != null)
      return this.delegateEventAware.booleanValue(); 
    try {
      this.logMethodCache = this._delegate.getClass().getMethod("log", new Class[] { LoggingEvent.class });
      this.delegateEventAware = Boolean.TRUE;
    } catch (NoSuchMethodException noSuchMethodException) {
      this.delegateEventAware = Boolean.FALSE;
    } 
    return this.delegateEventAware.booleanValue();
  }
  
  public void log(LoggingEvent paramLoggingEvent) {
    if (isDelegateEventAware())
      try {
        this.logMethodCache.invoke(this._delegate, new Object[] { paramLoggingEvent });
      } catch (IllegalAccessException illegalAccessException) {
      
      } catch (IllegalArgumentException illegalArgumentException) {
      
      } catch (InvocationTargetException invocationTargetException) {} 
  }
  
  public boolean isDelegateNull() {
    return (this._delegate == null);
  }
  
  public boolean isDelegateNOP() {
    return this._delegate instanceof NOPLogger;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\SubstituteLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */