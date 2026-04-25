package io.sentry;

import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ProfilingTraceData implements JsonUnknown, JsonSerializable {
  private static final String DEFAULT_ENVIRONMENT = "production";
  
  @Internal
  public static final String TRUNCATION_REASON_NORMAL = "normal";
  
  @Internal
  public static final String TRUNCATION_REASON_TIMEOUT = "timeout";
  
  @Internal
  public static final String TRUNCATION_REASON_BACKGROUNDED = "backgrounded";
  
  @NotNull
  private final File traceFile;
  
  @NotNull
  private final Callable<List<Integer>> deviceCpuFrequenciesReader;
  
  private int androidApiLevel;
  
  @NotNull
  private String deviceLocale;
  
  @NotNull
  private String deviceManufacturer;
  
  @NotNull
  private String deviceModel;
  
  @NotNull
  private String deviceOsBuildNumber;
  
  @NotNull
  private String deviceOsName;
  
  @NotNull
  private String deviceOsVersion;
  
  private boolean deviceIsEmulator;
  
  @NotNull
  private String cpuArchitecture;
  
  @NotNull
  private List<Integer> deviceCpuFrequencies = new ArrayList<>();
  
  @NotNull
  private String devicePhysicalMemoryBytes;
  
  @NotNull
  private String platform;
  
  @NotNull
  private String buildId;
  
  @NotNull
  private List<ProfilingTransactionData> transactions;
  
  @NotNull
  private String transactionName;
  
  @NotNull
  private String durationNs;
  
  @NotNull
  private String versionCode;
  
  @NotNull
  private String release;
  
  @NotNull
  private String transactionId;
  
  @NotNull
  private String traceId;
  
  @NotNull
  private String profileId;
  
  @NotNull
  private String environment;
  
  @NotNull
  private String truncationReason;
  
  @NotNull
  private Date timestamp;
  
  @NotNull
  private final Map<String, ProfileMeasurement> measurementsMap;
  
  @Nullable
  private String sampledProfile = null;
  
  @Nullable
  private Map<String, Object> unknown;
  
  private ProfilingTraceData() {
    this(new File("dummy"), NoOpTransaction.getInstance());
  }
  
  public ProfilingTraceData(@NotNull File paramFile, @NotNull ITransaction paramITransaction) {
    this(paramFile, DateUtils.getCurrentDateTime(), new ArrayList<>(), paramITransaction.getName(), paramITransaction.getEventId().toString(), paramITransaction.getSpanContext().getTraceId().toString(), "0", 0, "", () -> new ArrayList(), null, null, null, null, null, null, null, null, "normal", new HashMap<>());
  }
  
  public ProfilingTraceData(@NotNull File paramFile, @NotNull Date paramDate, @NotNull List<ProfilingTransactionData> paramList, @NotNull String paramString1, @NotNull String paramString2, @NotNull String paramString3, @NotNull String paramString4, int paramInt, @NotNull String paramString5, @NotNull Callable<List<Integer>> paramCallable, @Nullable String paramString6, @Nullable String paramString7, @Nullable String paramString8, @Nullable Boolean paramBoolean, @Nullable String paramString9, @Nullable String paramString10, @Nullable String paramString11, @Nullable String paramString12, @NotNull String paramString13, @NotNull Map<String, ProfileMeasurement> paramMap) {
    this.traceFile = paramFile;
    this.timestamp = paramDate;
    this.cpuArchitecture = paramString5;
    this.deviceCpuFrequenciesReader = paramCallable;
    this.androidApiLevel = paramInt;
    this.deviceLocale = Locale.getDefault().toString();
    this.deviceManufacturer = (paramString6 != null) ? paramString6 : "";
    this.deviceModel = (paramString7 != null) ? paramString7 : "";
    this.deviceOsVersion = (paramString8 != null) ? paramString8 : "";
    this.deviceIsEmulator = (paramBoolean != null) ? paramBoolean.booleanValue() : false;
    this.devicePhysicalMemoryBytes = (paramString9 != null) ? paramString9 : "0";
    this.deviceOsBuildNumber = "";
    this.deviceOsName = "android";
    this.platform = "android";
    this.buildId = (paramString10 != null) ? paramString10 : "";
    this.transactions = paramList;
    this.transactionName = paramString1;
    this.durationNs = paramString4;
    this.versionCode = "";
    this.release = (paramString11 != null) ? paramString11 : "";
    this.transactionId = paramString2;
    this.traceId = paramString3;
    this.profileId = UUID.randomUUID().toString();
    this.environment = (paramString12 != null) ? paramString12 : "production";
    this.truncationReason = paramString13;
    if (!isTruncationReasonValid())
      this.truncationReason = "normal"; 
    this.measurementsMap = paramMap;
  }
  
  private boolean isTruncationReasonValid() {
    return (this.truncationReason.equals("normal") || this.truncationReason.equals("timeout") || this.truncationReason.equals("backgrounded"));
  }
  
  @NotNull
  public File getTraceFile() {
    return this.traceFile;
  }
  
  public int getAndroidApiLevel() {
    return this.androidApiLevel;
  }
  
  @NotNull
  public String getCpuArchitecture() {
    return this.cpuArchitecture;
  }
  
  @NotNull
  public String getDeviceLocale() {
    return this.deviceLocale;
  }
  
  @NotNull
  public String getDeviceManufacturer() {
    return this.deviceManufacturer;
  }
  
  @NotNull
  public String getDeviceModel() {
    return this.deviceModel;
  }
  
  @NotNull
  public String getDeviceOsBuildNumber() {
    return this.deviceOsBuildNumber;
  }
  
  @NotNull
  public String getDeviceOsName() {
    return this.deviceOsName;
  }
  
  @NotNull
  public String getDeviceOsVersion() {
    return this.deviceOsVersion;
  }
  
  public boolean isDeviceIsEmulator() {
    return this.deviceIsEmulator;
  }
  
  @NotNull
  public String getPlatform() {
    return this.platform;
  }
  
  @NotNull
  public String getBuildId() {
    return this.buildId;
  }
  
  @NotNull
  public String getTransactionName() {
    return this.transactionName;
  }
  
  @NotNull
  public String getRelease() {
    return this.release;
  }
  
  @NotNull
  public String getTransactionId() {
    return this.transactionId;
  }
  
  @NotNull
  public List<ProfilingTransactionData> getTransactions() {
    return this.transactions;
  }
  
  @NotNull
  public String getTraceId() {
    return this.traceId;
  }
  
  @NotNull
  public String getProfileId() {
    return this.profileId;
  }
  
  @NotNull
  public String getEnvironment() {
    return this.environment;
  }
  
  @Nullable
  public String getSampledProfile() {
    return this.sampledProfile;
  }
  
  @NotNull
  public String getDurationNs() {
    return this.durationNs;
  }
  
  @NotNull
  public List<Integer> getDeviceCpuFrequencies() {
    return this.deviceCpuFrequencies;
  }
  
  @NotNull
  public String getDevicePhysicalMemoryBytes() {
    return this.devicePhysicalMemoryBytes;
  }
  
  @NotNull
  public String getTruncationReason() {
    return this.truncationReason;
  }
  
  @NotNull
  public Date getTimestamp() {
    return this.timestamp;
  }
  
  @NotNull
  public Map<String, ProfileMeasurement> getMeasurementsMap() {
    return this.measurementsMap;
  }
  
  public void setAndroidApiLevel(int paramInt) {
    this.androidApiLevel = paramInt;
  }
  
  public void setCpuArchitecture(@NotNull String paramString) {
    this.cpuArchitecture = paramString;
  }
  
  public void setDeviceLocale(@NotNull String paramString) {
    this.deviceLocale = paramString;
  }
  
  public void setDeviceManufacturer(@NotNull String paramString) {
    this.deviceManufacturer = paramString;
  }
  
  public void setDeviceModel(@NotNull String paramString) {
    this.deviceModel = paramString;
  }
  
  public void setDeviceOsBuildNumber(@NotNull String paramString) {
    this.deviceOsBuildNumber = paramString;
  }
  
  public void setDeviceOsVersion(@NotNull String paramString) {
    this.deviceOsVersion = paramString;
  }
  
  public void setDeviceIsEmulator(boolean paramBoolean) {
    this.deviceIsEmulator = paramBoolean;
  }
  
  public void setDeviceCpuFrequencies(@NotNull List<Integer> paramList) {
    this.deviceCpuFrequencies = paramList;
  }
  
  public void setDevicePhysicalMemoryBytes(@NotNull String paramString) {
    this.devicePhysicalMemoryBytes = paramString;
  }
  
  public void setTimestamp(@NotNull Date paramDate) {
    this.timestamp = paramDate;
  }
  
  public void setTruncationReason(@NotNull String paramString) {
    this.truncationReason = paramString;
  }
  
  public void setTransactions(@NotNull List<ProfilingTransactionData> paramList) {
    this.transactions = paramList;
  }
  
  public void setBuildId(@NotNull String paramString) {
    this.buildId = paramString;
  }
  
  public void setTransactionName(@NotNull String paramString) {
    this.transactionName = paramString;
  }
  
  public void setDurationNs(@NotNull String paramString) {
    this.durationNs = paramString;
  }
  
  public void setRelease(@NotNull String paramString) {
    this.release = paramString;
  }
  
  public void setTransactionId(@NotNull String paramString) {
    this.transactionId = paramString;
  }
  
  public void setTraceId(@NotNull String paramString) {
    this.traceId = paramString;
  }
  
  public void setProfileId(@NotNull String paramString) {
    this.profileId = paramString;
  }
  
  public void setEnvironment(@NotNull String paramString) {
    this.environment = paramString;
  }
  
  public void setSampledProfile(@Nullable String paramString) {
    this.sampledProfile = paramString;
  }
  
  public void readDeviceCpuFrequencies() {
    try {
      this.deviceCpuFrequencies = this.deviceCpuFrequenciesReader.call();
    } catch (Throwable throwable) {}
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("android_api_level").value(paramILogger, Integer.valueOf(this.androidApiLevel));
    paramObjectWriter.name("device_locale").value(paramILogger, this.deviceLocale);
    paramObjectWriter.name("device_manufacturer").value(this.deviceManufacturer);
    paramObjectWriter.name("device_model").value(this.deviceModel);
    paramObjectWriter.name("device_os_build_number").value(this.deviceOsBuildNumber);
    paramObjectWriter.name("device_os_name").value(this.deviceOsName);
    paramObjectWriter.name("device_os_version").value(this.deviceOsVersion);
    paramObjectWriter.name("device_is_emulator").value(this.deviceIsEmulator);
    paramObjectWriter.name("architecture").value(paramILogger, this.cpuArchitecture);
    paramObjectWriter.name("device_cpu_frequencies").value(paramILogger, this.deviceCpuFrequencies);
    paramObjectWriter.name("device_physical_memory_bytes").value(this.devicePhysicalMemoryBytes);
    paramObjectWriter.name("platform").value(this.platform);
    paramObjectWriter.name("build_id").value(this.buildId);
    paramObjectWriter.name("transaction_name").value(this.transactionName);
    paramObjectWriter.name("duration_ns").value(this.durationNs);
    paramObjectWriter.name("version_name").value(this.release);
    paramObjectWriter.name("version_code").value(this.versionCode);
    if (!this.transactions.isEmpty())
      paramObjectWriter.name("transactions").value(paramILogger, this.transactions); 
    paramObjectWriter.name("transaction_id").value(this.transactionId);
    paramObjectWriter.name("trace_id").value(this.traceId);
    paramObjectWriter.name("profile_id").value(this.profileId);
    paramObjectWriter.name("environment").value(this.environment);
    paramObjectWriter.name("truncation_reason").value(this.truncationReason);
    if (this.sampledProfile != null)
      paramObjectWriter.name("sampled_profile").value(this.sampledProfile); 
    paramObjectWriter.name("measurements").value(paramILogger, this.measurementsMap);
    paramObjectWriter.name("timestamp").value(paramILogger, this.timestamp);
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
    public static final String ANDROID_API_LEVEL = "android_api_level";
    
    public static final String DEVICE_LOCALE = "device_locale";
    
    public static final String DEVICE_MANUFACTURER = "device_manufacturer";
    
    public static final String DEVICE_MODEL = "device_model";
    
    public static final String DEVICE_OS_BUILD_NUMBER = "device_os_build_number";
    
    public static final String DEVICE_OS_NAME = "device_os_name";
    
    public static final String DEVICE_OS_VERSION = "device_os_version";
    
    public static final String DEVICE_IS_EMULATOR = "device_is_emulator";
    
    public static final String ARCHITECTURE = "architecture";
    
    public static final String DEVICE_CPU_FREQUENCIES = "device_cpu_frequencies";
    
    public static final String DEVICE_PHYSICAL_MEMORY_BYTES = "device_physical_memory_bytes";
    
    public static final String PLATFORM = "platform";
    
    public static final String BUILD_ID = "build_id";
    
    public static final String TRANSACTION_NAME = "transaction_name";
    
    public static final String DURATION_NS = "duration_ns";
    
    public static final String RELEASE = "version_name";
    
    public static final String VERSION_CODE = "version_code";
    
    public static final String TRANSACTION_LIST = "transactions";
    
    public static final String TRANSACTION_ID = "transaction_id";
    
    public static final String TRACE_ID = "trace_id";
    
    public static final String PROFILE_ID = "profile_id";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String SAMPLED_PROFILE = "sampled_profile";
    
    public static final String TRUNCATION_REASON = "truncation_reason";
    
    public static final String MEASUREMENTS = "measurements";
    
    public static final String TIMESTAMP = "timestamp";
  }
  
  public static final class Deserializer implements JsonDeserializer<ProfilingTraceData> {
    @NotNull
    public ProfilingTraceData deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      ProfilingTraceData profilingTraceData = new ProfilingTraceData();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Integer integer;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        Boolean bool;
        String str8;
        List list;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        String str14;
        String str15;
        List<?> list1;
        String str16;
        String str17;
        String str18;
        String str19;
        String str20;
        Map<String, ?> map;
        Date date;
        String str21;
        String str1 = param1JsonObjectReader.nextName();
        switch (str1) {
          case "android_api_level":
            integer = param1JsonObjectReader.nextIntegerOrNull();
            if (integer != null)
              profilingTraceData.androidApiLevel = integer.intValue(); 
            continue;
          case "device_locale":
            str2 = param1JsonObjectReader.nextStringOrNull();
            if (str2 != null)
              profilingTraceData.deviceLocale = str2; 
            continue;
          case "device_manufacturer":
            str3 = param1JsonObjectReader.nextStringOrNull();
            if (str3 != null)
              profilingTraceData.deviceManufacturer = str3; 
            continue;
          case "device_model":
            str4 = param1JsonObjectReader.nextStringOrNull();
            if (str4 != null)
              profilingTraceData.deviceModel = str4; 
            continue;
          case "device_os_build_number":
            str5 = param1JsonObjectReader.nextStringOrNull();
            if (str5 != null)
              profilingTraceData.deviceOsBuildNumber = str5; 
            continue;
          case "device_os_name":
            str6 = param1JsonObjectReader.nextStringOrNull();
            if (str6 != null)
              profilingTraceData.deviceOsName = str6; 
            continue;
          case "device_os_version":
            str7 = param1JsonObjectReader.nextStringOrNull();
            if (str7 != null)
              profilingTraceData.deviceOsVersion = str7; 
            continue;
          case "device_is_emulator":
            bool = param1JsonObjectReader.nextBooleanOrNull();
            if (bool != null)
              profilingTraceData.deviceIsEmulator = bool.booleanValue(); 
            continue;
          case "architecture":
            str8 = param1JsonObjectReader.nextStringOrNull();
            if (str8 != null)
              profilingTraceData.cpuArchitecture = str8; 
            continue;
          case "device_cpu_frequencies":
            list = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list != null)
              profilingTraceData.deviceCpuFrequencies = list; 
            continue;
          case "device_physical_memory_bytes":
            str9 = param1JsonObjectReader.nextStringOrNull();
            if (str9 != null)
              profilingTraceData.devicePhysicalMemoryBytes = str9; 
            continue;
          case "platform":
            str10 = param1JsonObjectReader.nextStringOrNull();
            if (str10 != null)
              profilingTraceData.platform = str10; 
            continue;
          case "build_id":
            str11 = param1JsonObjectReader.nextStringOrNull();
            if (str11 != null)
              profilingTraceData.buildId = str11; 
            continue;
          case "transaction_name":
            str12 = param1JsonObjectReader.nextStringOrNull();
            if (str12 != null)
              profilingTraceData.transactionName = str12; 
            continue;
          case "duration_ns":
            str13 = param1JsonObjectReader.nextStringOrNull();
            if (str13 != null)
              profilingTraceData.durationNs = str13; 
            continue;
          case "version_code":
            str14 = param1JsonObjectReader.nextStringOrNull();
            if (str14 != null)
              profilingTraceData.versionCode = str14; 
            continue;
          case "version_name":
            str15 = param1JsonObjectReader.nextStringOrNull();
            if (str15 != null)
              profilingTraceData.release = str15; 
            continue;
          case "transactions":
            list1 = param1JsonObjectReader.nextListOrNull(param1ILogger, new ProfilingTransactionData.Deserializer());
            if (list1 != null)
              profilingTraceData.transactions.addAll(list1); 
            continue;
          case "transaction_id":
            str16 = param1JsonObjectReader.nextStringOrNull();
            if (str16 != null)
              profilingTraceData.transactionId = str16; 
            continue;
          case "trace_id":
            str17 = param1JsonObjectReader.nextStringOrNull();
            if (str17 != null)
              profilingTraceData.traceId = str17; 
            continue;
          case "profile_id":
            str18 = param1JsonObjectReader.nextStringOrNull();
            if (str18 != null)
              profilingTraceData.profileId = str18; 
            continue;
          case "environment":
            str19 = param1JsonObjectReader.nextStringOrNull();
            if (str19 != null)
              profilingTraceData.environment = str19; 
            continue;
          case "truncation_reason":
            str20 = param1JsonObjectReader.nextStringOrNull();
            if (str20 != null)
              profilingTraceData.truncationReason = str20; 
            continue;
          case "measurements":
            map = param1JsonObjectReader.nextMapOrNull(param1ILogger, (JsonDeserializer<?>)new ProfileMeasurement.Deserializer());
            if (map != null)
              profilingTraceData.measurementsMap.putAll(map); 
            continue;
          case "timestamp":
            date = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            if (date != null)
              profilingTraceData.timestamp = date; 
            continue;
          case "sampled_profile":
            str21 = param1JsonObjectReader.nextStringOrNull();
            if (str21 != null)
              profilingTraceData.sampledProfile = str21; 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str1);
      } 
      profilingTraceData.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return profilingTraceData;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ProfilingTraceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */