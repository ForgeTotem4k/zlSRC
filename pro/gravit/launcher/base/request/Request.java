package pro.gravit.launcher.base.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.CurrentUserRequestEvent;
import pro.gravit.launcher.base.events.request.RefreshTokenRequestEvent;
import pro.gravit.launcher.base.events.request.RestoreRequestEvent;
import pro.gravit.launcher.base.request.auth.RefreshTokenRequest;
import pro.gravit.launcher.base.request.auth.RestoreRequest;
import pro.gravit.launcher.base.request.websockets.StdWebSocketService;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.LogHelper;

public abstract class Request<R extends WebSocketEvent> implements WebSocketRequest {
  private static final List<ExtendedTokenCallback> extendedTokenCallbacks = new ArrayList<>(4);
  
  private static final List<BiConsumer<String, AuthRequestEvent.OAuthRequestEvent>> oauthChangeHandlers = new ArrayList<>(4);
  
  private static volatile RequestService requestService;
  
  private static volatile AuthRequestEvent.OAuthRequestEvent oauth;
  
  private static volatile Map<String, ExtendedToken> extendedTokens;
  
  private static volatile String authId;
  
  private static volatile long tokenExpiredTime;
  
  private static volatile ScheduledExecutorService executorService;
  
  private static volatile boolean autoRefreshRunning;
  
  @LauncherNetworkAPI
  public final UUID requestUUID = UUID.randomUUID();
  
  private final transient AtomicBoolean started = new AtomicBoolean(false);
  
  public static synchronized void startAutoRefresh() {
    if (!autoRefreshRunning) {
      if (executorService == null)
        executorService = Executors.newSingleThreadScheduledExecutor(paramRunnable -> {
              Thread thread = new Thread(paramRunnable);
              thread.setName("AutoRefresh thread");
              thread.setDaemon(true);
              return thread;
            }); 
      executorService.scheduleAtFixedRate(() -> {
            try {
              restore(false, true, false);
            } catch (Exception exception) {
              LogHelper.error(exception);
            } 
          }5L, 5L, TimeUnit.SECONDS);
      autoRefreshRunning = true;
    } 
  }
  
  public static RequestService getRequestService() {
    return requestService;
  }
  
  public static void setRequestService(RequestService paramRequestService) {
    requestService = paramRequestService;
  }
  
  public static boolean isAvailable() {
    return (requestService != null);
  }
  
  public static void setOAuth(String paramString, AuthRequestEvent.OAuthRequestEvent paramOAuthRequestEvent) {
    oauth = paramOAuthRequestEvent;
    authId = paramString;
    if (oauth != null && oauth.expire != 0L) {
      tokenExpiredTime = System.currentTimeMillis() + oauth.expire;
    } else {
      tokenExpiredTime = 0L;
    } 
    for (BiConsumer<String, AuthRequestEvent.OAuthRequestEvent> biConsumer : oauthChangeHandlers)
      biConsumer.accept(paramString, paramOAuthRequestEvent); 
  }
  
  public static AuthRequestEvent.OAuthRequestEvent getOAuth() {
    return oauth;
  }
  
  public static String getAuthId() {
    return authId;
  }
  
  public static Map<String, ExtendedToken> getExtendedTokens() {
    return (extendedTokens != null) ? Collections.unmodifiableMap(extendedTokens) : null;
  }
  
  public static Map<String, String> getStringExtendedTokens() {
    if (extendedTokens != null) {
      HashMap<Object, Object> hashMap = new HashMap<>();
      for (Map.Entry<String, ExtendedToken> entry : extendedTokens.entrySet())
        hashMap.put(entry.getKey(), ((ExtendedToken)entry.getValue()).token); 
      return (Map)hashMap;
    } 
    return null;
  }
  
  public static void clearExtendedTokens() {
    if (extendedTokens != null)
      extendedTokens.clear(); 
  }
  
  public static void addExtendedToken(String paramString, ExtendedToken paramExtendedToken) {
    if (extendedTokens == null)
      extendedTokens = new ConcurrentHashMap<>(); 
    extendedTokens.put(paramString, paramExtendedToken);
  }
  
  public static void addAllExtendedToken(Map<String, ExtendedToken> paramMap) {
    if (extendedTokens == null)
      extendedTokens = new ConcurrentHashMap<>(); 
    extendedTokens.putAll(paramMap);
  }
  
  public static void setOAuth(String paramString, AuthRequestEvent.OAuthRequestEvent paramOAuthRequestEvent, long paramLong) {
    oauth = paramOAuthRequestEvent;
    authId = paramString;
    tokenExpiredTime = paramLong;
  }
  
  public static boolean isTokenExpired() {
    return (oauth == null) ? true : ((tokenExpiredTime == 0L) ? false : ((System.currentTimeMillis() > tokenExpiredTime)));
  }
  
  public static long getTokenExpiredTime() {
    return tokenExpiredTime;
  }
  
  public static String getAccessToken() {
    return (oauth == null) ? null : oauth.accessToken;
  }
  
  public static String getRefreshToken() {
    return (oauth == null) ? null : oauth.refreshToken;
  }
  
  public static void reconnect() throws Exception {
    getRequestService().connect();
    restore();
  }
  
  public static RequestRestoreReport restore() throws Exception {
    return restore(false, false, false);
  }
  
  private static synchronized Map<String, String> getExpiredExtendedTokens() {
    if (extendedTokens == null)
      return new HashMap<>(); 
    HashSet<String> hashSet = new HashSet();
    for (Map.Entry<String, ExtendedToken> entry : extendedTokens.entrySet()) {
      if (((ExtendedToken)entry.getValue()).expire != 0L && ((ExtendedToken)entry.getValue()).expire < System.currentTimeMillis())
        hashSet.add((String)entry.getKey()); 
    } 
    return hashSet.isEmpty() ? new HashMap<>() : makeNewTokens(hashSet);
  }
  
  public static synchronized RequestRestoreReport restore(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) throws Exception {
    RestoreRequest restoreRequest;
    boolean bool = false;
    if (oauth != null && (isTokenExpired() || oauth.accessToken == null))
      if (paramBoolean3) {
        oauth = null;
      } else {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(authId, oauth.refreshToken);
        RefreshTokenRequestEvent refreshTokenRequestEvent = (RefreshTokenRequestEvent)refreshTokenRequest.request();
        setOAuth(authId, refreshTokenRequestEvent.oauth);
        bool = true;
      }  
    if (oauth != null) {
      restoreRequest = new RestoreRequest(authId, oauth.accessToken, paramBoolean2 ? getExpiredExtendedTokens() : getStringExtendedTokens(), paramBoolean1);
    } else {
      restoreRequest = new RestoreRequest(authId, null, paramBoolean2 ? getExpiredExtendedTokens() : getStringExtendedTokens(), false);
    } 
    if (paramBoolean2 && (restoreRequest.extended == null || restoreRequest.extended.isEmpty()))
      return new RequestRestoreReport(bool, null, null); 
    RestoreRequestEvent restoreRequestEvent = (RestoreRequestEvent)restoreRequest.request();
    List<String> list = null;
    if (restoreRequestEvent.invalidTokens != null && !restoreRequestEvent.invalidTokens.isEmpty()) {
      Map<String, String> map = makeNewTokens(restoreRequestEvent.invalidTokens);
      if (!map.isEmpty()) {
        restoreRequest = new RestoreRequest(authId, null, map, false);
        restoreRequestEvent = (RestoreRequestEvent)restoreRequest.request();
        if (restoreRequestEvent.invalidTokens != null && !restoreRequestEvent.invalidTokens.isEmpty())
          LogHelper.warning("Tokens %s not restored", new Object[] { String.join(",", restoreRequestEvent.invalidTokens) }); 
      } 
      list = restoreRequestEvent.invalidTokens;
    } 
    return new RequestRestoreReport(bool, list, restoreRequestEvent.userInfo);
  }
  
  private static synchronized Map<String, String> makeNewTokens(Collection<String> paramCollection) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (ExtendedTokenCallback extendedTokenCallback : extendedTokenCallbacks) {
      for (String str : paramCollection) {
        ExtendedToken extendedToken = extendedTokenCallback.tryGetNewToken(str);
        if (extendedToken != null) {
          hashMap.put(str, extendedToken.token);
          addExtendedToken(str, extendedToken);
        } 
      } 
    } 
    return (Map)hashMap;
  }
  
  public static void requestError(String paramString) throws RequestException {
    throw new RequestException(paramString);
  }
  
  public void addExtendedTokenCallback(ExtendedTokenCallback paramExtendedTokenCallback) {
    extendedTokenCallbacks.add(paramExtendedTokenCallback);
  }
  
  public void removeExtendedTokenCallback(ExtendedTokenCallback paramExtendedTokenCallback) {
    extendedTokenCallbacks.remove(paramExtendedTokenCallback);
  }
  
  public void addOAuthChangeHandler(BiConsumer<String, AuthRequestEvent.OAuthRequestEvent> paramBiConsumer) {
    oauthChangeHandlers.add(paramBiConsumer);
  }
  
  public void removeOAuthChangeHandler(BiConsumer<String, AuthRequestEvent.OAuthRequestEvent> paramBiConsumer) {
    oauthChangeHandlers.remove(paramBiConsumer);
  }
  
  public R request() throws Exception {
    if (!this.started.compareAndSet(false, true))
      throw new IllegalStateException("Request already started"); 
    if (!isAvailable())
      throw new RequestException("RequestService not initialized"); 
    return requestDo(requestService);
  }
  
  @Deprecated
  public R request(StdWebSocketService paramStdWebSocketService) throws Exception {
    if (!this.started.compareAndSet(false, true))
      throw new IllegalStateException("Request already started"); 
    return requestDo((RequestService)paramStdWebSocketService);
  }
  
  public R request(RequestService paramRequestService) throws Exception {
    if (!this.started.compareAndSet(false, true))
      throw new IllegalStateException("Request already started"); 
    return requestDo(paramRequestService);
  }
  
  protected R requestDo(RequestService paramRequestService) throws Exception {
    return paramRequestService.requestSync(this);
  }
  
  public static class ExtendedToken {
    public final String token;
    
    public final long expire;
    
    public ExtendedToken(String param1String, long param1Long) {
      this.token = param1String;
      long l = System.currentTimeMillis();
      this.expire = (param1Long < l / 2L) ? (l + param1Long) : param1Long;
    }
  }
  
  public static class RequestRestoreReport {
    public final boolean refreshed;
    
    public final List<String> invalidExtendedTokens;
    
    public final CurrentUserRequestEvent.UserInfo userInfo;
    
    public RequestRestoreReport(boolean param1Boolean, List<String> param1List, CurrentUserRequestEvent.UserInfo param1UserInfo) {
      this.refreshed = param1Boolean;
      this.invalidExtendedTokens = param1List;
      this.userInfo = param1UserInfo;
    }
  }
  
  public static interface ExtendedTokenCallback {
    Request.ExtendedToken tryGetNewToken(String param1String);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\Request.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */