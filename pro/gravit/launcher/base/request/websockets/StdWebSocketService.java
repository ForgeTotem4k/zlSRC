package pro.gravit.launcher.base.request.websockets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLException;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.events.request.ErrorRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class StdWebSocketService extends ClientWebSocketService implements RequestService {
  private final ConcurrentHashMap<UUID, CompletableFuture> futureMap = new ConcurrentHashMap<>();
  
  private final HashSet<RequestService.EventHandler> eventHandlers = new HashSet<>();
  
  private final HashSet<ClientWebSocketService.EventHandler> legacyEventHandlers = new HashSet<>();
  
  public StdWebSocketService(String paramString) throws SSLException {
    super(paramString);
  }
  
  public static CompletableFuture<StdWebSocketService> initWebSockets(String paramString) {
    StdWebSocketService stdWebSocketService;
    try {
      stdWebSocketService = new StdWebSocketService(paramString);
    } catch (SSLException sSLException) {
      throw new SecurityException(sSLException);
    } 
    stdWebSocketService.registerResults();
    stdWebSocketService.registerRequests();
    CompletableFuture<StdWebSocketService> completableFuture = new CompletableFuture();
    Objects.requireNonNull(completableFuture);
    stdWebSocketService.openAsync(() -> {
          paramCompletableFuture.complete(paramStdWebSocketService);
          JVMHelper.RUNTIME.addShutdownHook(new Thread(()));
        }completableFuture::completeExceptionally);
    return completableFuture;
  }
  
  @Deprecated
  public void registerEventHandler(ClientWebSocketService.EventHandler paramEventHandler) {
    this.legacyEventHandlers.add(paramEventHandler);
  }
  
  @Deprecated
  public void unregisterEventHandler(ClientWebSocketService.EventHandler paramEventHandler) {
    this.legacyEventHandlers.remove(paramEventHandler);
  }
  
  public <T extends WebSocketEvent> void processEventHandlers(T paramT) {
    for (RequestService.EventHandler eventHandler : this.eventHandlers) {
      if (eventHandler.eventHandle((WebSocketEvent)paramT))
        return; 
    } 
    for (ClientWebSocketService.EventHandler eventHandler : this.legacyEventHandlers) {
      if (eventHandler.eventHandle(paramT))
        return; 
    } 
  }
  
  public <T extends WebSocketEvent> void eventHandle(T paramT) {
    if (paramT instanceof RequestEvent) {
      RequestEvent requestEvent = (RequestEvent)paramT;
      if (requestEvent.requestUUID == null) {
        LogHelper.warning("Request event type %s.requestUUID is null", new Object[] { (requestEvent.getType() == null) ? "null" : requestEvent.getType() });
        return;
      } 
      if (requestEvent.requestUUID.equals(RequestEvent.eventUUID)) {
        processEventHandlers(paramT);
        return;
      } 
      CompletableFuture<RequestEvent> completableFuture = this.futureMap.get(requestEvent.requestUUID);
      if (completableFuture != null) {
        if (requestEvent instanceof ErrorRequestEvent) {
          completableFuture.completeExceptionally((Throwable)new RequestException(((ErrorRequestEvent)requestEvent).error));
        } else {
          completableFuture.complete(requestEvent);
        } 
        this.futureMap.remove(requestEvent.requestUUID);
      } else {
        processEventHandlers(requestEvent);
        return;
      } 
    } 
    processEventHandlers(paramT);
  }
  
  public <T extends WebSocketEvent> CompletableFuture<T> request(Request<T> paramRequest) throws IOException {
    CompletableFuture<T> completableFuture = new CompletableFuture();
    this.futureMap.put(paramRequest.requestUUID, completableFuture);
    sendObject(paramRequest, WebSocketRequest.class);
    return completableFuture;
  }
  
  public void registerEventHandler(RequestService.EventHandler paramEventHandler) {
    this.eventHandlers.add(paramEventHandler);
  }
  
  public void unregisterEventHandler(RequestService.EventHandler paramEventHandler) {
    this.eventHandlers.remove(paramEventHandler);
  }
  
  public <T extends WebSocketEvent> T requestSync(Request<T> paramRequest) throws IOException {
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
  
  public boolean isClosed() {
    return this.isClosed;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\StdWebSocketService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */