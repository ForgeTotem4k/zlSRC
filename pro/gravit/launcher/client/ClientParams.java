package pro.gravit.launcher.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.ClientProfileVersions;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalActionClientArgs;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.utils.Version;

public class ClientParams {
  public long timestamp;
  
  public String assetDir;
  
  public String clientDir;
  
  public String resourcePackDir;
  
  public String nativesDir;
  
  public PlayerProfile playerProfile;
  
  public ClientProfile profile;
  
  public String accessToken;
  
  public boolean autoEnter;
  
  public boolean fullScreen;
  
  public boolean lwjglGlfwWayland;
  
  public int ram;
  
  public int width;
  
  public int height;
  
  public Set<OptionalAction> actions = new HashSet<>();
  
  public UUID session;
  
  public AuthRequestEvent.OAuthRequestEvent oauth;
  
  public String authId;
  
  public long oauthExpiredTime;
  
  public Map<String, Request.ExtendedToken> extendedTokens;
  
  public boolean offlineMode;
  
  public transient HashedDir assetHDir;
  
  public transient HashedDir clientHDir;
  
  public transient HashedDir javaHDir;
  
  public void addClientArgs(Collection<String> paramCollection) {
    if (this.profile.getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_6_4) >= 0) {
      addModernClientArgs(paramCollection);
    } else {
      addClientLegacyArgs(paramCollection);
    } 
  }
  
  public void addClientLegacyArgs(Collection<String> paramCollection) {
    paramCollection.add(this.playerProfile.username);
    paramCollection.add(this.accessToken);
    Collections.addAll(paramCollection, new String[] { "--version", this.profile.getVersion().toString() });
    Collections.addAll(paramCollection, new String[] { "--gameDir", this.clientDir });
    Collections.addAll(paramCollection, new String[] { "--assetsDir", this.assetDir });
  }
  
  private void addModernClientArgs(Collection<String> paramCollection) {
    ClientProfile.Version version = this.profile.getVersion();
    Collections.addAll(paramCollection, new String[] { "--username", this.playerProfile.username });
    if (version.compareTo(ClientProfileVersions.MINECRAFT_1_7_2) >= 0) {
      Collections.addAll(paramCollection, new String[] { "--uuid", Launcher.toHash(this.playerProfile.uuid) });
      Collections.addAll(paramCollection, new String[] { "--accessToken", this.accessToken });
      if (version.compareTo(ClientProfileVersions.MINECRAFT_1_7_10) >= 0) {
        Collections.addAll(paramCollection, new String[] { "--userType", "mojang" });
        Collections.addAll(paramCollection, new String[] { "--userProperties", "{}" });
        Collections.addAll(paramCollection, new String[] { "--assetIndex", this.profile.getAssetIndex() });
      } 
    } else {
      Collections.addAll(paramCollection, new String[] { "--session", this.accessToken });
    } 
    Collections.addAll(paramCollection, new String[] { "--version", this.profile.getVersion().toString() });
    Collections.addAll(paramCollection, new String[] { "--gameDir", this.clientDir });
    Collections.addAll(paramCollection, new String[] { "--assetsDir", this.assetDir });
    Collections.addAll(paramCollection, new String[] { "--resourcePackDir", this.resourcePackDir });
    if (version.compareTo(ClientProfileVersions.MINECRAFT_1_9_4) >= 0)
      Collections.addAll(paramCollection, new String[] { "--versionType", "Launcher v" + Version.getVersion().getVersionString() }); 
    if (this.autoEnter)
      if (version.compareTo(ClientProfileVersions.MINECRAFT_1_20) <= 0) {
        Collections.addAll(paramCollection, new String[] { "--server", this.profile.getServerAddress() });
        Collections.addAll(paramCollection, new String[] { "--port", Integer.toString(this.profile.getServerPort()) });
      } else {
        Collections.addAll(paramCollection, new String[] { "--quickPlayMultiplayer", String.format("%s:%d", new Object[] { this.profile.getServerAddress(), Integer.valueOf(this.profile.getServerPort()) }) });
      }  
    for (OptionalAction optionalAction : this.actions) {
      if (optionalAction instanceof OptionalActionClientArgs)
        paramCollection.addAll(((OptionalActionClientArgs)optionalAction).args); 
    } 
    if (this.fullScreen)
      Collections.addAll(paramCollection, new String[] { "--fullscreen", Boolean.toString(true) }); 
    if (this.width > 0 && this.height > 0) {
      Collections.addAll(paramCollection, new String[] { "--width", Integer.toString(this.width) });
      Collections.addAll(paramCollection, new String[] { "--height", Integer.toString(this.height) });
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientParams.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */