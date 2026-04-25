package pro.gravit.launcher.runtime.gui;

import javax.swing.JOptionPane;

public class NoRuntimeProvider implements RuntimeProvider {
  public void run(String[] paramArrayOfString) {
    JOptionPane.showMessageDialog(null, "Модуль графического интерфейса лаунчера отсутствует");
  }
  
  public void preLoad() {}
  
  public void init(boolean paramBoolean) {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\gui\NoRuntimeProvider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */