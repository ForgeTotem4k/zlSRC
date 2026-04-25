package pro.gravit.launcher.base.api;

import java.util.List;
import java.util.UUID;
import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.profiles.ClientProfile;

public class AuthService {
  public static String projectName;
  
  public static String username;
  
  public static ClientPermissions permissions = new ClientPermissions();
  
  public static UUID uuid;
  
  public static ClientProfile profile;
  
  public static boolean hasPermission(String paramString) {
    return permissions.hasPerm(paramString);
  }
  
  public static boolean hasRole(String paramString) {
    return permissions.hasRole(paramString);
  }
  
  public static List<String> getRoles() {
    return permissions.getRoles();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\api\AuthService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */