package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.details.AuthPasswordDetails;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginAuthButtonComponent;
import pro.gravit.launcher.gui.scenes.login.LoginScene;

public class LoginAndPasswordAuthMethod extends AbstractAuthMethod<AuthPasswordDetails> {
  private LoginAndPasswordOverlay overlay;
  
  private final JavaFXApplication application;
  
  private final LoginScene.LoginSceneAccessor accessor;
  
  public LoginAndPasswordAuthMethod(LoginScene.LoginSceneAccessor paramLoginSceneAccessor) {
    this.accessor = paramLoginSceneAccessor;
    this.application = paramLoginSceneAccessor.getApplication();
    this.overlay = new LoginAndPasswordOverlay(this.application);
  }
  
  public void prepare() {}
  
  public void reset() {
    this.overlay.reset();
  }
  
  public CompletableFuture<Void> show(AuthPasswordDetails paramAuthPasswordDetails) {
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    ContextHelper.runInFxThreadStatic(() -> {
          try {
            this.overlay = new LoginAndPasswordOverlay(this.application);
            this.accessor.showContent(this.overlay);
            Platform.runLater(());
          } catch (Exception exception) {
            this.accessor.errorHandle(exception);
            paramCompletableFuture.completeExceptionally(exception);
          } 
        });
    return completableFuture;
  }
  
  public CompletableFuture<AuthFlow.LoginAndPasswordResult> auth(AuthPasswordDetails paramAuthPasswordDetails) {
    this.overlay.future = new CompletableFuture<>();
    return this.overlay.future;
  }
  
  public void onAuthClicked() {
    AuthFlow.LoginAndPasswordResult loginAndPasswordResult = this.overlay.getResult();
    if (loginAndPasswordResult != null)
      this.overlay.future.complete(loginAndPasswordResult); 
  }
  
  public void onUserCancel() {
    this.overlay.future.completeExceptionally(LoginAndPasswordOverlay.USER_AUTH_CANCELED_EXCEPTION);
  }
  
  public CompletableFuture<Void> hide() {
    return CompletableFuture.completedFuture(null);
  }
  
  public boolean isOverlay() {
    return false;
  }
  
  public class LoginAndPasswordOverlay extends AbstractVisualComponent {
    private static final AbstractAuthMethod.UserAuthCanceledException USER_AUTH_CANCELED_EXCEPTION = new AbstractAuthMethod.UserAuthCanceledException();
    
    private TextField login;
    
    private TextField password;
    
    private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
    
    public LoginAndPasswordOverlay(JavaFXApplication param1JavaFXApplication) {
      super("scenes/login/methods/loginpassword.fxml", param1JavaFXApplication);
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
      this.login.textProperty().addListener(param1Observable -> LoginAndPasswordAuthMethod.this.accessor.getAuthButton().setState(this.login.getText().isEmpty() ? LoginAuthButtonComponent.AuthButtonState.UNACTIVE : LoginAuthButtonComponent.AuthButtonState.ACTIVE));
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\LoginAndPasswordAuthMethod.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */