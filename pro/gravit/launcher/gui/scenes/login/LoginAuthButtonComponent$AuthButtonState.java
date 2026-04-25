package pro.gravit.launcher.gui.scenes.login;

public enum AuthButtonState {
  ACTIVE("activeButton"),
  UNACTIVE("unactiveButton"),
  ERROR("errorButton");
  
  private final String styleClass;
  
  public String getStyleClass() {
    return this.styleClass;
  }
  
  AuthButtonState(String paramString1) {
    this.styleClass = paramString1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginAuthButtonComponent$AuthButtonState.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */