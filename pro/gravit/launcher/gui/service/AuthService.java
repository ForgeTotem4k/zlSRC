package pro.gravit.launcher.gui.service;

import java.util.ArrayList;
import java.util.List;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.password.Auth2FAPassword;
import pro.gravit.launcher.base.request.auth.password.AuthAESPassword;
import pro.gravit.launcher.base.request.auth.password.AuthMultiPassword;
import pro.gravit.launcher.base.request.auth.password.AuthPlainPassword;
import pro.gravit.launcher.base.request.auth.password.AuthTOTPPassword;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.utils.helper.SecurityHelper;

public class AuthService {
  private final LauncherConfig config = Launcher.getConfig();
  
  private final JavaFXApplication application;
  
  private AuthRequestEvent rawAuthResult;
  
  private GetAvailabilityAuthRequestEvent.AuthAvailability authAvailability;
  
  public AuthService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public AuthRequest.AuthPasswordInterface makePassword(String paramString) {
    if (this.config.passwordEncryptKey != null)
      try {
        return (AuthRequest.AuthPasswordInterface)new AuthAESPassword(encryptAESPassword(paramString));
      } catch (Exception exception) {} 
    return (AuthRequest.AuthPasswordInterface)new AuthPlainPassword(paramString);
  }
  
  public AuthRequest.AuthPasswordInterface make2FAPassword(AuthRequest.AuthPasswordInterface paramAuthPasswordInterface, String paramString) {
    Auth2FAPassword auth2FAPassword = new Auth2FAPassword();
    auth2FAPassword.firstPassword = paramAuthPasswordInterface;
    auth2FAPassword.secondPassword = (AuthRequest.AuthPasswordInterface)new AuthTOTPPassword();
    ((AuthTOTPPassword)auth2FAPassword.secondPassword).totp = paramString;
    return (AuthRequest.AuthPasswordInterface)auth2FAPassword;
  }
  
  public List<AuthRequest.AuthPasswordInterface> getListFromPassword(AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    if (paramAuthPasswordInterface instanceof Auth2FAPassword) {
      Auth2FAPassword auth2FAPassword = (Auth2FAPassword)paramAuthPasswordInterface;
      ArrayList<AuthRequest.AuthPasswordInterface> arrayList1 = new ArrayList();
      arrayList1.add(auth2FAPassword.firstPassword);
      arrayList1.add(auth2FAPassword.secondPassword);
      return arrayList1;
    } 
    if (paramAuthPasswordInterface instanceof AuthMultiPassword) {
      AuthMultiPassword authMultiPassword = (AuthMultiPassword)paramAuthPasswordInterface;
      return authMultiPassword.list;
    } 
    ArrayList<AuthRequest.AuthPasswordInterface> arrayList = new ArrayList(1);
    arrayList.add(paramAuthPasswordInterface);
    return arrayList;
  }
  
  public AuthRequest.AuthPasswordInterface getPasswordFromList(List<AuthRequest.AuthPasswordInterface> paramList) {
    if (paramList.size() == 1)
      return paramList.get(0); 
    if (paramList.size() == 2) {
      Auth2FAPassword auth2FAPassword = new Auth2FAPassword();
      auth2FAPassword.firstPassword = paramList.get(0);
      auth2FAPassword.secondPassword = paramList.get(1);
      return (AuthRequest.AuthPasswordInterface)auth2FAPassword;
    } 
    AuthMultiPassword authMultiPassword = new AuthMultiPassword();
    authMultiPassword.list = paramList;
    return (AuthRequest.AuthPasswordInterface)authMultiPassword;
  }
  
  public AuthRequest makeAuthRequest(String paramString1, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface, String paramString2) {
    return new AuthRequest(paramString1, paramAuthPasswordInterface, paramString2, false, this.application.isDebugMode() ? AuthRequest.ConnectTypes.API : AuthRequest.ConnectTypes.CLIENT);
  }
  
  private byte[] encryptAESPassword(String paramString) throws Exception {
    return SecurityHelper.encrypt((Launcher.getConfig()).passwordEncryptKey, paramString);
  }
  
  public void setAuthResult(String paramString, AuthRequestEvent paramAuthRequestEvent) {
    this.rawAuthResult = paramAuthRequestEvent;
    if (paramAuthRequestEvent.oauth != null)
      Request.setOAuth(paramString, paramAuthRequestEvent.oauth); 
  }
  
  public void setAuthAvailability(GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability) {
    this.authAvailability = paramAuthAvailability;
  }
  
  public GetAvailabilityAuthRequestEvent.AuthAvailability getAuthAvailability() {
    return this.authAvailability;
  }
  
  public boolean isFeatureAvailable(String paramString) {
    return (this.authAvailability.features != null && this.authAvailability.features.contains(paramString));
  }
  
  public String getUsername() {
    return (this.rawAuthResult == null || this.rawAuthResult.playerProfile == null) ? "Player" : this.rawAuthResult.playerProfile.username;
  }
  
  public String getMainRole() {
    return (this.rawAuthResult == null || this.rawAuthResult.permissions == null || this.rawAuthResult.permissions.getRoles() == null || this.rawAuthResult.permissions.getRoles().isEmpty()) ? "" : this.rawAuthResult.permissions.getRoles().get(0);
  }
  
  public boolean checkPermission(String paramString) {
    return (this.rawAuthResult == null || this.rawAuthResult.permissions == null) ? false : this.rawAuthResult.permissions.hasPerm(paramString);
  }
  
  public boolean checkDebugPermission(String paramString) {
    return (this.application.isDebugMode() || (!this.application.guiModuleConfig.disableDebugPermissions && checkPermission("launcher.debug." + paramString)));
  }
  
  public PlayerProfile getPlayerProfile() {
    return (this.rawAuthResult == null) ? null : this.rawAuthResult.playerProfile;
  }
  
  public String getAccessToken() {
    return (this.rawAuthResult == null) ? null : this.rawAuthResult.accessToken;
  }
  
  public void exit() {
    this.rawAuthResult = null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\AuthService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */