package oshi.hardware.platform.unix.openbsd;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
public class OpenBsdFirmware extends AbstractFirmware {
  private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(OpenBsdFirmware::readDmesg);
  
  public String getManufacturer() {
    return (String)((Triplet)this.manufVersRelease.get()).getA();
  }
  
  public String getVersion() {
    return (String)((Triplet)this.manufVersRelease.get()).getB();
  }
  
  public String getReleaseDate() {
    return (String)((Triplet)this.manufVersRelease.get()).getC();
  }
  
  private static Triplet<String, String, String> readDmesg() {
    String str1 = null;
    String str2 = null;
    String str3 = "";
    List list = ExecutingCommand.runNative("dmesg");
    for (String str : list) {
      if (str.startsWith("bios0: vendor")) {
        str1 = ParseUtil.getStringBetween(str, '"');
        str3 = ParseUtil.parseMmDdYyyyToYyyyMmDD(ParseUtil.parseLastString(str));
        str2 = str.split("vendor")[1].trim();
      } 
    } 
    return new Triplet(Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str1) ? "unknown" : str1, Util.isBlank(str3) ? "unknown" : str3);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */