package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public interface MeasurementUnit {
  @Internal
  public static final String NONE = "none";
  
  @NotNull
  String name();
  
  @Internal
  @NotNull
  String apiName();
  
  public static final class Custom implements MeasurementUnit {
    @NotNull
    private final String name;
    
    public Custom(@NotNull String param1String) {
      this.name = param1String;
    }
    
    @NotNull
    public String name() {
      return this.name;
    }
    
    @NotNull
    public String apiName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
  
  public enum Fraction implements MeasurementUnit {
    RATIO, PERCENT;
    
    @NotNull
    public String apiName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
  
  public enum Information implements MeasurementUnit {
    BIT, BYTE, KILOBYTE, KIBIBYTE, MEGABYTE, MEBIBYTE, GIGABYTE, GIBIBYTE, TERABYTE, TEBIBYTE, PETABYTE, PEBIBYTE, EXABYTE, EXBIBYTE;
    
    @NotNull
    public String apiName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
  
  public enum Duration implements MeasurementUnit {
    NANOSECOND, MICROSECOND, MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK;
    
    @NotNull
    public String apiName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\MeasurementUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */