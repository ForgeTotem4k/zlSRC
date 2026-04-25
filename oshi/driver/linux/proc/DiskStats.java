package oshi.driver.linux.proc;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class DiskStats {
  public static Map<String, Map<IoStat, Long>> getDiskStats() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    IoStat[] arrayOfIoStat = IoStat.class.getEnumConstants();
    List list = FileUtil.readFile(ProcPath.DISKSTATS);
    for (String str1 : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str1.trim());
      EnumMap<IoStat, Object> enumMap = new EnumMap<>(IoStat.class);
      String str2 = null;
      for (byte b = 0; b < arrayOfIoStat.length && b < arrayOfString.length; b++) {
        if (arrayOfIoStat[b] == IoStat.NAME) {
          str2 = arrayOfString[b];
        } else {
          enumMap.put(arrayOfIoStat[b], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[b], 0L)));
        } 
      } 
      if (str2 != null)
        hashMap.put(str2, enumMap); 
    } 
    return (Map)hashMap;
  }
  
  public enum IoStat {
    MAJOR, MINOR, NAME, READS, READS_MERGED, READS_SECTOR, READS_MS, WRITES, WRITES_MERGED, WRITES_SECTOR, WRITES_MS, IO_QUEUE_LENGTH, IO_MS, IO_MS_WEIGHTED, DISCARDS, DISCARDS_MERGED, DISCARDS_SECTOR, DISCARDS_MS, FLUSHES, FLUSHES_MS;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\DiskStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */