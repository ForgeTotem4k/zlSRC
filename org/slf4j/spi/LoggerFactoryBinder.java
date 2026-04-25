package org.slf4j.spi;

import org.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder {
  ILoggerFactory getLoggerFactory();
  
  String getLoggerFactoryClassStr();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\LoggerFactoryBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */