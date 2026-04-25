package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class NOPLogger extends NamedLoggerBase implements Logger {
  private static final long serialVersionUID = -517220405410904473L;
  
  public static final NOPLogger NOP_LOGGER = new NOPLogger();
  
  public String getName() {
    return "NOP";
  }
  
  public final boolean isTraceEnabled() {
    return false;
  }
  
  public final void trace(String paramString) {}
  
  public final void trace(String paramString, Object paramObject) {}
  
  public final void trace(String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void trace(String paramString, Object... paramVarArgs) {}
  
  public final void trace(String paramString, Throwable paramThrowable) {}
  
  public final boolean isDebugEnabled() {
    return false;
  }
  
  public final void debug(String paramString) {}
  
  public final void debug(String paramString, Object paramObject) {}
  
  public final void debug(String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void debug(String paramString, Object... paramVarArgs) {}
  
  public final void debug(String paramString, Throwable paramThrowable) {}
  
  public final boolean isInfoEnabled() {
    return false;
  }
  
  public final void info(String paramString) {}
  
  public final void info(String paramString, Object paramObject) {}
  
  public final void info(String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void info(String paramString, Object... paramVarArgs) {}
  
  public final void info(String paramString, Throwable paramThrowable) {}
  
  public final boolean isWarnEnabled() {
    return false;
  }
  
  public final void warn(String paramString) {}
  
  public final void warn(String paramString, Object paramObject) {}
  
  public final void warn(String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void warn(String paramString, Object... paramVarArgs) {}
  
  public final void warn(String paramString, Throwable paramThrowable) {}
  
  public final boolean isErrorEnabled() {
    return false;
  }
  
  public final void error(String paramString) {}
  
  public final void error(String paramString, Object paramObject) {}
  
  public final void error(String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void error(String paramString, Object... paramVarArgs) {}
  
  public final void error(String paramString, Throwable paramThrowable) {}
  
  public final boolean isTraceEnabled(Marker paramMarker) {
    return false;
  }
  
  public final void trace(Marker paramMarker, String paramString) {}
  
  public final void trace(Marker paramMarker, String paramString, Object paramObject) {}
  
  public final void trace(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void trace(Marker paramMarker, String paramString, Object... paramVarArgs) {}
  
  public final void trace(Marker paramMarker, String paramString, Throwable paramThrowable) {}
  
  public final boolean isDebugEnabled(Marker paramMarker) {
    return false;
  }
  
  public final void debug(Marker paramMarker, String paramString) {}
  
  public final void debug(Marker paramMarker, String paramString, Object paramObject) {}
  
  public final void debug(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void debug(Marker paramMarker, String paramString, Object... paramVarArgs) {}
  
  public final void debug(Marker paramMarker, String paramString, Throwable paramThrowable) {}
  
  public boolean isInfoEnabled(Marker paramMarker) {
    return false;
  }
  
  public final void info(Marker paramMarker, String paramString) {}
  
  public final void info(Marker paramMarker, String paramString, Object paramObject) {}
  
  public final void info(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void info(Marker paramMarker, String paramString, Object... paramVarArgs) {}
  
  public final void info(Marker paramMarker, String paramString, Throwable paramThrowable) {}
  
  public final boolean isWarnEnabled(Marker paramMarker) {
    return false;
  }
  
  public final void warn(Marker paramMarker, String paramString) {}
  
  public final void warn(Marker paramMarker, String paramString, Object paramObject) {}
  
  public final void warn(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void warn(Marker paramMarker, String paramString, Object... paramVarArgs) {}
  
  public final void warn(Marker paramMarker, String paramString, Throwable paramThrowable) {}
  
  public final boolean isErrorEnabled(Marker paramMarker) {
    return false;
  }
  
  public final void error(Marker paramMarker, String paramString) {}
  
  public final void error(Marker paramMarker, String paramString, Object paramObject) {}
  
  public final void error(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {}
  
  public final void error(Marker paramMarker, String paramString, Object... paramVarArgs) {}
  
  public final void error(Marker paramMarker, String paramString, Throwable paramThrowable) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\NOPLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */