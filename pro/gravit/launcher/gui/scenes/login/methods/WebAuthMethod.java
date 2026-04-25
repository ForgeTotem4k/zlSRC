package pro.gravit.launcher.gui.scenes.login.methods;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.details.AuthWebViewDetails;
import pro.gravit.launcher.base.request.auth.password.AuthCodePassword;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.utils.helper.LogHelper;

public class WebAuthMethod extends AbstractAuthMethod<AuthWebViewDetails> {
  WebAuthOverlay overlay;
  
  private final JavaFXApplication application;
  
  private final LoginScene.LoginSceneAccessor accessor;
  
  public WebAuthMethod(LoginScene.LoginSceneAccessor paramLoginSceneAccessor) {
    this.application = paramLoginSceneAccessor.getApplication();
    this.accessor = paramLoginSceneAccessor;
    this.overlay = (WebAuthOverlay)this.application.gui.registerComponent(WebAuthOverlay.class);
    this.overlay.accessor = paramLoginSceneAccessor;
  }
  
  public void prepare() {}
  
  public void reset() {
    this.overlay.reset();
  }
  
  public CompletableFuture<Void> show(AuthWebViewDetails paramAuthWebViewDetails) {
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    try {
      this.accessor.showOverlay(this.overlay, paramActionEvent -> paramCompletableFuture.complete(null));
    } catch (Exception exception) {
      this.accessor.errorHandle(exception);
    } 
    return completableFuture;
  }
  
  public CompletableFuture<AuthFlow.LoginAndPasswordResult> auth(AuthWebViewDetails paramAuthWebViewDetails) {
    this.overlay.future = new CompletableFuture<>();
    this.overlay.follow(paramAuthWebViewDetails.url, paramAuthWebViewDetails.redirectUrl, paramString -> {
          LogHelper.dev("Redirect uri: %s", new Object[] { paramString });
          this.overlay.future.complete(new AuthFlow.LoginAndPasswordResult(null, (AuthRequest.AuthPasswordInterface)new AuthCodePassword(paramString)));
        });
    return this.overlay.future;
  }
  
  public void onAuthClicked() {}
  
  public void onUserCancel() {}
  
  public CompletableFuture<Void> hide() {
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    this.overlay.hide(paramActionEvent -> paramCompletableFuture.complete(null));
    return completableFuture;
  }
  
  public boolean isOverlay() {
    return true;
  }
  
  public static class WebAuthOverlay extends AbstractOverlay {
    private WebView webView;
    
    private LoginScene.LoginSceneAccessor accessor;
    
    private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
    
    public WebAuthOverlay(JavaFXApplication param1JavaFXApplication) {
      super("overlay/webauth/webauth.fxml", param1JavaFXApplication);
    }
    
    public String getName() {
      return "webView";
    }
    
    public void hide(EventHandler<ActionEvent> param1EventHandler) {
      hide(10.0D, param1EventHandler);
    }
    
    protected void doInit() {
      ScrollPane scrollPane = (ScrollPane)LookupHelper.lookup((Node)this.layout, new String[] { "#webview" });
      this.webView = new WebView();
      scrollPane.setContent((Node)new VBox(new Node[] { (Node)this.webView }));
      ((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#exit" })).setOnAction(param1ActionEvent -> {
            if (this.future != null)
              this.future.completeExceptionally(new AbstractAuthMethod.UserAuthCanceledException()); 
            hide((EventHandler<ActionEvent>)null);
          });
    }
    
    public void follow(String param1String1, String param1String2, Consumer<String> param1Consumer) {
      LogHelper.dev("Load url %s", new Object[] { param1String1 });
      this.webView.getEngine().setJavaScriptEnabled(true);
      this.webView.getEngine().load(param1String1);
      if (param1Consumer != null)
        this.webView.getEngine().locationProperty().addListener((param1ObservableValue, param1String2, param1String3) -> {
              if (param1String3 != null) {
                LogHelper.dev("Location: %s", new Object[] { param1String3 });
                if (param1String1 != null) {
                  if (param1String3.startsWith(param1String1))
                    param1Consumer.accept(param1String3); 
                } else {
                  param1Consumer.accept(param1String3);
                } 
              } 
            }); 
    }
    
    public WebView getWebView() {
      return this.webView;
    }
    
    public void reset() {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\WebAuthMethod.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */