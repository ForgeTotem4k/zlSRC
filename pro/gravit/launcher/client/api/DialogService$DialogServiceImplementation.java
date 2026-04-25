package pro.gravit.launcher.client.api;

import java.util.function.Consumer;

public interface DialogServiceImplementation {
  void showDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2);
  
  void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2);
  
  void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, Runnable paramRunnable3);
  
  void showTextDialog(String paramString, Consumer<String> paramConsumer, Runnable paramRunnable);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\api\DialogService$DialogServiceImplementation.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */