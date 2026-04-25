package pro.gravit.launcher.gui.service;

import java.io.EOFException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.runtime.client.ClientLauncherProcess;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class ClientInstance {
  private final ClientLauncherProcess process;
  
  private final ClientProfile clientProfile;
  
  private final RuntimeSettings.ProfileSettings settings;
  
  private final Thread writeParamsThread;
  
  private Thread runThread;
  
  private final CompletableFuture<Void> onWriteParams = new CompletableFuture<>();
  
  private final CompletableFuture<Integer> runFuture = new CompletableFuture<>();
  
  private final Set<ProcessListener> listeners = ConcurrentHashMap.newKeySet();
  
  public ClientInstance(ClientLauncherProcess paramClientLauncherProcess, ClientProfile paramClientProfile, RuntimeSettings.ProfileSettings paramProfileSettings) {
    this.process = paramClientLauncherProcess;
    this.clientProfile = paramClientProfile;
    this.settings = paramProfileSettings;
    this.writeParamsThread = CommonHelper.newThread("Client Params Writer Thread", true, () -> {
          try {
            paramClientLauncherProcess.runWriteParams(new InetSocketAddress("127.0.0.1", (Launcher.getConfig()).clientPort));
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
  
  private void handleListeners(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    for (ProcessListener processListener : this.listeners)
      processListener.onNext(paramArrayOfbyte, paramInt1, paramInt2); 
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
  
  public void registerListener(ProcessListener paramProcessListener) {
    this.listeners.add(paramProcessListener);
  }
  
  public void unregisterListener(ProcessListener paramProcessListener) {
    this.listeners.remove(paramProcessListener);
  }
  
  @FunctionalInterface
  public static interface ProcessListener {
    void onNext(byte[] param2ArrayOfbyte, int param2Int1, int param2Int2);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\LaunchService$ClientInstance.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */