package org.slf4j.helpers;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter implements MDCAdapter {
  private final ThreadLocalMapOfStacks threadLocalMapOfDeques = new ThreadLocalMapOfStacks();
  
  private final InheritableThreadLocal<Map<String, String>> inheritableThreadLocalMap = new InheritableThreadLocal<Map<String, String>>() {
      protected Map<String, String> childValue(Map<String, String> param1Map) {
        return (param1Map == null) ? null : new HashMap<>(param1Map);
      }
    };
  
  public void put(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new IllegalArgumentException("key cannot be null"); 
    Map<Object, Object> map = (Map)this.inheritableThreadLocalMap.get();
    if (map == null) {
      map = new HashMap<>();
      this.inheritableThreadLocalMap.set(map);
    } 
    map.put(paramString1, paramString2);
  }
  
  public String get(String paramString) {
    Map map = this.inheritableThreadLocalMap.get();
    return (map != null && paramString != null) ? (String)map.get(paramString) : null;
  }
  
  public void remove(String paramString) {
    Map map = this.inheritableThreadLocalMap.get();
    if (map != null)
      map.remove(paramString); 
  }
  
  public void clear() {
    Map map = this.inheritableThreadLocalMap.get();
    if (map != null) {
      map.clear();
      this.inheritableThreadLocalMap.remove();
    } 
  }
  
  public Set<String> getKeys() {
    Map map = this.inheritableThreadLocalMap.get();
    return (map != null) ? map.keySet() : null;
  }
  
  public Map<String, String> getCopyOfContextMap() {
    Map<? extends String, ? extends String> map = this.inheritableThreadLocalMap.get();
    return (map != null) ? new HashMap<>(map) : null;
  }
  
  public void setContextMap(Map<String, String> paramMap) {
    HashMap<String, String> hashMap = null;
    if (paramMap != null)
      hashMap = new HashMap<>(paramMap); 
    this.inheritableThreadLocalMap.set(hashMap);
  }
  
  public void pushByKey(String paramString1, String paramString2) {
    this.threadLocalMapOfDeques.pushByKey(paramString1, paramString2);
  }
  
  public String popByKey(String paramString) {
    return this.threadLocalMapOfDeques.popByKey(paramString);
  }
  
  public Deque<String> getCopyOfDequeByKey(String paramString) {
    return this.threadLocalMapOfDeques.getCopyOfDequeByKey(paramString);
  }
  
  public void clearDequeByKey(String paramString) {
    this.threadLocalMapOfDeques.clearDequeByKey(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\BasicMDCAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */