package oshi.hardware.platform.linux;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Dmidecode;
import oshi.driver.linux.Sysfs;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@Immutable
final class LinuxFirmware extends AbstractFirmware {
  private static final DateTimeFormatter VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss", Locale.ENGLISH);
  
  private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
  
  private final Supplier<String> description = Memoizer.memoize(this::queryDescription);
  
  private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
  
  private final Supplier<String> releaseDate = Memoizer.memoize(this::queryReleaseDate);
  
  private final Supplier<String> name = Memoizer.memoize(this::queryName);
  
  private final Supplier<VcGenCmdStrings> vcGenCmd = Memoizer.memoize(LinuxFirmware::queryVcGenCmd);
  
  private final Supplier<Pair<String, String>> biosNameRev = Memoizer.memoize(Dmidecode::queryBiosNameRev);
  
  public String getManufacturer() {
    return this.manufacturer.get();
  }
  
  public String getDescription() {
    return this.description.get();
  }
  
  public String getVersion() {
    return this.version.get();
  }
  
  public String getReleaseDate() {
    return this.releaseDate.get();
  }
  
  public String getName() {
    return this.name.get();
  }
  
  private String queryManufacturer() {
    String str = null;
    return ((str = Sysfs.queryBiosVendor()) == null && (str = (this.vcGenCmd.get()).manufacturer) == null) ? "unknown" : str;
  }
  
  private String queryDescription() {
    String str = null;
    return ((str = Sysfs.queryBiosDescription()) == null && (str = (this.vcGenCmd.get()).description) == null) ? "unknown" : str;
  }
  
  private String queryVersion() {
    String str = null;
    return ((str = Sysfs.queryBiosVersion((String)((Pair)this.biosNameRev.get()).getB())) == null && (str = (this.vcGenCmd.get()).version) == null) ? "unknown" : str;
  }
  
  private String queryReleaseDate() {
    String str = null;
    return ((str = Sysfs.queryBiosReleaseDate()) == null && (str = (this.vcGenCmd.get()).releaseDate) == null) ? "unknown" : str;
  }
  
  private String queryName() {
    String str = null;
    return ((str = (String)((Pair)this.biosNameRev.get()).getA()) == null && (str = (this.vcGenCmd.get()).name) == null) ? "unknown" : str;
  }
  
  private static VcGenCmdStrings queryVcGenCmd() {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    List<CharSequence> list = ExecutingCommand.runNative("vcgencmd version");
    if (list.size() >= 3) {
      try {
        str1 = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse(list.get(0)));
      } catch (DateTimeParseException dateTimeParseException) {
        str1 = "unknown";
      } 
      String[] arrayOfString = ParseUtil.whitespaces.split(list.get(1));
      str2 = arrayOfString[arrayOfString.length - 1];
      str3 = ((String)list.get(2)).replace("version ", "");
      return new VcGenCmdStrings(str1, str2, str3, "RPi", "Bootloader");
    } 
    return new VcGenCmdStrings(null, null, null, null, null);
  }
  
  private static final class VcGenCmdStrings {
    private final String releaseDate;
    
    private final String manufacturer;
    
    private final String version;
    
    private final String name;
    
    private final String description;
    
    private VcGenCmdStrings(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5) {
      this.releaseDate = param1String1;
      this.manufacturer = param1String2;
      this.version = param1String3;
      this.name = param1String4;
      this.description = param1String5;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */