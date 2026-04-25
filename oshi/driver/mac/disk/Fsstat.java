package oshi.driver.mac.disk;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.SystemB;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Fsstat {
  public static Map<String, String> queryPartitionToMountMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    int i = queryFsstat(null, 0, 0);
    SystemB.Statfs statfs = new SystemB.Statfs();
    SystemB.Statfs[] arrayOfStatfs = (SystemB.Statfs[])statfs.toArray(i);
    queryFsstat(arrayOfStatfs, i * arrayOfStatfs[0].size(), 16);
    for (SystemB.Statfs statfs1 : arrayOfStatfs) {
      String str = Native.toString(statfs1.f_mntfromname, StandardCharsets.UTF_8);
      hashMap.put(str.replace("/dev/", ""), Native.toString(statfs1.f_mntonname, StandardCharsets.UTF_8));
    } 
    return (Map)hashMap;
  }
  
  private static int queryFsstat(SystemB.Statfs[] paramArrayOfStatfs, int paramInt1, int paramInt2) {
    return SystemB.INSTANCE.getfsstat64(paramArrayOfStatfs, paramInt1, paramInt2);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\mac\disk\Fsstat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */