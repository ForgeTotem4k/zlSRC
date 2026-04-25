package io.sentry;

import java.util.List;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class BaggageHeader {
  @NotNull
  public static final String BAGGAGE_HEADER = "baggage";
  
  @NotNull
  private final String value;
  
  @Nullable
  public static BaggageHeader fromBaggageAndOutgoingHeader(@NotNull Baggage paramBaggage, @Nullable List<String> paramList) {
    Baggage baggage = Baggage.fromHeader(paramList, true, paramBaggage.logger);
    String str = paramBaggage.toHeaderString(baggage.getThirdPartyHeader());
    return str.isEmpty() ? null : new BaggageHeader(str);
  }
  
  public BaggageHeader(@NotNull String paramString) {
    this.value = paramString;
  }
  
  @NotNull
  public String getName() {
    return "baggage";
  }
  
  @NotNull
  public String getValue() {
    return this.value;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\BaggageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */