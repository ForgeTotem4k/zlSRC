package pro.gravit.launcher.gui.scenes.login;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public final class LoginAndPasswordResult extends Record {
  private final String login;
  
  private final AuthRequest.AuthPasswordInterface password;
  
  public LoginAndPasswordResult(String paramString, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    this.login = paramString;
    this.password = paramAuthPasswordInterface;
  }
  
  public final String toString() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;)Ljava/lang/String;
    //   6: areturn
  }
  
  public final int hashCode() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;)I
    //   6: ireturn
  }
  
  public final boolean equals(Object paramObject) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;Ljava/lang/Object;)Z
    //   7: ireturn
  }
  
  public String login() {
    return this.login;
  }
  
  public AuthRequest.AuthPasswordInterface password() {
    return this.password;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\AuthFlow$LoginAndPasswordResult.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */