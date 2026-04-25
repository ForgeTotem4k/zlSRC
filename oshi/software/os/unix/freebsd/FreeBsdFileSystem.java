package oshi.software.os.unix.freebsd;

import java.io.File;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.linux.LinuxOSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.FileSystemUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdFileSystem extends AbstractFileSystem {
  public static final String OSHI_FREEBSD_FS_PATH_EXCLUDES = "oshi.os.freebsd.filesystem.path.excludes";
  
  public static final String OSHI_FREEBSD_FS_PATH_INCLUDES = "oshi.os.freebsd.filesystem.path.includes";
  
  public static final String OSHI_FREEBSD_FS_VOLUME_EXCLUDES = "oshi.os.freebsd.filesystem.volume.excludes";
  
  public static final String OSHI_FREEBSD_FS_VOLUME_INCLUDES = "oshi.os.freebsd.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.freebsd.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.freebsd.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.freebsd.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.freebsd.filesystem.volume.includes");
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    String str = "";
    for (String str1 : ExecutingCommand.runNative("geom part list")) {
      if (str1.contains("Name: "))
        str = str1.substring(str1.lastIndexOf(' ') + 1); 
      if (str.isEmpty())
        continue; 
      str1 = str1.trim();
      if (str1.startsWith("rawuuid:")) {
        hashMap1.put(str, str1.substring(str1.lastIndexOf(' ') + 1));
        str = "";
      } 
    } 
    ArrayList<LinuxOSFileStore> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    for (String str1 : ExecutingCommand.runNative("df -i")) {
      if (str1.startsWith("/")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str1);
        if (arrayOfString.length > 7) {
          hashMap2.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[6], 0L)));
          hashMap3.put(arrayOfString[0], Long.valueOf(((Long)hashMap2.get(arrayOfString[0])).longValue() + ParseUtil.parseLongOrDefault(arrayOfString[5], 0L)));
        } 
      } 
    } 
    for (String str1 : ExecutingCommand.runNative("mount -p")) {
      String str7;
      String[] arrayOfString = ParseUtil.whitespaces.split(str1);
      if (arrayOfString.length < 5)
        continue; 
      String str2 = arrayOfString[0];
      String str3 = arrayOfString[1];
      String str4 = arrayOfString[2];
      String str5 = arrayOfString[3];
      if ((paramBoolean && NETWORK_FS_TYPES.contains(str4)) || (!str3.equals("/") && (PSEUDO_FS_TYPES.contains(str4) || FileSystemUtil.isFileStoreExcluded(str3, str2, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))))
        continue; 
      String str6 = str3.substring(str3.lastIndexOf('/') + 1);
      if (str6.isEmpty())
        str6 = str2.substring(str2.lastIndexOf('/') + 1); 
      File file = new File(str3);
      long l1 = file.getTotalSpace();
      long l2 = file.getUsableSpace();
      long l3 = file.getFreeSpace();
      if (str2.startsWith("/dev") || str3.equals("/")) {
        str7 = "Local Disk";
      } else if (str2.equals("tmpfs")) {
        str7 = "Ram Disk";
      } else if (NETWORK_FS_TYPES.contains(str4)) {
        str7 = "Network Disk";
      } else {
        str7 = "Mount Point";
      } 
      String str8 = (String)hashMap1.getOrDefault(str6, "");
      arrayList.add(new LinuxOSFileStore(str6, str2, str6, str3, str5, str8, "", str7, str4, l3, l2, l1, hashMap2.containsKey(str3) ? ((Long)hashMap2.get(str3)).longValue() : 0L, hashMap3.containsKey(str3) ? ((Long)hashMap3.get(str3)).longValue() : 0L));
    } 
    return (List)arrayList;
  }
  
  public long getOpenFileDescriptors() {
    return BsdSysctlUtil.sysctl("kern.openfiles", 0);
  }
  
  public long getMaxFileDescriptors() {
    return BsdSysctlUtil.sysctl("kern.maxfiles", 0);
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    return getMaxFileDescriptors();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */