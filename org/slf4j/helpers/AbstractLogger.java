package org.slf4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public abstract class AbstractLogger implements Logger, Serializable {
  private static final long serialVersionUID = -2529255052481744503L;
  
  protected String name;
  
  public String getName() {
    return this.name;
  }
  
  protected Object readResolve() throws ObjectStreamException {
    return LoggerFactory.getLogger(getName());
  }
  
  public void trace(String paramString) {
    if (isTraceEnabled())
      handle_0ArgsCall(Level.TRACE, (Marker)null, paramString, (Throwable)null); 
  }
  
  public void trace(String paramString, Object paramObject) {
    if (isTraceEnabled())
      handle_1ArgsCall(Level.TRACE, (Marker)null, paramString, paramObject); 
  }
  
  public void trace(String paramString, Object paramObject1, Object paramObject2) {
    if (isTraceEnabled())
      handle2ArgsCall(Level.TRACE, (Marker)null, paramString, paramObject1, paramObject2); 
  }
  
  public void trace(String paramString, Object... paramVarArgs) {
    if (isTraceEnabled())
      handleArgArrayCall(Level.TRACE, (Marker)null, paramString, paramVarArgs); 
  }
  
  public void trace(String paramString, Throwable paramThrowable) {
    if (isTraceEnabled())
      handle_0ArgsCall(Level.TRACE, (Marker)null, paramString, paramThrowable); 
  }
  
  public void trace(Marker paramMarker, String paramString) {
    if (isTraceEnabled(paramMarker))
      handle_0ArgsCall(Level.TRACE, paramMarker, paramString, (Throwable)null); 
  }
  
  public void trace(Marker paramMarker, String paramString, Object paramObject) {
    if (isTraceEnabled(paramMarker))
      handle_1ArgsCall(Level.TRACE, paramMarker, paramString, paramObject); 
  }
  
  public void trace(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (isTraceEnabled(paramMarker))
      handle2ArgsCall(Level.TRACE, paramMarker, paramString, paramObject1, paramObject2); 
  }
  
  public void trace(Marker paramMarker, String paramString, Object... paramVarArgs) {
    if (isTraceEnabled(paramMarker))
      handleArgArrayCall(Level.TRACE, paramMarker, paramString, paramVarArgs); 
  }
  
  public void trace(Marker paramMarker, String paramString, Throwable paramThrowable) {
    if (isTraceEnabled(paramMarker))
      handle_0ArgsCall(Level.TRACE, paramMarker, paramString, paramThrowable); 
  }
  
  public void debug(String paramString) {
    if (isDebugEnabled())
      handle_0ArgsCall(Level.DEBUG, (Marker)null, paramString, (Throwable)null); 
  }
  
  public void debug(String paramString, Object paramObject) {
    if (isDebugEnabled())
      handle_1ArgsCall(Level.DEBUG, (Marker)null, paramString, paramObject); 
  }
  
  public void debug(String paramString, Object paramObject1, Object paramObject2) {
    if (isDebugEnabled())
      handle2ArgsCall(Level.DEBUG, (Marker)null, paramString, paramObject1, paramObject2); 
  }
  
  public void debug(String paramString, Object... paramVarArgs) {
    if (isDebugEnabled())
      handleArgArrayCall(Level.DEBUG, (Marker)null, paramString, paramVarArgs); 
  }
  
  public void debug(String paramString, Throwable paramThrowable) {
    if (isDebugEnabled())
      handle_0ArgsCall(Level.DEBUG, (Marker)null, paramString, paramThrowable); 
  }
  
  public void debug(Marker paramMarker, String paramString) {
    if (isDebugEnabled(paramMarker))
      handle_0ArgsCall(Level.DEBUG, paramMarker, paramString, (Throwable)null); 
  }
  
  public void debug(Marker paramMarker, String paramString, Object paramObject) {
    if (isDebugEnabled(paramMarker))
      handle_1ArgsCall(Level.DEBUG, paramMarker, paramString, paramObject); 
  }
  
  public void debug(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (isDebugEnabled(paramMarker))
      handle2ArgsCall(Level.DEBUG, paramMarker, paramString, paramObject1, paramObject2); 
  }
  
  public void debug(Marker paramMarker, String paramString, Object... paramVarArgs) {
    if (isDebugEnabled(paramMarker))
      handleArgArrayCall(Level.DEBUG, paramMarker, paramString, paramVarArgs); 
  }
  
  public void debug(Marker paramMarker, String paramString, Throwable paramThrowable) {
    if (isDebugEnabled(paramMarker))
      handle_0ArgsCall(Level.DEBUG, paramMarker, paramString, paramThrowable); 
  }
  
  public void info(String paramString) {
    if (isInfoEnabled())
      handle_0ArgsCall(Level.INFO, (Marker)null, paramString, (Throwable)null); 
  }
  
  public void info(String paramString, Object paramObject) {
    if (isInfoEnabled())
      handle_1ArgsCall(Level.INFO, (Marker)null, paramString, paramObject); 
  }
  
  public void info(String paramString, Object paramObject1, Object paramObject2) {
    if (isInfoEnabled())
      handle2ArgsCall(Level.INFO, (Marker)null, paramString, paramObject1, paramObject2); 
  }
  
  public void info(String paramString, Object... paramVarArgs) {
    if (isInfoEnabled())
      handleArgArrayCall(Level.INFO, (Marker)null, paramString, paramVarArgs); 
  }
  
  public void info(String paramString, Throwable paramThrowable) {
    if (isInfoEnabled())
      handle_0ArgsCall(Level.INFO, (Marker)null, paramString, paramThrowable); 
  }
  
  public void info(Marker paramMarker, String paramString) {
    if (isInfoEnabled(paramMarker))
      handle_0ArgsCall(Level.INFO, paramMarker, paramString, (Throwable)null); 
  }
  
  public void info(Marker paramMarker, String paramString, Object paramObject) {
    if (isInfoEnabled(paramMarker))
      handle_1ArgsCall(Level.INFO, paramMarker, paramString, paramObject); 
  }
  
  public void info(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (isInfoEnabled(paramMarker))
      handle2ArgsCall(Level.INFO, paramMarker, paramString, paramObject1, paramObject2); 
  }
  
  public void info(Marker paramMarker, String paramString, Object... paramVarArgs) {
    if (isInfoEnabled(paramMarker))
      handleArgArrayCall(Level.INFO, paramMarker, paramString, paramVarArgs); 
  }
  
  public void info(Marker paramMarker, String paramString, Throwable paramThrowable) {
    if (isInfoEnabled(paramMarker))
      handle_0ArgsCall(Level.INFO, paramMarker, paramString, paramThrowable); 
  }
  
  public void warn(String paramString) {
    if (isWarnEnabled())
      handle_0ArgsCall(Level.WARN, (Marker)null, paramString, (Throwable)null); 
  }
  
  public void warn(String paramString, Object paramObject) {
    if (isWarnEnabled())
      handle_1ArgsCall(Level.WARN, (Marker)null, paramString, paramObject); 
  }
  
  public void warn(String paramString, Object paramObject1, Object paramObject2) {
    if (isWarnEnabled())
      handle2ArgsCall(Level.WARN, (Marker)null, paramString, paramObject1, paramObject2); 
  }
  
  public void warn(String paramString, Object... paramVarArgs) {
    if (isWarnEnabled())
      handleArgArrayCall(Level.WARN, (Marker)null, paramString, paramVarArgs); 
  }
  
  public void warn(String paramString, Throwable paramThrowable) {
    if (isWarnEnabled())
      handle_0ArgsCall(Level.WARN, (Marker)null, paramString, paramThrowable); 
  }
  
  public void warn(Marker paramMarker, String paramString) {
    if (isWarnEnabled(paramMarker))
      handle_0ArgsCall(Level.WARN, paramMarker, paramString, (Throwable)null); 
  }
  
  public void warn(Marker paramMarker, String paramString, Object paramObject) {
    if (isWarnEnabled(paramMarker))
      handle_1ArgsCall(Level.WARN, paramMarker, paramString, paramObject); 
  }
  
  public void warn(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (isWarnEnabled(paramMarker))
      handle2ArgsCall(Level.WARN, paramMarker, paramString, paramObject1, paramObject2); 
  }
  
  public void warn(Marker paramMarker, String paramString, Object... paramVarArgs) {
    if (isWarnEnabled(paramMarker))
      handleArgArrayCall(Level.WARN, paramMarker, paramString, paramVarArgs); 
  }
  
  public void warn(Marker paramMarker, String paramString, Throwable paramThrowable) {
    if (isWarnEnabled(paramMarker))
      handle_0ArgsCall(Level.WARN, paramMarker, paramString, paramThrowable); 
  }
  
  public void error(String paramString) {
    if (isErrorEnabled())
      handle_0ArgsCall(Level.ERROR, (Marker)null, paramString, (Throwable)null); 
  }
  
  public void error(String paramString, Object paramObject) {
    if (isErrorEnabled())
      handle_1ArgsCall(Level.ERROR, (Marker)null, paramString, paramObject); 
  }
  
  public void error(String paramString, Object paramObject1, Object paramObject2) {
    if (isErrorEnabled())
      handle2ArgsCall(Level.ERROR, (Marker)null, paramString, paramObject1, paramObject2); 
  }
  
  public void error(String paramString, Object... paramVarArgs) {
    if (isErrorEnabled())
      handleArgArrayCall(Level.ERROR, (Marker)null, paramString, paramVarArgs); 
  }
  
  public void error(String paramString, Throwable paramThrowable) {
    if (isErrorEnabled())
      handle_0ArgsCall(Level.ERROR, (Marker)null, paramString, paramThrowable); 
  }
  
  public void error(Marker paramMarker, String paramString) {
    if (isErrorEnabled(paramMarker))
      handle_0ArgsCall(Level.ERROR, paramMarker, paramString, (Throwable)null); 
  }
  
  public void error(Marker paramMarker, String paramString, Object paramObject) {
    if (isErrorEnabled(paramMarker))
      handle_1ArgsCall(Level.ERROR, paramMarker, paramString, paramObject); 
  }
  
  public void error(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (isErrorEnabled(paramMarker))
      handle2ArgsCall(Level.ERROR, paramMarker, paramString, paramObject1, paramObject2); 
  }
  
  public void error(Marker paramMarker, String paramString, Object... paramVarArgs) {
    if (isErrorEnabled(paramMarker))
      handleArgArrayCall(Level.ERROR, paramMarker, paramString, paramVarArgs); 
  }
  
  public void error(Marker paramMarker, String paramString, Throwable paramThrowable) {
    if (isErrorEnabled(paramMarker))
      handle_0ArgsCall(Level.ERROR, paramMarker, paramString, paramThrowable); 
  }
  
  private void handle_0ArgsCall(Level paramLevel, Marker paramMarker, String paramString, Throwable paramThrowable) {
    handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, (Object[])null, paramThrowable);
  }
  
  private void handle_1ArgsCall(Level paramLevel, Marker paramMarker, String paramString, Object paramObject) {
    handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, new Object[] { paramObject }, (Throwable)null);
  }
  
  private void handle2ArgsCall(Level paramLevel, Marker paramMarker, String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject2 instanceof Throwable) {
      handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, new Object[] { paramObject1 }, (Throwable)paramObject2);
    } else {
      handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, new Object[] { paramObject1, paramObject2 }, (Throwable)null);
    } 
  }
  
  private void handleArgArrayCall(Level paramLevel, Marker paramMarker, String paramString, Object[] paramArrayOfObject) {
    Throwable throwable = MessageFormatter.getThrowableCandidate(paramArrayOfObject);
    if (throwable != null) {
      Object[] arrayOfObject = MessageFormatter.trimmedCopy(paramArrayOfObject);
      handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, arrayOfObject, throwable);
    } else {
      handleNormalizedLoggingCall(paramLevel, paramMarker, paramString, paramArrayOfObject, (Throwable)null);
    } 
  }
  
  protected abstract String getFullyQualifiedCallerName();
  
  protected abstract void handleNormalizedLoggingCall(Level paramLevel, Marker paramMarker, String paramString, Object[] paramArrayOfObject, Throwable paramThrowable);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\AbstractLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */