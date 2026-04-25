package oshi.driver.unix.aix;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class Who {
  private static final Pattern BOOT_FORMAT_AIX = Pattern.compile("\\D+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}).*");
  
  private static final DateTimeFormatter BOOT_DATE_FORMAT_AIX = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);
  
  public static long queryBootTime() {
    String str = ExecutingCommand.getFirstAnswer("who -b");
    if (str.isEmpty())
      str = ExecutingCommand.getFirstAnswer("/usr/bin/who -b"); 
    Matcher matcher = BOOT_FORMAT_AIX.matcher(str);
    if (matcher.matches())
      try {
        return LocalDateTime.parse(matcher.group(1) + " " + matcher.group(2), BOOT_DATE_FORMAT_AIX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      } catch (DateTimeParseException|NullPointerException dateTimeParseException) {} 
    return 0L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */