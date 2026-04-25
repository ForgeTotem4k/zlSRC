package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;
import org.slf4j.helpers.CheckReturnValue;

public interface LoggingEventBuilder {
  @CheckReturnValue
  LoggingEventBuilder setCause(Throwable paramThrowable);
  
  @CheckReturnValue
  LoggingEventBuilder addMarker(Marker paramMarker);
  
  @CheckReturnValue
  LoggingEventBuilder addArgument(Object paramObject);
  
  @CheckReturnValue
  LoggingEventBuilder addArgument(Supplier<?> paramSupplier);
  
  @CheckReturnValue
  LoggingEventBuilder addKeyValue(String paramString, Object paramObject);
  
  @CheckReturnValue
  LoggingEventBuilder addKeyValue(String paramString, Supplier<Object> paramSupplier);
  
  @CheckReturnValue
  LoggingEventBuilder setMessage(String paramString);
  
  @CheckReturnValue
  LoggingEventBuilder setMessage(Supplier<String> paramSupplier);
  
  void log();
  
  void log(String paramString);
  
  void log(String paramString, Object paramObject);
  
  void log(String paramString, Object paramObject1, Object paramObject2);
  
  void log(String paramString, Object... paramVarArgs);
  
  void log(Supplier<String> paramSupplier);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\LoggingEventBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */