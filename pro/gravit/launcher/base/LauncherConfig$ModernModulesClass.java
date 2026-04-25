package pro.gravit.launcher.base;

import java.util.ArrayList;
import java.util.List;
import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.launchermodules.sentryl.SentryModule;

class ModernModulesClass {
  private static final List<Class<?>> modulesClasses = new ArrayList<>(2);
  
  static {
    null;
    (new ArrayList<>(2)).add(JavaRuntimeModule.class);
    (new ArrayList<>(2)).add(SentryModule.class);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\LauncherConfig$ModernModulesClass.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */