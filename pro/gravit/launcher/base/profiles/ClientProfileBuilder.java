package pro.gravit.launcher.base.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.utils.launch.LaunchOptions;

public class ClientProfileBuilder {
  private String title;
  
  private UUID uuid;
  
  private ClientProfile.Version version;
  
  private String info;
  
  private String dir;
  
  private int sortIndex;
  
  private String assetIndex;
  
  private String assetDir;
  
  private List<String> update;
  
  private List<String> updateExclusions;
  
  private List<String> updateVerify;
  
  private Set<OptionalFile> updateOptional;
  
  private List<String> jvmArgs;
  
  private List<String> classPath;
  
  private List<String> altClassPath;
  
  private List<String> clientArgs;
  
  private List<String> compatClasses;
  
  private List<String> loadNatives;
  
  private Map<String, String> properties;
  
  private List<ClientProfile.ServerProfile> servers;
  
  private ClientProfile.ClassLoaderConfig classLoaderConfig;
  
  private List<ClientProfile.CompatibilityFlags> flags;
  
  private int recommendJavaVersion;
  
  private int minJavaVersion;
  
  private int maxJavaVersion;
  
  private ClientProfile.ProfileDefaultSettings settings;
  
  private boolean limited;
  
  private String mainClass;
  
  private String mainModule;
  
  private LaunchOptions.ModuleConf moduleConf;
  
  public ClientProfileBuilder() {
    this.update = new ArrayList<>();
    this.updateExclusions = new ArrayList<>();
    this.updateVerify = new ArrayList<>();
    this.updateOptional = new HashSet<>();
    this.jvmArgs = new ArrayList<>();
    this.classPath = new ArrayList<>();
    this.altClassPath = new ArrayList<>();
    this.clientArgs = new ArrayList<>();
    this.compatClasses = new ArrayList<>();
    this.loadNatives = new ArrayList<>();
    this.properties = new HashMap<>();
    this.servers = new ArrayList<>();
    this.flags = new ArrayList<>();
    this.settings = new ClientProfile.ProfileDefaultSettings();
    this.recommendJavaVersion = 21;
    this.minJavaVersion = 17;
    this.maxJavaVersion = 999;
    this.classLoaderConfig = ClientProfile.ClassLoaderConfig.LAUNCHER;
  }
  
  public ClientProfileBuilder(ClientProfile paramClientProfile) {
    this.title = paramClientProfile.getTitle();
    this.uuid = paramClientProfile.getUUID();
    this.version = paramClientProfile.getVersion();
    this.info = paramClientProfile.getInfo();
    this.dir = paramClientProfile.getDir();
    this.sortIndex = paramClientProfile.getSortIndex();
    this.assetIndex = paramClientProfile.getAssetIndex();
    this.assetDir = paramClientProfile.getAssetDir();
    this.update = new ArrayList<>(paramClientProfile.getUpdate());
    this.updateExclusions = new ArrayList<>(paramClientProfile.getUpdateExclusions());
    this.updateVerify = new ArrayList<>(paramClientProfile.getUpdateVerify());
    this.updateOptional = new HashSet<>(paramClientProfile.getOptional());
    this.jvmArgs = new ArrayList<>(paramClientProfile.getJvmArgs());
    this.classPath = new ArrayList<>(paramClientProfile.getClassPath());
    this.altClassPath = new ArrayList<>(paramClientProfile.getAlternativeClassPath());
    this.clientArgs = new ArrayList<>(paramClientProfile.getClientArgs());
    this.compatClasses = new ArrayList<>(paramClientProfile.getCompatClasses());
    this.loadNatives = new ArrayList<>(paramClientProfile.getLoadNatives());
    this.properties = new HashMap<>(paramClientProfile.getProperties());
    this.servers = new ArrayList<>(paramClientProfile.getServers());
    this.classLoaderConfig = paramClientProfile.getClassLoaderConfig();
    this.flags = new ArrayList<>(paramClientProfile.getFlags());
    this.recommendJavaVersion = paramClientProfile.getRecommendJavaVersion();
    this.minJavaVersion = paramClientProfile.getMinJavaVersion();
    this.maxJavaVersion = paramClientProfile.getMaxJavaVersion();
    this.settings = paramClientProfile.getSettings();
    this.limited = paramClientProfile.isLimited();
    this.mainClass = paramClientProfile.getMainClass();
    this.mainModule = paramClientProfile.getMainModule();
    this.moduleConf = paramClientProfile.getModuleConf();
  }
  
  public ClientProfileBuilder setTitle(String paramString) {
    this.title = paramString;
    return this;
  }
  
  public ClientProfileBuilder setUuid(UUID paramUUID) {
    this.uuid = paramUUID;
    return this;
  }
  
  public ClientProfileBuilder setVersion(ClientProfile.Version paramVersion) {
    this.version = paramVersion;
    return this;
  }
  
  public ClientProfileBuilder setInfo(String paramString) {
    this.info = paramString;
    return this;
  }
  
  public ClientProfileBuilder setDir(String paramString) {
    this.dir = paramString;
    return this;
  }
  
  public ClientProfileBuilder setSortIndex(int paramInt) {
    this.sortIndex = paramInt;
    return this;
  }
  
  public ClientProfileBuilder setAssetIndex(String paramString) {
    this.assetIndex = paramString;
    return this;
  }
  
  public ClientProfileBuilder setAssetDir(String paramString) {
    this.assetDir = paramString;
    return this;
  }
  
  public ClientProfileBuilder setUpdate(List<String> paramList) {
    this.update = paramList;
    return this;
  }
  
  public ClientProfileBuilder update(String paramString) {
    this.update.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setUpdateExclusions(List<String> paramList) {
    this.updateExclusions = paramList;
    return this;
  }
  
  public ClientProfileBuilder updateExclusions(String paramString) {
    this.updateExclusions.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setUpdateVerify(List<String> paramList) {
    this.updateVerify = paramList;
    return this;
  }
  
  public ClientProfileBuilder updateVerify(String paramString) {
    this.updateVerify.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setUpdateOptional(Set<OptionalFile> paramSet) {
    this.updateOptional = paramSet;
    return this;
  }
  
  public ClientProfileBuilder updateOptional(OptionalFile paramOptionalFile) {
    this.updateOptional.add(paramOptionalFile);
    return this;
  }
  
  public ClientProfileBuilder setJvmArgs(List<String> paramList) {
    this.jvmArgs = paramList;
    return this;
  }
  
  public ClientProfileBuilder jvmArg(String paramString) {
    this.jvmArgs.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setClassPath(List<String> paramList) {
    this.classPath = paramList;
    return this;
  }
  
  public ClientProfileBuilder classPath(String paramString) {
    this.classPath.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setAltClassPath(List<String> paramList) {
    this.altClassPath = paramList;
    return this;
  }
  
  public ClientProfileBuilder altClassPath(String paramString) {
    this.altClassPath.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setClientArgs(List<String> paramList) {
    this.clientArgs = paramList;
    return this;
  }
  
  public ClientProfileBuilder clientArg(String paramString) {
    this.clientArgs.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setCompatClasses(List<String> paramList) {
    this.compatClasses = paramList;
    return this;
  }
  
  public ClientProfileBuilder compatClass(String paramString) {
    this.compatClasses.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setLoadNatives(List<String> paramList) {
    this.loadNatives = paramList;
    return this;
  }
  
  public ClientProfileBuilder loadNatives(String paramString) {
    this.loadNatives.add(paramString);
    return this;
  }
  
  public ClientProfileBuilder setProperties(Map<String, String> paramMap) {
    this.properties = paramMap;
    return this;
  }
  
  public ClientProfileBuilder property(String paramString1, String paramString2) {
    this.properties.put(paramString1, paramString2);
    return this;
  }
  
  public ClientProfileBuilder setServers(List<ClientProfile.ServerProfile> paramList) {
    this.servers = paramList;
    return this;
  }
  
  public ClientProfileBuilder server(ClientProfile.ServerProfile paramServerProfile) {
    this.servers.add(paramServerProfile);
    return this;
  }
  
  public ClientProfileBuilder setClassLoaderConfig(ClientProfile.ClassLoaderConfig paramClassLoaderConfig) {
    this.classLoaderConfig = paramClassLoaderConfig;
    return this;
  }
  
  public ClientProfileBuilder setFlags(List<ClientProfile.CompatibilityFlags> paramList) {
    this.flags = paramList;
    return this;
  }
  
  public ClientProfileBuilder flag(ClientProfile.CompatibilityFlags paramCompatibilityFlags) {
    this.flags.add(paramCompatibilityFlags);
    return this;
  }
  
  public ClientProfileBuilder setRecommendJavaVersion(int paramInt) {
    this.recommendJavaVersion = paramInt;
    return this;
  }
  
  public ClientProfileBuilder setMinJavaVersion(int paramInt) {
    this.minJavaVersion = paramInt;
    return this;
  }
  
  public ClientProfileBuilder setMaxJavaVersion(int paramInt) {
    this.maxJavaVersion = paramInt;
    return this;
  }
  
  public ClientProfileBuilder setSettings(ClientProfile.ProfileDefaultSettings paramProfileDefaultSettings) {
    this.settings = paramProfileDefaultSettings;
    return this;
  }
  
  public ClientProfileBuilder setLimited(boolean paramBoolean) {
    this.limited = paramBoolean;
    return this;
  }
  
  public ClientProfileBuilder setMainClass(String paramString) {
    this.mainClass = paramString;
    return this;
  }
  
  public ClientProfileBuilder setMainModule(String paramString) {
    this.mainModule = paramString;
    return this;
  }
  
  public ClientProfileBuilder setModuleConf(LaunchOptions.ModuleConf paramModuleConf) {
    this.moduleConf = paramModuleConf;
    return this;
  }
  
  public ClientProfile createClientProfile() {
    return new ClientProfile(this.title, this.uuid, this.version, this.info, this.dir, this.sortIndex, this.assetIndex, this.assetDir, this.update, this.updateExclusions, this.updateVerify, this.updateOptional, this.jvmArgs, this.classPath, this.altClassPath, this.clientArgs, this.compatClasses, this.loadNatives, this.properties, this.servers, this.classLoaderConfig, this.flags, this.recommendJavaVersion, this.minJavaVersion, this.maxJavaVersion, this.settings, this.limited, this.mainClass, this.mainModule, this.moduleConf);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\ClientProfileBuilder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */