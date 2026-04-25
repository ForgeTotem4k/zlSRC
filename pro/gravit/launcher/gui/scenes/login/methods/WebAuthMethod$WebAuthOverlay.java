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
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.utils.helper.LogHelper;

public class WebAuthOverlay extends AbstractOverlay {
  private WebView webView;
  
  private LoginScene.LoginSceneAccessor accessor;
  
  private CompletableFuture<AuthFlow.LoginAndPasswordResult> future;
  
  public WebAuthOverlay(JavaFXApplication paramJavaFXApplication) {
    super("overlay/webauth/webauth.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "webView";
  }
  
  public void hide(EventHandler<ActionEvent> paramEventHandler) {
    hide(10.0D, paramEventHandler);
  }
  
  protected void doInit() {
    ScrollPane scrollPane = (ScrollPane)LookupHelper.lookup((Node)this.layout, new String[] { "#webview" });
    this.webView = new WebView();
    scrollPane.setContent((Node)new VBox(new Node[] { (Node)this.webView }));
    ((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#exit" })).setOnAction(paramActionEvent -> {
          if (this.future != null)
            this.future.completeExceptionally(new AbstractAuthMethod.UserAuthCanceledException()); 
          hide((EventHandler<ActionEvent>)null);
        });
  }
  
  public void follow(String paramString1, String paramString2, Consumer<String> paramConsumer) {
    LogHelper.dev("Load url %s", new Object[] { paramString1 });
    this.webView.getEngine().setJavaScriptEnabled(true);
    this.webView.getEngine().load(paramString1);
    if (paramConsumer != null)
      this.webView.getEngine().locationProperty().addListener((paramObservableValue, paramString2, paramString3) -> {
            if (paramString3 != null) {
              LogHelper.dev("Location: %s", new Object[] { paramString3 });
              if (paramString1 != null) {
                if (paramString3.startsWith(paramString1))
                  paramConsumer.accept(paramString3); 
              } else {
                paramConsumer.accept(paramString3);
              } 
            } 
          }); 
  }
  
  public WebView getWebView() {
    return this.webView;
  }
  
  public void reset() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\methods\WebAuthMethod$WebAuthOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */