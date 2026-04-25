package org.slf4j;

import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Reporter;
import org.slf4j.spi.SLF4JServiceProvider;

public class MarkerFactory {
  static IMarkerFactory MARKER_FACTORY;
  
  public static Marker getMarker(String paramString) {
    return MARKER_FACTORY.getMarker(paramString);
  }
  
  public static Marker getDetachedMarker(String paramString) {
    return MARKER_FACTORY.getDetachedMarker(paramString);
  }
  
  public static IMarkerFactory getIMarkerFactory() {
    return MARKER_FACTORY;
  }
  
  static {
    SLF4JServiceProvider sLF4JServiceProvider = LoggerFactory.getProvider();
    if (sLF4JServiceProvider != null) {
      MARKER_FACTORY = sLF4JServiceProvider.getMarkerFactory();
    } else {
      Reporter.error("Failed to find provider");
      Reporter.error("Defaulting to BasicMarkerFactory.");
      MARKER_FACTORY = (IMarkerFactory)new BasicMarkerFactory();
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\MarkerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */