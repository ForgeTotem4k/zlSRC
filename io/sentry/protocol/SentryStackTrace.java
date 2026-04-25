package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryStackTrace implements JsonUnknown, JsonSerializable {
  @Nullable
  private List<SentryStackFrame> frames;
  
  @Nullable
  private Map<String, String> registers;
  
  @Nullable
  private Boolean snapshot;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SentryStackTrace() {}
  
  public SentryStackTrace(@Nullable List<SentryStackFrame> paramList) {
    this.frames = paramList;
  }
  
  @Nullable
  public List<SentryStackFrame> getFrames() {
    return this.frames;
  }
  
  public void setFrames(@Nullable List<SentryStackFrame> paramList) {
    this.frames = paramList;
  }
  
  @Nullable
  public Map<String, String> getRegisters() {
    return this.registers;
  }
  
  public void setRegisters(@Nullable Map<String, String> paramMap) {
    this.registers = paramMap;
  }
  
  @Nullable
  public Boolean getSnapshot() {
    return this.snapshot;
  }
  
  public void setSnapshot(@Nullable Boolean paramBoolean) {
    this.snapshot = paramBoolean;
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
    if (this.frames != null)
      paramObjectWriter.name("frames").value(paramILogger, this.frames); 
    if (this.registers != null)
      paramObjectWriter.name("registers").value(paramILogger, this.registers); 
    if (this.snapshot != null)
      paramObjectWriter.name("snapshot").value(this.snapshot); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String FRAMES = "frames";
    
    public static final String REGISTERS = "registers";
    
    public static final String SNAPSHOT = "snapshot";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryStackTrace> {
    @NotNull
    public SentryStackTrace deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryStackTrace sentryStackTrace = new SentryStackTrace();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "frames":
            sentryStackTrace.frames = param1JsonObjectReader.nextListOrNull(param1ILogger, new SentryStackFrame.Deserializer());
            continue;
          case "registers":
            sentryStackTrace.registers = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            continue;
          case "snapshot":
            sentryStackTrace.snapshot = param1JsonObjectReader.nextBooleanOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      sentryStackTrace.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryStackTrace;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryStackTrace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */