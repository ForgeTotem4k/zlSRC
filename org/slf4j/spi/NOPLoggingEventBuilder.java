package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;

public class NOPLoggingEventBuilder implements LoggingEventBuilder {
  static final NOPLoggingEventBuilder SINGLETON = new NOPLoggingEventBuilder();
  
  public static LoggingEventBuilder singleton() {
    return SINGLETON;
  }
  
  public LoggingEventBuilder addMarker(Marker paramMarker) {
    return singleton();
  }
  
  public LoggingEventBuilder addArgument(Object paramObject) {
    return singleton();
  }
  
  public LoggingEventBuilder addArgument(Supplier<?> paramSupplier) {
    return singleton();
  }
  
  public LoggingEventBuilder addKeyValue(String paramString, Object paramObject) {
    return singleton();
  }
  
  public LoggingEventBuilder addKeyValue(String paramString, Supplier<Object> paramSupplier) {
    return singleton();
  }
  
  public LoggingEventBuilder setCause(Throwable paramThrowable) {
    return singleton();
  }
  
  public void log() {}
  
  public LoggingEventBuilder setMessage(String paramString) {
    return this;
  }
  
  public LoggingEventBuilder setMessage(Supplier<String> paramSupplier) {
    return this;
  }
  
  public void log(String paramString) {}
  
  public void log(Supplier<String> paramSupplier) {}
  
  public void log(String paramString, Object paramObject) {}
  
  public void log(String paramString, Object paramObject1, Object paramObject2) {}
  
  public void log(String paramString, Object... paramVarArgs) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\NOPLoggingEventBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */