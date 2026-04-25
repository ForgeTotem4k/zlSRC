package pro.gravit.launcher.gui.scenes.login;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.events.request.LauncherRequestEvent;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.profiles.Texture;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.GetAvailabilityAuthRequest;
import pro.gravit.launcher.base.request.update.LauncherRequest;
import pro.gravit.launcher.base.request.update.ProfilesRequest;
import pro.gravit.launcher.client.events.ClientExitPhase;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.StdJavaRuntimeProvider;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.scenes.internal.OnlineCounter;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.utils.LauncherUpdater;
import pro.gravit.utils.helper.LogHelper;

public class LoginScene extends AbstractScene {
  private List<GetAvailabilityAuthRequestEvent.AuthAvailability> auth;
  
  private Pane content;
  
  private AbstractVisualComponent contentComponent;
  
  private LoginAuthButtonComponent authButton;
  
  private ComboBox<GetAvailabilityAuthRequestEvent.AuthAvailability> authList;
  
  private GetAvailabilityAuthRequestEvent.AuthAvailability authAvailability;
  
  private final AuthFlow authFlow;
  
  private OnlineCounter onlineCounter;
  
  public LoginScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/login/login.fxml", paramJavaFXApplication);
    LoginSceneAccessor loginSceneAccessor = new LoginSceneAccessor();
    this.authFlow = new AuthFlow(loginSceneAccessor, this::onSuccessLogin);
  }
  
  public void doInit() {
    animation();
    this.authFlow.prepare();
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#settingsb" })).setOnAction(paramActionEvent -> {
          try {
            switchScene((AbstractScene)this.application.gui.settingsScene);
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    this.application.runtimeSettings.locale = RuntimeSettings.LAUNCHER_LOCALE.RUSSIAN;
    this.application.guiModuleConfig.locale = "ru";
    if (this.application.runtimeSettings.password != null) {
      LogHelper.info("Application Password here!");
    } else {
      LogHelper.info("Saved password is null");
    } 
    String str1 = System.getProperty("user.home");
    Path path1 = Paths.get(str1, new String[0]);
    Path path2 = path1.resolve("zLauncher").resolve("updates");
    String str2 = path2.toAbsolutePath().toString();
    this.application.runtimeSettings.updatesDirPath = str2;
    this.application.runtimeSettings.updatesDir = Paths.get(str2, new String[0]);
    this.application.runtimeSettings.oauthAccessToken = null;
    this.application.runtimeSettings.oauthExpire = 0L;
    this.application.guiModuleConfig.lazy = true;
    this.application.runtimeSettings.apply();
    try {
      this.application.saveSettings();
    } catch (Exception exception) {}
    try {
      this.onlineCounter = new OnlineCounter();
      final TextArea text = (TextArea)LookupHelper.lookup((Node)this.layout, new String[] { "#online" });
      this.onlineCounter.setCallback(new OnlineCounter.OnlineUpdateCallback() {
            public void onOnlineReceived(int param1Int) {
              text.setText("Онлайн: \n" + param1Int + " человек");
            }
            
            public void onError(String param1String) {
              text.setText("Недоступно");
              text.setStyle("-fx-text-fill: red;");
              text.setTooltip(new Tooltip("Ошибка: " + param1String));
            }
          });
      loadOnline();
    } catch (Exception exception) {}
    this.authButton = new LoginAuthButtonComponent((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#authButton" }), this.application, paramActionEvent -> {
          Objects.requireNonNull(this.authFlow);
          this.contextHelper.runCallback(this.authFlow::loginWithGui);
        });
    this.content = (Pane)LookupHelper.lookup((Node)this.layout, new String[] { "#content" });
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#createAccount" })).setOnAction(paramActionEvent -> {
          try {
            switchScene((AbstractScene)this.application.gui.browserScene);
            this.application.gui.browserScene.reset();
          } catch (Exception exception) {
            throw new RuntimeException(exception);
          } 
        });
    ((ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#vk" })).setOnMouseClicked(paramMouseEvent -> this.application.openURL("https://vk.com/end_craft_ru"));
    ((ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#ds" })).setOnMouseClicked(paramMouseEvent -> this.application.openURL("https://discord.gg/wuczdENP"));
    this.authList = (ComboBox<GetAvailabilityAuthRequestEvent.AuthAvailability>)LookupHelper.lookup((Node)this.layout, new String[] { "#authList" });
    this.authList.setConverter(new AuthAvailabilityStringConverter());
    this.authList.setOnAction(paramActionEvent -> changeAuthAvailability((GetAvailabilityAuthRequestEvent.AuthAvailability)this.authList.getSelectionModel().getSelectedItem()));
  }
  
  private void loadOnline() {
    this.onlineCounter.fetchOnlineAsync().thenAccept(paramInteger -> LogHelper.info("Online loaded successfully: " + paramInteger)).exceptionally(paramThrowable -> {
          LogHelper.error("Failed to load online: " + paramThrowable.getMessage());
          return null;
        });
  }
  
  private void animation() {
    ImageView imageView1 = (ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#oblako1" });
    ImageView imageView2 = (ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#oblako2" });
    TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(2.5D), (Node)imageView1);
    translateTransition1.setFromX(this.layout.getWidth());
    translateTransition1.setToX(-this.layout.getWidth() - 550.0D);
    translateTransition1.setCycleCount(-1);
    translateTransition1.setRate(0.6D);
    TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(4.5D), (Node)imageView2);
    translateTransition2.setFromX(imageView2.getFitWidth());
    translateTransition2.setToX(-this.layout.getWidth() - 650.0D);
    translateTransition2.setCycleCount(-1);
    translateTransition2.setRate(1.0D);
    translateTransition2.setDelay(Duration.seconds(1.0D));
    Runnable runnable1 = () -> {
        paramTranslateTransition.setFromX(paramImageView.getFitWidth());
        paramTranslateTransition.setToX(-this.layout.getWidth() - 550.0D);
        paramTranslateTransition.playFromStart();
      };
    translateTransition1.setOnFinished(paramActionEvent -> paramRunnable.run());
    Runnable runnable2 = () -> {
        paramTranslateTransition.setFromX(paramImageView.getFitWidth());
        paramTranslateTransition.setToX(-this.layout.getWidth() - 670.0D);
        paramTranslateTransition.playFromStart();
      };
    translateTransition2.setOnFinished(paramActionEvent -> paramRunnable.run());
    translateTransition1.play();
    translateTransition2.play();
  }
  
  protected void doPostInit() {
    if (!this.application.isDebugMode()) {
      launcherRequest();
    } else {
      getAvailabilityAuth();
    } 
  }
  
  private void launcherRequest() {
    LauncherRequest launcherRequest = new LauncherRequest();
    processRequest(this.application.getTranslation("runtime.overlay.processing.text.launcher"), (Request)launcherRequest, paramLauncherRequestEvent -> {
          if (paramLauncherRequestEvent.needUpdate)
            try {
              LogHelper.debug("Start update processing");
              disable();
              StdJavaRuntimeProvider.updatePath = LauncherUpdater.prepareUpdate((new URI(paramLauncherRequestEvent.url)).toURL());
              LogHelper.debug("Exit with Platform.exit");
              Platform.exit();
              return;
            } catch (Throwable throwable) {
              this.contextHelper.runInFxThread(());
              try {
                Thread.sleep(1500L);
                LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientExitPhase(0));
                Platform.exit();
              } catch (Throwable throwable1) {
                LauncherEngine.exitLauncher(0);
              } 
            }  
          LogHelper.dev("Launcher update processed");
          getAvailabilityAuth();
        }paramActionEvent -> LauncherEngine.exitLauncher(0));
  }
  
  private void getAvailabilityAuth() {
    GetAvailabilityAuthRequest getAvailabilityAuthRequest = new GetAvailabilityAuthRequest();
    processing((Request<WebSocketEvent>)getAvailabilityAuthRequest, this.application.getTranslation("runtime.overlay.processing.text.authAvailability"), paramGetAvailabilityAuthRequestEvent -> this.contextHelper.runInFxThread(()), (Consumer<String>)null);
  }
  
  private void runAutoAuth() {
    if (this.application.guiModuleConfig.autoAuth || this.application.runtimeSettings.autoAuth) {
      Objects.requireNonNull(this.authFlow);
      this.contextHelper.runInFxThread(this.authFlow::loginWithGui);
    } 
  }
  
  public void changeAuthAvailability(GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability) {
    LogHelper.info("=== changeAuthAvailability CALLED ===");
    LogHelper.info("Current auth: " + ((this.authAvailability != null) ? this.authAvailability.name : "null"));
    LogHelper.info("New auth: " + paramAuthAvailability.name);
    this.authAvailability = paramAuthAvailability;
    this.application.authService.setAuthAvailability(paramAuthAvailability);
    ((SingleSelectionModel)this.authList.selectionModelProperty().get()).select(paramAuthAvailability);
    LogHelper.info("Calling authFlow.init...");
    this.authFlow.init(paramAuthAvailability);
    if (this.content != null)
      this.content.requestLayout(); 
    LogHelper.info("=== changeAuthAvailability COMPLETED ===");
  }
  
  public void resetAuthForm() {
    Platform.runLater(() -> {
          if (this.authAvailability != null) {
            this.authFlow.init(this.authAvailability);
            this.authFlow.loginWithGui();
          } 
        });
  }
  
  public void addAuthAvailability(GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability) {
    this.authList.getItems().add(paramAuthAvailability);
    LogHelper.info("Added %s: %s", new Object[] { paramAuthAvailability.name, paramAuthAvailability.displayName });
  }
  
  public <T extends WebSocketEvent> void processing(Request<T> paramRequest, String paramString, Consumer<T> paramConsumer, Consumer<String> paramConsumer1) {
    processRequest(paramString, paramRequest, paramConsumer, paramThrowable -> paramConsumer.accept(paramThrowable.getCause().getMessage()), null);
  }
  
  public void errorHandle(Throwable paramThrowable) {
    super.errorHandle(paramThrowable);
  }
  
  public void reset() {
    this.authFlow.reset();
  }
  
  public String getName() {
    return "login";
  }
  
  private boolean checkSavePasswordAvailable(AuthRequest.AuthPasswordInterface paramAuthPasswordInterface) {
    return (paramAuthPasswordInterface instanceof pro.gravit.launcher.base.request.auth.password.Auth2FAPassword) ? false : ((paramAuthPasswordInterface instanceof pro.gravit.launcher.base.request.auth.password.AuthMultiPassword) ? false : ((this.authAvailability != null && this.authAvailability.details != null && !this.authAvailability.details.isEmpty() && this.authAvailability.details.get(0) instanceof pro.gravit.launcher.base.request.auth.details.AuthPasswordDetails)));
  }
  
  public void onSuccessLogin(AuthFlow.SuccessAuth paramSuccessAuth) {
    AuthRequestEvent authRequestEvent = paramSuccessAuth.requestEvent();
    this.application.authService.setAuthResult(this.authAvailability.name, authRequestEvent);
    this.application.runtimeSettings.login = paramSuccessAuth.recentLogin();
    if (paramSuccessAuth.recentPassword() != null)
      this.application.runtimeSettings.password = paramSuccessAuth.recentPassword(); 
    if (authRequestEvent.oauth == null) {
      LogHelper.warning("Password not saved");
    } else {
      this.application.runtimeSettings.oauthAccessToken = authRequestEvent.oauth.accessToken;
      this.application.runtimeSettings.oauthRefreshToken = authRequestEvent.oauth.refreshToken;
      this.application.runtimeSettings.oauthExpire = Request.getTokenExpiredTime();
    } 
    this.application.runtimeSettings.lastAuth = this.authAvailability;
    if (authRequestEvent.playerProfile != null && authRequestEvent.playerProfile.assets != null)
      try {
        Texture texture1 = (Texture)authRequestEvent.playerProfile.assets.get("SKIN");
        Texture texture2 = (Texture)authRequestEvent.playerProfile.assets.get("AVATAR");
        if (texture1 != null || texture2 != null) {
          this.application.skinManager.addSkinWithAvatar(authRequestEvent.playerProfile.username, (texture1 != null) ? new URI(texture1.url) : null, (texture2 != null) ? new URI(texture2.url) : null);
          this.application.skinManager.getSkin(authRequestEvent.playerProfile.username);
        } 
      } catch (Exception exception) {
        LogHelper.error(exception);
      }  
    this.contextHelper.runInFxThread(() -> {
          if (this.application.gui.welcomeOverlay.isInit())
            this.application.gui.welcomeOverlay.reset(); 
          showOverlay((AbstractOverlay)this.application.gui.welcomeOverlay, ());
        });
  }
  
  public void onGetProfiles() {
    processing((Request<WebSocketEvent>)new ProfilesRequest(), this.application.getTranslation("runtime.overlay.processing.text.profiles"), paramProfilesRequestEvent -> {
          this.application.profilesService.setProfilesResult(paramProfilesRequestEvent);
          this.application.runtimeSettings.profiles = paramProfilesRequestEvent.profiles;
          this.contextHelper.runInFxThread(());
        }(Consumer<String>)null);
  }
  
  public void clearPassword() {
    this.application.runtimeSettings.password = null;
    this.application.runtimeSettings.login = null;
    this.application.runtimeSettings.oauthAccessToken = null;
    this.application.runtimeSettings.oauthRefreshToken = null;
  }
  
  public AuthFlow getAuthFlow() {
    return this.authFlow;
  }
  
  public class LoginSceneAccessor extends AbstractScene.SceneAccessor {
    public LoginSceneAccessor() {
      super(LoginScene.this);
    }
    
    public void showContent(AbstractVisualComponent param1AbstractVisualComponent) throws Exception {
      param1AbstractVisualComponent.init();
      param1AbstractVisualComponent.postInit();
      if (LoginScene.this.contentComponent != null)
        LoginScene.this.content.getChildren().clear(); 
      LoginScene.this.contentComponent = param1AbstractVisualComponent;
      LoginScene.this.content.getChildren().add(param1AbstractVisualComponent.getLayout());
    }
    
    public LoginAuthButtonComponent getAuthButton() {
      return LoginScene.this.authButton;
    }
    
    public void setState(LoginAuthButtonComponent.AuthButtonState param1AuthButtonState) {
      LoginScene.this.authButton.setState(param1AuthButtonState);
    }
    
    public boolean isEmptyContent() {
      return LoginScene.this.content.getChildren().isEmpty();
    }
    
    public void clearContent() {
      LoginScene.this.content.getChildren().clear();
    }
    
    public <T extends WebSocketEvent> void processing(Request<T> param1Request, String param1String, Consumer<T> param1Consumer, Consumer<String> param1Consumer1) {
      LoginScene.this.processing(param1Request, param1String, param1Consumer, param1Consumer1);
    }
  }
  
  private static class AuthAvailabilityStringConverter extends StringConverter<GetAvailabilityAuthRequestEvent.AuthAvailability> {
    public String toString(GetAvailabilityAuthRequestEvent.AuthAvailability param1AuthAvailability) {
      return (param1AuthAvailability == null) ? "null" : param1AuthAvailability.displayName;
    }
    
    public GetAvailabilityAuthRequestEvent.AuthAvailability fromString(String param1String) {
      return null;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */