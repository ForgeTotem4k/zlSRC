package pro.gravit.launcher.gui.scenes.login;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import pro.gravit.launcher.gui.JavaFXApplication;

public class LoginAuthButtonComponent extends Button {
  private final JavaFXApplication application;
  
  private final Button button;
  
  private AuthButtonState state = AuthButtonState.UNACTIVE;
  
  private String originalText;
  
  public LoginAuthButtonComponent(Button paramButton, JavaFXApplication paramJavaFXApplication, EventHandler<ActionEvent> paramEventHandler) {
    this.application = paramJavaFXApplication;
    this.button = paramButton;
    this.button.setOnAction(paramEventHandler);
    this.originalText = this.button.getText();
  }
  
  public AuthButtonState getState() {
    return this.state;
  }
  
  public void setState(AuthButtonState paramAuthButtonState) {
    if (paramAuthButtonState == null)
      throw new NullPointerException("State can't be null"); 
    if (paramAuthButtonState == this.state)
      return; 
    if (this.state != null)
      this.button.getStyleClass().remove(this.state.getStyleClass()); 
    this.button.getStyleClass().add(paramAuthButtonState.getStyleClass());
    if (paramAuthButtonState == AuthButtonState.ERROR) {
      this.button.setText("ERROR");
    } else if (this.state == AuthButtonState.ERROR) {
      this.button.setText(this.originalText);
    } 
    this.state = paramAuthButtonState;
  }
  
  public enum AuthButtonState {
    ACTIVE("activeButton"),
    UNACTIVE("unactiveButton"),
    ERROR("errorButton");
    
    private final String styleClass;
    
    public String getStyleClass() {
      return this.styleClass;
    }
    
    AuthButtonState(String param1String1) {
      this.styleClass = param1String1;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginAuthButtonComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */