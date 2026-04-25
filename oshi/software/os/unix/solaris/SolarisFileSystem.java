package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.io.File;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.FileSystemUtil;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class SolarisFileSystem extends AbstractFileSystem {
  private static final Supplier<Pair<Long, Long>> FILE_DESC = Memoizer.memoize(SolarisFileSystem::queryFileDescriptors, Memoizer.defaultExpiration());
  
  public static final String OSHI_SOLARIS_FS_PATH_EXCLUDES = "oshi.os.solaris.filesystem.path.excludes";
  
  public static final String OSHI_SOLARIS_FS_PATH_INCLUDES = "oshi.os.solaris.filesystem.path.includes";
  
  public static final String OSHI_SOLARIS_FS_VOLUME_EXCLUDES = "oshi.os.solaris.filesystem.volume.excludes";
  
  public static final String OSHI_SOLARIS_FS_VOLUME_INCLUDES = "oshi.os.solaris.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.solaris.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.solaris.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.solaris.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.solaris.filesystem.volume.includes");
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    return getFileStoreMatching((String)null, paramBoolean);
  }
  
  static List<OSFileStore> getFileStoreMatching(String paramString) {
    return getFileStoreMatching(paramString, false);
  }
  
  private static List<OSFileStore> getFileStoreMatching(String paramString, boolean paramBoolean) {
    ArrayList<SolarisOSFileStore> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = "df -g" + (paramBoolean ? " -l" : "");
    for (String str : ExecutingCommand.runNative(str4)) {
      if (str.startsWith("/")) {
        str1 = ParseUtil.whitespaces.split(str)[0];
        str2 = null;
        continue;
      } 
      if (str.contains("available") && str.contains("total files")) {
        str2 = ParseUtil.getTextBetweenStrings(str, "available", "total files").trim();
        continue;
      } 
      if (str.contains("free files")) {
        str3 = ParseUtil.getTextBetweenStrings(str, "", "free files").trim();
        if (str1 != null && str2 != null) {
          hashMap1.put(str1, Long.valueOf(ParseUtil.parseLongOrDefault(str3, 0L)));
          hashMap2.put(str1, Long.valueOf(ParseUtil.parseLongOrDefault(str2, 0L)));
          str1 = null;
        } 
      } 
    } 
    for (String str5 : ExecutingCommand.runNative("cat /etc/mnttab")) {
      String str11;
      String[] arrayOfString = ParseUtil.whitespaces.split(str5);
      if (arrayOfString.length < 5)
        continue; 
      String str6 = arrayOfString[0];
      String str7 = arrayOfString[1];
      String str8 = arrayOfString[2];
      String str9 = arrayOfString[3];
      if ((paramBoolean && NETWORK_FS_TYPES.contains(str8)) || (!str7.equals("/") && (PSEUDO_FS_TYPES.contains(str8) || FileSystemUtil.isFileStoreExcluded(str7, str6, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES))))
        continue; 
      String str10 = str7.substring(str7.lastIndexOf('/') + 1);
      if (str10.isEmpty())
        str10 = str6.substring(str6.lastIndexOf('/') + 1); 
      if (paramString != null && !paramString.equals(str10))
        continue; 
      File file = new File(str7);
      long l1 = file.getTotalSpace();
      long l2 = file.getUsableSpace();
      long l3 = file.getFreeSpace();
      if (str6.startsWith("/dev") || str7.equals("/")) {
        str11 = "Local Disk";
      } else if (str6.equals("tmpfs")) {
        str11 = "Ram Disk";
      } else if (NETWORK_FS_TYPES.contains(str8)) {
        str11 = "Network Disk";
      } else {
        str11 = "Mount Point";
      } 
      arrayList.add(new SolarisOSFileStore(str10, str6, str10, str7, str9, "", "", str11, str8, l3, l2, l1, hashMap1.containsKey(str7) ? ((Long)hashMap1.get(str7)).longValue() : 0L, hashMap2.containsKey(str7) ? ((Long)hashMap2.get(str7)).longValue() : 0L));
    } 
    return (List)arrayList;
  }
  
  public long getOpenFileDescriptors() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return ((Long)((Pair)FILE_DESC.get()).getA()).longValue(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup(null, -1, "file_cache");
      if (kstat != null && kstatChain.read(kstat)) {
        long l = KstatUtil.dataLookupLong(kstat, "buf_inuse");
        if (kstatChain != null)
          kstatChain.close(); 
        return l;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return 0L;
  }
  
  public long getMaxFileDescriptors() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return ((Long)((Pair)FILE_DESC.get()).getB()).longValue(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup(null, -1, "file_cache");
      if (kstat != null && kstatChain.read(kstat)) {
        long l = KstatUtil.dataLookupLong(kstat, "buf_max");
        if (kstatChain != null)
          kstatChain.close(); 
        return l;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return 0L;
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    List list = FileUtil.readFile("/etc/system");
    for (String str : list) {
      if (str.startsWith("set rlim_fd_max"))
        return ParseUtil.parseLastLong(str, 65536L); 
    } 
    return 65536L;
  }
  
  private static Pair<Long, Long> queryFileDescriptors() {
    Object[] arrayOfObject = KstatUtil.queryKstat2("kstat:/kmem_cache/kmem_default/file_cache", new String[] { "buf_inuse", "buf_max" });
    long l1 = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
    long l2 = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */