package oshi.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Constants {
  public static final String UNKNOWN = "unknown";
  
  public static final OffsetDateTime UNIX_EPOCH = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
  
  public static final Pattern DIGITS = Pattern.compile("\\d+");
  
  private Constants() {
    throw new AssertionError();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\Constants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */