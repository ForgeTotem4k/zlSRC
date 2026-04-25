package pro.gravit.launcher.base.request.websockets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.NotificationEvent;
import pro.gravit.launcher.base.events.request.AdditionalDataRequestEvent;
import pro.gravit.launcher.base.events.request.AssetUploadInfoRequestEvent;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.BatchProfileByUsernameRequestEvent;
import pro.gravit.launcher.base.events.request.CheckServerRequestEvent;
import pro.gravit.launcher.base.events.request.CurrentUserRequestEvent;
import pro.gravit.launcher.base.events.request.ErrorRequestEvent;
import pro.gravit.launcher.base.events.request.ExitRequestEvent;
import pro.gravit.launcher.base.events.request.FeaturesRequestEvent;
import pro.gravit.launcher.base.events.request.FetchClientProfileKeyRequestEvent;
import pro.gravit.launcher.base.events.request.GetAssetUploadUrlRequestEvent;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.events.request.GetConnectUUIDRequestEvent;
import pro.gravit.launcher.base.events.request.GetPublicKeyRequestEvent;
import pro.gravit.launcher.base.events.request.GetSecureLevelInfoRequestEvent;
import pro.gravit.launcher.base.events.request.HardwareReportRequestEvent;
import pro.gravit.launcher.base.events.request.JoinServerRequestEvent;
import pro.gravit.launcher.base.events.request.LauncherRequestEvent;
import pro.gravit.launcher.base.events.request.ProfileByUUIDRequestEvent;
import pro.gravit.launcher.base.events.request.ProfileByUsernameRequestEvent;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.events.request.RefreshTokenRequestEvent;
import pro.gravit.launcher.base.events.request.RestoreRequestEvent;
import pro.gravit.launcher.base.events.request.SecurityReportRequestEvent;
import pro.gravit.launcher.base.events.request.SetProfileRequestEvent;
import pro.gravit.launcher.base.events.request.UpdateRequestEvent;
import pro.gravit.launcher.base.events.request.VerifySecureLevelKeyRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.GetAvailabilityAuthRequest;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.launcher.core.hasher.HashedEntryAdapter;
import pro.gravit.utils.ProviderMap;
import pro.gravit.utils.UniversalJsonAdapter;
import pro.gravit.utils.helper.LogHelper;

public abstract class ClientWebSocketService extends ClientJSONPoint {
  public static final ProviderMap<WebSocketEvent> results = new ProviderMap();
  
  public static final ProviderMap<WebSocketRequest> requests = new ProviderMap();
  
  private static boolean resultsRegistered = false;
  
  public final Gson gson = Launcher.gsonManager.gson;
  
  public final Boolean onConnect = Boolean.valueOf(true);
  
  public final Object waitObject = new Object();
  
  public OnCloseCallback onCloseCallback;
  
  public ReconnectCallback reconnectCallback;
  
  public ClientWebSocketService(String paramString) {
    super(createURL(paramString));
  }
  
  public static void appendTypeAdapters(GsonBuilder paramGsonBuilder) {
    paramGsonBuilder.registerTypeAdapter(HashedEntry.class, new HashedEntryAdapter());
    paramGsonBuilder.registerTypeAdapter(ClientProfile.Version.class, new ClientProfile.Version.GsonSerializer());
    paramGsonBuilder.registerTypeAdapter(WebSocketEvent.class, new UniversalJsonAdapter(results));
    paramGsonBuilder.registerTypeAdapter(WebSocketRequest.class, new UniversalJsonAdapter(requests));
    paramGsonBuilder.registerTypeAdapter(AuthRequest.AuthPasswordInterface.class, new UniversalJsonAdapter(AuthRequest.providers));
    paramGsonBuilder.registerTypeAdapter(GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails.class, new UniversalJsonAdapter(GetAvailabilityAuthRequest.providers));
    paramGsonBuilder.registerTypeAdapter(OptionalAction.class, new UniversalJsonAdapter(OptionalAction.providers));
    paramGsonBuilder.registerTypeAdapter(OptionalTrigger.class, new UniversalJsonAdapter(OptionalTrigger.providers));
  }
  
  private static URI createURL(String paramString) {
    try {
      return new URI(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw new RuntimeException(uRISyntaxException);
    } 
  }
  
  void onMessage(String paramString) {
    WebSocketEvent webSocketEvent = (WebSocketEvent)this.gson.fromJson(paramString, WebSocketEvent.class);
    eventHandle(webSocketEvent);
  }
  
  public abstract <T extends WebSocketEvent> void eventHandle(T paramT);
  
  void onDisconnect(int paramInt, String paramString) {
    LogHelper.info("WebSocket disconnected: %d: %s", new Object[] { Integer.valueOf(paramInt), paramString });
    if (this.onCloseCallback != null)
      this.onCloseCallback.onClose(paramInt, paramString, !this.isClosed); 
  }
  
  void onOpen() {
    synchronized (this.waitObject) {
      this.waitObject.notifyAll();
    } 
  }
  
  public void registerRequests() {}
  
  public void registerResults() {
    if (!resultsRegistered) {
      results.register("auth", AuthRequestEvent.class);
      results.register("checkServer", CheckServerRequestEvent.class);
      results.register("joinServer", JoinServerRequestEvent.class);
      results.register("launcher", LauncherRequestEvent.class);
      results.register("profileByUsername", ProfileByUsernameRequestEvent.class);
      results.register("profileByUUID", ProfileByUUIDRequestEvent.class);
      results.register("batchProfileByUsername", BatchProfileByUsernameRequestEvent.class);
      results.register("profiles", ProfilesRequestEvent.class);
      results.register("setProfile", SetProfileRequestEvent.class);
      results.register("error", ErrorRequestEvent.class);
      results.register("update", UpdateRequestEvent.class);
      results.register("getAvailabilityAuth", GetAvailabilityAuthRequestEvent.class);
      results.register("notification", NotificationEvent.class);
      results.register("exit", ExitRequestEvent.class);
      results.register("getSecureLevelInfo", GetSecureLevelInfoRequestEvent.class);
      results.register("verifySecureLevelKey", VerifySecureLevelKeyRequestEvent.class);
      results.register("securityReport", SecurityReportRequestEvent.class);
      results.register("hardwareReport", HardwareReportRequestEvent.class);
      results.register("currentUser", CurrentUserRequestEvent.class);
      results.register("features", FeaturesRequestEvent.class);
      results.register("refreshToken", RefreshTokenRequestEvent.class);
      results.register("restore", RestoreRequestEvent.class);
      results.register("additionalData", AdditionalDataRequestEvent.class);
      results.register("clientProfileKey", FetchClientProfileKeyRequestEvent.class);
      results.register("getPublicKey", GetPublicKeyRequestEvent.class);
      results.register("getAssetUploadUrl", GetAssetUploadUrlRequestEvent.class);
      results.register("assetUploadInfo", AssetUploadInfoRequestEvent.class);
      results.register("getConnectUUID", GetConnectUUIDRequestEvent.class);
      resultsRegistered = true;
    } 
  }
  
  public void waitIfNotConnected() {}
  
  public void sendObject(Object paramObject) throws IOException {
    waitIfNotConnected();
    if (this.webSocket == null || this.webSocket.isInputClosed())
      this.reconnectCallback.onReconnect(); 
    send(this.gson.toJson(paramObject, WebSocketRequest.class));
  }
  
  public void sendObject(Object paramObject, Type paramType) throws IOException {
    waitIfNotConnected();
    if (this.webSocket == null || this.webSocket.isInputClosed())
      this.reconnectCallback.onReconnect(); 
    send(this.gson.toJson(paramObject, paramType));
  }
  
  @FunctionalInterface
  public static interface OnCloseCallback {
    void onClose(int param1Int, String param1String, boolean param1Boolean);
    
    static {
    
    }
  }
  
  public static interface ReconnectCallback {
    void onReconnect() throws IOException;
    
    static {
    
    }
  }
  
  @FunctionalInterface
  public static interface EventHandler {
    <T extends WebSocketEvent> boolean eventHandle(T param1T);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\ClientWebSocketService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */