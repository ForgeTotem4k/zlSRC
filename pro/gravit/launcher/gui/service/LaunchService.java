package pro.gravit.launcher.gui.service;

import java.io.EOFException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.SetProfileRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.ClientProfileBuilder;
import pro.gravit.launcher.base.profiles.ClientProfileVersions;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.auth.SetProfileRequest;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.runtime.client.ClientLauncherProcess;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.JavaHelper;
import pro.gravit.utils.helper.LogHelper;

public class LaunchService {
  private final JavaFXApplication application;
  
  public LaunchService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public boolean isTestUpdate(ClientProfile paramClientProfile, RuntimeSettings.ProfileSettings paramProfileSettings) {
    return (this.application.offlineService.isOfflineMode() || (this.application.authService.checkDebugPermission("skipupdate") && paramProfileSettings.debugSkipUpdate));
  }
  
  private void downloadClients(CompletableFuture<ClientInstance> paramCompletableFuture, ClientProfile paramClientProfile, RuntimeSettings.ProfileSettings paramProfileSettings, JavaHelper.JavaVersion paramJavaVersion, HashedDir paramHashedDir) {
    Path path = DirBridge.dirUpdates.resolve(paramClientProfile.getAssetDir());
    LogHelper.info("Start update to %s", new Object[] { path.toString() });
    boolean bool = isTestUpdate(paramClientProfile, paramProfileSettings);
    Consumer consumer = paramHashedDir2 -> {
        Path path = DirBridge.dirUpdates.resolve(paramClientProfile.getDir());
        LogHelper.info("Start update to %s", new Object[] { path.toString() });
        this.application.gui.updateScene.sendUpdateRequest(paramClientProfile.getDir(), path, paramClientProfile.getClientUpdateMatcher(), true, this.application.profilesService.getOptionalView(), true, paramBoolean, ());
      };
    if (paramClientProfile.getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_6_4) <= 0) {
      this.application.gui.updateScene.sendUpdateRequest(paramClientProfile.getAssetDir(), path, paramClientProfile.getAssetUpdateMatcher(), true, null, false, bool, consumer);
    } else {
      this.application.gui.updateScene.sendUpdateAssetRequest(paramClientProfile.getAssetDir(), path, paramClientProfile.getAssetUpdateMatcher(), true, paramClientProfile.getAssetIndex(), bool, consumer);
    } 
  }
  
  private ClientInstance doLaunchClient(Path paramPath1, HashedDir paramHashedDir1, Path paramPath2, HashedDir paramHashedDir2, ClientProfile paramClientProfile, OptionalView paramOptionalView, JavaHelper.JavaVersion paramJavaVersion, HashedDir paramHashedDir3) {
    RuntimeSettings.ProfileSettings profileSettings = this.application.getProfileSettings();
    if (paramJavaVersion == null)
      paramJavaVersion = this.application.javaService.getRecommendJavaVersion(paramClientProfile); 
    if (paramJavaVersion == null)
      paramJavaVersion = JavaHelper.JavaVersion.getCurrentJavaVersion(); 
    if (this.application.authService.checkDebugPermission("skipfilemonitor") && profileSettings.debugSkipFileMonitor) {
      ClientProfileBuilder clientProfileBuilder = new ClientProfileBuilder(paramClientProfile);
      clientProfileBuilder.setUpdate(new ArrayList());
      clientProfileBuilder.setUpdateVerify(new ArrayList());
      clientProfileBuilder.setUpdateExclusions(new ArrayList());
      paramClientProfile = clientProfileBuilder.createClientProfile();
    } 
    ClientLauncherProcess clientLauncherProcess = new ClientLauncherProcess(paramPath2, paramPath1, paramJavaVersion, paramPath2.resolve("resourcepacks"), paramClientProfile, this.application.authService.getPlayerProfile(), paramOptionalView, this.application.authService.getAccessToken(), paramHashedDir2, paramHashedDir1, paramHashedDir3);
    clientLauncherProcess.params.ram = profileSettings.ram;
    clientLauncherProcess.params.offlineMode = this.application.offlineService.isOfflineMode();
    if (clientLauncherProcess.params.ram > 0) {
      clientLauncherProcess.jvmArgs.add("-Xms" + clientLauncherProcess.params.ram + "M");
      clientLauncherProcess.jvmArgs.add("-Xmx" + clientLauncherProcess.params.ram + "M");
    } 
    clientLauncherProcess.params.fullScreen = profileSettings.fullScreen;
    clientLauncherProcess.params.autoEnter = profileSettings.autoEnter;
    if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX)
      clientLauncherProcess.params.lwjglGlfwWayland = profileSettings.waylandSupport; 
    return new ClientInstance(clientLauncherProcess, paramClientProfile, profileSettings);
  }
  
  private String getJavaDirName(Path paramPath) {
    String str = DirBridge.dirUpdates.toAbsolutePath().toString();
    if (paramPath == null || !paramPath.startsWith(str))
      return null; 
    Path path = DirBridge.dirUpdates.relativize(paramPath);
    return path.toString();
  }
  
  private void showJavaAlert(ClientProfile paramClientProfile) {
    if ((JVMHelper.ARCH_TYPE == JVMHelper.ARCH.ARM32 || JVMHelper.ARCH_TYPE == JVMHelper.ARCH.ARM64) && paramClientProfile.getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_12_2) <= 0) {
      this.application.messageManager.showDialog(this.application.getTranslation("runtime.scenes.serverinfo.javaalert.lwjgl2.header"), this.application.getTranslation("runtime.scenes.serverinfo.javaalert.lwjgl2.description").formatted(new Object[] { Integer.valueOf(paramClientProfile.getRecommendJavaVersion()) }, ), () -> {
          
          }() -> {
          
          }true);
    } else {
      this.application.messageManager.showDialog(this.application.getTranslation("runtime.scenes.serverinfo.javaalert.header"), this.application.getTranslation("runtime.scenes.serverinfo.javaalert.description").formatted(new Object[] { Integer.valueOf(paramClientProfile.getRecommendJavaVersion()) }, ), () -> {
          
          }() -> {
          
          }true);
    } 
  }
  
  public CompletableFuture<ClientInstance> launchClient() {
    return launchClient((AbstractStage)this.application.getMainStage());
  }
  
  private CompletableFuture<ClientInstance> launchClient(AbstractStage paramAbstractStage) {
    ClientProfile clientProfile = this.application.profilesService.getProfile();
    if (clientProfile == null)
      throw new NullPointerException("profilesService.getProfile() is null"); 
    CompletableFuture<ClientInstance> completableFuture = new CompletableFuture();
    Objects.requireNonNull(completableFuture);
    this.application.gui.processingOverlay.processRequest(paramAbstractStage, this.application.getTranslation("runtime.overlay.processing.text.setprofile"), (Request)new SetProfileRequest(clientProfile), paramSetProfileRequestEvent -> ContextHelper.runInFxThreadStatic(()), completableFuture::completeExceptionally, null);
    return completableFuture;
  }
  
  public class ClientInstance {
    private final ClientLauncherProcess process;
    
    private final ClientProfile clientProfile;
    
    private final RuntimeSettings.ProfileSettings settings;
    
    private final Thread writeParamsThread;
    
    private Thread runThread;
    
    private final CompletableFuture<Void> onWriteParams = new CompletableFuture<>();
    
    private final CompletableFuture<Integer> runFuture = new CompletableFuture<>();
    
    private final Set<ProcessListener> listeners = ConcurrentHashMap.newKeySet();
    
    public ClientInstance(ClientLauncherProcess param1ClientLauncherProcess, ClientProfile param1ClientProfile, RuntimeSettings.ProfileSettings param1ProfileSettings) {
      this.process = param1ClientLauncherProcess;
      this.clientProfile = param1ClientProfile;
      this.settings = param1ProfileSettings;
      this.writeParamsThread = CommonHelper.newThread("Client Params Writer Thread", true, () -> {
            try {
              param1ClientLauncherProcess.runWriteParams(new InetSocketAddress("127.0.0.1", (Launcher.getConfig()).clientPort));
              this.onWriteParams.complete(null);
            } catch (Throwable throwable) {
              LogHelper.error(throwable);
              this.onWriteParams.completeExceptionally(throwable);
            } 
          });
    }
    
    private void run() {
      try {
        this.process.start(true);
        Process process = this.process.getProcess();
        InputStream inputStream = process.getInputStream();
        byte[] arrayOfByte = IOHelper.newBuffer();
        try {
          int i;
          for (i = inputStream.read(arrayOfByte); i >= 0; i = inputStream.read(arrayOfByte))
            handleListeners(arrayOfByte, 0, i); 
        } catch (EOFException eOFException) {}
        if (process.isAlive())
          process.waitFor(); 
        if (this.writeParamsThread != null && this.writeParamsThread.isAlive())
          this.writeParamsThread.interrupt(); 
        this.runFuture.complete(Integer.valueOf(process.exitValue()));
      } catch (Throwable throwable) {
        if (this.writeParamsThread != null && this.writeParamsThread.isAlive())
          this.writeParamsThread.interrupt(); 
        this.runFuture.completeExceptionally(throwable);
      } 
    }
    
    public void kill() {
      this.process.getProcess().destroyForcibly();
    }
    
    private void handleListeners(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      for (ProcessListener processListener : this.listeners)
        processListener.onNext(param1ArrayOfbyte, param1Int1, param1Int2); 
    }
    
    public synchronized CompletableFuture<Integer> start() {
      if (this.runThread == null) {
        this.runThread = CommonHelper.newThread("Run Thread", true, this::run);
        this.writeParamsThread.start();
        this.runThread.start();
      } 
      return this.runFuture;
    }
    
    public ClientLauncherProcess getProcess() {
      return this.process;
    }
    
    public ClientProfile getClientProfile() {
      return this.clientProfile;
    }
    
    public RuntimeSettings.ProfileSettings getSettings() {
      return this.settings;
    }
    
    public CompletableFuture<Void> getOnWriteParamsFuture() {
      return this.onWriteParams;
    }
    
    public void registerListener(ProcessListener param1ProcessListener) {
      this.listeners.add(param1ProcessListener);
    }
    
    public void unregisterListener(ProcessListener param1ProcessListener) {
      this.listeners.remove(param1ProcessListener);
    }
    
    @FunctionalInterface
    public static interface ProcessListener {
      void onNext(byte[] param2ArrayOfbyte, int param2Int1, int param2Int2);
      
      static {
      
      }
    }
  }
  
  @FunctionalInterface
  public static interface ProcessListener {
    void onNext(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\LaunchService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */