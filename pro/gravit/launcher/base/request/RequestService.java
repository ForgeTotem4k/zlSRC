package pro.gravit.launcher.base.request;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface RequestService {
  <T extends WebSocketEvent> CompletableFuture<T> request(Request<T> paramRequest) throws IOException;
  
  void connect() throws Exception;
  
  void registerEventHandler(EventHandler paramEventHandler);
  
  void unregisterEventHandler(EventHandler paramEventHandler);
  
  default <T extends WebSocketEvent> T requestSync(Request<T> paramRequest) throws IOException {
    try {
      return (T)request(paramRequest).get();
    } catch (InterruptedException interruptedException) {
      throw new RequestException("Request interrupted");
    } catch (ExecutionException executionException) {
      Throwable throwable = executionException.getCause();
      if (throwable instanceof IOException)
        throw (IOException)executionException.getCause(); 
      throw new RequestException(throwable);
    } 
  }
  
  boolean isClosed();
  
  static {
  
  }
  
  @FunctionalInterface
  public static interface EventHandler {
    <T extends WebSocketEvent> boolean eventHandle(T param1T);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\RequestService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */