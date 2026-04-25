package oshi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface SuppressForbidden {
  String reason();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\annotation\SuppressForbidden.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */