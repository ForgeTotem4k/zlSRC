package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Device implements JsonUnknown, JsonSerializable {
  public static final String TYPE = "device";
  
  @Nullable
  private String name;
  
  @Nullable
  private String manufacturer;
  
  @Nullable
  private String brand;
  
  @Nullable
  private String family;
  
  @Nullable
  private String model;
  
  @Nullable
  private String modelId;
  
  @Nullable
  private String[] archs;
  
  @Nullable
  private Float batteryLevel;
  
  @Nullable
  private Boolean charging;
  
  @Nullable
  private Boolean online;
  
  @Nullable
  private DeviceOrientation orientation;
  
  @Nullable
  private Boolean simulator;
  
  @Nullable
  private Long memorySize;
  
  @Nullable
  private Long freeMemory;
  
  @Nullable
  private Long usableMemory;
  
  @Nullable
  private Boolean lowMemory;
  
  @Nullable
  private Long storageSize;
  
  @Nullable
  private Long freeStorage;
  
  @Nullable
  private Long externalStorageSize;
  
  @Nullable
  private Long externalFreeStorage;
  
  @Nullable
  private Integer screenWidthPixels;
  
  @Nullable
  private Integer screenHeightPixels;
  
  @Nullable
  private Float screenDensity;
  
  @Nullable
  private Integer screenDpi;
  
  @Nullable
  private Date bootTime;
  
  @Nullable
  private TimeZone timezone;
  
  @Nullable
  private String id;
  
  @Deprecated
  @Nullable
  private String language;
  
  @Nullable
  private String locale;
  
  @Nullable
  private String connectionType;
  
  @Nullable
  private Float batteryTemperature;
  
  @Nullable
  private Integer processorCount;
  
  @Nullable
  private Double processorFrequency;
  
  @Nullable
  private String cpuDescription;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Device() {}
  
  Device(@NotNull Device paramDevice) {
    this.name = paramDevice.name;
    this.manufacturer = paramDevice.manufacturer;
    this.brand = paramDevice.brand;
    this.family = paramDevice.family;
    this.model = paramDevice.model;
    this.modelId = paramDevice.modelId;
    this.charging = paramDevice.charging;
    this.online = paramDevice.online;
    this.orientation = paramDevice.orientation;
    this.simulator = paramDevice.simulator;
    this.memorySize = paramDevice.memorySize;
    this.freeMemory = paramDevice.freeMemory;
    this.usableMemory = paramDevice.usableMemory;
    this.lowMemory = paramDevice.lowMemory;
    this.storageSize = paramDevice.storageSize;
    this.freeStorage = paramDevice.freeStorage;
    this.externalStorageSize = paramDevice.externalStorageSize;
    this.externalFreeStorage = paramDevice.externalFreeStorage;
    this.screenWidthPixels = paramDevice.screenWidthPixels;
    this.screenHeightPixels = paramDevice.screenHeightPixels;
    this.screenDensity = paramDevice.screenDensity;
    this.screenDpi = paramDevice.screenDpi;
    this.bootTime = paramDevice.bootTime;
    this.id = paramDevice.id;
    this.language = paramDevice.language;
    this.connectionType = paramDevice.connectionType;
    this.batteryTemperature = paramDevice.batteryTemperature;
    this.batteryLevel = paramDevice.batteryLevel;
    String[] arrayOfString = paramDevice.archs;
    this.archs = (arrayOfString != null) ? (String[])arrayOfString.clone() : null;
    this.locale = paramDevice.locale;
    TimeZone timeZone = paramDevice.timezone;
    this.timezone = (timeZone != null) ? (TimeZone)timeZone.clone() : null;
    this.processorCount = paramDevice.processorCount;
    this.processorFrequency = paramDevice.processorFrequency;
    this.cpuDescription = paramDevice.cpuDescription;
    this.unknown = CollectionUtils.newConcurrentHashMap(paramDevice.unknown);
  }
  
  @Nullable
  public String getName() {
    return this.name;
  }
  
  public void setName(@Nullable String paramString) {
    this.name = paramString;
  }
  
  @Nullable
  public String getManufacturer() {
    return this.manufacturer;
  }
  
  public void setManufacturer(@Nullable String paramString) {
    this.manufacturer = paramString;
  }
  
  @Nullable
  public String getBrand() {
    return this.brand;
  }
  
  public void setBrand(@Nullable String paramString) {
    this.brand = paramString;
  }
  
  @Nullable
  public String getFamily() {
    return this.family;
  }
  
  public void setFamily(@Nullable String paramString) {
    this.family = paramString;
  }
  
  @Nullable
  public String getModel() {
    return this.model;
  }
  
  public void setModel(@Nullable String paramString) {
    this.model = paramString;
  }
  
  @Nullable
  public String getModelId() {
    return this.modelId;
  }
  
  public void setModelId(@Nullable String paramString) {
    this.modelId = paramString;
  }
  
  @Nullable
  public Float getBatteryLevel() {
    return this.batteryLevel;
  }
  
  public void setBatteryLevel(@Nullable Float paramFloat) {
    this.batteryLevel = paramFloat;
  }
  
  @Nullable
  public Boolean isCharging() {
    return this.charging;
  }
  
  public void setCharging(@Nullable Boolean paramBoolean) {
    this.charging = paramBoolean;
  }
  
  @Nullable
  public Boolean isOnline() {
    return this.online;
  }
  
  public void setOnline(@Nullable Boolean paramBoolean) {
    this.online = paramBoolean;
  }
  
  @Nullable
  public DeviceOrientation getOrientation() {
    return this.orientation;
  }
  
  public void setOrientation(@Nullable DeviceOrientation paramDeviceOrientation) {
    this.orientation = paramDeviceOrientation;
  }
  
  @Nullable
  public Boolean isSimulator() {
    return this.simulator;
  }
  
  public void setSimulator(@Nullable Boolean paramBoolean) {
    this.simulator = paramBoolean;
  }
  
  @Nullable
  public Long getMemorySize() {
    return this.memorySize;
  }
  
  public void setMemorySize(@Nullable Long paramLong) {
    this.memorySize = paramLong;
  }
  
  @Nullable
  public Long getFreeMemory() {
    return this.freeMemory;
  }
  
  public void setFreeMemory(@Nullable Long paramLong) {
    this.freeMemory = paramLong;
  }
  
  @Nullable
  public Long getUsableMemory() {
    return this.usableMemory;
  }
  
  public void setUsableMemory(@Nullable Long paramLong) {
    this.usableMemory = paramLong;
  }
  
  @Nullable
  public Boolean isLowMemory() {
    return this.lowMemory;
  }
  
  public void setLowMemory(@Nullable Boolean paramBoolean) {
    this.lowMemory = paramBoolean;
  }
  
  @Nullable
  public Long getStorageSize() {
    return this.storageSize;
  }
  
  public void setStorageSize(@Nullable Long paramLong) {
    this.storageSize = paramLong;
  }
  
  @Nullable
  public Long getFreeStorage() {
    return this.freeStorage;
  }
  
  public void setFreeStorage(@Nullable Long paramLong) {
    this.freeStorage = paramLong;
  }
  
  @Nullable
  public Long getExternalStorageSize() {
    return this.externalStorageSize;
  }
  
  public void setExternalStorageSize(@Nullable Long paramLong) {
    this.externalStorageSize = paramLong;
  }
  
  @Nullable
  public Long getExternalFreeStorage() {
    return this.externalFreeStorage;
  }
  
  public void setExternalFreeStorage(@Nullable Long paramLong) {
    this.externalFreeStorage = paramLong;
  }
  
  @Nullable
  public Float getScreenDensity() {
    return this.screenDensity;
  }
  
  public void setScreenDensity(@Nullable Float paramFloat) {
    this.screenDensity = paramFloat;
  }
  
  @Nullable
  public Integer getScreenDpi() {
    return this.screenDpi;
  }
  
  public void setScreenDpi(@Nullable Integer paramInteger) {
    this.screenDpi = paramInteger;
  }
  
  @Nullable
  public Date getBootTime() {
    Date date = this.bootTime;
    return (date != null) ? (Date)date.clone() : null;
  }
  
  public void setBootTime(@Nullable Date paramDate) {
    this.bootTime = paramDate;
  }
  
  @Nullable
  public TimeZone getTimezone() {
    return this.timezone;
  }
  
  public void setTimezone(@Nullable TimeZone paramTimeZone) {
    this.timezone = paramTimeZone;
  }
  
  @Nullable
  public String[] getArchs() {
    return this.archs;
  }
  
  public void setArchs(@Nullable String[] paramArrayOfString) {
    this.archs = paramArrayOfString;
  }
  
  @Nullable
  public Integer getScreenWidthPixels() {
    return this.screenWidthPixels;
  }
  
  public void setScreenWidthPixels(@Nullable Integer paramInteger) {
    this.screenWidthPixels = paramInteger;
  }
  
  @Nullable
  public Integer getScreenHeightPixels() {
    return this.screenHeightPixels;
  }
  
  public void setScreenHeightPixels(@Nullable Integer paramInteger) {
    this.screenHeightPixels = paramInteger;
  }
  
  @Nullable
  public String getId() {
    return this.id;
  }
  
  public void setId(@Nullable String paramString) {
    this.id = paramString;
  }
  
  @Nullable
  public String getLanguage() {
    return this.language;
  }
  
  public void setLanguage(@Nullable String paramString) {
    this.language = paramString;
  }
  
  @Nullable
  public String getConnectionType() {
    return this.connectionType;
  }
  
  public void setConnectionType(@Nullable String paramString) {
    this.connectionType = paramString;
  }
  
  @Nullable
  public Float getBatteryTemperature() {
    return this.batteryTemperature;
  }
  
  public void setBatteryTemperature(@Nullable Float paramFloat) {
    this.batteryTemperature = paramFloat;
  }
  
  @Nullable
  public Integer getProcessorCount() {
    return this.processorCount;
  }
  
  public void setProcessorCount(@Nullable Integer paramInteger) {
    this.processorCount = paramInteger;
  }
  
  @Nullable
  public Double getProcessorFrequency() {
    return this.processorFrequency;
  }
  
  public void setProcessorFrequency(@Nullable Double paramDouble) {
    this.processorFrequency = paramDouble;
  }
  
  @Nullable
  public String getCpuDescription() {
    return this.cpuDescription;
  }
  
  public void setCpuDescription(@Nullable String paramString) {
    this.cpuDescription = paramString;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Device device = (Device)paramObject;
    return (Objects.equals(this.name, device.name) && Objects.equals(this.manufacturer, device.manufacturer) && Objects.equals(this.brand, device.brand) && Objects.equals(this.family, device.family) && Objects.equals(this.model, device.model) && Objects.equals(this.modelId, device.modelId) && Arrays.equals((Object[])this.archs, (Object[])device.archs) && Objects.equals(this.batteryLevel, device.batteryLevel) && Objects.equals(this.charging, device.charging) && Objects.equals(this.online, device.online) && this.orientation == device.orientation && Objects.equals(this.simulator, device.simulator) && Objects.equals(this.memorySize, device.memorySize) && Objects.equals(this.freeMemory, device.freeMemory) && Objects.equals(this.usableMemory, device.usableMemory) && Objects.equals(this.lowMemory, device.lowMemory) && Objects.equals(this.storageSize, device.storageSize) && Objects.equals(this.freeStorage, device.freeStorage) && Objects.equals(this.externalStorageSize, device.externalStorageSize) && Objects.equals(this.externalFreeStorage, device.externalFreeStorage) && Objects.equals(this.screenWidthPixels, device.screenWidthPixels) && Objects.equals(this.screenHeightPixels, device.screenHeightPixels) && Objects.equals(this.screenDensity, device.screenDensity) && Objects.equals(this.screenDpi, device.screenDpi) && Objects.equals(this.bootTime, device.bootTime) && Objects.equals(this.id, device.id) && Objects.equals(this.language, device.language) && Objects.equals(this.locale, device.locale) && Objects.equals(this.connectionType, device.connectionType) && Objects.equals(this.batteryTemperature, device.batteryTemperature) && Objects.equals(this.processorCount, device.processorCount) && Objects.equals(this.processorFrequency, device.processorFrequency) && Objects.equals(this.cpuDescription, device.cpuDescription));
  }
  
  public int hashCode() {
    null = Objects.hash(new Object[] { 
          this.name, this.manufacturer, this.brand, this.family, this.model, this.modelId, this.batteryLevel, this.charging, this.online, this.orientation, 
          this.simulator, this.memorySize, this.freeMemory, this.usableMemory, this.lowMemory, this.storageSize, this.freeStorage, this.externalStorageSize, this.externalFreeStorage, this.screenWidthPixels, 
          this.screenHeightPixels, this.screenDensity, this.screenDpi, this.bootTime, this.timezone, this.id, this.language, this.locale, this.connectionType, this.batteryTemperature, 
          this.processorCount, this.processorFrequency, this.cpuDescription });
    return 31 * null + Arrays.hashCode((Object[])this.archs);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.name != null)
      paramObjectWriter.name("name").value(this.name); 
    if (this.manufacturer != null)
      paramObjectWriter.name("manufacturer").value(this.manufacturer); 
    if (this.brand != null)
      paramObjectWriter.name("brand").value(this.brand); 
    if (this.family != null)
      paramObjectWriter.name("family").value(this.family); 
    if (this.model != null)
      paramObjectWriter.name("model").value(this.model); 
    if (this.modelId != null)
      paramObjectWriter.name("model_id").value(this.modelId); 
    if (this.archs != null)
      paramObjectWriter.name("archs").value(paramILogger, this.archs); 
    if (this.batteryLevel != null)
      paramObjectWriter.name("battery_level").value(this.batteryLevel); 
    if (this.charging != null)
      paramObjectWriter.name("charging").value(this.charging); 
    if (this.online != null)
      paramObjectWriter.name("online").value(this.online); 
    if (this.orientation != null)
      paramObjectWriter.name("orientation").value(paramILogger, this.orientation); 
    if (this.simulator != null)
      paramObjectWriter.name("simulator").value(this.simulator); 
    if (this.memorySize != null)
      paramObjectWriter.name("memory_size").value(this.memorySize); 
    if (this.freeMemory != null)
      paramObjectWriter.name("free_memory").value(this.freeMemory); 
    if (this.usableMemory != null)
      paramObjectWriter.name("usable_memory").value(this.usableMemory); 
    if (this.lowMemory != null)
      paramObjectWriter.name("low_memory").value(this.lowMemory); 
    if (this.storageSize != null)
      paramObjectWriter.name("storage_size").value(this.storageSize); 
    if (this.freeStorage != null)
      paramObjectWriter.name("free_storage").value(this.freeStorage); 
    if (this.externalStorageSize != null)
      paramObjectWriter.name("external_storage_size").value(this.externalStorageSize); 
    if (this.externalFreeStorage != null)
      paramObjectWriter.name("external_free_storage").value(this.externalFreeStorage); 
    if (this.screenWidthPixels != null)
      paramObjectWriter.name("screen_width_pixels").value(this.screenWidthPixels); 
    if (this.screenHeightPixels != null)
      paramObjectWriter.name("screen_height_pixels").value(this.screenHeightPixels); 
    if (this.screenDensity != null)
      paramObjectWriter.name("screen_density").value(this.screenDensity); 
    if (this.screenDpi != null)
      paramObjectWriter.name("screen_dpi").value(this.screenDpi); 
    if (this.bootTime != null)
      paramObjectWriter.name("boot_time").value(paramILogger, this.bootTime); 
    if (this.timezone != null)
      paramObjectWriter.name("timezone").value(paramILogger, this.timezone); 
    if (this.id != null)
      paramObjectWriter.name("id").value(this.id); 
    if (this.language != null)
      paramObjectWriter.name("language").value(this.language); 
    if (this.connectionType != null)
      paramObjectWriter.name("connection_type").value(this.connectionType); 
    if (this.batteryTemperature != null)
      paramObjectWriter.name("battery_temperature").value(this.batteryTemperature); 
    if (this.locale != null)
      paramObjectWriter.name("locale").value(this.locale); 
    if (this.processorCount != null)
      paramObjectWriter.name("processor_count").value(this.processorCount); 
    if (this.processorFrequency != null)
      paramObjectWriter.name("processor_frequency").value(this.processorFrequency); 
    if (this.cpuDescription != null)
      paramObjectWriter.name("cpu_description").value(this.cpuDescription); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @Nullable
  public String getLocale() {
    return this.locale;
  }
  
  public void setLocale(@Nullable String paramString) {
    this.locale = paramString;
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public enum DeviceOrientation implements JsonSerializable {
    PORTRAIT, LANDSCAPE;
    
    public void serialize(@NotNull ObjectWriter param1ObjectWriter, @NotNull ILogger param1ILogger) throws IOException {
      param1ObjectWriter.value(toString().toLowerCase(Locale.ROOT));
    }
    
    public static final class Deserializer implements JsonDeserializer<DeviceOrientation> {
      @NotNull
      public Device.DeviceOrientation deserialize(@NotNull JsonObjectReader param2JsonObjectReader, @NotNull ILogger param2ILogger) throws Exception {
        return Device.DeviceOrientation.valueOf(param2JsonObjectReader.nextString().toUpperCase(Locale.ROOT));
      }
      
      static {
      
      }
    }
  }
  
  public static final class JsonKeys {
    public static final String NAME = "name";
    
    public static final String MANUFACTURER = "manufacturer";
    
    public static final String BRAND = "brand";
    
    public static final String FAMILY = "family";
    
    public static final String MODEL = "model";
    
    public static final String MODEL_ID = "model_id";
    
    public static final String ARCHS = "archs";
    
    public static final String BATTERY_LEVEL = "battery_level";
    
    public static final String CHARGING = "charging";
    
    public static final String ONLINE = "online";
    
    public static final String ORIENTATION = "orientation";
    
    public static final String SIMULATOR = "simulator";
    
    public static final String MEMORY_SIZE = "memory_size";
    
    public static final String FREE_MEMORY = "free_memory";
    
    public static final String USABLE_MEMORY = "usable_memory";
    
    public static final String LOW_MEMORY = "low_memory";
    
    public static final String STORAGE_SIZE = "storage_size";
    
    public static final String FREE_STORAGE = "free_storage";
    
    public static final String EXTERNAL_STORAGE_SIZE = "external_storage_size";
    
    public static final String EXTERNAL_FREE_STORAGE = "external_free_storage";
    
    public static final String SCREEN_WIDTH_PIXELS = "screen_width_pixels";
    
    public static final String SCREEN_HEIGHT_PIXELS = "screen_height_pixels";
    
    public static final String SCREEN_DENSITY = "screen_density";
    
    public static final String SCREEN_DPI = "screen_dpi";
    
    public static final String BOOT_TIME = "boot_time";
    
    public static final String TIMEZONE = "timezone";
    
    public static final String ID = "id";
    
    public static final String LANGUAGE = "language";
    
    public static final String CONNECTION_TYPE = "connection_type";
    
    public static final String BATTERY_TEMPERATURE = "battery_temperature";
    
    public static final String LOCALE = "locale";
    
    public static final String PROCESSOR_COUNT = "processor_count";
    
    public static final String CPU_DESCRIPTION = "cpu_description";
    
    public static final String PROCESSOR_FREQUENCY = "processor_frequency";
  }
  
  public static final class Deserializer implements JsonDeserializer<Device> {
    @NotNull
    public Device deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Device device = new Device();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List list;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            device.name = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "manufacturer":
            device.manufacturer = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "brand":
            device.brand = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "family":
            device.family = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "model":
            device.model = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "model_id":
            device.modelId = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "archs":
            list = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list != null) {
              String[] arrayOfString = new String[list.size()];
              list.toArray((Object[])arrayOfString);
              device.archs = arrayOfString;
            } 
            continue;
          case "battery_level":
            device.batteryLevel = param1JsonObjectReader.nextFloatOrNull();
            continue;
          case "charging":
            device.charging = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "online":
            device.online = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "orientation":
            device.orientation = (Device.DeviceOrientation)param1JsonObjectReader.nextOrNull(param1ILogger, new Device.DeviceOrientation.Deserializer());
            continue;
          case "simulator":
            device.simulator = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "memory_size":
            device.memorySize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "free_memory":
            device.freeMemory = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "usable_memory":
            device.usableMemory = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "low_memory":
            device.lowMemory = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "storage_size":
            device.storageSize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "free_storage":
            device.freeStorage = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "external_storage_size":
            device.externalStorageSize = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "external_free_storage":
            device.externalFreeStorage = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "screen_width_pixels":
            device.screenWidthPixels = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "screen_height_pixels":
            device.screenHeightPixels = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "screen_density":
            device.screenDensity = param1JsonObjectReader.nextFloatOrNull();
            continue;
          case "screen_dpi":
            device.screenDpi = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "boot_time":
            if (param1JsonObjectReader.peek() == JsonToken.STRING)
              device.bootTime = param1JsonObjectReader.nextDateOrNull(param1ILogger); 
            continue;
          case "timezone":
            device.timezone = param1JsonObjectReader.nextTimeZoneOrNull(param1ILogger);
            continue;
          case "id":
            device.id = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "language":
            device.language = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "connection_type":
            device.connectionType = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "battery_temperature":
            device.batteryTemperature = param1JsonObjectReader.nextFloatOrNull();
            continue;
          case "locale":
            device.locale = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "processor_count":
            device.processorCount = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "processor_frequency":
            device.processorFrequency = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "cpu_description":
            device.cpuDescription = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      device.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return device;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\Device.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */