package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

public class SubstituteLoggingEvent implements LoggingEvent {
  Level level;
  
  List<Marker> markers;
  
  String loggerName;
  
  SubstituteLogger logger;
  
  String threadName;
  
  String message;
  
  Object[] argArray;
  
  List<KeyValuePair> keyValuePairList;
  
  long timeStamp;
  
  Throwable throwable;
  
  public Level getLevel() {
    return this.level;
  }
  
  public void setLevel(Level paramLevel) {
    this.level = paramLevel;
  }
  
  public List<Marker> getMarkers() {
    return this.markers;
  }
  
  public void addMarker(Marker paramMarker) {
    if (paramMarker == null)
      return; 
    if (this.markers == null)
      this.markers = new ArrayList<>(2); 
    this.markers.add(paramMarker);
  }
  
  public String getLoggerName() {
    return this.loggerName;
  }
  
  public void setLoggerName(String paramString) {
    this.loggerName = paramString;
  }
  
  public SubstituteLogger getLogger() {
    return this.logger;
  }
  
  public void setLogger(SubstituteLogger paramSubstituteLogger) {
    this.logger = paramSubstituteLogger;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String paramString) {
    this.message = paramString;
  }
  
  public Object[] getArgumentArray() {
    return this.argArray;
  }
  
  public void setArgumentArray(Object[] paramArrayOfObject) {
    this.argArray = paramArrayOfObject;
  }
  
  public List<Object> getArguments() {
    return (this.argArray == null) ? null : Arrays.asList(this.argArray);
  }
  
  public long getTimeStamp() {
    return this.timeStamp;
  }
  
  public void setTimeStamp(long paramLong) {
    this.timeStamp = paramLong;
  }
  
  public String getThreadName() {
    return this.threadName;
  }
  
  public void setThreadName(String paramString) {
    this.threadName = paramString;
  }
  
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  public void setThrowable(Throwable paramThrowable) {
    this.throwable = paramThrowable;
  }
  
  public List<KeyValuePair> getKeyValuePairs() {
    return this.keyValuePairList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\event\SubstituteLoggingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */