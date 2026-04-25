package pro.gravit.launcher.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.base.modules.events.OfflineModeEvent;
import pro.gravit.launcher.base.modules.events.PreConfigPhase;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.GetAvailabilityAuthRequest;
import pro.gravit.launcher.base.request.websockets.OfflineRequestService;
import pro.gravit.launcher.base.request.websockets.StdWebSocketService;
import pro.gravit.launcher.client.BasicLauncherEventHandler;
import pro.gravit.launcher.client.ClientLauncherEntryPoint;
import pro.gravit.launcher.client.ClientLauncherMethods;
import pro.gravit.launcher.client.ClientParams;
import pro.gravit.launcher.client.RuntimeLauncherCoreModule;
import pro.gravit.launcher.client.events.ClientExitPhase;
import pro.gravit.launcher.client.utils.NativeJVMHalt;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.launcher.core.managers.GsonManager;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.launcher.runtime.client.RuntimeGsonManager;
import pro.gravit.launcher.runtime.client.events.ClientEngineInitPhase;
import pro.gravit.launcher.runtime.client.events.ClientPreGuiPhase;
import pro.gravit.launcher.runtime.console.GetPublicKeyCommand;
import pro.gravit.launcher.runtime.console.ModulesCommand;
import pro.gravit.launcher.runtime.console.SignDataCommand;
import pro.gravit.launcher.runtime.gui.NoRuntimeProvider;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;
import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.launcher.start.RuntimeModuleManager;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.EnvHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public class LauncherEngine {
  public static ClientParams clientParams;
  
  public static RuntimeModuleManager modulesManager;
  
  public final boolean clientInstance;
  
  private final AtomicBoolean started = new AtomicBoolean(false);
  
  public RuntimeProvider runtimeProvider;
  
  public ECPublicKey publicKey;
  
  public ECPrivateKey privateKey;
  
  public Class<? extends RuntimeProvider> basicRuntimeProvider;
  
  private LauncherEngine(boolean paramBoolean, Class<? extends RuntimeProvider> paramClass) {
    this.clientInstance = paramBoolean;
    this.basicRuntimeProvider = paramClass;
  }
  
  public static X509Certificate[] getCertificates(Class<?> paramClass) {
    Object[] arrayOfObject = paramClass.getSigners();
    return (arrayOfObject == null) ? null : (X509Certificate[])Arrays.<Object>stream(arrayOfObject).filter(paramObject -> paramObject instanceof X509Certificate).map(paramObject -> (X509Certificate)paramObject).toArray(paramInt -> new X509Certificate[paramInt]);
  }
  
  public static void checkClass(Class<?> paramClass) throws SecurityException {
    LauncherTrustManager launcherTrustManager = (Launcher.getConfig()).trustManager;
    if (launcherTrustManager == null)
      return; 
    X509Certificate[] arrayOfX509Certificate = getCertificates(paramClass);
    if (arrayOfX509Certificate == null)
      throw new SecurityException(String.format("Class %s not signed", new Object[] { paramClass.getName() })); 
    try {
      Objects.requireNonNull(launcherTrustManager);
      launcherTrustManager.checkCertificatesSuccess(arrayOfX509Certificate, launcherTrustManager::stdCertificateChecker);
    } catch (Exception exception) {
      throw new SecurityException(exception);
    } 
  }
  
  public static void beforeExit(int paramInt) {
    try {
      modulesManager.invokeEvent((LauncherModule.Event)new ClientExitPhase(paramInt));
    } catch (Throwable throwable) {}
  }
  
  public static void forceExit(int paramInt) {
    try {
      System.exit(paramInt);
    } catch (Throwable throwable) {
      NativeJVMHalt.haltA(paramInt);
    } 
  }
  
  public static void exitLauncher(int paramInt) {
    beforeExit(paramInt);
    forceExit(paramInt);
  }
  
  public static boolean contains(String[] paramArrayOfString, String paramString) {
    for (String str : paramArrayOfString) {
      if (str.equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public static void main(String... paramVarArgs) throws Throwable {
    JVMHelper.checkStackTrace(LauncherEngineWrapper.class);
    JVMHelper.verifySystemProperties(Launcher.class, false);
    checkClass(LauncherEngine.class.getClassLoader().getClass());
    EnvHelper.checkDangerousParams();
    verifyNoAgent();
    if (contains(paramVarArgs, "--log-output") && (Launcher.getConfig()).environment != LauncherConfig.LauncherEnvironment.PROD)
      LogHelper.addOutput(Paths.get("Launcher.log", new String[0])); 
    LogHelper.printVersion("Launcher");
    LogHelper.printLicense("Launcher");
    checkClass(LauncherEngineWrapper.class);
    checkClass(LauncherEngine.class);
    checkClass(ClientLauncherEntryPoint.class);
    modulesManager = new RuntimeModuleManager();
    modulesManager.loadModule((LauncherModule)new RuntimeLauncherCoreModule());
    LauncherConfig.initModules((LauncherModulesManager)modulesManager);
    modulesManager.initModules(null);
    initGson(modulesManager);
    ConsoleManager.initConsole();
    modulesManager.invokeEvent((LauncherModule.Event)new PreConfigPhase());
    Launcher.getConfig();
    long l1 = System.currentTimeMillis();
    try {
      newInstance(false).start(paramVarArgs);
    } catch (Exception exception) {
      LogHelper.error(exception);
      return;
    } 
    long l2 = System.currentTimeMillis();
    LogHelper.debug("Launcher started in %dms", new Object[] { Long.valueOf(l2 - l1) });
    exitLauncher(0);
  }
  
  public static void initGson(RuntimeModuleManager paramRuntimeModuleManager) {
    AuthRequest.registerProviders();
    GetAvailabilityAuthRequest.registerProviders();
    OptionalAction.registerProviders();
    OptionalTrigger.registerProviders();
    Launcher.gsonManager = (GsonManager)new RuntimeGsonManager(paramRuntimeModuleManager);
    Launcher.gsonManager.initGson();
  }
  
  public static void verifyNoAgent() {
    if (JVMHelper.RUNTIME_MXBEAN.getInputArguments().stream().filter(paramString -> (paramString != null && !paramString.isEmpty())).anyMatch(paramString -> paramString.contains("javaagent")))
      throw new SecurityException("JavaAgent found"); 
  }
  
  public static RequestService initOffline() {
    OfflineRequestService offlineRequestService = new OfflineRequestService();
    ClientLauncherMethods.applyBasicOfflineProcessors(offlineRequestService);
    OfflineModeEvent offlineModeEvent = new OfflineModeEvent((RequestService)offlineRequestService);
    modulesManager.invokeEvent((LauncherModule.Event)offlineModeEvent);
    return offlineModeEvent.service;
  }
  
  public static LauncherEngine newInstance(boolean paramBoolean) {
    return new LauncherEngine(paramBoolean, (Class)NoRuntimeProvider.class);
  }
  
  public static LauncherEngine newInstance(boolean paramBoolean, Class<? extends RuntimeProvider> paramClass) {
    return new LauncherEngine(paramBoolean, paramClass);
  }
  
  public ECPublicKey getClientPublicKey() {
    return this.publicKey;
  }
  
  public byte[] sign(byte[] paramArrayOfbyte) {
    return SecurityHelper.sign(paramArrayOfbyte, this.privateKey);
  }
  
  public void readKeys() throws IOException, InvalidKeySpecException {
    if (this.privateKey != null || this.publicKey != null)
      return; 
    Path path1 = DirBridge.dir;
    Path path2 = path1.resolve("public.key");
    Path path3 = path1.resolve("private.key");
    if (IOHelper.isFile(path2) && IOHelper.isFile(path3)) {
      LogHelper.info("Reading EC keypair");
      this.publicKey = SecurityHelper.toPublicECDSAKey(IOHelper.read(path2));
      this.privateKey = SecurityHelper.toPrivateECDSAKey(IOHelper.read(path3));
    } else {
      LogHelper.info("Generating EC keypair");
      KeyPair keyPair = SecurityHelper.genECDSAKeyPair(new SecureRandom());
      this.publicKey = (ECPublicKey)keyPair.getPublic();
      this.privateKey = (ECPrivateKey)keyPair.getPrivate();
      LogHelper.info("Writing EC keypair list");
      IOHelper.write(path2, this.publicKey.getEncoded());
      IOHelper.write(path3, this.privateKey.getEncoded());
    } 
  }
  
  public void start(String... paramVarArgs) throws Throwable {
    ClientPreGuiPhase clientPreGuiPhase = new ClientPreGuiPhase(null);
    modulesManager.invokeEvent((LauncherModule.Event)clientPreGuiPhase);
    this.runtimeProvider = clientPreGuiPhase.runtimeProvider;
    if (this.runtimeProvider == null)
      this.runtimeProvider = this.basicRuntimeProvider.getConstructor(new Class[0]).newInstance(new Object[0]); 
    this.runtimeProvider.init(this.clientInstance);
    if (!Request.isAvailable()) {
      RequestService requestService;
      String str = (Launcher.getConfig()).address;
      LogHelper.debug("Start async connection to %s", new Object[] { str });
      try {
        requestService = StdWebSocketService.initWebSockets(str).get();
      } catch (Throwable throwable) {
        if (LogHelper.isDebugEnabled())
          LogHelper.error(throwable); 
        LogHelper.warning("Launcher in offline mode");
        requestService = initOffline();
      } 
      Request.setRequestService(requestService);
      if (requestService instanceof StdWebSocketService)
        ((StdWebSocketService)requestService).reconnectCallback = (() -> {
            LogHelper.debug("WebSocket connect closed. Try reconnect");
            try {
              Request.reconnect();
            } catch (Exception exception) {
              LogHelper.error(exception);
              throw new RequestException("Connection failed", exception);
            } 
          }); 
    } 
    Request.startAutoRefresh();
    Request.getRequestService().registerEventHandler((RequestService.EventHandler)new BasicLauncherEventHandler());
    Objects.requireNonNull(paramVarArgs, "args");
    if (this.started.getAndSet(true))
      throw new IllegalStateException("Launcher has been already started"); 
    readKeys();
    registerCommands();
    modulesManager.invokeEvent((LauncherModule.Event)new ClientEngineInitPhase(this));
    this.runtimeProvider.preLoad();
    LogHelper.debug("Dir: %s", new Object[] { DirBridge.dir });
    this.runtimeProvider.run(paramVarArgs);
  }
  
  private void registerCommands() {
    ConsoleManager.handler.registerCommand("getpublickey", (Command)new GetPublicKeyCommand(this));
    ConsoleManager.handler.registerCommand("signdata", (Command)new SignDataCommand(this));
    ConsoleManager.handler.registerCommand("modules", (Command)new ModulesCommand());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\LauncherEngine.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */