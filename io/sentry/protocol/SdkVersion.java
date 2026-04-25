package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SdkVersion implements JsonUnknown, JsonSerializable {
  @NotNull
  private String name;
  
  @NotNull
  private String version;
  
  @Nullable
  private Set<SentryPackage> deserializedPackages;
  
  @Nullable
  private Set<String> deserializedIntegrations;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public SdkVersion(@NotNull String paramString1, @NotNull String paramString2) {
    this.name = (String)Objects.requireNonNull(paramString1, "name is required.");
    this.version = (String)Objects.requireNonNull(paramString2, "version is required.");
  }
  
  @NotNull
  public String getVersion() {
    return this.version;
  }
  
  public void setVersion(@NotNull String paramString) {
    this.version = (String)Objects.requireNonNull(paramString, "version is required.");
  }
  
  @NotNull
  public String getName() {
    return this.name;
  }
  
  public void setName(@NotNull String paramString) {
    this.name = (String)Objects.requireNonNull(paramString, "name is required.");
  }
  
  public void addPackage(@NotNull String paramString1, @NotNull String paramString2) {
    SentryIntegrationPackageStorage.getInstance().addPackage(paramString1, paramString2);
  }
  
  public void addIntegration(@NotNull String paramString) {
    SentryIntegrationPackageStorage.getInstance().addIntegration(paramString);
  }
  
  @Deprecated
  @Nullable
  public List<SentryPackage> getPackages() {
    Set<? extends SentryPackage> set = (this.deserializedPackages != null) ? this.deserializedPackages : SentryIntegrationPackageStorage.getInstance().getPackages();
    return new CopyOnWriteArrayList<>(set);
  }
  
  @NotNull
  public Set<SentryPackage> getPackageSet() {
    return (this.deserializedPackages != null) ? this.deserializedPackages : SentryIntegrationPackageStorage.getInstance().getPackages();
  }
  
  @Deprecated
  @Nullable
  public List<String> getIntegrations() {
    Set<? extends String> set = (this.deserializedIntegrations != null) ? this.deserializedIntegrations : SentryIntegrationPackageStorage.getInstance().getIntegrations();
    return new CopyOnWriteArrayList<>(set);
  }
  
  @NotNull
  public Set<String> getIntegrationSet() {
    return (this.deserializedIntegrations != null) ? this.deserializedIntegrations : SentryIntegrationPackageStorage.getInstance().getIntegrations();
  }
  
  @NotNull
  public static SdkVersion updateSdkVersion(@Nullable SdkVersion paramSdkVersion, @NotNull String paramString1, @NotNull String paramString2) {
    Objects.requireNonNull(paramString1, "name is required.");
    Objects.requireNonNull(paramString2, "version is required.");
    if (paramSdkVersion == null) {
      paramSdkVersion = new SdkVersion(paramString1, paramString2);
    } else {
      paramSdkVersion.setName(paramString1);
      paramSdkVersion.setVersion(paramString2);
    } 
    return paramSdkVersion;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SdkVersion sdkVersion = (SdkVersion)paramObject;
    return (this.name.equals(sdkVersion.name) && this.version.equals(sdkVersion.version));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.version });
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
    paramObjectWriter.name("name").value(this.name);
    paramObjectWriter.name("version").value(this.version);
    Set<SentryPackage> set = getPackageSet();
    Set<String> set1 = getIntegrationSet();
    if (!set.isEmpty())
      paramObjectWriter.name("packages").value(paramILogger, set); 
    if (!set1.isEmpty())
      paramObjectWriter.name("integrations").value(paramILogger, set1); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String NAME = "name";
    
    public static final String VERSION = "version";
    
    public static final String PACKAGES = "packages";
    
    public static final String INTEGRATIONS = "integrations";
  }
  
  public static final class Deserializer implements JsonDeserializer<SdkVersion> {
    @NotNull
    public SdkVersion deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      String str1 = null;
      String str2 = null;
      ArrayList<?> arrayList1 = new ArrayList();
      ArrayList<?> arrayList2 = new ArrayList();
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        List list1;
        List list2;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "name":
            str1 = param1JsonObjectReader.nextString();
            continue;
          case "version":
            str2 = param1JsonObjectReader.nextString();
            continue;
          case "packages":
            list1 = param1JsonObjectReader.nextListOrNull(param1ILogger, new SentryPackage.Deserializer());
            if (list1 != null)
              arrayList1.addAll(list1); 
            continue;
          case "integrations":
            list2 = (List)param1JsonObjectReader.nextObjectOrNull();
            if (list2 != null)
              arrayList2.addAll(list2); 
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (str1 == null) {
        String str = "Missing required field \"name\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      if (str2 == null) {
        String str = "Missing required field \"version\"";
        IllegalStateException illegalStateException = new IllegalStateException(str);
        param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
        throw illegalStateException;
      } 
      SdkVersion sdkVersion = new SdkVersion(str1, str2);
      sdkVersion.deserializedPackages = new CopyOnWriteArraySet(arrayList1);
      sdkVersion.deserializedIntegrations = new CopyOnWriteArraySet(arrayList2);
      sdkVersion.setUnknown((Map)hashMap);
      return sdkVersion;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SdkVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */