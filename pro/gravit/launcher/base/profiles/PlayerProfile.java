package pro.gravit.launcher.base.profiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import pro.gravit.utils.helper.IOHelper;

public final class PlayerProfile {
  public final UUID uuid;
  
  public final String username;
  
  public final Map<String, Texture> assets;
  
  public final Map<String, String> properties;
  
  @Deprecated
  public PlayerProfile(UUID paramUUID, String paramString, Texture paramTexture1, Texture paramTexture2) {
    this(paramUUID, paramString, paramTexture1, paramTexture2, new HashMap<>());
  }
  
  @Deprecated
  public PlayerProfile(UUID paramUUID, String paramString, Texture paramTexture1, Texture paramTexture2, Map<String, String> paramMap) {
    this.uuid = Objects.<UUID>requireNonNull(paramUUID, "uuid");
    this.username = paramString;
    this.assets = new HashMap<>();
    if (paramTexture1 != null)
      this.assets.put("SKIN", paramTexture1); 
    if (paramTexture2 != null)
      this.assets.put("CAPE", paramTexture2); 
    this.properties = paramMap;
  }
  
  public PlayerProfile(UUID paramUUID, String paramString, Map<String, Texture> paramMap, Map<String, String> paramMap1) {
    this.uuid = paramUUID;
    this.username = paramString;
    this.assets = paramMap;
    this.properties = paramMap1;
  }
  
  public static PlayerProfile newOfflineProfile(String paramString) {
    return new PlayerProfile(offlineUUID(paramString), paramString, new HashMap<>(), new HashMap<>());
  }
  
  public static UUID offlineUUID(String paramString) {
    return UUID.nameUUIDFromBytes(IOHelper.encodeASCII("OfflinePlayer:" + paramString));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\PlayerProfile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */