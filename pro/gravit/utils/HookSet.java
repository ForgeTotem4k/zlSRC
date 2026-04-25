package pro.gravit.utils;

import java.util.HashSet;
import java.util.Set;

public class HookSet<R> {
  public final Set<Hook<R>> list = new HashSet<>();
  
  public void registerHook(Hook<R> paramHook) {
    this.list.add(paramHook);
  }
  
  public void unregisterHook(Hook<R> paramHook) {
    this.list.remove(paramHook);
  }
  
  public boolean hook(R paramR) throws HookException {
    for (Hook<R> hook : this.list) {
      if (hook.hook(paramR))
        return true; 
    } 
    return false;
  }
  
  @FunctionalInterface
  public static interface Hook<R> {
    boolean hook(R param1R) throws HookException;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\HookSet.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */