package pro.gravit.launcher.start;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.utils.Version;
import pro.gravit.utils.helper.EnvHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.JavaHelper;
import pro.gravit.utils.helper.LogHelper;

public class ClientLauncherWrapper {
  public static final String MAGIC_ARG = "-Djdk.attach.allowAttachSelf";
  
  public static final String WAIT_PROCESS_PROPERTY = "launcher.waitProcess";
  
  public static final String NO_JAVA_CHECK_PROPERTY = "launcher.noJavaCheck";
  
  public static boolean noJavaCheck = Boolean.getBoolean("launcher.noJavaCheck");
  
  public static boolean waitProcess = Boolean.getBoolean("launcher.waitProcess");
  
  public static int launcherMemoryLimit = 512;
  
  public static List<String> customJvmOptions = new ArrayList<>(1);
  
  public static RuntimeModuleManager modulesManager;
  
  public static boolean contains(String[] paramArrayOfString, String paramString) {
    for (String str : paramArrayOfString) {
      if (str.equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public static void main(String[] paramArrayOfString) throws IOException, InterruptedException {
    LogHelper.printVersion("Launcher");
    LogHelper.printLicense("Launcher");
    JVMHelper.checkStackTrace(ClientLauncherWrapper.class);
    JVMHelper.verifySystemProperties(Launcher.class, true);
    EnvHelper.checkDangerousParams();
    LauncherConfig launcherConfig = Launcher.getConfig();
    modulesManager = new RuntimeModuleManager();
    LauncherConfig.initModules((LauncherModulesManager)modulesManager);
    LogHelper.info("Launcher for project %s", new Object[] { launcherConfig.projectName });
    if (launcherConfig.environment.equals(LauncherConfig.LauncherEnvironment.PROD)) {
      if (System.getProperty("launcher.debug") != null)
        LogHelper.warning("Found -Dlauncher.debug=true"); 
      if (System.getProperty("launcher.stacktrace") != null)
        LogHelper.warning("Found -Dlauncher.stacktrace=true"); 
      LogHelper.info("Debug mode disabled (found env PRODUCTION)");
    } else {
      LogHelper.info("If need debug output use -Dlauncher.debug=true");
      LogHelper.info("If need stacktrace output use -Dlauncher.stacktrace=true");
      if (contains(paramArrayOfString, "--debug")) {
        LogHelper.setDebugEnabled(true);
        LogHelper.setStacktraceEnabled(true);
      } 
      if (LogHelper.isDebugEnabled())
        waitProcess = true; 
    } 
    LogHelper.info("Restart Launcher with JavaAgent...");
    ClientLauncherWrapperContext clientLauncherWrapperContext = new ClientLauncherWrapperContext();
    clientLauncherWrapperContext.processBuilder = new ProcessBuilder(new String[0]);
    if (waitProcess)
      clientLauncherWrapperContext.processBuilder.inheritIO(); 
    clientLauncherWrapperContext.javaVersion = null;
    try {
      if (!noJavaCheck) {
        List list = JavaHelper.findJava();
        for (JavaHelper.JavaVersion javaVersion : list) {
          LogHelper.debug("Found Java %d b%d in %s javafx %s", new Object[] { Integer.valueOf(javaVersion.version), Integer.valueOf(javaVersion.build), javaVersion.jvmDir.toString(), javaVersion.enabledJavaFX ? "supported" : "not supported" });
          if (clientLauncherWrapperContext.javaVersion == null) {
            clientLauncherWrapperContext.javaVersion = javaVersion;
            continue;
          } 
          if (javaVersion.enabledJavaFX && !clientLauncherWrapperContext.javaVersion.enabledJavaFX) {
            clientLauncherWrapperContext.javaVersion = javaVersion;
            continue;
          } 
          if (javaVersion.enabledJavaFX == clientLauncherWrapperContext.javaVersion.enabledJavaFX) {
            if (clientLauncherWrapperContext.javaVersion.version < javaVersion.version) {
              clientLauncherWrapperContext.javaVersion = javaVersion;
              continue;
            } 
            if (clientLauncherWrapperContext.javaVersion.version == javaVersion.version && clientLauncherWrapperContext.javaVersion.build < javaVersion.build)
              clientLauncherWrapperContext.javaVersion = javaVersion; 
          } 
        } 
      } 
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
    } 
    if (clientLauncherWrapperContext.javaVersion == null)
      clientLauncherWrapperContext.javaVersion = JavaHelper.JavaVersion.getCurrentJavaVersion(); 
    if (clientLauncherWrapperContext.javaVersion.version < 17) {
      String str1 = String.format("GravitLauncher v%s required Java 17 or higher", new Object[] { Version.getVersion() });
      LogHelper.error(str1);
      JOptionPane.showMessageDialog(null, str1, "GravitLauncher", 0);
      System.exit(0);
    } 
    clientLauncherWrapperContext.executePath = IOHelper.resolveJavaBin(clientLauncherWrapperContext.javaVersion.jvmDir);
    String str = IOHelper.getCodeSource(ClientLauncherWrapper.class).toString();
    clientLauncherWrapperContext.mainClass = "pro.gravit.launcher.runtime.LauncherEngineWrapper";
    clientLauncherWrapperContext.memoryLimit = launcherMemoryLimit;
    clientLauncherWrapperContext.classpath.add(str);
    clientLauncherWrapperContext.jvmProperties.put("launcher.debug", Boolean.toString(LogHelper.isDebugEnabled()));
    clientLauncherWrapperContext.jvmProperties.put("launcher.stacktrace", Boolean.toString(LogHelper.isStacktraceEnabled()));
    clientLauncherWrapperContext.jvmProperties.put("launcher.dev", Boolean.toString(LogHelper.isDevEnabled()));
    clientLauncherWrapperContext.jvmModules.add("javafx.base");
    clientLauncherWrapperContext.jvmModules.add("javafx.graphics");
    clientLauncherWrapperContext.jvmModules.add("javafx.fxml");
    clientLauncherWrapperContext.jvmModules.add("javafx.controls");
    clientLauncherWrapperContext.jvmModules.add("javafx.media");
    clientLauncherWrapperContext.jvmModules.add("javafx.web");
    clientLauncherWrapperContext.args.add("-Djdk.attach.allowAttachSelf");
    clientLauncherWrapperContext.args.add("-XX:+DisableAttachMechanism");
    clientLauncherWrapperContext.clientArgs.addAll(Arrays.asList(paramArrayOfString));
    EnvHelper.addEnv(clientLauncherWrapperContext.processBuilder);
    modulesManager.callWrapper(clientLauncherWrapperContext);
    ArrayList<String> arrayList = new ArrayList(16);
    arrayList.add(clientLauncherWrapperContext.executePath.toAbsolutePath().toString());
    arrayList.addAll(clientLauncherWrapperContext.args);
    clientLauncherWrapperContext.jvmProperties.forEach((paramString1, paramString2) -> paramList.add(String.format("-D%s=%s", new Object[] { paramString1, paramString2 })));
    if (clientLauncherWrapperContext.memoryLimit != 0)
      arrayList.add(String.format("-Xmx%dM", new Object[] { Integer.valueOf(clientLauncherWrapperContext.memoryLimit) })); 
    if (customJvmOptions != null)
      arrayList.addAll(customJvmOptions); 
    if (clientLauncherWrapperContext.useLegacyClasspathProperty) {
      arrayList.add(String.format("-Djava.class.path=%s", new Object[] { String.join(IOHelper.PLATFORM_SEPARATOR, (Iterable)clientLauncherWrapperContext.classpath) }));
    } else {
      arrayList.add("-cp");
      arrayList.add(String.join(IOHelper.PLATFORM_SEPARATOR, (Iterable)clientLauncherWrapperContext.classpath));
    } 
    arrayList.add(clientLauncherWrapperContext.mainClass);
    arrayList.addAll(clientLauncherWrapperContext.clientArgs);
    LogHelper.debug("Commandline: " + String.valueOf(arrayList));
    clientLauncherWrapperContext.processBuilder.command(arrayList);
    Process process = clientLauncherWrapperContext.processBuilder.start();
    if (!waitProcess) {
      Thread.sleep(3000L);
      if (!process.isAlive()) {
        int i = process.exitValue();
        if (i != 0) {
          LogHelper.error("Process exit with error code: %d", new Object[] { Integer.valueOf(i) });
        } else {
          LogHelper.info("Process exit with code 0");
        } 
      } else {
        LogHelper.debug("Process started success");
      } 
    } else {
      process.waitFor();
    } 
  }
  
  static {
    (new ArrayList<>(1)).add("-Dfile.encoding=UTF-8");
  }
  
  public static class ClientLauncherWrapperContext {
    public JavaHelper.JavaVersion javaVersion;
    
    public Path executePath;
    
    public String mainClass;
    
    public int memoryLimit;
    
    public boolean useLegacyClasspathProperty;
    
    public ProcessBuilder processBuilder;
    
    public List<String> args = new ArrayList<>(8);
    
    public Map<String, String> jvmProperties = new HashMap<>();
    
    public List<String> classpath = new ArrayList<>();
    
    public List<String> jvmModules = new ArrayList<>();
    
    public List<String> clientArgs = new ArrayList<>();
    
    public List<Path> javaFXPaths = new ArrayList<>();
    
    public void addSystemProperty(String param1String) {
      String str = System.getProperty(param1String);
      if (str != null)
        this.jvmProperties.put(param1String, str); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\start\ClientLauncherWrapper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */