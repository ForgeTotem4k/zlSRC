package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.request.auth.details.AuthLoginOnlyDetails;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginAuthButtonComponent;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.utils.helper.LogHelper;

public class LoginOnlyAuthMethod extends AbstractAuthMethod<AuthLoginOnlyDetails> {
  private final LoginOnlyOverlay overlay;
  
  private final JavaFXApplication application;
  
  private final LoginScene.LoginSceneAccessor accessor;
  
  public LoginOnlyAuthMethod(LoginScene.LoginSceneAccessor paramLoginSceneAccessor) {
    this.accessor = paramLoginSceneAccessor;
    this.application = paramLoginSceneAccessor.getApplication();
    this.overlay = new LoginOnlyOverlay(this.application);
  }
  
  public void prepare() {}
  
  public void reset() {
    this.overlay.reset();
  }
  
  public CompletableFuture<Void> show(AuthLoginOnlyDetails paramAuthLoginOnlyDetails) {
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    try {
      ContextHelper.runInFxThreadStatic(() -> {
            this.accessor.showContent(this.overlay);
            paramCompletableFuture.complete(null);
          }).exceptionally(paramThrowable -> {
            LogHelper.error(paramThrowable);
            return null;
          });
    } catch (Exception exception) {
      this.accessor.errorHandle(exception);
    } 
    return completableFuture;
  }
  
  public CompletableFuture<AuthFlow.LoginAndPasswordResult> auth(AuthLoginOnlyDetails paramAuthLoginOnlyDetails) {
    this.overlay.future = new CompletableFuture<>();
    String str = this.overlay.login.getText();
    return (str != null && !str.isEmpty()) ? CompletableFuture.completedFuture(new AuthFlow.LoginAndPasswordResult(str, null)) : this.overlay.future;
  }
  
  public void onAuthClicked() {
    this.overlay.future.complete(this.overlay.getResult());
  }
  
  public void onUserCancel() {
    this.overlay.future.completeExceptionally(LoginOnlyOverlay.USER_AUTH_CANCELED_EXCEPTION);
  }
  
  public CompletableFuture<Void> hide() {
    return CompletableFuture.completedFuture(null);
  }
  
  public boolean isOverlay() {
    return false;
  }
  
  public class LoginOnlyOverlay extends AbstractVisualComponent {
    private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
    
    private TextField login;
    
    private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
    
    public LoginOnlyOverlay(JavaFXApplication param1JavaFXApplication) {
      super("scenes/login/methods/loginonly.fxml", param1JavaFXApplication);
    }
    
    public String getName() {
      return "loginonly";
    }
    
    public AuthFlow.LoginAndPasswordResult getResult() {
      String str = this.login.getText();
      return new AuthFlow.LoginAndPasswordResult(str, null);
    }
    
    protected void doInit() {
      this.login = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#login" });
      this.login.textProperty().addListener(param1Observable -> LoginOnlyAuthMethod.this.accessor.getAuthButton().setState(this.login.getText().isEmpty() ? LoginAuthButtonComponent.AuthButtonState.UNACTIVE : LoginAuthButtonComponent.AuthButtonState.ACTIVE));
      if (this.application.runtimeSettings.login != null) {
        this.login.setText(this.application.runtimeSettings.login);
        LoginOnlyAuthMethod.this.accessor.getAuthButton().setState(LoginAuthButtonComponent.AuthButtonState.ACTIVE);
      } else {
        LoginOnlyAuthMethod.this.accessor.getAuthButton().setState(LoginAuthButtonComponent.AuthButtonState.UNACTIVE);
      } 
    }
    
    protected void doPostInit() {}
    
    public void reset() {
      if (this.login == null)
        return; 
      this.login.setText("");
    }
    
    public void disable() {}
    
    public void enable() {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\LoginOnlyAuthMethod.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */