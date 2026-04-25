package org.slf4j.helpers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class BasicMarkerFactory implements IMarkerFactory {
  private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<>();
  
  public Marker getMarker(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Marker name cannot be null"); 
    Marker marker = this.markerMap.get(paramString);
    if (marker == null) {
      marker = new BasicMarker(paramString);
      Marker marker1 = this.markerMap.putIfAbsent(paramString, marker);
      if (marker1 != null)
        marker = marker1; 
    } 
    return marker;
  }
  
  public boolean exists(String paramString) {
    return (paramString == null) ? false : this.markerMap.containsKey(paramString);
  }
  
  public boolean detachMarker(String paramString) {
    return (paramString == null) ? false : ((this.markerMap.remove(paramString) != null));
  }
  
  public Marker getDetachedMarker(String paramString) {
    return new BasicMarker(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\BasicMarkerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */