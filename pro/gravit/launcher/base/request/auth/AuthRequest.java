package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.auth.password.Auth2FAPassword;
import pro.gravit.launcher.base.request.auth.password.AuthAESPassword;
import pro.gravit.launcher.base.request.auth.password.AuthCodePassword;
import pro.gravit.launcher.base.request.auth.password.AuthMultiPassword;
import pro.gravit.launcher.base.request.auth.password.AuthOAuthPassword;
import pro.gravit.launcher.base.request.auth.password.AuthPlainPassword;
import pro.gravit.launcher.base.request.auth.password.AuthRSAPassword;
import pro.gravit.launcher.base.request.auth.password.AuthSignaturePassword;
import pro.gravit.launcher.base.request.auth.password.AuthTOTPPassword;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.ProviderMap;

public final class AuthRequest extends Request<AuthRequestEvent> implements WebSocketRequest {
  public static final ProviderMap<AuthPasswordInterface> providers = new ProviderMap();
  
  private static boolean registerProviders = false;
  
  @LauncherNetworkAPI
  public final String login;
  
  @LauncherNetworkAPI
  public final AuthPasswordInterface password;
  
  @LauncherNetworkAPI
  public final String auth_id;
  
  @LauncherNetworkAPI
  public final boolean getSession;
  
  @LauncherNetworkAPI
  public final ConnectTypes authType;
  
  public AuthRequest(String paramString1, String paramString2, String paramString3, ConnectTypes paramConnectTypes) {
    this.login = paramString1;
    this.password = (AuthPasswordInterface)new AuthPlainPassword(paramString2);
    this.auth_id = paramString3;
    this.authType = paramConnectTypes;
    this.getSession = false;
  }
  
  public AuthRequest(String paramString1, AuthPasswordInterface paramAuthPasswordInterface, String paramString2, boolean paramBoolean, ConnectTypes paramConnectTypes) {
    this.login = paramString1;
    this.password = paramAuthPasswordInterface;
    this.auth_id = paramString2;
    this.getSession = paramBoolean;
    this.authType = paramConnectTypes;
  }
  
  public static void registerProviders() {
    if (!registerProviders) {
      providers.register("plain", AuthPlainPassword.class);
      providers.register("rsa2", AuthRSAPassword.class);
      providers.register("aes", AuthAESPassword.class);
      providers.register("2fa", Auth2FAPassword.class);
      providers.register("multi", AuthMultiPassword.class);
      providers.register("signature", AuthSignaturePassword.class);
      providers.register("totp", AuthTOTPPassword.class);
      providers.register("oauth", AuthOAuthPassword.class);
      providers.register("code", AuthCodePassword.class);
      registerProviders = true;
    } 
  }
  
  public String getType() {
    return "auth";
  }
  
  public static interface AuthPasswordInterface {
    boolean check();
    
    default boolean isAllowSave() {
      return false;
    }
    
    static {
    
    }
  }
  
  public enum ConnectTypes {
    CLIENT, API;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\AuthRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */