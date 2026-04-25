package pro.gravit.launcher.base.request.websockets;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.utils.helper.LogHelper;

public class OfflineRequestService implements RequestService {
  private final HashSet<RequestService.EventHandler> eventHandlers = new HashSet<>();
  
  private final Map<Class<?>, RequestProcessor<?, ?>> processors = new ConcurrentHashMap<>();
  
  public <T extends WebSocketEvent> CompletableFuture<T> request(Request<T> paramRequest) {
    RequestProcessor<Object, Request<T>> requestProcessor = (RequestProcessor)this.processors.get(paramRequest.getClass());
    CompletableFuture<T> completableFuture = new CompletableFuture();
    if (requestProcessor == null) {
      completableFuture.completeExceptionally((Throwable)new RequestException(String.format("Offline mode not support '%s'", new Object[] { paramRequest.getType() })));
      return completableFuture;
    } 
    if (LogHelper.isDevEnabled())
      LogHelper.dev("Request %s: %s", new Object[] { paramRequest.getType(), Launcher.gsonManager.gson.toJson(paramRequest) }); 
    try {
      WebSocketEvent webSocketEvent = (WebSocketEvent)requestProcessor.process(paramRequest);
      if (LogHelper.isDevEnabled())
        LogHelper.dev("Response %s: %s", new Object[] { webSocketEvent.getType(), Launcher.gsonManager.gson.toJson(webSocketEvent) }); 
      completableFuture.complete((T)webSocketEvent);
    } catch (Throwable throwable) {
      if (throwable instanceof RequestException) {
        completableFuture.completeExceptionally(throwable);
      } else {
        completableFuture.completeExceptionally((Throwable)new RequestException(throwable));
      } 
    } 
    return completableFuture;
  }
  
  public void connect() {}
  
  public void registerEventHandler(RequestService.EventHandler paramEventHandler) {
    this.eventHandlers.add(paramEventHandler);
  }
  
  public void unregisterEventHandler(RequestService.EventHandler paramEventHandler) {
    this.eventHandlers.remove(paramEventHandler);
  }
  
  public boolean isClosed() {
    return false;
  }
  
  public <T extends WebSocketEvent, V extends WebSocketRequest> void registerRequestProcessor(Class<V> paramClass, RequestProcessor<T, V> paramRequestProcessor) {
    this.processors.put(paramClass, paramRequestProcessor);
  }
  
  public <T extends WebSocketEvent> void unregisterRequestProcessor(Class<Request<T>> paramClass) {
    this.processors.remove(paramClass);
  }
  
  public static interface RequestProcessor<T extends WebSocketEvent, V extends WebSocketRequest> {
    T process(V param1V) throws RequestException;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\OfflineRequestService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */