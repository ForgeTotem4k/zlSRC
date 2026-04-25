package io.sentry.clientreport;

import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
final class ClientReportKey {
  @NotNull
  private final String reason;
  
  @NotNull
  private final String category;
  
  ClientReportKey(@NotNull String paramString1, @NotNull String paramString2) {
    this.reason = paramString1;
    this.category = paramString2;
  }
  
  @NotNull
  public String getReason() {
    return this.reason;
  }
  
  @NotNull
  public String getCategory() {
    return this.category;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ClientReportKey))
      return false; 
    ClientReportKey clientReportKey = (ClientReportKey)paramObject;
    return (Objects.equals(getReason(), clientReportKey.getReason()) && Objects.equals(getCategory(), clientReportKey.getCategory()));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { getReason(), getCategory() });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\ClientReportKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */