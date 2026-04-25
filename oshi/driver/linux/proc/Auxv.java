package oshi.driver.linux.proc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class Auxv {
  public static final int AT_PAGESZ = 6;
  
  public static final int AT_HWCAP = 16;
  
  public static final int AT_CLKTCK = 17;
  
  public static Map<Integer, Long> queryAuxv() {
    ByteBuffer byteBuffer = FileUtil.readAllBytesAsBuffer(ProcPath.AUXV);
    HashMap<Object, Object> hashMap = new HashMap<>();
    while (true) {
      int i = FileUtil.readNativeLongFromBuffer(byteBuffer).intValue();
      if (i > 0)
        hashMap.put(Integer.valueOf(i), Long.valueOf(FileUtil.readNativeLongFromBuffer(byteBuffer).longValue())); 
      if (i <= 0)
        return (Map)hashMap; 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\Auxv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */