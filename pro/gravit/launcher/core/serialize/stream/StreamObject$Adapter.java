package pro.gravit.launcher.core.serialize.stream;

import pro.gravit.launcher.core.serialize.HInput;

@FunctionalInterface
public interface Adapter<O extends StreamObject> {
  O convert(HInput paramHInput);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\stream\StreamObject$Adapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */