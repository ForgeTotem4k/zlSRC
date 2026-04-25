package org.slf4j.helpers;

import org.slf4j.Marker;

public abstract class LegacyAbstractLogger extends AbstractLogger {
  private static final long serialVersionUID = -7041884104854048950L;
  
  public boolean isTraceEnabled(Marker paramMarker) {
    return isTraceEnabled();
  }
  
  public boolean isDebugEnabled(Marker paramMarker) {
    return isDebugEnabled();
  }
  
  public boolean isInfoEnabled(Marker paramMarker) {
    return isInfoEnabled();
  }
  
  public boolean isWarnEnabled(Marker paramMarker) {
    return isWarnEnabled();
  }
  
  public boolean isErrorEnabled(Marker paramMarker) {
    return isErrorEnabled();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\LegacyAbstractLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */