package oshi.software.os.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.driver.windows.wmi.Win32LogicalDisk;
import oshi.jna.ByRef;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public class WindowsFileSystem extends AbstractFileSystem {
  private static final int BUFSIZE = 255;
  
  private static final int SEM_FAILCRITICALERRORS = 1;
  
  private static final int FILE_CASE_SENSITIVE_SEARCH = 1;
  
  private static final int FILE_CASE_PRESERVED_NAMES = 2;
  
  private static final int FILE_FILE_COMPRESSION = 16;
  
  private static final int FILE_DAX_VOLUME = 536870912;
  
  private static final int FILE_NAMED_STREAMS = 262144;
  
  private static final int FILE_PERSISTENT_ACLS = 8;
  
  private static final int FILE_READ_ONLY_VOLUME = 524288;
  
  private static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
  
  private static final int FILE_SUPPORTS_ENCRYPTION = 131072;
  
  private static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
  
  private static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
  
  private static final int FILE_SUPPORTS_SPARSE_FILES = 64;
  
  private static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
  
  private static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
  
  private static final int FILE_UNICODE_ON_DISK = 4;
  
  private static final int FILE_VOLUME_IS_COMPRESSED = 32768;
  
  private static final int FILE_VOLUME_QUOTAS = 32;
  
  private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
  
  static final long MAX_WINDOWS_HANDLES;
  
  public WindowsFileSystem() {
    Kernel32.INSTANCE.SetErrorMode(1);
  }
  
  public List<OSFileStore> getFileStores(boolean paramBoolean) {
    ArrayList<OSFileStore> arrayList = getLocalVolumes(null);
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (OSFileStore oSFileStore : arrayList)
      hashMap.put(oSFileStore.getMount(), oSFileStore); 
    for (OSFileStore oSFileStore : getWmiVolumes(null, paramBoolean)) {
      if (hashMap.containsKey(oSFileStore.getMount())) {
        OSFileStore oSFileStore1 = (OSFileStore)hashMap.get(oSFileStore.getMount());
        arrayList.remove(oSFileStore1);
        arrayList.add(new WindowsOSFileStore(oSFileStore.getName(), oSFileStore1.getVolume(), oSFileStore1.getLabel().isEmpty() ? oSFileStore.getLabel() : oSFileStore1.getLabel(), oSFileStore1.getMount(), oSFileStore1.getOptions(), oSFileStore1.getUUID(), "", oSFileStore1.getDescription(), oSFileStore1.getType(), oSFileStore1.getFreeSpace(), oSFileStore1.getUsableSpace(), oSFileStore1.getTotalSpace(), 0L, 0L));
        continue;
      } 
      if (!paramBoolean)
        arrayList.add(oSFileStore); 
    } 
    return arrayList;
  }
  
  static ArrayList<OSFileStore> getLocalVolumes(String paramString) {
    ArrayList<OSFileStore> arrayList = new ArrayList();
    char[] arrayOfChar = new char[255];
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.FindFirstVolume(arrayOfChar, 255);
    if (WinBase.INVALID_HANDLE_VALUE.equals(hANDLE))
      return arrayList; 
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    } finally {
      Kernel32.INSTANCE.FindVolumeClose(hANDLE);
    } 
  }
  
  static List<OSFileStore> getWmiVolumes(String paramString, boolean paramBoolean) {
    ArrayList<WindowsOSFileStore> arrayList = new ArrayList();
    WbemcliUtil.WmiResult wmiResult = Win32LogicalDisk.queryLogicalDisk(paramString, paramBoolean);
    for (byte b = 0; b < wmiResult.getResultCount(); b++) {
      String str5;
      long l1 = WmiUtil.getUint64(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.FREESPACE, b);
      long l2 = WmiUtil.getUint64(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.SIZE, b);
      String str1 = WmiUtil.getString(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.DESCRIPTION, b);
      String str2 = WmiUtil.getString(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.NAME, b);
      String str3 = WmiUtil.getString(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.VOLUMENAME, b);
      String str4 = (WmiUtil.getUint16(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.ACCESS, b) == 1) ? "ro" : "rw";
      int i = WmiUtil.getUint32(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.DRIVETYPE, b);
      if (i != 4) {
        char[] arrayOfChar = new char[255];
        Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(str2 + "\\", arrayOfChar, 255);
        str5 = Native.toString(arrayOfChar);
      } else {
        str5 = WmiUtil.getString(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.PROVIDERNAME, b);
        String[] arrayOfString = str5.split("\\\\");
        if (arrayOfString.length > 1 && arrayOfString[arrayOfString.length - 1].length() > 0)
          str1 = arrayOfString[arrayOfString.length - 1]; 
      } 
      arrayList.add(new WindowsOSFileStore(String.format(Locale.ROOT, "%s (%s)", new Object[] { str1, str2 }), str5, str3, str2 + "\\", str4, "", "", getDriveType(str2), WmiUtil.getString(wmiResult, (Enum)Win32LogicalDisk.LogicalDiskProperty.FILESYSTEM, b), l1, l1, l2, 0L, 0L));
    } 
    return (List)arrayList;
  }
  
  private static String getDriveType(String paramString) {
    switch (Kernel32.INSTANCE.GetDriveType(paramString)) {
      case 2:
        return "Removable drive";
      case 3:
        return "Fixed drive";
      case 4:
        return "Network drive";
      case 5:
        return "CD-ROM";
      case 6:
        return "RAM drive";
    } 
    return "Unknown drive type";
  }
  
  public long getOpenFileDescriptors() {
    Map map = (Map)ProcessInformation.queryHandles().getB();
    List list = (List)map.get(ProcessInformation.HandleCountProperty.HANDLECOUNT);
    long l = 0L;
    if (list != null)
      for (Long long_ : list)
        l += long_.longValue();  
    return l;
  }
  
  public long getMaxFileDescriptors() {
    return MAX_WINDOWS_HANDLES;
  }
  
  public long getMaxFileDescriptorsPerProcess() {
    return MAX_WINDOWS_HANDLES;
  }
  
  static {
    OPTIONS_MAP.put(Integer.valueOf(2), "casepn");
    OPTIONS_MAP.put(Integer.valueOf(1), "casess");
    OPTIONS_MAP.put(Integer.valueOf(16), "fcomp");
    OPTIONS_MAP.put(Integer.valueOf(536870912), "dax");
    OPTIONS_MAP.put(Integer.valueOf(262144), "streams");
    OPTIONS_MAP.put(Integer.valueOf(8), "acls");
    OPTIONS_MAP.put(Integer.valueOf(1048576), "wronce");
    OPTIONS_MAP.put(Integer.valueOf(131072), "efs");
    OPTIONS_MAP.put(Integer.valueOf(65536), "oids");
    OPTIONS_MAP.put(Integer.valueOf(128), "reparse");
    OPTIONS_MAP.put(Integer.valueOf(64), "sparse");
    OPTIONS_MAP.put(Integer.valueOf(2097152), "trans");
    OPTIONS_MAP.put(Integer.valueOf(33554432), "journaled");
    OPTIONS_MAP.put(Integer.valueOf(4), "unicode");
    OPTIONS_MAP.put(Integer.valueOf(32768), "vcomp");
    OPTIONS_MAP.put(Integer.valueOf(32), "quota");
    if (System.getenv("ProgramFiles(x86)") == null) {
      MAX_WINDOWS_HANDLES = 16744448L;
    } else {
      MAX_WINDOWS_HANDLES = 16711680L;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */