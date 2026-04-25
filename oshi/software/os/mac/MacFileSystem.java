package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.FileSystemUtil;
import oshi.util.platform.mac.CFUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacFileSystem extends AbstractFileSystem {
  private static final Logger LOG = LoggerFactory.getLogger(MacFileSystem.class);
  
  public static final String OSHI_MAC_FS_PATH_EXCLUDES = "oshi.os.mac.filesystem.path.excludes";
  
  public static final String OSHI_MAC_FS_PATH_INCLUDES = "oshi.os.mac.filesystem.path.includes";
  
  public static final String OSHI_MAC_FS_VOLUME_EXCLUDES = "oshi.os.mac.filesystem.volume.excludes";
  
  public static final String OSHI_MAC_FS_VOLUME_INCLUDES = "oshi.os.mac.filesystem.volume.includes";
  
  private static final List<PathMatcher> FS_PATH_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.path.excludes");
  
  private static final List<PathMatcher> FS_PATH_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.path.includes");
  
  private static final List<PathMatcher> FS_VOLUME_EXCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.volume.excludes");
  
  private static final List<PathMatcher> FS_VOLUME_INCLUDES = FileSystemUtil.loadAndParseFileSystemConfig("oshi.os.mac.filesystem.volume.includes");
  
  private static final Pattern LOCAL_DISK = Pattern.compile("/dev/disk\\d");
  
  private static final int MNT_RDONLY = 1;
  
  private static final int MNT_SYNCHRONOUS = 2;
  
  private static final int MNT_NOEXEC = 4;
  
  private static final int MNT_NOSUID = 8;
  
  private static final int MNT_NODEV = 16;
  
  private static final int MNT_UNION = 32;
  
  private static final int MNT_ASYNC = 64;
  
  private static final int MNT_CPROTECT = 128;
  
  private static final int MNT_EXPORTED = 256;
  
  private static final int MNT_QUARANTINE = 1024;
  
  private static final int MNT_LOCAL = 4096;
  
  private static final int MNT_QUOTA = 8192;
  
  private static final int MNT_ROOTFS = 16384;
  
  private static final int MNT_DOVOLFS = 32768;
  
  private static final int MNT_DONTBROWSE = 1048576;
  
  private static final int MNT_IGNORE_OWNERSHIP = 2097152;
  
  private static final int MNT_AUTOMOUNTED = 4194304;
  
  private static final int MNT_JOURNALED = 8388608;
  
  private static final int MNT_NOUSERXATTR = 16777216;
  
  private static final int MNT_DEFWRITE = 33554432;
  
  private static final int MNT_MULTILABEL = 67108864;
  
  private static final int MNT_NOATIME = 268435456;
  
  private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    return getFileStoreMatching((String)null, paramBoolean);
  }
  
  static List<OSFileStore> getFileStoreMatching(String paramString) {
    return getFileStoreMatching(paramString, false);
  }
  
  private static List<OSFileStore> getFileStoreMatching(String paramString, boolean paramBoolean) {
    ArrayList<MacOSFileStore> arrayList = new ArrayList();
    int i = SystemB.INSTANCE.getfsstat64(null, 0, 0);
    if (i > 0) {
      DiskArbitration.DASessionRef dASessionRef = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
      if (dASessionRef == null) {
        LOG.error("Unable to open session to DiskArbitration framework.");
      } else {
        CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString("DAVolumeName");
        SystemB.Statfs statfs = new SystemB.Statfs();
        SystemB.Statfs[] arrayOfStatfs = (SystemB.Statfs[])statfs.toArray(i);
        i = SystemB.INSTANCE.getfsstat64(arrayOfStatfs, arrayOfStatfs[0].size() * arrayOfStatfs.length, 16);
        for (byte b = 0; b < i; b++) {
          String str1 = Native.toString((arrayOfStatfs[b]).f_mntfromname, StandardCharsets.UTF_8);
          String str2 = Native.toString((arrayOfStatfs[b]).f_mntonname, StandardCharsets.UTF_8);
          String str3 = Native.toString((arrayOfStatfs[b]).f_fstypename, StandardCharsets.UTF_8);
          int j = (arrayOfStatfs[b]).f_flags;
          if ((!paramBoolean || (j & 0x1000) != 0) && (str2.equals("/") || (!PSEUDO_FS_TYPES.contains(str3) && !FileSystemUtil.isFileStoreExcluded(str2, str1, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES, FS_VOLUME_EXCLUDES)))) {
            String str4 = "Volume";
            if (LOCAL_DISK.matcher(str1).matches()) {
              str4 = "Local Disk";
            } else if (str1.startsWith("localhost:") || str1.startsWith("//") || str1.startsWith("smb://") || NETWORK_FS_TYPES.contains(str3)) {
              str4 = "Network Drive";
            } 
            File file = new File(str2);
            String str5 = file.getName();
            if (str5.isEmpty())
              str5 = file.getPath(); 
            if (paramString == null || paramString.equals(str5)) {
              StringBuilder stringBuilder = new StringBuilder(((0x1 & j) == 0) ? "rw" : "ro");
              String str6 = OPTIONS_MAP.entrySet().stream().filter(paramEntry -> ((((Integer)paramEntry.getKey()).intValue() & paramInt) > 0)).map(Map.Entry::getValue).collect(Collectors.joining(","));
              if (!str6.isEmpty())
                stringBuilder.append(',').append(str6); 
              String str7 = "";
              String str8 = str1.replace("/dev/disk", "disk");
              if (str8.startsWith("disk")) {
                DiskArbitration.DADiskRef dADiskRef = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE.CFAllocatorGetDefault(), dASessionRef, str1);
                if (dADiskRef != null) {
                  CoreFoundation.CFDictionaryRef cFDictionaryRef = DiskArbitration.INSTANCE.DADiskCopyDescription(dADiskRef);
                  if (cFDictionaryRef != null) {
                    Pointer pointer = cFDictionaryRef.getValue((PointerType)cFStringRef);
                    str5 = CFUtil.cfPointerToString(pointer);
                    cFDictionaryRef.release();
                  } 
                  dADiskRef.release();
                } 
                CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = IOKitUtil.getBSDNameMatchingDict(str8);
                if (cFMutableDictionaryRef != null) {
                  IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)cFMutableDictionaryRef);
                  if (iOIterator != null) {
                    IOKit.IORegistryEntry iORegistryEntry = iOIterator.next();
                    if (iORegistryEntry != null && iORegistryEntry.conformsTo("IOMedia")) {
                      str7 = iORegistryEntry.getStringProperty("UUID");
                      if (str7 != null)
                        str7 = str7.toLowerCase(Locale.ROOT); 
                      iORegistryEntry.release();
                    } 
                    iOIterator.release();
                  } 
                } 
              } 
              arrayList.add(new MacOSFileStore(str5, str1, str5, str2, stringBuilder.toString(), (str7 == null) ? "" : str7, "", str4, str3, file.getFreeSpace(), file.getUsableSpace(), file.getTotalSpace(), (arrayOfStatfs[b]).f_ffree, (arrayOfStatfs[b]).f_files));
            } 
          } 
        } 
        cFStringRef.release();
        dASessionRef.release();
      } 
    } 
    return (List)arrayList;
  }
  
  public long getOpenFileDescriptors() {
    return SysctlUtil.sysctl("kern.num_files", 0);
  }
  
  public long getMaxFileDescriptors() {
    return SysctlUtil.sysctl("kern.maxfiles", 0);
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    return SysctlUtil.sysctl("kern.maxfilesperproc", 0);
  }
  
  static {
    OPTIONS_MAP.put(Integer.valueOf(2), "synchronous");
    OPTIONS_MAP.put(Integer.valueOf(4), "noexec");
    OPTIONS_MAP.put(Integer.valueOf(8), "nosuid");
    OPTIONS_MAP.put(Integer.valueOf(16), "nodev");
    OPTIONS_MAP.put(Integer.valueOf(32), "union");
    OPTIONS_MAP.put(Integer.valueOf(64), "asynchronous");
    OPTIONS_MAP.put(Integer.valueOf(128), "content-protection");
    OPTIONS_MAP.put(Integer.valueOf(256), "exported");
    OPTIONS_MAP.put(Integer.valueOf(1024), "quarantined");
    OPTIONS_MAP.put(Integer.valueOf(4096), "local");
    OPTIONS_MAP.put(Integer.valueOf(8192), "quotas");
    OPTIONS_MAP.put(Integer.valueOf(16384), "rootfs");
    OPTIONS_MAP.put(Integer.valueOf(32768), "volfs");
    OPTIONS_MAP.put(Integer.valueOf(1048576), "nobrowse");
    OPTIONS_MAP.put(Integer.valueOf(2097152), "noowners");
    OPTIONS_MAP.put(Integer.valueOf(4194304), "automounted");
    OPTIONS_MAP.put(Integer.valueOf(8388608), "journaled");
    OPTIONS_MAP.put(Integer.valueOf(16777216), "nouserxattr");
    OPTIONS_MAP.put(Integer.valueOf(33554432), "defwrite");
    OPTIONS_MAP.put(Integer.valueOf(67108864), "multilabel");
    OPTIONS_MAP.put(Integer.valueOf(268435456), "noatime");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */