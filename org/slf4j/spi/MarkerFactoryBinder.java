package org.slf4j.spi;

import org.slf4j.IMarkerFactory;

public interface MarkerFactoryBinder {
  IMarkerFactory getMarkerFactory();
  
  String getMarkerFactoryClassStr();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\MarkerFactoryBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */