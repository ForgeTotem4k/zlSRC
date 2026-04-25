package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@Internal
public final class SentryAppStartProfilingOptions implements JsonUnknown, JsonSerializable {
  boolean profileSampled = false;
  
  @Nullable
  Double profileSampleRate = null;
  
  boolean traceSampled = false;
  
  @Nullable
  Double traceSampleRate = null;
  
  @Nullable
  String profilingTracesDirPath = null;
  
  boolean isProfilingEnabled = false;
  
  int profilingTracesHz = 0;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @VisibleForTesting
  public SentryAppStartProfilingOptions() {}
  
  SentryAppStartProfilingOptions(@NotNull SentryOptions paramSentryOptions, @NotNull TracesSamplingDecision paramTracesSamplingDecision) {}
  
  public void setProfileSampled(boolean paramBoolean) {
    this.profileSampled = paramBoolean;
  }
  
  public boolean isProfileSampled() {
    return this.profileSampled;
  }
  
  public void setProfileSampleRate(@Nullable Double paramDouble) {
    this.profileSampleRate = paramDouble;
  }
  
  @Nullable
  public Double getProfileSampleRate() {
    return this.profileSampleRate;
  }
  
  public void setTraceSampled(boolean paramBoolean) {
    this.traceSampled = paramBoolean;
  }
  
  public boolean isTraceSampled() {
    return this.traceSampled;
  }
  
  public void setTraceSampleRate(@Nullable Double paramDouble) {
    this.traceSampleRate = paramDouble;
  }
  
  @Nullable
  public Double getTraceSampleRate() {
    return this.traceSampleRate;
  }
  
  public void setProfilingTracesDirPath(@Nullable String paramString) {
    this.profilingTracesDirPath = paramString;
  }
  
  @Nullable
  public String getProfilingTracesDirPath() {
    return this.profilingTracesDirPath;
  }
  
  public void setProfilingEnabled(boolean paramBoolean) {
    this.isProfilingEnabled = paramBoolean;
  }
  
  public boolean isProfilingEnabled() {
    return this.isProfilingEnabled;
  }
  
  public void setProfilingTracesHz(int paramInt) {
    this.profilingTracesHz = paramInt;
  }
  
  public int getProfilingTracesHz() {
    return this.profilingTracesHz;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("profile_sampled").value(paramILogger, Boolean.valueOf(this.profileSampled));
    paramObjectWriter.name("profile_sample_rate").value(paramILogger, this.profileSampleRate);
    paramObjectWriter.name("trace_sampled").value(paramILogger, Boolean.valueOf(this.traceSampled));
    paramObjectWriter.name("trace_sample_rate").value(paramILogger, this.traceSampleRate);
    paramObjectWriter.name("profiling_traces_dir_path").value(paramILogger, this.profilingTracesDirPath);
    paramObjectWriter.name("is_profiling_enabled").value(paramILogger, Boolean.valueOf(this.isProfilingEnabled));
    paramObjectWriter.name("profiling_traces_hz").value(paramILogger, Integer.valueOf(this.profilingTracesHz));
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public static final class JsonKeys {
    public static final String PROFILE_SAMPLED = "profile_sampled";
    
    public static final String PROFILE_SAMPLE_RATE = "profile_sample_rate";
    
    public static final String TRACE_SAMPLED = "trace_sampled";
    
    public static final String TRACE_SAMPLE_RATE = "trace_sample_rate";
    
    public static final String PROFILING_TRACES_DIR_PATH = "profiling_traces_dir_path";
    
    public static final String IS_PROFILING_ENABLED = "is_profiling_enabled";
    
    public static final String PROFILING_TRACES_HZ = "profiling_traces_hz";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryAppStartProfilingOptions> {
    @NotNull
    public SentryAppStartProfilingOptions deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      SentryAppStartProfilingOptions sentryAppStartProfilingOptions = new SentryAppStartProfilingOptions();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Boolean bool1;
        Double double_1;
        Boolean bool2;
        Double double_2;
        String str2;
        Boolean bool3;
        Integer integer;
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "profile_sampled":
            bool1 = param1JsonObjectReader.nextBooleanOrNull();
            if (bool1 != null)
              sentryAppStartProfilingOptions.profileSampled = bool1.booleanValue(); 
            continue;
          case "profile_sample_rate":
            double_1 = param1JsonObjectReader.nextDoubleOrNull();
            if (double_1 != null)
              sentryAppStartProfilingOptions.profileSampleRate = double_1; 
            continue;
          case "trace_sampled":
            bool2 = param1JsonObjectReader.nextBooleanOrNull();
            if (bool2 != null)
              sentryAppStartProfilingOptions.traceSampled = bool2.booleanValue(); 
            continue;
          case "trace_sample_rate":
            double_2 = param1JsonObjectReader.nextDoubleOrNull();
            if (double_2 != null)
              sentryAppStartProfilingOptions.traceSampleRate = double_2; 
            continue;
          case "profiling_traces_dir_path":
            str2 = param1JsonObjectReader.nextStringOrNull();
            if (str2 != null)
              sentryAppStartProfilingOptions.profilingTracesDirPath = str2; 
            continue;
          case "is_profiling_enabled":
            bool3 = param1JsonObjectReader.nextBooleanOrNull();
            if (bool3 != null)
              sentryAppStartProfilingOptions.isProfilingEnabled = bool3.booleanValue(); 
            continue;
          case "profiling_traces_hz":
            integer = param1JsonObjectReader.nextIntegerOrNull();
            if (integer != null)
              sentryAppStartProfilingOptions.profilingTracesHz = integer.intValue(); 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str1);
      } 
      sentryAppStartProfilingOptions.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryAppStartProfilingOptions;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryAppStartProfilingOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */