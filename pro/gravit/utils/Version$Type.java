package pro.gravit.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Type {
  LTS, STABLE, BETA, ALPHA, DEV, EXPERIMENTAL, UNKNOWN;
  
  private static final Map<String, Type> types;
  
  public static final Map<String, Type> unModTypes;
  
  static {
    types = new HashMap<>();
    unModTypes = Collections.unmodifiableMap(types);
    Arrays.<Type>asList(values()).forEach(paramType -> types.put(paramType.name().substring(0, Math.min(paramType.name().length(), 3)).toLowerCase(Locale.ENGLISH), paramType));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\Version$Type.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */