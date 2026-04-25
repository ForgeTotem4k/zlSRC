package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.FileSystemUtil;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.DevPath;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxFileSystem extends AbstractFileSystem {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxFileSystem.class);
  
  public static final String OSHI_LINUX_FS_PATH_EXCLUDES = "oshi.os.linux.filesystem.path.excludes";
  
  public static final String OSHI_LINUX_FS_PATH_INCLUDES = "oshi.os.linux.filesystem.path.includes";
  
  public static final String OSHI_LINUX_FS_VOLUME_EXCLUDES = "oshi.os.linux.filesystem.volume.excludes";
  
  public static final String OSHI_LINUX_FS_VOLUME_INCLUDES = "oshi.os.linux.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.linux.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.linux.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.linux.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.linux.filesystem.volume.includes");
  
  private static final String UNICODE_SPACE = "\\040";
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    File file1 = new File(DevPath.MAPPER);
    File[] arrayOfFile1 = file1.listFiles();
    if (arrayOfFile1 != null)
      for (File file : arrayOfFile1) {
        try {
          hashMap1.put(file.getCanonicalPath(), file.getAbsolutePath());
        } catch (IOException iOException) {
          LOG.error("Couldn't get canonical path for {}. {}", file.getName(), iOException.getMessage());
        } 
      }  
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    File file2 = new File(DevPath.DISK_BY_UUID);
    File[] arrayOfFile2 = file2.listFiles();
    if (arrayOfFile2 != null)
      for (File file : arrayOfFile2) {
        try {
          String str = file.getCanonicalPath();
          hashMap2.put(str, file.getName().toLowerCase(Locale.ROOT));
          if (hashMap1.containsKey(str))
            hashMap2.put(hashMap1.get(str), file.getName().toLowerCase(Locale.ROOT)); 
        } catch (IOException iOException) {
          LOG.error("Couldn't get canonical path for {}. {}", file.getName(), iOException.getMessage());
        } 
      }  
    return getFileStoreMatching((String)null, (Map)hashMap2, paramBoolean);
  }
  
  static List<OSFileStore> getFileStoreMatching(String paramString, Map<String, String> paramMap) {
    return getFileStoreMatching(paramString, paramMap, false);
  }
  
  private static List<OSFileStore> getFileStoreMatching(String paramString, Map<String, String> paramMap, boolean paramBoolean) {
    ArrayList<LinuxOSFileStore> arrayList = new ArrayList();
    Map<String, String> map = queryLabelMap();
    List list = FileUtil.readFile(ProcPath.MOUNTS);
    for (String str1 : list) {
      String str8;
      String[] arrayOfString = str1.split(" ");
      if (arrayOfString.length < 6)
        continue; 
      String str2 = arrayOfString[0].replace("\\040", " ");
      String str3 = str2;
      String str4 = arrayOfString[1].replace("\\040", " ");
      if (str4.equals("/"))
        str3 = "/"; 
      String str5 = arrayOfString[2];
      if ((paramBoolean && NETWORK_FS_TYPES.contains(str5)) || (!str4.equals("/") && (PSEUDO_FS_TYPES.contains(str5) || FileSystemUtil.isFileStoreExcluded(str4, str2, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))))
        continue; 
      String str6 = arrayOfString[3];
      if (paramString != null && !paramString.equals(str3))
        continue; 
      String str7 = (paramMap != null) ? paramMap.getOrDefault(arrayOfString[0], "") : "";
      if (str2.startsWith(DevPath.DEV)) {
        str8 = "Local Disk";
      } else if (str2.equals("tmpfs")) {
        str8 = "Ram Disk";
      } else if (NETWORK_FS_TYPES.contains(str5)) {
        str8 = "Network Disk";
      } else {
        str8 = "Mount Point";
      } 
      String str9 = "";
      Path path = Paths.get(str2, new String[0]);
      if (path.toFile().exists() && Files.isSymbolicLink(path))
        try {
          Path path1 = Files.readSymbolicLink(path);
          Path path2 = Paths.get(DevPath.MAPPER + path1.toString(), new String[0]);
          if (path2.toFile().exists())
            str9 = path2.normalize().toString(); 
        } catch (IOException iOException) {
          LOG.warn("Couldn't access symbolic path  {}. {}", path, iOException.getMessage());
        }  
      long l1 = 0L;
      long l2 = 0L;
      long l3 = 0L;
      long l4 = 0L;
      long l5 = 0L;
      try {
        LibC.Statvfs statvfs = new LibC.Statvfs();
        if (0 == LibC.INSTANCE.statvfs(str4, statvfs)) {
          l1 = statvfs.f_files.longValue();
          l2 = statvfs.f_ffree.longValue();
          l3 = statvfs.f_blocks.longValue() * statvfs.f_frsize.longValue();
          l4 = statvfs.f_bavail.longValue() * statvfs.f_frsize.longValue();
          l5 = statvfs.f_bfree.longValue() * statvfs.f_frsize.longValue();
        } else {
          LOG.warn("Failed to get information to use statvfs. path: {}, Error code: {}", str4, Integer.valueOf(Native.getLastError()));
        } 
      } catch (UnsatisfiedLinkError|NoClassDefFoundError unsatisfiedLinkError) {
        LOG.error("Failed to get file counts from statvfs. {}", unsatisfiedLinkError.getMessage());
      } 
      if (l3 == 0L) {
        File file = new File(str4);
        l3 = file.getTotalSpace();
        l4 = file.getUsableSpace();
        l5 = file.getFreeSpace();
      } 
      arrayList.add(new LinuxOSFileStore(str3, str2, map.getOrDefault(str4, str3), str4, str6, str7, str9, str8, str5, l5, l4, l3, l2, l1));
    } 
    return (List)arrayList;
  }
  
  private static Map<String, String> queryLabelMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (String str : ExecutingCommand.runNative("lsblk -o mountpoint,label")) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str, 2);
      if (arrayOfString.length == 2)
        hashMap.put(arrayOfString[0], arrayOfString[1]); 
    } 
    return (Map)hashMap;
  }
  
  public long getOpenFileDescriptors() {
    return getFileDescriptors(0);
  }
  
  public long getMaxFileDescriptors() {
    return getFileDescriptors(2);
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    return getFileDescriptorsPerProcess();
  }
  
  private static long getFileDescriptors(int paramInt) {
    String str = ProcPath.SYS_FS_FILE_NR;
    if (paramInt < 0 || paramInt > 2)
      throw new IllegalArgumentException("Index must be between 0 and 2."); 
    List<String> list = FileUtil.readFile(str);
    if (!list.isEmpty()) {
      String[] arrayOfString = ((String)list.get(0)).split("\\D+");
      return ParseUtil.parseLongOrDefault(arrayOfString[paramInt], 0L);
    } 
    return 0L;
  }
  
  private static long getFileDescriptorsPerProcess() {
    return FileUtil.getLongFromFile(ProcPath.SYS_FS_FILE_MAX);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */