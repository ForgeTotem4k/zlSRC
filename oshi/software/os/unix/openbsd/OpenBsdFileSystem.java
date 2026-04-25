package oshi.software.os.unix.openbsd;

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
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;

@ThreadSafe
public class OpenBsdFileSystem extends AbstractFileSystem {
  public static final String OSHI_OPENBSD_FS_PATH_EXCLUDES = "oshi.os.openbsd.filesystem.path.excludes";
  
  public static final String OSHI_OPENBSD_FS_PATH_INCLUDES = "oshi.os.openbsd.filesystem.path.includes";
  
  public static final String OSHI_OPENBSD_FS_VOLUME_EXCLUDES = "oshi.os.openbsd.filesystem.volume.excludes";
  
  public static final String OSHI_OPENBSD_FS_VOLUME_INCLUDES = "oshi.os.openbsd.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.openbsd.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.openbsd.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.openbsd.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.openbsd.filesystem.volume.includes");
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    return getFileStoreMatching((String)null, paramBoolean);
  }
  
  static List<OSFileStore> getFileStoreMatching(String paramString) {
    return getFileStoreMatching(paramString, false);
  }
  
  private static List<OSFileStore> getFileStoreMatching(String paramString, boolean paramBoolean) {
    ArrayList<OpenBsdOSFileStore> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    String str = "df -i" + (paramBoolean ? " -l" : "");
    for (String str1 : ExecutingCommand.runNative(str)) {
      if (str1.startsWith("/")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str1);
        if (arrayOfString.length > 6) {
          hashMap2.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[5], 0L)));
          hashMap1.put(arrayOfString[0], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[6], 0L)));
        } 
      } 
    } 
    for (String str1 : ExecutingCommand.runNative("mount -v")) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str1, 7);
      if (arrayOfString.length == 7) {
        String str8;
        String str2 = arrayOfString[0];
        String str3 = arrayOfString[1];
        String str4 = arrayOfString[3];
        String str5 = arrayOfString[5];
        String str6 = arrayOfString[6];
        if ((paramBoolean && NETWORK_FS_TYPES.contains(str5)) || (!str4.equals("/") && (PSEUDO_FS_TYPES.contains(str5) || FileSystemUtil.isFileStoreExcluded(str4, str2, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))))
          continue; 
        String str7 = str4.substring(str4.lastIndexOf('/') + 1);
        if (str7.isEmpty())
          str7 = str2.substring(str2.lastIndexOf('/') + 1); 
        if (paramString != null && !paramString.equals(str7))
          continue; 
        File file = new File(str4);
        long l1 = file.getTotalSpace();
        long l2 = file.getUsableSpace();
        long l3 = file.getFreeSpace();
        if (str2.startsWith("/dev") || str4.equals("/")) {
          str8 = "Local Disk";
        } else if (str2.equals("tmpfs")) {
          str8 = "Ram Disk (dynamic)";
        } else if (str2.equals("mfs")) {
          str8 = "Ram Disk (fixed)";
        } else if (NETWORK_FS_TYPES.contains(str5)) {
          str8 = "Network Disk";
        } else {
          str8 = "Mount Point";
        } 
        arrayList.add(new OpenBsdOSFileStore(str7, str2, str7, str4, str6, str3, "", str8, str5, l3, l2, l1, ((Long)hashMap1.getOrDefault(str2, Long.valueOf(0L))).longValue(), ((Long)hashMap2.getOrDefault(str2, Long.valueOf(0L))).longValue() + ((Long)hashMap1.getOrDefault(str2, Long.valueOf(0L))).longValue()));
      } 
    } 
    return (List)arrayList;
  }
  
  public long getOpenFileDescriptors() {
    return OpenBsdSysctlUtil.sysctl("kern.nfiles", 0);
  }
  
  public long getMaxFileDescriptors() {
    return OpenBsdSysctlUtil.sysctl("kern.maxfiles", 0);
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    return OpenBsdSysctlUtil.sysctl("kern.maxfilesperproc", 0);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\openbsd\OpenBsdFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */