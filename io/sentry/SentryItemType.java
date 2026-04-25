package io.sentry;

import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public enum SentryItemType implements JsonSerializable {
  Session("session"),
  Event("event"),
  UserFeedback("user_report"),
  Attachment("attachment"),
  Transaction("transaction"),
  Profile("profile"),
  ClientReport("client_report"),
  ReplayEvent("replay_event"),
  ReplayRecording("replay_recording"),
  CheckIn("check_in"),
  Statsd("statsd"),
  Unknown("__unknown__");
  
  private final String itemType;
  
  public static SentryItemType resolve(Object paramObject) {
    return (paramObject instanceof SentryEvent) ? Event : ((paramObject instanceof io.sentry.protocol.SentryTransaction) ? Transaction : ((paramObject instanceof Session) ? Session : ((paramObject instanceof io.sentry.clientreport.ClientReport) ? ClientReport : Attachment)));
  }
  
  SentryItemType(String paramString1) {
    this.itemType = paramString1;
  }
  
  public String getItemType() {
    return this.itemType;
  }
  
  @NotNull
  public static SentryItemType valueOfLabel(String paramString) {
    for (SentryItemType sentryItemType : values()) {
      if (sentryItemType.itemType.equals(paramString))
        return sentryItemType; 
    } 
    return Unknown;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.value(this.itemType);
  }
  
  static final class Deserializer implements JsonDeserializer<SentryItemType> {
    @NotNull
    public SentryItemType deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      return SentryItemType.valueOfLabel(param1JsonObjectReader.nextString().toLowerCase(Locale.ROOT));
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */