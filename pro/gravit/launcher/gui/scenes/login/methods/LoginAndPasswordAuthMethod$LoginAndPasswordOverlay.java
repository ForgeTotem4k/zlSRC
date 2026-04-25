package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginAuthButtonComponent;

public class LoginAndPasswordOverlay extends AbstractVisualComponent {
  private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
  
  private TextField login;
  
  private TextField password;
  
  private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
  
  public LoginAndPasswordOverlay(JavaFXApplication paramJavaFXApplication) {
    super("scenes/login/methods/loginpassword.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "loginandpassword";
  }
  
  public AuthFlow.LoginAndPasswordResult getResult() {
    AuthRequest.AuthPasswordInterface authPasswordInterface;
    String str = this.login.getText();
    if (this.password.getText().isEmpty() && this.password.getPromptText().equals(this.application.getTranslation("runtime.scenes.login.password.saved"))) {
      authPasswordInterface = this.application.runtimeSettings.password;
    } else if (!this.password.getText().isEmpty()) {
      authPasswordInterface = this.application.authService.makePassword(this.password.getText());
    } else {
      this.application.getTranslation("runtime.request.common.accessdenied");
      return null;
    } 
    return new AuthFlow.LoginAndPasswordResult(str, authPasswordInterface);
  }
  
  protected void doInit() {
    this.login = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#login" });
    this.password = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#password" });
    this.login.textProperty().addListener(paramObservable -> LoginAndPasswordAuthMethod.this.accessor.getAuthButton().setState(this.login.getText().isEmpty() ? LoginAuthButtonComponent.AuthButtonState.UNACTIVE : LoginAuthButtonComponent.AuthButtonState.ACTIVE));
    if (this.application.runtimeSettings.login != null) {
      this.login.setText(this.application.runtimeSettings.login);
      LoginAndPasswordAuthMethod.this.accessor.getAuthButton().setState(LoginAuthButtonComponent.AuthButtonState.ACTIVE);
    } else {
      LoginAndPasswordAuthMethod.this.accessor.getAuthButton().setState(LoginAuthButtonComponent.AuthButtonState.UNACTIVE);
    } 
    if (this.application.runtimeSettings.password != null) {
      this.password.getStyleClass().add("hasSaved");
      this.password.setPromptText(this.application.getTranslation("runtime.scenes.login.password.saved"));
    } 
  }
  
  protected void doPostInit() {}
  
  public void reset() {
    if (this.password == null)
      return; 
    this.password.getStyleClass().removeAll((Object[])new String[] { "hasSaved" });
    this.password.setPromptText(this.application.getTranslation("runtime.scenes.login.password"));
  }
  
  public void disable() {}
  
  public void enable() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\LoginAndPasswordAuthMethod$LoginAndPasswordOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */