package org.slf4j.helpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalMapOfStacks {
  final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = new ThreadLocal<>();
  
  public void pushByKey(String paramString1, String paramString2) {
    if (paramString1 == null)
      return; 
    Map<Object, Object> map = (Map)this.tlMapOfStacks.get();
    if (map == null) {
      map = new HashMap<>();
      this.tlMapOfStacks.set(map);
    } 
    Deque<String> deque = (Deque)map.get(paramString1);
    if (deque == null)
      deque = new ArrayDeque(); 
    deque.push(paramString2);
    map.put(paramString1, deque);
  }
  
  public String popByKey(String paramString) {
    if (paramString == null)
      return null; 
    Map map = this.tlMapOfStacks.get();
    if (map == null)
      return null; 
    Deque<String> deque = (Deque)map.get(paramString);
    return (deque == null) ? null : deque.pop();
  }
  
  public Deque<String> getCopyOfDequeByKey(String paramString) {
    if (paramString == null)
      return null; 
    Map map = this.tlMapOfStacks.get();
    if (map == null)
      return null; 
    Deque<? extends String> deque = (Deque)map.get(paramString);
    return (deque == null) ? null : new ArrayDeque<>(deque);
  }
  
  public void clearDequeByKey(String paramString) {
    if (paramString == null)
      return; 
    Map map = this.tlMapOfStacks.get();
    if (map == null)
      return; 
    Deque deque = (Deque)map.get(paramString);
    if (deque == null)
      return; 
    deque.clear();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\ThreadLocalMapOfStacks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */