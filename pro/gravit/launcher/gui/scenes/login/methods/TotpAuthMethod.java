package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.details.AuthTotpDetails;
import pro.gravit.launcher.base.request.auth.password.AuthTOTPPassword;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginScene;

public class TotpAuthMethod extends AbstractAuthMethod<AuthTotpDetails> {
  private final TotpOverlay overlay;
  
  private final JavaFXApplication application;
  
  private final LoginScene.LoginSceneAccessor accessor;
  
  public TotpAuthMethod(LoginScene.LoginSceneAccessor paramLoginSceneAccessor) {
    this.accessor = paramLoginSceneAccessor;
    this.application = paramLoginSceneAccessor.getApplication();
    this.overlay = new TotpOverlay(this.application);
    this.overlay.accessor = paramLoginSceneAccessor;
  }
  
  public void prepare() {}
  
  public void reset() {
    this.overlay.reset();
  }
  
  public CompletableFuture<Void> show(AuthTotpDetails paramAuthTotpDetails) {
    this.overlay.maxLength = paramAuthTotpDetails.maxKeyLength;
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    try {
      ContextHelper.runInFxThreadStatic(() -> {
            this.accessor.showContent(this.overlay);
            paramCompletableFuture.complete(null);
          });
    } catch (Exception exception) {
      this.accessor.errorHandle(exception);
    } 
    return completableFuture;
  }
  
  public CompletableFuture<AuthFlow.LoginAndPasswordResult> auth(AuthTotpDetails paramAuthTotpDetails) {
    this.overlay.future = new CompletableFuture<>();
    String str = this.overlay.getCode();
    if (str != null && !str.isEmpty()) {
      AuthTOTPPassword authTOTPPassword = new AuthTOTPPassword();
      authTOTPPassword.totp = str;
      return CompletableFuture.completedFuture(new AuthFlow.LoginAndPasswordResult(null, (AuthRequest.AuthPasswordInterface)authTOTPPassword));
    } 
    return this.overlay.future;
  }
  
  public void onAuthClicked() {
    this.overlay.complete();
  }
  
  public void onUserCancel() {
    this.overlay.future.completeExceptionally(TotpOverlay.USER_AUTH_CANCELED_EXCEPTION);
  }
  
  public CompletableFuture<Void> hide() {
    return CompletableFuture.completedFuture(null);
  }
  
  public boolean isOverlay() {
    return true;
  }
  
  public static class TotpOverlay extends AbstractVisualComponent {
    private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
    
    private TextField totpField;
    
    private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
    
    private LoginScene.LoginSceneAccessor accessor;
    
    private int maxLength;
    
    public TotpOverlay(JavaFXApplication param1JavaFXApplication) {
      super("scenes/login/methods/totp.fxml", param1JavaFXApplication);
    }
    
    public String getName() {
      return "totp";
    }
    
    protected void doInit() {
      this.totpField = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#totp" });
      this.totpField.textProperty().addListener((param1ObservableValue, param1String1, param1String2) -> {
            if (param1String2 != null && param1String2.length() == this.maxLength)
              complete(); 
          });
      this.totpField.setOnAction(param1ActionEvent -> {
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\TotpAuthMethod.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */