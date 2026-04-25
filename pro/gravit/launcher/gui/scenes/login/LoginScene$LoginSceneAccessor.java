package pro.gravit.launcher.gui.scenes.login;

import java.util.function.Consumer;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.scenes.AbstractScene;

public class LoginSceneAccessor extends AbstractScene.SceneAccessor {
  public LoginSceneAccessor() {
    super(paramLoginScene);
  }
  
  public void showContent(AbstractVisualComponent paramAbstractVisualComponent) throws Exception {
    paramAbstractVisualComponent.init();
    paramAbstractVisualComponent.postInit();
    if (LoginScene.this.contentComponent != null)
      LoginScene.this.content.getChildren().clear(); 
    LoginScene.this.contentComponent = paramAbstractVisualComponent;
    LoginScene.this.content.getChildren().add(paramAbstractVisualComponent.getLayout());
  }
  
  public LoginAuthButtonComponent getAuthButton() {
    return LoginScene.this.authButton;
  }
  
  public void setState(LoginAuthButtonComponent.AuthButtonState paramAuthButtonState) {
    LoginScene.this.authButton.setState(paramAuthButtonState);
  }
  
  public boolean isEmptyContent() {
    return LoginScene.this.content.getChildren().isEmpty();
  }
  
  public void clearContent() {
    LoginScene.this.content.getChildren().clear();
  }
  
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> void processing(Request<T> paramRequest, String paramString, Consumer<T> paramConsumer, Consumer<String> paramConsumer1) {
    LoginScene.this.processing(paramRequest, paramString, paramConsumer, paramConsumer1);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginScene$LoginSceneAccessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */