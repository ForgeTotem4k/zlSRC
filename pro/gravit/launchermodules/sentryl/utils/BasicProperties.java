package pro.gravit.launchermodules.sentryl.utils;

import io.sentry.Scope;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import pro.gravit.utils.helper.JVMHelper;

public class BasicProperties {
  public static void setupBasicProperties(Scope paramScope) {
    Properties properties = System.getProperties();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    hashMap1.put("Name", properties.getProperty("os.name"));
    hashMap1.put("Arch", String.valueOf(JVMHelper.ARCH_TYPE));
    paramScope.setContexts("OS", hashMap1);
    paramScope.setTag("OS_TYPE", properties.getProperty("os.name"));
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    hashMap2.put("Version", String.valueOf(JVMHelper.JVM_VERSION));
    hashMap2.put("Bits", String.valueOf(JVMHelper.JVM_BITS));
    hashMap2.put("runtime_mxbean", String.valueOf(JVMHelper.RUNTIME_MXBEAN.getVmVersion()));
    paramScope.setContexts("Java", hashMap2);
    paramScope.setTag("JVM_VERSION", String.valueOf(JVMHelper.JVM_VERSION));
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    hashMap3.put("Name", properties.getProperty("os.name"));
    hashMap3.put("file.encoding", properties.getProperty("file.encoding"));
    hashMap3.put("java.class.path", properties.getProperty("java.class.path"));
    hashMap3.put("java.class.version", properties.getProperty("java.class.version"));
    hashMap3.put("java.endorsed.dirs", properties.getProperty("java.endorsed.dirs"));
    hashMap3.put("java.ext.dirs", properties.getProperty("java.ext.dirs"));
    hashMap3.put("java.home", properties.getProperty("java.home"));
    hashMap3.put("java.io.tmpdir", properties.getProperty("java.io.tmpdir"));
    hashMap3.put("os.arch", properties.getProperty("os.arch"));
    hashMap3.put("sun.arch.data.model", properties.getProperty("sun.arch.data.model"));
    hashMap3.put("sun.boot.class.path", properties.getProperty("sun.boot.class.path"));
    hashMap3.put("sun.jnu.encoding", properties.getProperty("sun.jnu.encoding"));
    hashMap3.put("user.language", properties.getProperty("user.language"));
    hashMap3.put("user.timezone", properties.getProperty("user.timezone"));
    hashMap3.put("javafx.runtime.version", properties.getProperty("javafx.runtime.version"));
    paramScope.setContexts("System Properties", hashMap3);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Date:' yyyy.dd.MM 'Time:' HH:mm:ss 'Timezone:' X");
    Calendar calendar = Calendar.getInstance();
    paramScope.setContexts("User Time", simpleDateFormat.format(calendar.getTime()));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentry\\utils\BasicProperties.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */