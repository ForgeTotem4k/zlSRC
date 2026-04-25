package org.slf4j.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Marker;

public class BasicMarker implements Marker {
  private static final long serialVersionUID = -2849567615646933777L;
  
  private final String name;
  
  private final List<Marker> referenceList = new CopyOnWriteArrayList<>();
  
  private static final String OPEN = "[ ";
  
  private static final String CLOSE = " ]";
  
  private static final String SEP = ", ";
  
  BasicMarker(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("A marker name cannot be null"); 
    this.name = paramString;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void add(Marker paramMarker) {
    if (paramMarker == null)
      throw new IllegalArgumentException("A null value cannot be added to a Marker as reference."); 
    if (contains(paramMarker))
      return; 
    if (paramMarker.contains(this))
      return; 
    this.referenceList.add(paramMarker);
  }
  
  public boolean hasReferences() {
    return (this.referenceList.size() > 0);
  }
  
  @Deprecated
  public boolean hasChildren() {
    return hasReferences();
  }
  
  public Iterator<Marker> iterator() {
    return this.referenceList.iterator();
  }
  
  public boolean remove(Marker paramMarker) {
    return this.referenceList.remove(paramMarker);
  }
  
  public boolean contains(Marker paramMarker) {
    if (paramMarker == null)
      throw new IllegalArgumentException("Other cannot be null"); 
    if (equals(paramMarker))
      return true; 
    if (hasReferences())
      for (Marker marker : this.referenceList) {
        if (marker.contains(paramMarker))
          return true; 
      }  
    return false;
  }
  
  public boolean contains(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Other cannot be null"); 
    if (this.name.equals(paramString))
      return true; 
    if (hasReferences())
      for (Marker marker : this.referenceList) {
        if (marker.contains(paramString))
          return true; 
      }  
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof Marker))
      return false; 
    Marker marker = (Marker)paramObject;
    return this.name.equals(marker.getName());
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public String toString() {
    if (!hasReferences())
      return getName(); 
    Iterator<Marker> iterator = iterator();
    StringBuilder stringBuilder = new StringBuilder(getName());
    stringBuilder.append(' ').append("[ ");
    while (iterator.hasNext()) {
      Marker marker = iterator.next();
      stringBuilder.append(marker.getName());
      if (iterator.hasNext())
        stringBuilder.append(", "); 
    } 
    stringBuilder.append(" ]");
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\BasicMarker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */