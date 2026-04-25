package pro.gravit.launcher.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR})
public @interface LauncherInjectionConstructor {
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\LauncherInjectionConstructor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */