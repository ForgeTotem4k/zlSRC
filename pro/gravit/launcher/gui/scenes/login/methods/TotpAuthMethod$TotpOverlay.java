package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.password.AuthTOTPPassword;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginScene;

public class TotpOverlay extends AbstractVisualComponent {
  private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
  
  private TextField totpField;
  
  private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
  
  private LoginScene.LoginSceneAccessor accessor;
  
  private int maxLength;
  
  public TotpOverlay(JavaFXApplication paramJavaFXApplication) {
    super("scenes/login/methods/totp.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "totp";
  }
  
  protected void doInit() {
    this.totpField = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#totp" });
    this.totpField.textProperty().addListener((paramObservableValue, paramString1, paramString2) -> {
          if (paramString2 != null && paramString2.length() == this.maxLength)
            complete(); 
        });
    this.totpField.setOnAction(paramActionEvent -> {
          if (this.totpField.getText() != null && !this.totpField.getText().isEmpty())
            complete(); 
        });
  }
  
  protected void doPostInit() {}
  
  public void complete() {
    AuthTOTPPassword authTOTPPassword = new AuthTOTPPassword();
    authTOTPPassword.totp = getCode();
    this.future.complete(new AuthFlow.LoginAndPasswordResult(null, (AuthRequest.AuthPasswordInterface)authTOTPPassword));
  }
  
  public void requestFocus() {
    this.totpField.requestFocus();
  }
  
  public String getCode() {
    return this.totpField.getText();
  }
  
  public void reset() {
    if (this.totpField == null)
      return; 
    this.totpField.setText("");
  }
  
  public void disable() {}
  
  public void enable() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\TotpAuthMethod$TotpOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */