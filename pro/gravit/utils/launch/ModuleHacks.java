package pro.gravit.utils.launch;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ModuleHacks {
  public static ModuleLayer.Controller createController(MethodHandles.Lookup paramLookup, ModuleLayer paramModuleLayer) {
    try {
      return paramLookup.findConstructor(ModuleLayer.Controller.class, MethodType.methodType(void.class, ModuleLayer.class)).invoke(paramModuleLayer);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ModuleHacks.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */