package oshi.driver.linux;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.linux.SysPath;

@ThreadSafe
public final class Sysfs {
  public static String querySystemVendor() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "sys_vendor").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryProductModel() {
    String str1 = FileUtil.getStringFromFile(SysPath.DMI_ID + "product_name").trim();
    String str2 = FileUtil.getStringFromFile(SysPath.DMI_ID + "product_version").trim();
    if (str1.isEmpty()) {
      if (!str2.isEmpty())
        return str2; 
    } else {
      return (!str2.isEmpty() && !"None".equals(str2)) ? (str1 + " (version: " + str2 + ")") : str1;
    } 
    return null;
  }
  
  public static String queryProductSerial() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "product_serial");
    return (!str.isEmpty() && !"None".equals(str)) ? str : queryBoardSerial();
  }
  
  public static String queryUUID() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "product_uuid");
    return (!str.isEmpty() && !"None".equals(str)) ? str : null;
  }
  
  public static String queryBoardVendor() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "board_vendor").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryBoardModel() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "board_name").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryBoardVersion() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "board_version").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryBoardSerial() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "board_serial").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryBiosVendor() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "bios_vendor").trim();
    return str.isEmpty() ? str : null;
  }
  
  public static String queryBiosDescription() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "modalias").trim();
    return !str.isEmpty() ? str : null;
  }
  
  public static String queryBiosVersion(String paramString) {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "bios_version").trim();
    return !str.isEmpty() ? (str + (Util.isBlank(paramString) ? "" : (" (revision " + paramString + ")"))) : null;
  }
  
  public static String queryBiosReleaseDate() {
    String str = FileUtil.getStringFromFile(SysPath.DMI_ID + "bios_date").trim();
    return !str.isEmpty() ? ParseUtil.parseMmDdYyyyToYyyyMmDD(str) : null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Sysfs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */