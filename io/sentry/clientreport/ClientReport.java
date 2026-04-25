package io.sentry.clientreport;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ClientReport implements JsonUnknown, JsonSerializable {
  @NotNull
  private final Date timestamp;
  
  @NotNull
  private final List<DiscardedEvent> discardedEvents;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public ClientReport(@NotNull Date paramDate, @NotNull List<DiscardedEvent> paramList) {
    this.timestamp = paramDate;
    this.discardedEvents = paramList;
  }
  
  @NotNull
  public Date getTimestamp() {
    return this.timestamp;
  }
  
  @NotNull
  public List<DiscardedEvent> getDiscardedEvents() {
    return this.discardedEvents;
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("timestamp").value(DateUtils.getTimestamp(this.timestamp));
    paramObjectWriter.name("discarded_events").value(paramILogger, this.discardedEvents);
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TIMESTAMP = "timestamp";
    
    public static final String DISCARDED_EVENTS = "discarded_events";
  }
  
  public static final class Deserializer implements JsonDeserializer<ClientReport> {
    @NotNull
    public ClientReport deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      Date date = null;
      ArrayList<DiscardedEvent> arrayList = new ArrayList();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List list;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "timestamp":
            date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            continue;
          case "discarded_events":
            list = param1JsonObjectReader.nextListOrNull(param1ILogger, new DiscardedEvent.Deserializer());
            arrayList.addAll(list);
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (date == null)
        throw missingRequiredFieldException("timestamp", param1ILogger); 
      if (arrayList.isEmpty())
        throw missingRequiredFieldException("discarded_events", param1ILogger); 
      ClientReport clientReport = new ClientReport(date, arrayList);
      clientReport.setUnknown((Map)hashMap);
      return clientReport;
    }
    
    private Exception missingRequiredFieldException(String param1String, ILogger param1ILogger) {
      String str = "Missing required field \"" + param1String + "\"";
      IllegalStateException illegalStateException = new IllegalStateException(str);
      param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
      return illegalStateException;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\ClientReport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */