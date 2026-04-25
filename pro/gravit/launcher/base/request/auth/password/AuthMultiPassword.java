package pro.gravit.launcher.base.request.auth.password;

import java.util.List;
import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthMultiPassword implements AuthRequest.AuthPasswordInterface {
  public List<AuthRequest.AuthPasswordInterface> list;
  
  public boolean check() {
    return (this.list != null && this.list.stream().allMatch(paramAuthPasswordInterface -> (paramAuthPasswordInterface != null && paramAuthPasswordInterface.check())));
  }
  
  public boolean isAllowSave() {
    return (this.list != null && this.list.stream().allMatch(paramAuthPasswordInterface -> (paramAuthPasswordInterface != null && paramAuthPasswordInterface.isAllowSave())));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthMultiPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */