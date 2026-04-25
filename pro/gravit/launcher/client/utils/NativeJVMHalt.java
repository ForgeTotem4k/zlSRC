package pro.gravit.launcher.client.utils;

import java.lang.reflect.Method;
import pro.gravit.utils.helper.LogHelper;

public final class NativeJVMHalt {
  public final int haltCode;
  
  public NativeJVMHalt(int paramInt) {
    this.haltCode = paramInt;
    System.out.println("JVM exit code " + paramInt);
  }
  
  public static void initFunc() {}
  
  public static void haltA(int paramInt) {
    NativeJVMHalt nativeJVMHalt = new NativeJVMHalt(paramInt);
    try {
      LogHelper.dev("Try invoke Shutdown.exit");
      Class<?> clazz = Class.forName("java.lang.Shutdown", true, ClassLoader.getSystemClassLoader());
      Method method = clazz.getDeclaredMethod("exit", new Class[] { int.class });
      method.setAccessible(true);
      method.invoke(null, new Object[] { Integer.valueOf(paramInt) });
    } catch (Throwable throwable) {
      if (LogHelper.isDevEnabled())
        LogHelper.error(throwable); 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\clien\\utils\NativeJVMHalt.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */