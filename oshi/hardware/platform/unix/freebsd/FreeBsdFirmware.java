package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
final class FreeBsdFirmware extends AbstractFirmware {
  private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(FreeBsdFirmware::readDmiDecode);
  
  public String getManufacturer() {
    return (String)((Triplet)this.manufVersRelease.get()).getA();
  }
  
  public String getVersion() {
    return (String)((Triplet)this.manufVersRelease.get()).getB();
  }
  
  public String getReleaseDate() {
    return (String)((Triplet)this.manufVersRelease.get()).getC();
  }
  
  private static Triplet<String, String, String> readDmiDecode() {
    String str1 = null;
    String str2 = null;
    String str3 = "";
    String str4 = "Vendor:";
    String str5 = "Version:";
    String str6 = "Release Date:";
    for (String str : ExecutingCommand.runNative("dmidecode -t bios")) {
      if (str.contains("Vendor:")) {
        str1 = str.split("Vendor:")[1].trim();
        continue;
      } 
      if (str.contains("Version:")) {
        str2 = str.split("Version:")[1].trim();
        continue;
      } 
      if (str.contains("Release Date:"))
        str3 = str.split("Release Date:")[1].trim(); 
    } 
    str3 = ParseUtil.parseMmDdYyyyToYyyyMmDD(str3);
    return new Triplet(Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */