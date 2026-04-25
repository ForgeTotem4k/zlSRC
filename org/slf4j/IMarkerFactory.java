package org.slf4j;

public interface IMarkerFactory {
  Marker getMarker(String paramString);
  
  boolean exists(String paramString);
  
  boolean detachMarker(String paramString);
  
  Marker getDetachedMarker(String paramString);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\IMarkerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */