package pro.gravit.launcher.gui.scenes.login;

import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import pro.gravit.launcher.gui.scenes.internal.OnlineCounter;

class null implements OnlineCounter.OnlineUpdateCallback {
  public void onOnlineReceived(int paramInt) {
    text.setText("Онлайн: \n" + paramInt + " человек");
  }
  
  public void onError(String paramString) {
    text.setText("Недоступно");
    text.setStyle("-fx-text-fill: red;");
    text.setTooltip(new Tooltip("Ошибка: " + paramString));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginScene$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */