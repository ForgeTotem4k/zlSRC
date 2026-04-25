package pro.gravit.launcher.gui.scenes.login;

import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;

public final class SuccessAuth extends Record {
  private final AuthRequestEvent requestEvent;
  
  private final String recentLogin;
  
  private final AuthRequest.AuthPasswordInterface recentPassword;
  
  public SuccessAuth(AuthRequestEvent paramAuthRequestEvent, String paramString, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    this.requestEvent = paramAuthRequestEvent;
    this.recentLogin = paramString;
    this.recentPassword = paramAuthPasswordInterface;
  }
  
  public final String toString() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;)Ljava/lang/String;
    //   6: areturn
  }
  
  public final int hashCode() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;)I
    //   6: ireturn
  }
  
  public final boolean equals(Object paramObject) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;Ljava/lang/Object;)Z
    //   7: ireturn
  }
  
  public AuthRequestEvent requestEvent() {
    return this.requestEvent;
  }
  
  public String recentLogin() {
    return this.recentLogin;
  }
  
  public AuthRequest.AuthPasswordInterface recentPassword() {
    return this.recentPassword;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\AuthFlow$SuccessAuth.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */