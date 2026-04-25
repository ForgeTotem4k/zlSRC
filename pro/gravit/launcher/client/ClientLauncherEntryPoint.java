package pro.gravit.launcher.client;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.CipherInputStream;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.api.AuthService;
import pro.gravit.launcher.base.api.ClientService;
import pro.gravit.launcher.base.api.KeyService;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.base.modules.events.PreConfigPhase;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.ClientProfileVersions;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalActionClassPath;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalActionClientArgs;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.websockets.StdWebSocketService;
import pro.gravit.launcher.client.events.ClientProcessClassLoaderEvent;
import pro.gravit.launcher.client.events.ClientProcessInitPhase;
import pro.gravit.launcher.client.events.ClientProcessLaunchEvent;
import pro.gravit.launcher.client.events.ClientProcessPreInvokeMainClassEvent;
import pro.gravit.launcher.client.events.ClientProcessReadyEvent;
import pro.gravit.launcher.client.utils.DirWatcher;
import pro.gravit.launcher.core.hasher.FileNameMatcher;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.EnvHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;
import pro.gravit.utils.launch.BasicLaunch;
import pro.gravit.utils.launch.ClassLoaderControl;
import pro.gravit.utils.launch.Launch;
import pro.gravit.utils.launch.LaunchOptions;
import pro.gravit.utils.launch.LegacyLaunch;
import pro.gravit.utils.launch.ModuleLaunch;

public class ClientLauncherEntryPoint {
  public static ClientModuleManager modulesManager;
  
  public static ClientParams clientParams;
  
  private static Launch launch;
  
  private static ClassLoaderControl classLoaderControl;
  
  private static ClientParams readParams(SocketAddress paramSocketAddress) throws IOException {
    Socket socket = IOHelper.newSocket();
    try {
      socket.connect(paramSocketAddress);
      HInput hInput = new HInput(new CipherInputStream(socket.getInputStream(), SecurityHelper.newAESDecryptCipher(SecurityHelper.fromHex((Launcher.getConfig()).secretKeyClient))));
      try {
        byte[] arrayOfByte = hInput.readByteArray(0);
        ClientParams clientParams1 = (ClientParams)Launcher.gsonManager.gson.fromJson(IOHelper.decode(arrayOfByte), ClientParams.class);
        clientParams1.clientHDir = new HashedDir(hInput);
        clientParams1.assetHDir = new HashedDir(hInput);
        boolean bool = hInput.readBoolean();
        if (bool)
          clientParams1.javaHDir = new HashedDir(hInput); 
        ClientParams clientParams2 = clientParams1;
        hInput.close();
        if (socket != null)
          socket.close(); 
        return clientParams2;
      } catch (Throwable throwable) {
        try {
          hInput.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      if (socket != null)
        try {
          socket.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    JVMHelper.verifySystemProperties(ClientLauncherEntryPoint.class, true);
    EnvHelper.checkDangerousParams();
    JVMHelper.checkStackTrace(ClientLauncherEntryPoint.class);
    LogHelper.printVersion("Client Launcher");
    ClientLauncherMethods.checkClass(ClientLauncherEntryPoint.class);
    try {
      realMain(paramArrayOfString);
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
    } 
  }
  
  private static void realMain(String[] paramArrayOfString) throws Throwable {
    modulesManager = new ClientModuleManager();
    modulesManager.loadModule(new ClientLauncherCoreModule());
    LauncherConfig.initModules((LauncherModulesManager)modulesManager);
    modulesManager.initModules(null);
    ClientLauncherMethods.initGson(modulesManager);
    modulesManager.invokeEvent((LauncherModule.Event)new PreConfigPhase());
    LogHelper.debug("Reading ClientLauncher params");
    ClientParams clientParams = readParams(new InetSocketAddress("127.0.0.1", (Launcher.getConfig()).clientPort));
    ClientLauncherMethods.verifyNoAgent();
    if (clientParams.timestamp > System.currentTimeMillis() || clientParams.timestamp + 30000L < System.currentTimeMillis()) {
      LogHelper.error("Timestamp failed: current %d | params %d | diff %d", new Object[] { Long.valueOf(System.currentTimeMillis()), Long.valueOf(clientParams.timestamp), Long.valueOf(System.currentTimeMillis() - clientParams.timestamp) });
      ClientLauncherMethods.exitLauncher(-662);
      return;
    } 
    ClientProfile clientProfile = clientParams.profile;
    Launcher.profile = clientProfile;
    AuthService.profile = clientProfile;
    clientParams = clientParams;
    if (clientParams.oauth != null) {
      LogHelper.info("Using OAuth");
      if (clientParams.oauthExpiredTime != 0L) {
        Request.setOAuth(clientParams.authId, clientParams.oauth, clientParams.oauthExpiredTime);
      } else {
        Request.setOAuth(clientParams.authId, clientParams.oauth);
      } 
      if (clientParams.extendedTokens != null)
        Request.addAllExtendedToken(clientParams.extendedTokens); 
    } else if (clientParams.session != null) {
      throw new UnsupportedOperationException("Legacy session not supported");
    } 
    modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessInitPhase(clientParams));
    Path path1 = Paths.get(clientParams.clientDir, new String[0]);
    Path path2 = Paths.get(clientParams.assetDir, new String[0]);
    LogHelper.debug("Verifying ClientLauncher sign and classpath");
    if (clientParams.offlineMode) {
      RequestService requestService = ClientLauncherMethods.initOffline((LauncherModulesManager)modulesManager, clientParams);
      Request.setRequestService(requestService);
    } else {
      RequestService requestService = StdWebSocketService.initWebSockets((Launcher.getConfig()).address).get();
      Request.setRequestService(requestService);
      LogHelper.debug("Restore sessions");
      Request.restore(false, false, true);
      requestService.registerEventHandler(new BasicLauncherEventHandler());
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
    LogHelper.debug("Natives dir %s", new Object[] { clientParams.nativesDir });
    ClientProfile.ClassLoaderConfig classLoaderConfig = clientProfile.getClassLoaderConfig();
    LaunchOptions launchOptions = new LaunchOptions();
    launchOptions.enableHacks = clientProfile.hasFlag(ClientProfile.CompatibilityFlags.ENABLE_HACKS);
    launchOptions.moduleConf = clientProfile.getModuleConf();
    ClientService.nativePath = clientParams.nativesDir;
    if (clientProfile.getLoadNatives() != null)
      for (String str : clientProfile.getLoadNatives())
        System.load(Paths.get(clientParams.nativesDir, new String[0]).resolve(ClientService.findLibrary(str)).toAbsolutePath().toString());  
    HashSet<Path> hashSet = new HashSet();
    if (launchOptions.moduleConf != null && launchOptions.moduleConf.modulePath != null)
      List<Path> list = resolveClassPathStream(hashSet, path1, launchOptions.moduleConf.modulePath).toList(); 
    List list1 = resolveClassPath(hashSet, path1, clientParams.actions, clientParams.profile).collect(Collectors.toCollection(ArrayList::new));
    if (LogHelper.isDevEnabled())
      for (Path path : list1) {
        LogHelper.dev("Classpath entry %s", new Object[] { path });
      }  
    List list2 = list1.stream().map(IOHelper::toURL).toList();
    if (classLoaderConfig == ClientProfile.ClassLoaderConfig.LAUNCHER || classLoaderConfig == ClientProfile.ClassLoaderConfig.MODULE) {
      if (JVMHelper.JVM_VERSION <= 11) {
        launch = (Launch)new LegacyLaunch();
      } else {
        launch = (Launch)new ModuleLaunch();
      } 
      classLoaderControl = launch.init(list1, clientParams.nativesDir, launchOptions);
      System.setProperty("java.class.path", list1.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
      modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessClassLoaderEvent(launch, classLoaderControl, clientProfile));
      ClientService.baseURLs = classLoaderControl.getURLs();
    } else if (classLoaderConfig == ClientProfile.ClassLoaderConfig.SYSTEM_ARGS) {
      launch = (Launch)new BasicLaunch();
      classLoaderControl = launch.init(list1, clientParams.nativesDir, launchOptions);
      ClientService.baseURLs = (URL[])list2.toArray((Object[])new URL[0]);
    } else {
      throw new UnsupportedOperationException(String.format("Unknown classLoaderConfig %s", new Object[] { classLoaderConfig }));
    } 
    if (clientProfile.hasFlag(ClientProfile.CompatibilityFlags.CLASS_CONTROL_API))
      ClientService.classLoaderControl = classLoaderControl; 
    if (clientParams.lwjglGlfwWayland && clientProfile.hasFlag(ClientProfile.CompatibilityFlags.WAYLAND_USE_CUSTOM_GLFW)) {
      String str = ClientService.findLibrary("glfw_wayland");
      System.setProperty("org.lwjgl.glfw.libname", str);
    } 
    AuthService.projectName = (Launcher.getConfig()).projectName;
    AuthService.username = clientParams.playerProfile.username;
    AuthService.uuid = clientParams.playerProfile.uuid;
    KeyService.serverRsaPublicKey = (Launcher.getConfig()).rsaPublicKey;
    KeyService.serverEcPublicKey = (Launcher.getConfig()).ecdsaPublicKey;
    modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessReadyEvent(clientParams));
    LogHelper.debug("Starting JVM and client WatchService");
    FileNameMatcher fileNameMatcher1 = clientProfile.getAssetUpdateMatcher();
    FileNameMatcher fileNameMatcher2 = clientProfile.getClientUpdateMatcher();
    Path path3 = Paths.get(System.getProperty("java.home"), new String[0]);
    DirWatcher dirWatcher = new DirWatcher(path2, clientParams.assetHDir, fileNameMatcher1, true);
    try {
      DirWatcher dirWatcher1 = new DirWatcher(path1, clientParams.clientHDir, fileNameMatcher2, true);
      try {
        DirWatcher dirWatcher2 = (clientParams.javaHDir == null) ? null : new DirWatcher(path3, clientParams.javaHDir, null, true);
        try {
          CommonHelper.newThread("Asset Directory Watcher", true, (Runnable)dirWatcher).start();
          CommonHelper.newThread("Client Directory Watcher", true, (Runnable)dirWatcher1).start();
          if (dirWatcher2 != null)
            CommonHelper.newThread("Java Directory Watcher", true, (Runnable)dirWatcher2).start(); 
          verifyHDir(path2, clientParams.assetHDir, fileNameMatcher1, false, false);
          verifyHDir(path1, clientParams.clientHDir, fileNameMatcher2, false, true);
          if (dirWatcher2 != null)
            verifyHDir(path3, clientParams.javaHDir, null, false, true); 
          modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessLaunchEvent(clientParams));
          launch(clientProfile, clientParams);
          if (dirWatcher2 != null)
            dirWatcher2.close(); 
        } catch (Throwable throwable) {
          if (dirWatcher2 != null)
            try {
              dirWatcher2.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        dirWatcher1.close();
      } catch (Throwable throwable) {
        try {
          dirWatcher1.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      dirWatcher.close();
    } catch (Throwable throwable) {
      try {
        dirWatcher.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static void verifyHDir(Path paramPath, HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    HashedDir hashedDir = new HashedDir(paramPath, paramFileNameMatcher, true, paramBoolean1);
    HashedDir.Diff diff = paramHashedDir.diff(hashedDir, paramFileNameMatcher);
    AtomicReference<String> atomicReference = new AtomicReference<>("unknown");
    if (!diff.mismatch.isEmpty() || (paramBoolean2 && !diff.extra.isEmpty())) {
      diff.extra.walk(File.separator, (paramString1, paramString2, paramHashedEntry) -> {
            if (paramHashedEntry.getType().equals(HashedEntry.Type.FILE)) {
              LogHelper.error("Extra file %s", new Object[] { paramString1 });
              paramAtomicReference.set(paramString1);
            } else {
              LogHelper.error("Extra %s", new Object[] { paramString1 });
            } 
            return HashedDir.WalkAction.CONTINUE;
          });
      diff.mismatch.walk(File.separator, (paramString1, paramString2, paramHashedEntry) -> {
            if (paramHashedEntry.getType().equals(HashedEntry.Type.FILE)) {
              LogHelper.error("Mismatch file %s", new Object[] { paramString1 });
              paramAtomicReference.set(paramString1);
            } else {
              LogHelper.error("Mismatch %s", new Object[] { paramString1 });
            } 
            return HashedDir.WalkAction.CONTINUE;
          });
      throw new SecurityException(String.format("Forbidden modification: '%s' file '%s'", new Object[] { IOHelper.getFileName(paramPath), atomicReference.get() }));
    } 
  }
  
  private static LinkedList<Path> resolveClassPathList(Set<Path> paramSet, Path paramPath, List<String> paramList) throws IOException {
    return resolveClassPathStream(paramSet, paramPath, paramList).collect(Collectors.toCollection(LinkedList::new));
  }
  
  private static Stream<Path> resolveClassPathStream(Set<Path> paramSet, Path paramPath, List<String> paramList) throws IOException {
    Stream.Builder<?> builder = Stream.builder();
    for (String str : paramList) {
      Path path = paramPath.resolve(IOHelper.toPath(str.replace("/", IOHelper.PLATFORM_SEPARATOR)));
      if (IOHelper.isDir(path)) {
        ArrayList<Path> arrayList = new ArrayList(32);
        IOHelper.walk(path, new ClassPathFileVisitor(arrayList), false);
        Collections.sort(arrayList);
        for (Path path1 : arrayList) {
          if (paramSet.contains(path1))
            continue; 
          builder.accept(path1);
          paramSet.add(path1);
        } 
        continue;
      } 
      if (paramSet.contains(path))
        continue; 
      builder.accept(path);
      paramSet.add(path);
    } 
    return (Stream)builder.build();
  }
  
  public static Stream<Path> resolveClassPath(Set<Path> paramSet, Path paramPath, Set<OptionalAction> paramSet1, ClientProfile paramClientProfile) throws IOException {
    Stream<Path> stream = resolveClassPathStream(paramSet, paramPath, paramClientProfile.getClassPath());
    if (paramSet1 != null)
      for (OptionalAction optionalAction : paramSet1) {
        if (optionalAction instanceof OptionalActionClassPath)
          stream = Stream.concat(stream, resolveClassPathStream(paramSet, paramPath, ((OptionalActionClassPath)optionalAction).args)); 
      }  
    return stream;
  }
  
  private static void launch(ClientProfile paramClientProfile, ClientParams paramClientParams) throws Throwable {
    LinkedList<String> linkedList = new LinkedList();
    if (paramClientProfile.getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_6_4) >= 0) {
      paramClientParams.addClientArgs(linkedList);
    } else {
      paramClientParams.addClientLegacyArgs(linkedList);
      System.setProperty("minecraft.applet.TargetDirectory", paramClientParams.clientDir);
    } 
    linkedList.addAll(paramClientProfile.getClientArgs());
    for (OptionalAction optionalAction : paramClientParams.actions) {
      if (optionalAction instanceof OptionalActionClientArgs)
        linkedList.addAll(((OptionalActionClientArgs)optionalAction).args); 
    } 
    ArrayList<String> arrayList = new ArrayList<>(linkedList);
    byte b = 0;
    int i = arrayList.size();
    while (b < i) {
      String str = arrayList.get(b);
      if (b + 1 < i && ("--accessToken".equals(str) || "--session".equals(str)))
        arrayList.set(b + 1, "censored"); 
      b++;
    } 
    LogHelper.debug("Args: " + String.valueOf(arrayList));
    modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessPreInvokeMainClassEvent(paramClientParams, paramClientProfile, linkedList));
    try {
      List list = paramClientProfile.getCompatClasses();
      for (String str : list) {
        Class<?> clazz = classLoaderControl.getClass(str);
        MethodHandle methodHandle = MethodHandles.lookup().findStatic(clazz, "run", MethodType.methodType(void.class, ClassLoaderControl.class));
        methodHandle.invoke(classLoaderControl);
      } 
      Launcher.LAUNCHED.set(true);
      JVMHelper.fullGC();
      launch.launch(paramClientParams.profile.getMainClass(), paramClientParams.profile.getMainModule(), linkedList);
      LogHelper.debug("Main exit successful");
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      throw throwable;
    } finally {
      ClientLauncherMethods.exitLauncher(0);
    } 
  }
  
  private static final class ClassPathFileVisitor extends SimpleFileVisitor<Path> {
    private final List<Path> result;
    
    private ClassPathFileVisitor(List<Path> param1List) {
      this.result = param1List;
    }
    
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      if (IOHelper.hasExtension(param1Path, "jar") || IOHelper.hasExtension(param1Path, "zip"))
        this.result.add(param1Path); 
      return super.visitFile(param1Path, param1BasicFileAttributes);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientLauncherEntryPoint.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */