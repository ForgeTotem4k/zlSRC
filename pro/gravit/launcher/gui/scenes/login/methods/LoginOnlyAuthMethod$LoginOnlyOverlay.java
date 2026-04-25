package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginAuthButtonComponent;

public class LoginOnlyOverlay extends AbstractVisualComponent {
  private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
  
  private TextField login;
  
  private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
  
  public LoginOnlyOverlay(JavaFXApplication paramJavaFXApplication) {
    super("scenes/login/methods/loginonly.fxml", paramJavaFXApplication);
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
    this.login.textProperty().addListener(paramObservable -> LoginOnlyAuthMethod.this.accessor.getAuthButton().setState(this.login.getText().isEmpty() ? LoginAuthButtonComponent.AuthButtonState.UNACTIVE : LoginAuthButtonComponent.AuthButtonState.ACTIVE));
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\LoginOnlyAuthMethod$LoginOnlyOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */