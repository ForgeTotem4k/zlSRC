package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class AuthRequestEvent extends RequestEvent {
  public static final String TWO_FACTOR_NEED_ERROR_MESSAGE = "auth.require2fa";
  
  public static final String ONE_FACTOR_NEED_ERROR_MESSAGE_PREFIX = "auth.require.factor.";
  
  public static final String OAUTH_TOKEN_EXPIRE = "auth.expiretoken";
  
  public static final String OAUTH_TOKEN_INVALID = "auth.invalidtoken";
  
  public static final String USER_NOT_FOUND_ERROR_MESSAGE = "auth.usernotfound";
  
  public static final String WRONG_PASSWORD_ERROR_MESSAGE = "auth.wrongpassword";
  
  public static final String ACCOUNT_BLOCKED_ERROR_MESSAGE = "auth.userblocked";
  
  @LauncherNetworkAPI
  public ClientPermissions permissions;
  
  @LauncherNetworkAPI
  public PlayerProfile playerProfile;
  
  @LauncherNetworkAPI
  public String accessToken;
  
  @LauncherNetworkAPI
  public String protectToken;
  
  @Deprecated
  @LauncherNetworkAPI
  public UUID session;
  
  @LauncherNetworkAPI
  public OAuthRequestEvent oauth;
  
  public AuthRequestEvent() {}
  
  public AuthRequestEvent(PlayerProfile paramPlayerProfile, String paramString, ClientPermissions paramClientPermissions) {
    this.playerProfile = paramPlayerProfile;
    this.accessToken = paramString;
    this.permissions = paramClientPermissions;
  }
  
  public AuthRequestEvent(ClientPermissions paramClientPermissions, PlayerProfile paramPlayerProfile, String paramString1, String paramString2) {
    this.permissions = paramClientPermissions;
    this.playerProfile = paramPlayerProfile;
    this.accessToken = paramString1;
    this.protectToken = paramString2;
  }
  
  public AuthRequestEvent(ClientPermissions paramClientPermissions, PlayerProfile paramPlayerProfile, String paramString1, String paramString2, UUID paramUUID) {
    this.permissions = paramClientPermissions;
    this.playerProfile = paramPlayerProfile;
    this.accessToken = paramString1;
    this.protectToken = paramString2;
    this.session = paramUUID;
  }
  
  public AuthRequestEvent(ClientPermissions paramClientPermissions, PlayerProfile paramPlayerProfile, String paramString1, String paramString2, UUID paramUUID, OAuthRequestEvent paramOAuthRequestEvent) {
    this.permissions = paramClientPermissions;
    this.playerProfile = paramPlayerProfile;
    this.accessToken = paramString1;
    this.protectToken = paramString2;
    this.session = paramUUID;
    this.oauth = paramOAuthRequestEvent;
  }
  
  public String getType() {
    return "auth";
  }
  
  public static class OAuthRequestEvent {
    public final String accessToken;
    
    public final String refreshToken;
    
    public final long expire;
    
    public OAuthRequestEvent(String param1String1, String param1String2, long param1Long) {
      this.accessToken = param1String1;
      this.refreshToken = param1String2;
      this.expire = param1Long;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\AuthRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */