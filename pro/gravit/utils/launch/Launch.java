package pro.gravit.utils.launch;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface Launch {
  ClassLoaderControl init(List<Path> paramList, String paramString, LaunchOptions paramLaunchOptions);
  
  void launch(String paramString1, String paramString2, Collection<String> paramCollection) throws Throwable;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\Launch.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */