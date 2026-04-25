package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;

public abstract class AbstractAuthMethod<T extends GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> {
  public abstract void prepare();
  
  public abstract void reset();
  
  public abstract CompletableFuture<Void> show(T paramT);
  
  public abstract CompletableFuture<AuthFlow.LoginAndPasswordResult> auth(T paramT);
  
  public abstract void onAuthClicked();
  
  public abstract void onUserCancel();
  
  public abstract CompletableFuture<Void> hide();
  
  public abstract boolean isOverlay();
  
  static {
  
  }
  
  public static class UserAuthCanceledException extends RuntimeException {
    public UserAuthCanceledException() {}
    
    public UserAuthCanceledException(String param1String) {
      super(param1String);
    }
    
    public UserAuthCanceledException(String param1String, Throwable param1Throwable) {
      super(param1String, param1Throwable);
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\AbstractAuthMethod.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */