package pro.gravit.launcher.gui.scenes.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.events.request.RefreshTokenRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.RefreshTokenRequest;
import pro.gravit.launcher.base.request.auth.details.AuthLoginOnlyDetails;
import pro.gravit.launcher.base.request.auth.details.AuthPasswordDetails;
import pro.gravit.launcher.base.request.auth.details.AuthTotpDetails;
import pro.gravit.launcher.base.request.auth.details.AuthWebViewDetails;
import pro.gravit.launcher.base.request.auth.password.Auth2FAPassword;
import pro.gravit.launcher.base.request.auth.password.AuthMultiPassword;
import pro.gravit.launcher.base.request.auth.password.AuthOAuthPassword;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.scenes.login.methods.AbstractAuthMethod;
import pro.gravit.launcher.gui.scenes.login.methods.LoginAndPasswordAuthMethod;
import pro.gravit.launcher.gui.scenes.login.methods.LoginOnlyAuthMethod;
import pro.gravit.launcher.gui.scenes.login.methods.TotpAuthMethod;
import pro.gravit.launcher.gui.scenes.login.methods.WebAuthMethod;
import pro.gravit.utils.helper.LogHelper;

public class AuthFlow {
  public Map<Class<? extends GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails>, AbstractAuthMethod<? extends GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails>> authMethods = new HashMap<>(8);
  
  private final LoginScene.LoginSceneAccessor accessor;
  
  private final List<Integer> authFlow = new ArrayList<>();
  
  private GetAvailabilityAuthRequestEvent.AuthAvailability authAvailability;
  
  private volatile AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> authMethodOnShow;
  
  private final Consumer<SuccessAuth> onSuccessAuth;
  
  public boolean isLoginStarted = false;
  
  public AuthFlow(LoginScene.LoginSceneAccessor paramLoginSceneAccessor, Consumer<SuccessAuth> paramConsumer) {
    this.accessor = paramLoginSceneAccessor;
    this.onSuccessAuth = paramConsumer;
    this.authMethods.put(AuthPasswordDetails.class, new LoginAndPasswordAuthMethod(paramLoginSceneAccessor));
    this.authMethods.put(AuthWebViewDetails.class, new WebAuthMethod(paramLoginSceneAccessor));
    this.authMethods.put(AuthTotpDetails.class, new TotpAuthMethod(paramLoginSceneAccessor));
    this.authMethods.put(AuthLoginOnlyDetails.class, new LoginOnlyAuthMethod(paramLoginSceneAccessor));
  }
  
  public void init(GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability) {
    this.authAvailability = paramAuthAvailability;
    reset();
    loginWithGui();
  }
  
  public void reset() {
    LogHelper.info("=== AuthFlow.reset called ===");
    this.authFlow.clear();
    this.authFlow.add(Integer.valueOf(0));
    if (this.authMethodOnShow != null)
      this.authMethodOnShow.onUserCancel(); 
    if (!this.accessor.isEmptyContent()) {
      this.accessor.clearContent();
      this.accessor.setState(LoginAuthButtonComponent.AuthButtonState.ACTIVE);
    } 
    if (this.authMethodOnShow != null && !this.authMethodOnShow.isOverlay())
      loginWithGui(); 
    this.authMethodOnShow = null;
    for (AbstractAuthMethod<? extends GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> abstractAuthMethod : this.authMethods.values())
      abstractAuthMethod.reset(); 
    LogHelper.info("authMethodOnShow: " + String.valueOf(this.authMethodOnShow));
    LogHelper.info("isEmptyContent: " + this.accessor.isEmptyContent());
  }
  
  private CompletableFuture<LoginAndPasswordResult> tryLogin(String paramString, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    CompletableFuture<LoginAndPasswordResult> completableFuture = null;
    if (paramAuthPasswordInterface != null) {
      completableFuture = new CompletableFuture();
      completableFuture.complete(new LoginAndPasswordResult(paramString, paramAuthPasswordInterface));
    } 
    Iterator<Integer> iterator = this.authFlow.iterator();
    while (iterator.hasNext()) {
      int i = ((Integer)iterator.next()).intValue();
      GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails authAvailabilityDetails = this.authAvailability.details.get(i);
      AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> abstractAuthMethod = detailsToMethod(authAvailabilityDetails);
      if (completableFuture == null) {
        completableFuture = abstractAuthMethod.show(authAvailabilityDetails).thenCompose(paramVoid -> {
              this.authMethodOnShow = paramAbstractAuthMethod;
              return CompletableFuture.completedFuture(paramVoid);
            }).thenCompose(paramVoid -> paramAbstractAuthMethod.auth(paramAuthAvailabilityDetails)).thenCompose(paramLoginAndPasswordResult -> {
              this.authMethodOnShow = null;
              return CompletableFuture.completedFuture(paramLoginAndPasswordResult);
            });
      } else {
        completableFuture = completableFuture.thenCompose(paramLoginAndPasswordResult -> paramAbstractAuthMethod.show(paramAuthAvailabilityDetails).thenApply(()));
        completableFuture = completableFuture.thenCompose(paramLoginAndPasswordResult -> {
              this.authMethodOnShow = paramAbstractAuthMethod;
              return CompletableFuture.completedFuture(paramLoginAndPasswordResult);
            });
        completableFuture = completableFuture.thenCompose(paramLoginAndPasswordResult -> paramAbstractAuthMethod.auth(paramAuthAvailabilityDetails).thenApply(()));
        completableFuture = completableFuture.thenCompose(paramLoginAndPasswordResult -> {
              this.authMethodOnShow = null;
              return CompletableFuture.completedFuture(paramLoginAndPasswordResult);
            });
      } 
      completableFuture = completableFuture.thenCompose(paramLoginAndPasswordResult -> paramAbstractAuthMethod.hide().thenApply(()));
    } 
    return completableFuture;
  }
  
  public AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> getAuthMethodOnShow() {
    return this.authMethodOnShow;
  }
  
  private void start(CompletableFuture<SuccessAuth> paramCompletableFuture, String paramString, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    CompletableFuture<LoginAndPasswordResult> completableFuture = tryLogin(paramString, paramAuthPasswordInterface);
    completableFuture.thenAccept(paramLoginAndPasswordResult -> login(paramLoginAndPasswordResult.login, paramLoginAndPasswordResult.password, this.authAvailability, paramCompletableFuture)).exceptionally(paramThrowable -> {
          paramThrowable = paramThrowable.getCause();
          reset();
          (this.accessor.getApplication()).runtimeSettings.login = null;
          (this.accessor.getApplication()).runtimeSettings.password = null;
          (this.accessor.getApplication()).runtimeSettings.autoAuth = false;
          this.isLoginStarted = false;
          if (paramThrowable instanceof AbstractAuthMethod.UserAuthCanceledException)
            return null; 
          this.accessor.errorHandle(paramThrowable);
          return null;
        });
  }
  
  private CompletableFuture<SuccessAuth> start() {
    CompletableFuture<SuccessAuth> completableFuture = new CompletableFuture();
    start(completableFuture, null, null);
    return completableFuture;
  }
  
  private void login(String paramString, AuthRequest.AuthPasswordInterface paramAuthPasswordInterface, GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability, CompletableFuture<SuccessAuth> paramCompletableFuture) {
    this.isLoginStarted = true;
    JavaFXApplication javaFXApplication = this.accessor.getApplication();
    LogHelper.dev("Auth with %s password ***** authId %s", new Object[] { paramString, paramAuthAvailability });
    AuthRequest authRequest = javaFXApplication.authService.makeAuthRequest(paramString, paramAuthPasswordInterface, paramAuthAvailability.name);
    this.accessor.processing((Request<WebSocketEvent>)authRequest, javaFXApplication.getTranslation("runtime.overlay.processing.text.auth"), paramAuthRequestEvent -> paramCompletableFuture.complete(new SuccessAuth(paramAuthRequestEvent, paramString, paramAuthPasswordInterface)), paramString2 -> {
          if (paramString2.equals("auth.invalidtoken")) {
            paramJavaFXApplication.runtimeSettings.oauthAccessToken = null;
            paramJavaFXApplication.runtimeSettings.oauthRefreshToken = null;
            paramCompletableFuture.completeExceptionally((Throwable)new RequestException(paramString2));
          } else if (paramString2.equals("auth.require2fa")) {
            this.authFlow.clear();
            this.authFlow.add(Integer.valueOf(1));
            this.accessor.runInFxThread(());
          } else if (paramString2.startsWith("auth.require.factor.")) {
            ArrayList<Integer> arrayList = new ArrayList();
            for (String str : paramString2.substring("auth.require.factor.".length() + 1).split("\\."))
              arrayList.add(Integer.valueOf(Integer.parseInt(str))); 
            this.authFlow.clear();
            this.authFlow.addAll(arrayList);
            this.accessor.runInFxThread(());
          } else {
            this.authFlow.clear();
            this.authFlow.add(Integer.valueOf(0));
            this.accessor.runInFxThread(());
            this.accessor.errorHandle((Throwable)new RequestException(paramString2));
          } 
        });
  }
  
  public void loginWithGui() {
    this.accessor.setState(LoginAuthButtonComponent.AuthButtonState.UNACTIVE);
    AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> abstractAuthMethod = getAuthMethodOnShow();
    if (abstractAuthMethod != null) {
      abstractAuthMethod.onAuthClicked();
      return;
    } 
    start().thenAccept(paramSuccessAuth -> {
          if (this.onSuccessAuth != null)
            this.onSuccessAuth.accept(paramSuccessAuth); 
        });
  }
  
  private boolean tryOAuthLogin() {
    JavaFXApplication javaFXApplication = this.accessor.getApplication();
    if (javaFXApplication.runtimeSettings.lastAuth != null && this.authAvailability.name.equals(javaFXApplication.runtimeSettings.lastAuth.name) && javaFXApplication.runtimeSettings.oauthAccessToken != null) {
      if (javaFXApplication.runtimeSettings.oauthExpire != 0L && javaFXApplication.runtimeSettings.oauthExpire < System.currentTimeMillis()) {
        refreshToken();
        return true;
      } 
      if (!javaFXApplication.guiModuleConfig.autoAuth && !javaFXApplication.runtimeSettings.autoAuth)
        return false; 
      Request.setOAuth(this.authAvailability.name, new AuthRequestEvent.OAuthRequestEvent(javaFXApplication.runtimeSettings.oauthAccessToken, javaFXApplication.runtimeSettings.oauthRefreshToken, javaFXApplication.runtimeSettings.oauthExpire), javaFXApplication.runtimeSettings.oauthExpire);
      AuthOAuthPassword authOAuthPassword = new AuthOAuthPassword(javaFXApplication.runtimeSettings.oauthAccessToken);
      LogHelper.info("Login with OAuth AccessToken");
      loginWithOAuth(authOAuthPassword, this.authAvailability, true);
      return true;
    } 
    return false;
  }
  
  private void refreshToken() {
    JavaFXApplication javaFXApplication = this.accessor.getApplication();
    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(this.authAvailability.name, javaFXApplication.runtimeSettings.oauthRefreshToken);
    this.accessor.processing((Request<WebSocketEvent>)refreshTokenRequest, javaFXApplication.getTranslation("runtime.overlay.processing.text.auth"), paramRefreshTokenRequestEvent -> {
          paramJavaFXApplication.runtimeSettings.oauthAccessToken = paramRefreshTokenRequestEvent.oauth.accessToken;
          paramJavaFXApplication.runtimeSettings.oauthRefreshToken = paramRefreshTokenRequestEvent.oauth.refreshToken;
          paramJavaFXApplication.runtimeSettings.oauthExpire = (paramRefreshTokenRequestEvent.oauth.expire == 0L) ? 0L : (System.currentTimeMillis() + paramRefreshTokenRequestEvent.oauth.expire);
          Request.setOAuth(this.authAvailability.name, paramRefreshTokenRequestEvent.oauth);
          AuthOAuthPassword authOAuthPassword = new AuthOAuthPassword(paramJavaFXApplication.runtimeSettings.oauthAccessToken);
          LogHelper.info("Login with OAuth AccessToken");
          loginWithOAuth(authOAuthPassword, this.authAvailability, false);
        }paramString -> {
          paramJavaFXApplication.runtimeSettings.oauthAccessToken = null;
          paramJavaFXApplication.runtimeSettings.oauthRefreshToken = null;
          this.accessor.runInFxThread(this::loginWithGui);
        });
  }
  
  private void loginWithOAuth(AuthOAuthPassword paramAuthOAuthPassword, GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability, boolean paramBoolean) {
    JavaFXApplication javaFXApplication = this.accessor.getApplication();
    AuthRequest authRequest = javaFXApplication.authService.makeAuthRequest(null, (AuthRequest.AuthPasswordInterface)paramAuthOAuthPassword, paramAuthAvailability.name);
    this.accessor.processing((Request<WebSocketEvent>)authRequest, javaFXApplication.getTranslation("runtime.overlay.processing.text.auth"), paramAuthRequestEvent -> this.accessor.runInFxThread(()), paramString -> {
          if (paramBoolean && paramString.equals("auth.expiretoken")) {
            refreshToken();
            return;
          } 
          if (paramString.equals("auth.invalidtoken")) {
            paramJavaFXApplication.runtimeSettings.oauthAccessToken = null;
            paramJavaFXApplication.runtimeSettings.oauthRefreshToken = null;
            this.accessor.runInFxThread(this::loginWithGui);
          } else {
            this.accessor.errorHandle((Throwable)new RequestException(paramString));
          } 
        });
  }
  
  private AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> detailsToMethod(GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails paramAuthAvailabilityDetails) {
    return (AbstractAuthMethod<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails>)this.authMethods.get(paramAuthAvailabilityDetails.getClass());
  }
  
  public void prepare() {
    this.authMethods.forEach((paramClass, paramAbstractAuthMethod) -> paramAbstractAuthMethod.prepare());
  }
  
  public static final class LoginAndPasswordResult extends Record {
    private final String login;
    
    private final AuthRequest.AuthPasswordInterface password;
    
    public LoginAndPasswordResult(String param1String, AuthRequest.AuthPasswordInterface param1AuthPasswordInterface) {
      this.login = param1String;
      this.password = param1AuthPasswordInterface;
    }
    
    public final String toString() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;)Ljava/lang/String;
      //   6: areturn
    }
    
    public final int hashCode() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;)I
      //   6: ireturn
    }
    
    public final boolean equals(Object param1Object) {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$LoginAndPasswordResult;Ljava/lang/Object;)Z
      //   7: ireturn
    }
    
    public String login() {
      return this.login;
    }
    
    public AuthRequest.AuthPasswordInterface password() {
      return this.password;
    }
  }
  
  public static final class SuccessAuth extends Record {
    private final AuthRequestEvent requestEvent;
    
    private final String recentLogin;
    
    private final AuthRequest.AuthPasswordInterface recentPassword;
    
    public SuccessAuth(AuthRequestEvent param1AuthRequestEvent, String param1String, AuthRequest.AuthPasswordInterface param1AuthPasswordInterface) {
      this.requestEvent = param1AuthRequestEvent;
      this.recentLogin = param1String;
      this.recentPassword = param1AuthPasswordInterface;
    }
    
    public final String toString() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;)Ljava/lang/String;
      //   6: areturn
    }
    
    public final int hashCode() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;)I
      //   6: ireturn
    }
    
    public final boolean equals(Object param1Object) {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/scenes/login/AuthFlow$SuccessAuth;Ljava/lang/Object;)Z
      //   7: ireturn
    }
    
    public AuthRequestEvent requestEvent() {
      return this.requestEvent;
    }
    
    public String recentLogin() {
      return this.recentLogin;
    }
    
    public AuthRequest.AuthPasswordInterface recentPassword() {
      return this.recentPassword;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\AuthFlow.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */