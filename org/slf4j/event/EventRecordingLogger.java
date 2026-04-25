package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Marker;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.SubstituteLogger;

public class EventRecordingLogger extends LegacyAbstractLogger {
  private static final long serialVersionUID = -176083308134819629L;
  
  String name;
  
  SubstituteLogger logger;
  
  Queue<SubstituteLoggingEvent> eventQueue;
  
  static final boolean RECORD_ALL_EVENTS = true;
  
  public EventRecordingLogger(SubstituteLogger paramSubstituteLogger, Queue<SubstituteLoggingEvent> paramQueue) {
    this.logger = paramSubstituteLogger;
    this.name = paramSubstituteLogger.getName();
    this.eventQueue = paramQueue;
  }
  
  public String getName() {
    return this.name;
  }
  
  public boolean isTraceEnabled() {
    return true;
  }
  
  public boolean isDebugEnabled() {
    return true;
  }
  
  public boolean isInfoEnabled() {
    return true;
  }
  
  public boolean isWarnEnabled() {
    return true;
  }
  
  public boolean isErrorEnabled() {
    return true;
  }
  
  protected void handleNormalizedLoggingCall(Level paramLevel, Marker paramMarker, String paramString, Object[] paramArrayOfObject, Throwable paramThrowable) {
    SubstituteLoggingEvent substituteLoggingEvent = new SubstituteLoggingEvent();
    substituteLoggingEvent.setTimeStamp(System.currentTimeMillis());
    substituteLoggingEvent.setLevel(paramLevel);
    substituteLoggingEvent.setLogger(this.logger);
    substituteLoggingEvent.setLoggerName(this.name);
    if (paramMarker != null)
      substituteLoggingEvent.addMarker(paramMarker); 
    substituteLoggingEvent.setMessage(paramString);
    substituteLoggingEvent.setThreadName(Thread.currentThread().getName());
    substituteLoggingEvent.setArgumentArray(paramArrayOfObject);
    substituteLoggingEvent.setThrowable(paramThrowable);
    this.eventQueue.add(substituteLoggingEvent);
  }
  
  protected String getFullyQualifiedCallerName() {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\event\EventRecordingLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */