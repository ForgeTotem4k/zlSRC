package org.slf4j.spi;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;

public interface SLF4JServiceProvider {
  ILoggerFactory getLoggerFactory();
  
  IMarkerFactory getMarkerFactory();
  
  MDCAdapter getMDCAdapter();
  
  String getRequestedApiVersion();
  
  void initialize();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\SLF4JServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */