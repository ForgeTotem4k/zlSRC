package pro.gravit.launcher.gui.impl;

import pro.gravit.launcher.gui.JavaFXApplication;

public class BackgroundComponent extends AbstractVisualComponent {
  public BackgroundComponent(JavaFXApplication paramJavaFXApplication) {
    super("components/background.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "background";
  }
  
  protected void doInit() {}
  
  protected void doPostInit() {}
  
  public void reset() {}
  
  public void disable() {}
  
  public void enable() {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\BackgroundComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */