package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class DefaultLoggingEvent implements LoggingEvent {
  Logger logger;
  
  Level level;
  
  String message;
  
  List<Marker> markers;
  
  List<Object> arguments;
  
  List<KeyValuePair> keyValuePairs;
  
  Throwable throwable;
  
  String threadName;
  
  long timeStamp;
  
  String callerBoundary;
  
  public DefaultLoggingEvent(Level paramLevel, Logger paramLogger) {
    this.logger = paramLogger;
    this.level = paramLevel;
  }
  
  public void addMarker(Marker paramMarker) {
    if (this.markers == null)
      this.markers = new ArrayList<>(2); 
    this.markers.add(paramMarker);
  }
  
  public List<Marker> getMarkers() {
    return this.markers;
  }
  
  public void addArgument(Object paramObject) {
    getNonNullArguments().add(paramObject);
  }
  
  public void addArguments(Object... paramVarArgs) {
    getNonNullArguments().addAll(Arrays.asList(paramVarArgs));
  }
  
  private List<Object> getNonNullArguments() {
    if (this.arguments == null)
      this.arguments = new ArrayList(3); 
    return this.arguments;
  }
  
  public List<Object> getArguments() {
    return this.arguments;
  }
  
  public Object[] getArgumentArray() {
    return (this.arguments == null) ? null : this.arguments.toArray();
  }
  
  public void addKeyValue(String paramString, Object paramObject) {
    getNonnullKeyValuePairs().add(new KeyValuePair(paramString, paramObject));
  }
  
  private List<KeyValuePair> getNonnullKeyValuePairs() {
    if (this.keyValuePairs == null)
      this.keyValuePairs = new ArrayList<>(4); 
    return this.keyValuePairs;
  }
  
  public List<KeyValuePair> getKeyValuePairs() {
    return this.keyValuePairs;
  }
  
  public void setThrowable(Throwable paramThrowable) {
    this.throwable = paramThrowable;
  }
  
  public Level getLevel() {
    return this.level;
  }
  
  public String getLoggerName() {
    return this.logger.getName();
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String paramString) {
    this.message = paramString;
  }
  
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  public String getThreadName() {
    return this.threadName;
  }
  
  public long getTimeStamp() {
    return this.timeStamp;
  }
  
  public void setTimeStamp(long paramLong) {
    this.timeStamp = paramLong;
  }
  
  public void setCallerBoundary(String paramString) {
    this.callerBoundary = paramString;
  }
  
  public String getCallerBoundary() {
    return this.callerBoundary;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\event\DefaultLoggingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */