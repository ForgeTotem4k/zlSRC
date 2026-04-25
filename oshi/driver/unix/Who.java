package oshi.driver.unix;

import com.sun.jna.Platform;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSSession;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class Who {
  private static final Pattern WHO_FORMAT_LINUX = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2})\\s*(?:\\((.+)\\))?");
  
  private static final DateTimeFormatter WHO_DATE_FORMAT_LINUX = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);
  
  private static final Pattern WHO_FORMAT_UNIX = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d{2}:\\d{2})\\s*(?:\\((.+)\\))?");
  
  private static final DateTimeFormatter WHO_DATE_FORMAT_UNIX = (new DateTimeFormatterBuilder()).appendPattern("MMM d HH:mm").parseDefaulting(ChronoField.YEAR, Year.now(ZoneId.systemDefault()).getValue()).toFormatter(Locale.US);
  
  public static synchronized List<OSSession> queryWho() {
    ArrayList<OSSession> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("who");
    for (String str : list) {
      boolean bool = false;
      if (Platform.isLinux())
        bool = matchLinux(arrayList, str); 
      if (!bool)
        matchUnix(arrayList, str); 
    } 
    return arrayList;
  }
  
  private static boolean matchLinux(List<OSSession> paramList, String paramString) {
    Matcher matcher = WHO_FORMAT_LINUX.matcher(paramString);
    if (matcher.matches())
      try {
        paramList.add(new OSSession(matcher.group(1), matcher.group(2), LocalDateTime.parse(matcher.group(3) + " " + matcher.group(4), WHO_DATE_FORMAT_LINUX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), (matcher.group(5) == null) ? "unknown" : matcher.group(5)));
        return true;
      } catch (DateTimeParseException|NullPointerException dateTimeParseException) {} 
    return false;
  }
  
  private static boolean matchUnix(List<OSSession> paramList, String paramString) {
    Matcher matcher = WHO_FORMAT_UNIX.matcher(paramString);
    if (matcher.matches())
      try {
        LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(3) + " " + matcher.group(4) + " " + matcher.group(5), WHO_DATE_FORMAT_UNIX);
        if (localDateTime.isAfter(LocalDateTime.now(ZoneId.systemDefault())))
          localDateTime = localDateTime.minus(1L, ChronoUnit.YEARS); 
        long l = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        paramList.add(new OSSession(matcher.group(1), matcher.group(2), l, (matcher.group(6) == null) ? "" : matcher.group(6)));
        return true;
      } catch (DateTimeParseException|NullPointerException dateTimeParseException) {} 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */