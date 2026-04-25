package pro.gravit.utils.helper;

import java.util.Locale;
import java.util.Map;

public final class EnvHelper {
  public static final String[] toTest = new String[] { "_JAVA_OPTIONS", "_JAVA_OPTS", "JAVA_OPTS", "JAVA_OPTIONS" };
  
  public static void addEnv(ProcessBuilder paramProcessBuilder) {
    Map<String, String> map = paramProcessBuilder.environment();
    for (String str1 : toTest) {
      if (map.containsKey(str1))
        map.put(str1, ""); 
      String str2 = str1.toLowerCase(Locale.US);
      if (map.containsKey(str2))
        map.put(str2, ""); 
    } 
  }
  
  public static void checkDangerousParams() {
    for (String str1 : toTest) {
      String str2 = System.getenv(str1);
      if (str2 != null) {
        str2 = str2.toLowerCase(Locale.US);
        if (str2.contains("-cp") || str2.contains("-classpath") || str2.contains("-javaagent") || str2.contains("-agentpath") || str2.contains("-agentlib"))
          throw new SecurityException("JavaAgent in global options not allow"); 
      } 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\EnvHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */