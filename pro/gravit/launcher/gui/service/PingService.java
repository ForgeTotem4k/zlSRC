package pro.gravit.launcher.gui.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.launcher.runtime.client.ServerPinger;

public class PingService {
  private final Map<String, CompletableFuture<PingServerReport>> reports = new ConcurrentHashMap<>();
  
  public CompletableFuture<PingServerReport> getPingReport(String paramString) {
    return this.reports.computeIfAbsent(paramString, paramString -> new CompletableFuture());
  }
  
  public void addReports(Map<String, PingServerReport> paramMap) {
    paramMap.forEach((paramString, paramPingServerReport) -> {
          CompletableFuture<PingServerReport> completableFuture = getPingReport(paramString);
          completableFuture.complete(paramPingServerReport);
        });
  }
  
  public void addReport(String paramString, ServerPinger.Result paramResult) {
    CompletableFuture<PingServerReport> completableFuture = getPingReport(paramString);
    PingServerReport pingServerReport = new PingServerReport(paramString, paramResult.maxPlayers, paramResult.onlinePlayers);
    completableFuture.complete(pingServerReport);
  }
  
  public void clear() {
    this.reports.forEach((paramString, paramCompletableFuture) -> {
          if (!paramCompletableFuture.isDone())
            paramCompletableFuture.completeExceptionally(new InterruptedException()); 
        });
    this.reports.clear();
  }
  
  public static class PingServerReport {
    public final String name;
    
    public final int maxPlayers;
    
    public final int playersOnline;
    
    public PingServerReport(String param1String, int param1Int1, int param1Int2) {
      this.name = param1String;
      this.maxPlayers = param1Int1;
      this.playersOnline = param1Int2;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\PingService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */