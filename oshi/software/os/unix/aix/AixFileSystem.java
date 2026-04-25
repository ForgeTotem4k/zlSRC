package oshi.software.os.unix.aix;

import java.io.File;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.FileSystemUtil;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public class AixFileSystem extends AbstractFileSystem {
  public static final String OSHI_AIX_FS_PATH_EXCLUDES = "oshi.os.aix.filesystem.path.excludes";
  
  public static final String OSHI_AIX_FS_PATH_INCLUDES = "oshi.os.aix.filesystem.path.includes";
  
  public static final String OSHI_AIX_FS_VOLUME_EXCLUDES = "oshi.os.aix.filesystem.volume.excludes";
  
  public static final String OSHI_AIX_FS_VOLUME_INCLUDES = "oshi.os.aix.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.aix.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.aix.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.aix.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.aix.filesystem.volume.includes");
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    return getFileStoreMatching((String)null, paramBoolean);
  }
  
  static List<OSFileStore> getFileStoreMatching(String paramString) {
    return getFileStoreMatching(paramString, false);
  }
  
  private static List<OSFileStore> getFileStoreMatching(String paramString, boolean paramBoolean) {
    ArrayList<AixOSFileStore> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    String str = "df -i" + (paramBoolean ? " -l" : "");
    for (String str1 : ExecutingCommand.runNative(str)) {
      if (str1.startsWith("/")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str1);
        if (arrayOfString.length > 5) {
          hashMap2.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[1], 0L)));
          hashMap1.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[3], 0L)));
        } 
      } 
    } 
    for (String str1 : ExecutingCommand.runNative("mount")) {
      String[] arrayOfString = ParseUtil.whitespaces.split("x" + str1);
      if (arrayOfString.length > 7) {
        String str7;
        String str2 = arrayOfString[1];
        String str3 = arrayOfString[2];
        String str4 = arrayOfString[3];
        String str5 = arrayOfString[4];
        if ((paramBoolean && NETWORK_FS_TYPES.contains(str4)) || (!str3.equals("/") && (PSEUDO_FS_TYPES.contains(str4) || FileSystemUtil.isFileStoreExcluded(str3, str2, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))))
          continue; 
        String str6 = str3.substring(str3.lastIndexOf('/') + 1);
        if (str6.isEmpty())
          str6 = str2.substring(str2.lastIndexOf('/') + 1); 
        if (paramString != null && !paramString.equals(str6))
          continue; 
        File file = new File(str3);
        if (!file.exists() || file.getTotalSpace() < 0L)
          continue; 
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
        arrayList.add(new AixOSFileStore(str6, str2, str6, str3, str5, "", "", str7, str4, l3, l2, l1, ((Long)hashMap1.getOrDefault(str2, Long.valueOf(0L))).longValue(), ((Long)hashMap2.getOrDefault(str2, Long.valueOf(0L))).longValue()));
      } 
    } 
    return (List)arrayList;
  }
  
  public long getOpenFileDescriptors() {
    boolean bool = false;
    long l = 0L;
    for (String str : ExecutingCommand.runNative("lsof -nl")) {
      if (!bool) {
        bool = str.startsWith("COMMAND");
        continue;
      } 
      l++;
    } 
    return l;
  }
  
  public long getMaxFileDescriptors() {
    return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("ulimit -n"), 0L);
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    List list = FileUtil.readFile("/etc/security/limits");
    for (String str : list) {
      if (str.trim().startsWith("nofiles"))
        return ParseUtil.parseLastLong(str, Long.MAX_VALUE); 
    } 
    return Long.MAX_VALUE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */