package pro.gravit.launcher.runtime.client;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.crypto.CipherOutputStream;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalActionJvmArgs;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.client.ClientLauncherEntryPoint;
import pro.gravit.launcher.client.ClientParams;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.client.events.ClientProcessBuilderCreateEvent;
import pro.gravit.launcher.runtime.client.events.ClientProcessBuilderLaunchedEvent;
import pro.gravit.launcher.runtime.client.events.ClientProcessBuilderParamsWrittedEvent;
import pro.gravit.launcher.runtime.client.events.ClientProcessBuilderPreLaunchEvent;
import pro.gravit.utils.helper.EnvHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.JavaHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;
import pro.gravit.utils.launch.LaunchOptions;

public class ClientLauncherProcess {
  public final List<String> pre;
  
  public final ClientParams params;
  
  public final List<String> jvmArgs;
  
  public final List<String> jvmModules;
  
  public final List<String> jvmModulesPaths;
  
  public final List<String> systemClientArgs;
  
  public final List<String> systemClassPath;
  
  public final Map<String, String> systemEnv;
  
  public final String mainClass;
  
  private final transient Boolean[] waitWriteParams;
  
  public Path executeFile;
  
  public Path workDir;
  
  public JavaHelper.JavaVersion javaVersion;
  
  public boolean useLegacyJavaClassPathProperty;
  
  public boolean isStarted;
  
  private transient Process process;
  
  public ClientLauncherProcess(Path paramPath1, Path paramPath2, JavaHelper.JavaVersion paramJavaVersion, String paramString) {
    this.pre = new LinkedList<>();
    this.params = new ClientParams();
    this.jvmArgs = new LinkedList<>();
    this.jvmModules = new LinkedList<>();
    this.jvmModulesPaths = new LinkedList<>();
    this.systemClientArgs = new LinkedList<>();
    this.systemClassPath = new LinkedList<>();
    this.systemEnv = new HashMap<>();
    this.waitWriteParams = new Boolean[] { Boolean.valueOf(false) };
    this.executeFile = paramPath1;
    this.workDir = paramPath2;
    this.javaVersion = paramJavaVersion;
    this.mainClass = paramString;
  }
  
  public ClientLauncherProcess(Path paramPath1, Path paramPath2, JavaHelper.JavaVersion paramJavaVersion, ClientProfile paramClientProfile, PlayerProfile paramPlayerProfile, String paramString, HashedDir paramHashedDir1, HashedDir paramHashedDir2, HashedDir paramHashedDir3) {
    this(paramPath1, paramPath2, paramJavaVersion, paramPath1.resolve("resourcepacks"), paramClientProfile, paramPlayerProfile, null, paramString, paramHashedDir1, paramHashedDir2, paramHashedDir3);
  }
  
  public ClientLauncherProcess(Path paramPath1, Path paramPath2, ClientProfile paramClientProfile, PlayerProfile paramPlayerProfile, String paramString, HashedDir paramHashedDir1, HashedDir paramHashedDir2, HashedDir paramHashedDir3) {
    this(paramPath1, paramPath2, JavaHelper.JavaVersion.getCurrentJavaVersion(), paramPath1.resolve("resourcepacks"), paramClientProfile, paramPlayerProfile, null, paramString, paramHashedDir1, paramHashedDir2, paramHashedDir3);
  }
  
  public ClientLauncherProcess(Path paramPath1, Path paramPath2, JavaHelper.JavaVersion paramJavaVersion, Path paramPath3, ClientProfile paramClientProfile, PlayerProfile paramPlayerProfile, OptionalView paramOptionalView, String paramString, HashedDir paramHashedDir1, HashedDir paramHashedDir2, HashedDir paramHashedDir3) {
    Path path;
    this.pre = new LinkedList<>();
    this.params = new ClientParams();
    this.jvmArgs = new LinkedList<>();
    this.jvmModules = new LinkedList<>();
    this.jvmModulesPaths = new LinkedList<>();
    this.systemClientArgs = new LinkedList<>();
    this.systemClassPath = new LinkedList<>();
    this.systemEnv = new HashMap<>();
    this.waitWriteParams = new Boolean[] { Boolean.valueOf(false) };
    this.javaVersion = paramJavaVersion;
    this.workDir = paramPath1.toAbsolutePath();
    this.executeFile = IOHelper.resolveJavaBin(this.javaVersion.jvmDir);
    this.mainClass = ClientLauncherEntryPoint.class.getName();
    this.params.clientDir = this.workDir.toString();
    this.params.resourcePackDir = paramPath3.toAbsolutePath().toString();
    this.params.assetDir = paramPath2.toAbsolutePath().toString();
    this.params.timestamp = System.currentTimeMillis();
    if (paramClientProfile.hasFlag(ClientProfile.CompatibilityFlags.LEGACY_NATIVES_DIR)) {
      path = this.workDir.resolve("natives");
    } else {
      path = this.workDir.resolve("natives").resolve(JVMHelper.OS_TYPE.name).resolve(paramJavaVersion.arch.name);
    } 
    if (!Files.isDirectory(path, new java.nio.file.LinkOption[0]))
      throw new RuntimeException(String.format("Natives dir %s not exist! Your operating system or architecture not supported", new Object[] { path.toAbsolutePath() })); 
    this.params.nativesDir = path.toString();
    this.params.profile = paramClientProfile;
    this.params.playerProfile = paramPlayerProfile;
    this.params.accessToken = paramString;
    this.params.assetHDir = paramHashedDir2;
    this.params.clientHDir = paramHashedDir1;
    this.params.javaHDir = paramHashedDir3;
    if (paramOptionalView != null)
      this.params.actions = paramOptionalView.getEnabledActions(); 
    applyClientProfile();
  }
  
  public static String getPathSeparator() {
    return (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) ? ";" : ":";
  }
  
  private void applyClientProfile() {
    this.systemClassPath.add(IOHelper.getCodeSource(ClientLauncherEntryPoint.class).toAbsolutePath().toString());
    this.jvmArgs.addAll(this.params.profile.getJvmArgs());
    for (OptionalAction optionalAction : this.params.actions) {
      if (optionalAction instanceof OptionalActionJvmArgs)
        this.jvmArgs.addAll(((OptionalActionJvmArgs)optionalAction).args); 
    } 
    this.systemEnv.put("JAVA_HOME", this.javaVersion.jvmDir.toString());
    this.systemClassPath.addAll(this.params.profile.getAlternativeClassPath());
    if (this.params.ram > 0)
      this.jvmArgs.add("-Xmx" + this.params.ram + "M"); 
    this.params.oauth = Request.getOAuth();
    if (this.params.oauth == null)
      throw new UnsupportedOperationException("Legacy session not supported"); 
    this.params.authId = Request.getAuthId();
    this.params.oauthExpiredTime = Request.getTokenExpiredTime();
    this.params.extendedTokens = Request.getExtendedTokens();
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessBuilderCreateEvent(this));
  }
  
  public void start(boolean paramBoolean) throws IOException, InterruptedException {
    if (this.isStarted)
      throw new IllegalStateException("Process already started"); 
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessBuilderPreLaunchEvent(this));
    LinkedList<String> linkedList = new LinkedList<>(this.pre);
    linkedList.add(this.executeFile.toString());
    linkedList.addAll(this.jvmArgs);
    if (this.javaVersion.version >= 9)
      applyJava9Params(linkedList); 
    linkedList.add(JVMHelper.jvmProperty("java.library.path", this.params.nativesDir));
    if (this.params.profile.getClassLoaderConfig() == ClientProfile.ClassLoaderConfig.SYSTEM_ARGS) {
      HashSet<Path> hashSet = new HashSet();
      LaunchOptions.ModuleConf moduleConf = this.params.profile.getModuleConf();
      if (moduleConf != null) {
        if (moduleConf.modulePath != null && !moduleConf.modulePath.isEmpty()) {
          linkedList.add("-p");
          for (String str : moduleConf.modulePath)
            hashSet.add(Path.of(str, new String[0])); 
          linkedList.add(String.join(File.pathSeparator, moduleConf.modulePath));
        } 
        if (moduleConf.modules != null && !moduleConf.modules.isEmpty()) {
          linkedList.add("--add-modules");
          linkedList.add(String.join(",", moduleConf.modules));
        } 
        if (moduleConf.exports != null && !moduleConf.exports.isEmpty())
          for (Map.Entry entry : moduleConf.exports.entrySet()) {
            linkedList.add("--add-exports");
            linkedList.add(String.format("%s=%s", new Object[] { entry.getKey(), entry.getValue() }));
          }  
        if (moduleConf.opens != null && !moduleConf.opens.isEmpty())
          for (Map.Entry entry : moduleConf.opens.entrySet()) {
            linkedList.add("--add-opens");
            linkedList.add(String.format("%s=%s", new Object[] { entry.getKey(), entry.getValue() }));
          }  
        if (moduleConf.reads != null && !moduleConf.reads.isEmpty())
          for (Map.Entry entry : moduleConf.reads.entrySet()) {
            linkedList.add("--add-reads");
            linkedList.add(String.format("%s=%s", new Object[] { entry.getKey(), entry.getValue() }));
          }  
      } 
      this.systemClassPath.addAll(ClientLauncherEntryPoint.resolveClassPath(hashSet, this.workDir, this.params.actions, this.params.profile).map(Path::toString).toList());
    } 
    if ((Launcher.getConfig()).environment != LauncherConfig.LauncherEnvironment.PROD) {
      linkedList.add(JVMHelper.jvmProperty("launcher.dev", String.valueOf(LogHelper.isDevEnabled())));
      linkedList.add(JVMHelper.jvmProperty("launcher.debug", String.valueOf(LogHelper.isDebugEnabled())));
      linkedList.add(JVMHelper.jvmProperty("launcher.stacktrace", String.valueOf(LogHelper.isStacktraceEnabled())));
    } 
    if (this.useLegacyJavaClassPathProperty) {
      linkedList.add("-Djava.class.path=".concat(String.join(getPathSeparator(), (Iterable)this.systemClassPath)));
    } else {
      linkedList.add("-cp");
      linkedList.add(String.join(getPathSeparator(), (Iterable)this.systemClassPath));
    } 
    linkedList.add(this.mainClass);
    linkedList.addAll(this.systemClientArgs);
    synchronized (this.waitWriteParams) {
      if (!this.waitWriteParams[0].booleanValue())
        this.waitWriteParams.wait(1000L); 
    } 
    if (LogHelper.isDebugEnabled())
      LogHelper.debug("Commandline: %s", new Object[] { Arrays.toString(linkedList.toArray()) }); 
    ProcessBuilder processBuilder = new ProcessBuilder(linkedList);
    EnvHelper.addEnv(processBuilder);
    if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX) {
      Map<String, String> map = processBuilder.environment();
      map.put("__GL_THREADED_OPTIMIZATIONS", "0");
      if (this.params.lwjglGlfwWayland && !this.params.profile.hasFlag(ClientProfile.CompatibilityFlags.WAYLAND_USE_CUSTOM_GLFW))
        map.remove("DISPLAY"); 
    } 
    processBuilder.environment().put("JAVA_HOME", this.javaVersion.jvmDir.toAbsolutePath().toString());
    processBuilder.environment().putAll(this.systemEnv);
    processBuilder.directory(this.workDir.toFile());
    processBuilder.inheritIO();
    if (paramBoolean) {
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    } 
    this.process = processBuilder.start();
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessBuilderLaunchedEvent(this));
    this.isStarted = true;
  }
  
  private void applyJava9Params(List<String> paramList) {
    StringBuilder stringBuilder1 = new StringBuilder();
    StringBuilder stringBuilder2 = new StringBuilder();
    for (String str : this.jvmModules) {
      if (!stringBuilder2.isEmpty())
        stringBuilder2.append(","); 
      stringBuilder2.append(str);
    } 
    for (String str : this.jvmModulesPaths) {
      if (!stringBuilder1.isEmpty())
        stringBuilder1.append(File.pathSeparator); 
      stringBuilder1.append(str);
    } 
    if (!stringBuilder2.isEmpty()) {
      paramList.add("--add-modules");
      paramList.add(stringBuilder2.toString());
    } 
    if (!stringBuilder1.isEmpty()) {
      paramList.add("--module-path");
      paramList.add(stringBuilder1.toString());
    } 
  }
  
  public void runWriteParams(SocketAddress paramSocketAddress) throws IOException {
    ServerSocket serverSocket = new ServerSocket();
    try {
      serverSocket.bind(paramSocketAddress);
      synchronized (this.waitWriteParams) {
        this.waitWriteParams[0] = Boolean.valueOf(true);
        this.waitWriteParams.notifyAll();
      } 
      Socket socket = serverSocket.accept();
      try {
        HOutput hOutput = new HOutput(new CipherOutputStream(socket.getOutputStream(), SecurityHelper.newAESEncryptCipher(SecurityHelper.fromHex((Launcher.getConfig()).secretKeyClient))));
        try {
          byte[] arrayOfByte = IOHelper.encode(Launcher.gsonManager.gson.toJson(this.params));
          hOutput.writeByteArray(arrayOfByte, 0);
          this.params.clientHDir.write(hOutput);
          this.params.assetHDir.write(hOutput);
          if (this.params.javaHDir == null || this.params.javaHDir == this.params.assetHDir) {
            hOutput.writeBoolean(false);
          } else {
            hOutput.writeBoolean(true);
            this.params.javaHDir.write(hOutput);
          } 
          hOutput.close();
        } catch (Throwable throwable) {
          try {
            hOutput.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Exception exception) {
        throw new IOException(exception);
      } 
      serverSocket.close();
    } catch (Throwable throwable) {
      try {
        serverSocket.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientProcessBuilderParamsWrittedEvent(this));
  }
  
  public Process getProcess() {
    return this.process;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\ClientLauncherProcess.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */