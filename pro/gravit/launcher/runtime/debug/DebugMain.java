package pro.gravit.launcher.runtime.debug;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.events.OfflineModeEvent;
import pro.gravit.launcher.base.modules.events.PreConfigPhase;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.websockets.OfflineRequestService;
import pro.gravit.launcher.base.request.websockets.StdWebSocketService;
import pro.gravit.launcher.client.ClientLauncherMethods;
import pro.gravit.launcher.client.RuntimeLauncherCoreModule;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.launcher.start.RuntimeModuleManager;
import pro.gravit.utils.helper.LogHelper;

public class DebugMain {
  public static final AtomicBoolean IS_DEBUG = new AtomicBoolean(false);
  
  public static String webSocketURL = System.getProperty("launcherdebug.websocket", "ws://localhost:9274/api");
  
  public static String projectName = System.getProperty("launcherdebug.projectname", "Minecraft");
  
  public static String unlockSecret = System.getProperty("launcherdebug.unlocksecret", "");
  
  public static boolean disableConsole = Boolean.getBoolean("launcherdebug.disableConsole");
  
  public static boolean offlineMode = Boolean.getBoolean("launcherdebug.offlinemode");
  
  public static boolean disableAutoRefresh = Boolean.getBoolean("launcherdebug.disableautorefresh");
  
  public static String[] moduleClasses = System.getProperty("launcherdebug.modules", "").split(",");
  
  public static String[] moduleFiles = System.getProperty("launcherdebug.modulefiles", "").split(",");
  
  public static LauncherConfig.LauncherEnvironment environment = LauncherConfig.LauncherEnvironment.valueOf(System.getProperty("launcherdebug.env", "STD"));
  
  public static void main(String[] paramArrayOfString) throws Throwable {
    LogHelper.printVersion("Launcher");
    LogHelper.printLicense("Launcher");
    initialize();
    LogHelper.debug("Initialization LauncherEngine");
    LauncherEngine launcherEngine = LauncherEngine.newInstance(false, ClientRuntimeProvider.class);
    launcherEngine.start(paramArrayOfString);
    LauncherEngine.exitLauncher(0);
  }
  
  public static void initialize() throws Exception {
    RequestService requestService;
    IS_DEBUG.set(true);
    LogHelper.info("Launcher start in DEBUG mode (Only for developers)");
    LogHelper.debug("Initialization LauncherConfig");
    LauncherConfig launcherConfig = new LauncherConfig(webSocketURL, new HashMap<>(), projectName, environment, new DebugLauncherTrustManager(DebugLauncherTrustManager.TrustDebugMode.TRUST_ALL));
    launcherConfig.unlockSecret = unlockSecret;
    Launcher.setConfig(launcherConfig);
    Launcher.applyLauncherEnv(environment);
    LauncherEngine.modulesManager = new RuntimeModuleManager();
    LauncherEngine.modulesManager.loadModule((LauncherModule)new RuntimeLauncherCoreModule());
    for (String str : moduleClasses) {
      if (!str.isEmpty())
        LauncherEngine.modulesManager.loadModule(newModule(str)); 
    } 
    for (String str : moduleFiles) {
      if (!str.isEmpty())
        LauncherEngine.modulesManager.loadModule(Paths.get(str, new String[0])); 
    } 
    LauncherEngine.modulesManager.initModules(null);
    LauncherEngine.initGson(LauncherEngine.modulesManager);
    if (!disableConsole)
      ConsoleManager.initConsole(); 
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new PreConfigPhase());
    if (offlineMode) {
      OfflineRequestService offlineRequestService = new OfflineRequestService();
      ClientLauncherMethods.applyBasicOfflineProcessors(offlineRequestService);
      OfflineModeEvent offlineModeEvent = new OfflineModeEvent((RequestService)offlineRequestService);
      LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)offlineModeEvent);
      requestService = offlineModeEvent.service;
    } else {
      requestService = StdWebSocketService.initWebSockets(webSocketURL).get();
    } 
    Request.setRequestService(requestService);
    if (!disableAutoRefresh)
      Request.startAutoRefresh(); 
  }
  
  public static LauncherModule newModule(String paramString) throws ClassNotFoundException, InvocationTargetException {
    Class<?> clazz = Class.forName(paramString);
    try {
      return MethodHandles.publicLookup().findConstructor(clazz, MethodType.methodType(void.class)).invoke();
    } catch (Throwable throwable) {
      throw new InvocationTargetException(throwable);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\debug\DebugMain.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */