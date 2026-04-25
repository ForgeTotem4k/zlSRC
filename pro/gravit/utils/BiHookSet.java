package pro.gravit.utils;

import java.util.HashSet;
import java.util.Set;

public class BiHookSet<V, R> {
  public final Set<Hook<V, R>> list = new HashSet<>();
  
  public void registerHook(Hook<V, R> paramHook) {
    this.list.add(paramHook);
  }
  
  public void unregisterHook(Hook<V, R> paramHook) {
    this.list.remove(paramHook);
  }
  
  public boolean hook(V paramV, R paramR) throws HookException {
    for (Hook<V, R> hook : this.list) {
      if (hook.hook(paramV, paramR))
        return true; 
    } 
    return false;
  }
  
  @FunctionalInterface
  public static interface Hook<V, R> {
    boolean hook(V param1V, R param1R) throws HookException;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\BiHookSet.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */