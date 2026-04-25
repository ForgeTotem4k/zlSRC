package pro.gravit.launcher.base.request.websockets;

import java.util.concurrent.CompletableFuture;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;

public class VoidRequestService implements RequestService {
  private final Throwable ex;
  
  public VoidRequestService(Throwable paramThrowable) {
    this.ex = paramThrowable;
  }
  
  public VoidRequestService() {
    this.ex = null;
  }
  
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> CompletableFuture<T> request(Request<T> paramRequest) {
    CompletableFuture<T> completableFuture = new CompletableFuture();
    completableFuture.completeExceptionally((this.ex != null) ? this.ex : (Throwable)new RequestException("Connection fail"));
    return completableFuture;
  }
  
  public void connect() {}
  
  public void registerEventHandler(RequestService.EventHandler paramEventHandler) {}
  
  public void unregisterEventHandler(RequestService.EventHandler paramEventHandler) {}
  
  public boolean isClosed() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\VoidRequestService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */