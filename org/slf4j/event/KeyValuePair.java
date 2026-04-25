package org.slf4j.event;

import java.util.Objects;

public class KeyValuePair {
  public final String key;
  
  public final Object value;
  
  public KeyValuePair(String paramString, Object paramObject) {
    this.key = paramString;
    this.value = paramObject;
  }
  
  public String toString() {
    return String.valueOf(this.key) + "=\"" + String.valueOf(this.value) + "\"";
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    KeyValuePair keyValuePair = (KeyValuePair)paramObject;
    return (Objects.equals(this.key, keyValuePair.key) && Objects.equals(this.value, keyValuePair.value));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.key, this.value });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\event\KeyValuePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */