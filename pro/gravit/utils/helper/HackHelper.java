package pro.gravit.utils.helper;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class HackHelper {
  private static native MethodHandles.Lookup createHackLookupNative(Class<?> paramClass);
  
  private static MethodHandles.Lookup createHackLookupImpl(Class<?> paramClass) {
    try {
      return createHackLookupNative(paramClass);
    } catch (Throwable throwable) {
      try {
        Field field = MethodHandles.Lookup.class.getDeclaredField("TRUSTED");
        field.setAccessible(true);
        int i = ((Integer)field.get(null)).intValue();
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(new Class[] { Class.class, Class.class, int.class });
        constructor.setAccessible(true);
        return constructor.newInstance(new Object[] { paramClass, null, Integer.valueOf(i) });
      } catch (Throwable throwable1) {
        throw new RuntimeException(throwable1);
      } 
    } 
  }
  
  public static MethodHandles.Lookup createHackLookup(Class<?> paramClass) {
    Exception exception = new Exception();
    StackTraceElement[] arrayOfStackTraceElement = exception.getStackTrace();
    String str = arrayOfStackTraceElement[arrayOfStackTraceElement.length - 1].getClassName();
    if (!str.startsWith("pro.gravit.launcher.") && !str.startsWith("pro.gravit.utils."))
      throw new SecurityException(String.format("Untrusted class %s", new Object[] { str })); 
    return createHackLookupImpl(paramClass);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\HackHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */