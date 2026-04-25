package com.sun.jna.platform.unix.aix;

import com.sun.jna.Native;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SharedObjectLoader {
  static Perfstat getPerfstatInstance() {
    Map<String, Object> map = getOptions();
    try {
      return (Perfstat)Native.load("/usr/lib/libperfstat.a(shr_64.o)", Perfstat.class, map);
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      return (Perfstat)Native.load("/usr/lib/libperfstat.a(shr.o)", Perfstat.class, map);
    } 
  }
  
  private static Map<String, Object> getOptions() {
    int i = 262144;
    int j = 65536;
    byte b = 4;
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("open-flags", Integer.valueOf(i | j | b));
    return (Map)Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\aix\SharedObjectLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */