package org.slf4j.spi;

import java.util.Deque;
import java.util.Map;

public interface MDCAdapter {
  void put(String paramString1, String paramString2);
  
  String get(String paramString);
  
  void remove(String paramString);
  
  void clear();
  
  Map<String, String> getCopyOfContextMap();
  
  void setContextMap(Map<String, String> paramMap);
  
  void pushByKey(String paramString1, String paramString2);
  
  String popByKey(String paramString);
  
  Deque<String> getCopyOfDequeByKey(String paramString);
  
  void clearDequeByKey(String paramString);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\spi\MDCAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */