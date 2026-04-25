package org.slf4j.helpers;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class NOPLoggerFactory implements ILoggerFactory {
  public Logger getLogger(String paramString) {
    return NOPLogger.NOP_LOGGER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\NOPLoggerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */