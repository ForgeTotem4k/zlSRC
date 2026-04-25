package pro.gravit.launcher.base.profiles;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import pro.gravit.launcher.base.profiles.optional.OptionalDepend;
import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.hasher.FileNameMatcher;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.VerifyHelper;
import pro.gravit.utils.launch.LaunchOptions;

public final class ClientProfile implements Comparable<ClientProfile> {
  private static final FileNameMatcher ASSET_MATCHER = new FileNameMatcher(new String[0], new String[] { "indexes", "objects" }, new String[0]);
  
  @LauncherNetworkAPI
  private String title;
  
  @LauncherNetworkAPI
  private UUID uuid;
  
  @LauncherNetworkAPI
  private Version version;
  
  @LauncherNetworkAPI
  private String info;
  
  @LauncherNetworkAPI
  private String dir;
  
  @LauncherNetworkAPI
  private int sortIndex;
  
  @LauncherNetworkAPI
  private String assetIndex;
  
  @LauncherNetworkAPI
  private String assetDir;
  
  @LauncherNetworkAPI
  private List<String> update;
  
  @LauncherNetworkAPI
  private List<String> updateExclusions;
  
  @LauncherNetworkAPI
  private List<String> updateVerify;
  
  @LauncherNetworkAPI
  private Set<OptionalFile> updateOptional;
  
  @LauncherNetworkAPI
  private List<String> jvmArgs;
  
  @LauncherNetworkAPI
  private List<String> classPath;
  
  @LauncherNetworkAPI
  private List<String> altClassPath;
  
  @LauncherNetworkAPI
  private List<String> clientArgs;
  
  @LauncherNetworkAPI
  private List<String> compatClasses;
  
  @LauncherNetworkAPI
  private List<String> loadNatives;
  
  @LauncherNetworkAPI
  private Map<String, String> properties;
  
  @LauncherNetworkAPI
  private List<ServerProfile> servers;
  
  @LauncherNetworkAPI
  private ClassLoaderConfig classLoaderConfig;
  
  @LauncherNetworkAPI
  private List<CompatibilityFlags> flags;
  
  @LauncherNetworkAPI
  private int recommendJavaVersion = 8;
  
  @LauncherNetworkAPI
  private int minJavaVersion = 8;
  
  @LauncherNetworkAPI
  private int maxJavaVersion = 999;
  
  @LauncherNetworkAPI
  private ProfileDefaultSettings settings = new ProfileDefaultSettings();
  
  @LauncherNetworkAPI
  private boolean limited;
  
  @LauncherNetworkAPI
  private String mainClass;
  
  @LauncherNetworkAPI
  private String mainModule;
  
  @LauncherNetworkAPI
  private LaunchOptions.ModuleConf moduleConf;
  
  public ClientProfile(String paramString1, UUID paramUUID, Version paramVersion, String paramString2, String paramString3, int paramInt1, String paramString4, String paramString5, List<String> paramList1, List<String> paramList2, List<String> paramList3, Set<OptionalFile> paramSet, List<String> paramList4, List<String> paramList5, List<String> paramList6, List<String> paramList7, List<String> paramList8, List<String> paramList9, Map<String, String> paramMap, List<ServerProfile> paramList, ClassLoaderConfig paramClassLoaderConfig, List<CompatibilityFlags> paramList10, int paramInt2, int paramInt3, int paramInt4, ProfileDefaultSettings paramProfileDefaultSettings, boolean paramBoolean, String paramString6, String paramString7, LaunchOptions.ModuleConf paramModuleConf) {
    this.title = paramString1;
    this.uuid = paramUUID;
    this.version = paramVersion;
    this.info = paramString2;
    this.dir = paramString3;
    this.sortIndex = paramInt1;
    this.assetIndex = paramString4;
    this.assetDir = paramString5;
    this.update = paramList1;
    this.updateExclusions = paramList2;
    this.updateVerify = paramList3;
    this.updateOptional = paramSet;
    this.jvmArgs = paramList4;
    this.classPath = paramList5;
    this.altClassPath = paramList6;
    this.clientArgs = paramList7;
    this.compatClasses = paramList8;
    this.loadNatives = paramList9;
    this.properties = paramMap;
    this.servers = paramList;
    this.classLoaderConfig = paramClassLoaderConfig;
    this.flags = paramList10;
    this.recommendJavaVersion = paramInt2;
    this.minJavaVersion = paramInt3;
    this.maxJavaVersion = paramInt4;
    this.settings = paramProfileDefaultSettings;
    this.limited = paramBoolean;
    this.mainClass = paramString6;
    this.mainModule = paramString7;
    this.moduleConf = paramModuleConf;
  }
  
  public ServerProfile getDefaultServerProfile() {
    for (ServerProfile serverProfile : this.servers) {
      if (serverProfile.isDefault)
        return serverProfile; 
    } 
    return null;
  }
  
  public int compareTo(ClientProfile paramClientProfile) {
    return Integer.compare(getSortIndex(), paramClientProfile.getSortIndex());
  }
  
  public String getAssetIndex() {
    return this.assetIndex;
  }
  
  public FileNameMatcher getAssetUpdateMatcher() {
    return (getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_7_10) >= 0) ? ASSET_MATCHER : null;
  }
  
  public List<String> getClassPath() {
    return Collections.unmodifiableList(this.classPath);
  }
  
  public List<String> getAlternativeClassPath() {
    return Collections.unmodifiableList(this.altClassPath);
  }
  
  public List<String> getClientArgs() {
    return Collections.unmodifiableList(this.clientArgs);
  }
  
  public String getDir() {
    return this.dir;
  }
  
  public String getAssetDir() {
    return this.assetDir;
  }
  
  public List<String> getUpdateExclusions() {
    return Collections.unmodifiableList(this.updateExclusions);
  }
  
  public List<String> getUpdate() {
    return Collections.unmodifiableList(this.update);
  }
  
  public List<String> getUpdateVerify() {
    return Collections.unmodifiableList(this.updateVerify);
  }
  
  public FileNameMatcher getClientUpdateMatcher() {
    String[] arrayOfString1 = this.update.<String>toArray(new String[0]);
    String[] arrayOfString2 = this.updateVerify.<String>toArray(new String[0]);
    List<String> list = this.updateExclusions;
    String[] arrayOfString3 = list.<String>toArray(new String[0]);
    return new FileNameMatcher(arrayOfString1, arrayOfString2, arrayOfString3);
  }
  
  public List<String> getJvmArgs() {
    return Collections.unmodifiableList(this.jvmArgs);
  }
  
  public String getMainClass() {
    return this.mainClass;
  }
  
  public String getMainModule() {
    return this.mainModule;
  }
  
  public LaunchOptions.ModuleConf getModuleConf() {
    return this.moduleConf;
  }
  
  public List<ServerProfile> getServers() {
    return this.servers;
  }
  
  public String getServerAddress() {
    ServerProfile serverProfile = getDefaultServerProfile();
    return (serverProfile == null) ? "localhost" : serverProfile.serverAddress;
  }
  
  public Set<OptionalFile> getOptional() {
    return this.updateOptional;
  }
  
  public int getRecommendJavaVersion() {
    return this.recommendJavaVersion;
  }
  
  public int getMinJavaVersion() {
    return this.minJavaVersion;
  }
  
  public int getMaxJavaVersion() {
    return this.maxJavaVersion;
  }
  
  public ProfileDefaultSettings getSettings() {
    return this.settings;
  }
  
  public List<String> getLoadNatives() {
    return this.loadNatives;
  }
  
  public void updateOptionalGraph() {
    for (OptionalFile optionalFile : this.updateOptional) {
      if (optionalFile.dependenciesFile != null) {
        optionalFile.dependencies = new OptionalFile[optionalFile.dependenciesFile.length];
        for (byte b = 0; b < optionalFile.dependenciesFile.length; b++)
          optionalFile.dependencies[b] = getOptionalFile((optionalFile.dependenciesFile[b]).name); 
      } 
      if (optionalFile.conflictFile != null) {
        optionalFile.conflict = new OptionalFile[optionalFile.conflictFile.length];
        for (byte b = 0; b < optionalFile.conflictFile.length; b++)
          optionalFile.conflict[b] = getOptionalFile((optionalFile.conflictFile[b]).name); 
      } 
      if (optionalFile.groupFile != null) {
        optionalFile.group = new OptionalFile[optionalFile.groupFile.length];
        for (byte b = 0; b < optionalFile.groupFile.length; b++)
          optionalFile.group[b] = getOptionalFile((optionalFile.groupFile[b]).name); 
      } 
    } 
  }
  
  public OptionalFile getOptionalFile(String paramString) {
    for (OptionalFile optionalFile : this.updateOptional) {
      if (optionalFile.name.equals(paramString))
        return optionalFile; 
    } 
    return null;
  }
  
  public int getServerPort() {
    ServerProfile serverProfile = getDefaultServerProfile();
    return (serverProfile == null) ? 25565 : serverProfile.serverPort;
  }
  
  public int getSortIndex() {
    return this.sortIndex;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public String getInfo() {
    return this.info;
  }
  
  public Version getVersion() {
    return this.version;
  }
  
  @Deprecated
  public boolean isUpdateFastCheck() {
    return true;
  }
  
  public String toString() {
    return String.format("%s (%s)", new Object[] { this.title, this.uuid });
  }
  
  public UUID getUUID() {
    return this.uuid;
  }
  
  public boolean hasFlag(CompatibilityFlags paramCompatibilityFlags) {
    return this.flags.contains(paramCompatibilityFlags);
  }
  
  public void verify() {
    getVersion();
    IOHelper.verifyFileName(getAssetIndex());
    VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Profile title can't be empty");
    VerifyHelper.verify(getInfo(), VerifyHelper.NOT_EMPTY, "Profile info can't be empty");
    VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Main class can't be empty");
    if (getUUID() == null)
      throw new IllegalArgumentException("Profile UUID can't be null"); 
    for (String str : this.update) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in update"); 
    } 
    for (String str : this.updateVerify) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in updateVerify"); 
    } 
    for (String str : this.updateExclusions) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in updateExclusions"); 
    } 
    for (String str : this.classPath) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in classPath"); 
    } 
    for (String str : this.jvmArgs) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in jvmArgs"); 
    } 
    for (String str : this.clientArgs) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in clientArgs"); 
    } 
    for (String str : this.compatClasses) {
      if (str == null)
        throw new IllegalArgumentException("Found null entry in compatClasses"); 
    } 
    for (OptionalFile optionalFile : this.updateOptional) {
      if (optionalFile == null)
        throw new IllegalArgumentException("Found null entry in updateOptional"); 
      if (optionalFile.name == null)
        throw new IllegalArgumentException("Optional: name must not be null"); 
      if (optionalFile.conflictFile != null)
        for (OptionalDepend optionalDepend : optionalFile.conflictFile) {
          if (optionalDepend == null)
            throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.conflictFile", new Object[] { optionalFile.name })); 
        }  
      if (optionalFile.dependenciesFile != null)
        for (OptionalDepend optionalDepend : optionalFile.dependenciesFile) {
          if (optionalDepend == null)
            throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.dependenciesFile", new Object[] { optionalFile.name })); 
        }  
      if (optionalFile.groupFile != null)
        for (OptionalDepend optionalDepend : optionalFile.groupFile) {
          if (optionalDepend == null)
            throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.groupFile", new Object[] { optionalFile.name })); 
        }  
      if (optionalFile.triggersList != null)
        for (OptionalTrigger optionalTrigger : optionalFile.triggersList) {
          if (optionalTrigger == null)
            throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.triggers", new Object[] { optionalFile.name })); 
        }  
    } 
  }
  
  public String getProperty(String paramString) {
    return this.properties.get(paramString);
  }
  
  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(this.properties);
  }
  
  public List<String> getCompatClasses() {
    return Collections.unmodifiableList(this.compatClasses);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ClientProfile clientProfile = (ClientProfile)paramObject;
    return Objects.equals(this.uuid, clientProfile.uuid);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.uuid });
  }
  
  public ClassLoaderConfig getClassLoaderConfig() {
    return this.classLoaderConfig;
  }
  
  public boolean isLimited() {
    return this.limited;
  }
  
  public List<CompatibilityFlags> getFlags() {
    return this.flags;
  }
  
  public static class ProfileDefaultSettings {
    public int ram;
    
    public boolean autoEnter;
    
    public boolean fullScreen;
  }
  
  public static class Version implements Comparable<Version> {
    private final long[] data;
    
    private final String original;
    
    private final boolean isObjectSerialized;
    
    public static Version of(String param1String) {
      String str = param1String.replaceAll("[^.0-9]", ".");
      String[] arrayOfString = str.split("\\.");
      return new Version(Arrays.<String>stream(arrayOfString).filter(param1String -> !param1String.isEmpty()).mapToLong(Long::parseLong).toArray(), param1String);
    }
    
    private Version(long[] param1ArrayOflong, String param1String) {
      this.data = param1ArrayOflong;
      this.original = param1String;
      this.isObjectSerialized = false;
    }
    
    public Version(long[] param1ArrayOflong, String param1String, boolean param1Boolean) {
      this.data = param1ArrayOflong;
      this.original = param1String;
      this.isObjectSerialized = param1Boolean;
    }
    
    public int compareTo(Version param1Version) {
      int i = 0;
      if (this.data.length == param1Version.data.length) {
        for (byte b = 0; b < this.data.length; b++) {
          i = Long.compare(this.data[b], param1Version.data[b]);
          if (i != 0)
            return i; 
        } 
      } else if (this.data.length < param1Version.data.length) {
        int j;
        for (j = 0; j < this.data.length; j++) {
          i = Long.compare(this.data[j], param1Version.data[j]);
          if (i != 0)
            return i; 
        } 
        for (j = this.data.length; j < param1Version.data.length; j++) {
          if (param1Version.data[j] > 0L)
            return -1; 
        } 
      } else {
        int j;
        for (j = 0; j < param1Version.data.length; j++) {
          i = Long.compare(this.data[j], param1Version.data[j]);
          if (i != 0)
            return i; 
        } 
        for (j = param1Version.data.length; j < this.data.length; j++) {
          if (this.data[j] > 0L)
            return 1; 
        } 
      } 
      return i;
    }
    
    public String toCleanString() {
      return join(this.data);
    }
    
    private static String join(long[] param1ArrayOflong) {
      return String.join(".", (CharSequence[])Arrays.stream(param1ArrayOflong).mapToObj(String::valueOf).toArray(param1Int -> new String[param1Int]));
    }
    
    public String toString() {
      return this.original;
    }
    
    public static class GsonSerializer implements JsonSerializer<Version>, JsonDeserializer<Version> {
      public ClientProfile.Version deserialize(JsonElement param2JsonElement, Type param2Type, JsonDeserializationContext param2JsonDeserializationContext) throws JsonParseException {
        if (param2JsonElement.isJsonObject()) {
          JsonObject jsonObject = param2JsonElement.getAsJsonObject();
          String str = jsonObject.get("name").getAsString();
          long[] arrayOfLong = (long[])param2JsonDeserializationContext.deserialize(jsonObject.get("data"), long[].class);
          return new ClientProfile.Version(arrayOfLong, str, true);
        } 
        if (param2JsonElement.isJsonArray()) {
          long[] arrayOfLong = (long[])param2JsonDeserializationContext.deserialize(param2JsonElement, long[].class);
          return new ClientProfile.Version(arrayOfLong, ClientProfile.Version.join(arrayOfLong), false);
        } 
        return ClientProfile.Version.of(param2JsonElement.getAsString());
      }
      
      public JsonElement serialize(ClientProfile.Version param2Version, Type param2Type, JsonSerializationContext param2JsonSerializationContext) {
        if (param2Version.isObjectSerialized) {
          JsonObject jsonObject = new JsonObject();
          jsonObject.add("name", (JsonElement)new JsonPrimitive(param2Version.original));
          JsonArray jsonArray = new JsonArray();
          for (long l : param2Version.data)
            jsonArray.add(Long.valueOf(l)); 
          jsonObject.add("data", (JsonElement)jsonArray);
          return (JsonElement)jsonObject;
        } 
        return (JsonElement)new JsonPrimitive(param2Version.toString());
      }
      
      static {
      
      }
    }
  }
  
  public enum ClassLoaderConfig {
    LAUNCHER, MODULE, SYSTEM_ARGS;
  }
  
  public static class ServerProfile {
    public String name;
    
    public String serverAddress;
    
    public int serverPort;
    
    public boolean isDefault = true;
    
    public int protocol = -1;
    
    public boolean socketPing = true;
    
    public ServerProfile() {}
    
    public ServerProfile(String param1String1, String param1String2, int param1Int) {
      this.name = param1String1;
      this.serverAddress = param1String2;
      this.serverPort = param1Int;
    }
    
    public ServerProfile(String param1String1, String param1String2, int param1Int, boolean param1Boolean) {
      this.name = param1String1;
      this.serverAddress = param1String2;
      this.serverPort = param1Int;
      this.isDefault = param1Boolean;
    }
    
    public InetSocketAddress toSocketAddress() {
      return InetSocketAddress.createUnresolved(this.serverAddress, this.serverPort);
    }
  }
  
  public enum CompatibilityFlags {
    LEGACY_NATIVES_DIR, CLASS_CONTROL_API, ENABLE_HACKS, WAYLAND_USE_CUSTOM_GLFW;
  }
  
  public static class GsonSerializer implements JsonSerializer<Version>, JsonDeserializer<Version> {
    public ClientProfile.Version deserialize(JsonElement param1JsonElement, Type param1Type, JsonDeserializationContext param1JsonDeserializationContext) throws JsonParseException {
      if (param1JsonElement.isJsonObject()) {
        JsonObject jsonObject = param1JsonElement.getAsJsonObject();
        String str = jsonObject.get("name").getAsString();
        long[] arrayOfLong = (long[])param1JsonDeserializationContext.deserialize(jsonObject.get("data"), long[].class);
        return new ClientProfile.Version(arrayOfLong, str, true);
      } 
      if (param1JsonElement.isJsonArray()) {
        long[] arrayOfLong = (long[])param1JsonDeserializationContext.deserialize(param1JsonElement, long[].class);
        return new ClientProfile.Version(arrayOfLong, ClientProfile.Version.join(arrayOfLong), false);
      } 
      return ClientProfile.Version.of(param1JsonElement.getAsString());
    }
    
    public JsonElement serialize(ClientProfile.Version param1Version, Type param1Type, JsonSerializationContext param1JsonSerializationContext) {
      if (param1Version.isObjectSerialized) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", (JsonElement)new JsonPrimitive(param1Version.original));
        JsonArray jsonArray = new JsonArray();
        for (long l : param1Version.data)
          jsonArray.add(Long.valueOf(l)); 
        jsonObject.add("data", (JsonElement)jsonArray);
        return (JsonElement)jsonObject;
      } 
      return (JsonElement)new JsonPrimitive(param1Version.toString());
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\ClientProfile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */