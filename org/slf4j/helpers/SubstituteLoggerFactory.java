package org.slf4j.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;

public class SubstituteLoggerFactory implements ILoggerFactory {
  volatile boolean postInitialization = false;
  
  final Map<String, SubstituteLogger> loggers = new ConcurrentHashMap<>();
  
  final LinkedBlockingQueue<SubstituteLoggingEvent> eventQueue = new LinkedBlockingQueue<>();
  
  public synchronized Logger getLogger(String paramString) {
    SubstituteLogger substituteLogger = this.loggers.get(paramString);
    if (substituteLogger == null) {
      substituteLogger = new SubstituteLogger(paramString, this.eventQueue, this.postInitialization);
      this.loggers.put(paramString, substituteLogger);
    } 
    return substituteLogger;
  }
  
  public List<String> getLoggerNames() {
    return new ArrayList<>(this.loggers.keySet());
  }
  
  public List<SubstituteLogger> getLoggers() {
    return new ArrayList<>(this.loggers.values());
  }
  
  public LinkedBlockingQueue<SubstituteLoggingEvent> getEventQueue() {
    return this.eventQueue;
  }
  
  public void postInitialization() {
    this.postInitialization = true;
  }
  
  public void clear() {
    this.loggers.clear();
    this.eventQueue.clear();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\SubstituteLoggerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */