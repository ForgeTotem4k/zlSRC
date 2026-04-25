package org.slf4j.helpers;

import java.util.Deque;
import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class NOPMDCAdapter implements MDCAdapter {
  public void clear() {}
  
  public String get(String paramString) {
    return null;
  }
  
  public void put(String paramString1, String paramString2) {}
  
  public void remove(String paramString) {}
  
  public Map<String, String> getCopyOfContextMap() {
    return null;
  }
  
  public void setContextMap(Map<String, String> paramMap) {}
  
  public void pushByKey(String paramString1, String paramString2) {}
  
  public String popByKey(String paramString) {
    return null;
  }
  
  public Deque<String> getCopyOfDequeByKey(String paramString) {
    return null;
  }
  
  public void clearDequeByKey(String paramString) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\NOPMDCAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */