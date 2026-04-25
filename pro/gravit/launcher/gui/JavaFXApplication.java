package pro.gravit.launcher.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.client.api.DialogService;
import pro.gravit.launcher.client.events.ClientExitPhase;
import pro.gravit.launcher.gui.commands.RuntimeCommand;
import pro.gravit.launcher.gui.commands.VersionCommand;
import pro.gravit.launcher.gui.config.GuiModuleConfig;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.config.StdSettingsManager;
import pro.gravit.launcher.gui.helper.EnFSHelper;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.FXMLFactory;
import pro.gravit.launcher.gui.impl.GuiEventHandler;
import pro.gravit.launcher.gui.impl.GuiObjectsContainer;
import pro.gravit.launcher.gui.impl.MessageManager;
import pro.gravit.launcher.gui.impl.TriggerManager;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.service.AuthService;
import pro.gravit.launcher.gui.service.JavaService;
import pro.gravit.launcher.gui.service.LaunchService;
import pro.gravit.launcher.gui.service.OfflineService;
import pro.gravit.launcher.gui.service.PingService;
import pro.gravit.launcher.gui.service.ProfilesService;
import pro.gravit.launcher.gui.service.RuntimeDialogService;
import pro.gravit.launcher.gui.stage.PrimaryStage;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.NewLauncherSettings;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.launcher.runtime.client.UserSettings;
import pro.gravit.launcher.runtime.client.events.ClientGuiPhase;
import pro.gravit.launcher.runtime.debug.DebugMain;
import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.launcher.runtime.managers.SettingsManager;
import pro.gravit.utils.command.BaseCommandCategory;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.command.CommandCategory;
import pro.gravit.utils.command.CommandHandler;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class JavaFXApplication extends Application {
  private static final AtomicReference<JavaFXApplication> INSTANCE = new AtomicReference<>();
  
  private static Path runtimeDirectory = null;
  
  public final LauncherConfig config = Launcher.getConfig();
  
  public final ExecutorService workers = Executors.newWorkStealingPool(4);
  
  public RuntimeSettings runtimeSettings;
  
  public RequestService service;
  
  public GuiObjectsContainer gui;
  
  public AuthService authService;
  
  public ProfilesService profilesService;
  
  public LaunchService launchService;
  
  public GuiModuleConfig guiModuleConfig;
  
  public MessageManager messageManager;
  
  public RuntimeSecurityService securityService;
  
  public SkinManager skinManager;
  
  public FXMLFactory fxmlFactory;
  
  public JavaService javaService;
  
  public PingService pingService;
  
  public OfflineService offlineService;
  
  public TriggerManager triggerManager;
  
  private SettingsManager settingsManager;
  
  private PrimaryStage mainStage;
  
  private boolean debugMode;
  
  private ResourceBundle resources;
  
  private static Path enfsDirectory;
  
  private CommandCategory runtimeCategory;
  
  public JavaFXApplication() {
    INSTANCE.set(this);
  }
  
  public static JavaFXApplication getInstance() {
    return INSTANCE.get();
  }
  
  public AbstractScene getCurrentScene() {
    return (AbstractScene)this.mainStage.getVisualComponent();
  }
  
  public PrimaryStage getMainStage() {
    return this.mainStage;
  }
  
  public void init() throws Exception {
    this.guiModuleConfig = new GuiModuleConfig();
    this.settingsManager = (SettingsManager)new StdSettingsManager();
    UserSettings.providers.register("stdruntime", RuntimeSettings.class);
    this.settingsManager.loadConfig();
    NewLauncherSettings newLauncherSettings = this.settingsManager.getConfig();
    if (newLauncherSettings.userSettings.get("stdruntime") == null)
      newLauncherSettings.userSettings.put("stdruntime", RuntimeSettings.getDefault(this.guiModuleConfig)); 
    this.runtimeSettings = (RuntimeSettings)newLauncherSettings.userSettings.get("stdruntime");
    this.runtimeSettings.apply();
    System.setProperty("prism.vsync", String.valueOf(this.runtimeSettings.globalSettings.prismVSync));
    DirBridge.dirUpdates = (this.runtimeSettings.updatesDir == null) ? DirBridge.defaultUpdatesDir : this.runtimeSettings.updatesDir;
    this.service = Request.getRequestService();
    this.service.registerEventHandler((RequestService.EventHandler)new GuiEventHandler(this));
    this.authService = new AuthService(this);
    this.launchService = new LaunchService(this);
    this.profilesService = new ProfilesService(this);
    this.messageManager = new MessageManager(this);
    this.securityService = new RuntimeSecurityService(this);
    this.skinManager = new SkinManager(this);
    this.triggerManager = new TriggerManager(this);
    this.javaService = new JavaService(this);
    this.offlineService = new OfflineService(this);
    this.pingService = new PingService();
    registerCommands();
  }
  
  public final <T extends pro.gravit.launcher.base.request.WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, EventHandler<ActionEvent> paramEventHandler) {
    this.gui.processingOverlay.processRequest((AbstractStage)getMainStage(), paramString, paramRequest, paramConsumer, paramEventHandler);
  }
  
  public final <T extends pro.gravit.launcher.base.request.WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, Consumer<Throwable> paramConsumer1, EventHandler<ActionEvent> paramEventHandler) {
    this.gui.processingOverlay.processRequest((AbstractStage)getMainStage(), paramString, paramRequest, paramConsumer, paramConsumer1, paramEventHandler);
  }
  
  public void start(Stage paramStage) {
    try {
      Class.forName("pro.gravit.launcher.runtime.debug.DebugMain", false, JavaFXApplication.class.getClassLoader());
      if (DebugMain.IS_DEBUG.get()) {
        runtimeDirectory = IOHelper.WORKING_DIR.resolve("runtime");
        this.debugMode = true;
      } 
    } catch (Throwable throwable) {
      if (!(throwable instanceof ClassNotFoundException) && !(throwable instanceof NoClassDefFoundError))
        LogHelper.error(throwable); 
    } 
    try {
      Class.forName("pro.gravit.utils.enfs.EnFS", false, JavaFXApplication.class.getClassLoader());
      EnFSHelper.initEnFS();
      String str = (this.runtimeSettings.theme == null) ? RuntimeSettings.LAUNCHER_THEME.COMMON.name : this.runtimeSettings.theme.name;
      enfsDirectory = EnFSHelper.initEnFSDirectory(this.config, str, runtimeDirectory);
    } catch (Throwable throwable) {
      if (!(throwable instanceof ClassNotFoundException))
        LogHelper.error(throwable); 
      if (this.config.runtimeEncryptKey != null)
        JavaRuntimeModule.noEnFSAlert(); 
    } 
    if (this.runtimeSettings.locale == null)
      this.runtimeSettings.locale = RuntimeSettings.DEFAULT_LOCALE; 
    try {
      updateLocaleResources(this.runtimeSettings.locale.name);
    } catch (Throwable throwable) {
      JavaRuntimeModule.noLocaleAlert(this.runtimeSettings.locale.name);
      if (!(throwable instanceof FileNotFoundException))
        LogHelper.error(throwable); 
      Platform.exit();
    } 
    RuntimeDialogService runtimeDialogService = new RuntimeDialogService(this.messageManager);
    DialogService.setDialogImpl((DialogService.DialogServiceImplementation)runtimeDialogService);
    DialogService.setNotificationImpl((DialogService.DialogServiceNotificationImplementation)runtimeDialogService);
    if (this.offlineService.isOfflineMode() && !this.offlineService.isAvailableOfflineMode() && !this.debugMode) {
      this.messageManager.showDialog(getTranslation("runtime.offline.dialog.header"), getTranslation("runtime.offline.dialog.text"), Platform::exit, Platform::exit, false);
      return;
    } 
    try {
      this.mainStage = new PrimaryStage(this, paramStage, "%s Launcher".formatted(new Object[] { this.config.projectName }));
      this.gui = new GuiObjectsContainer(this);
      this.gui.init();
      this.mainStage.setScene((AbstractVisualComponent)this.gui.loginScene, true);
      this.gui.background.init();
      this.mainStage.pushBackground((AbstractVisualComponent)this.gui.background);
      this.mainStage.show();
      if (this.offlineService.isOfflineMode())
        this.messageManager.createNotification(getTranslation("runtime.offline.notification.header"), getTranslation("runtime.offline.notification.text")); 
      LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientGuiPhase(StdJavaRuntimeProvider.getInstance()));
      AuthRequest.registerProviders();
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      JavaRuntimeModule.errorHandleAlert(throwable);
      Platform.exit();
    } 
  }
  
  public void updateLocaleResources(String paramString) throws IOException {
    InputStream inputStream = getResource("runtime_%s.properties".formatted(new Object[] { paramString }));
    try {
      this.resources = new PropertyResourceBundle(inputStream);
      if (inputStream != null)
        inputStream.close(); 
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    this.fxmlFactory = new FXMLFactory(this.resources, this.workers);
  }
  
  public void resetDirectory() throws IOException {
    if (enfsDirectory != null) {
      String str = (this.runtimeSettings.theme == null) ? RuntimeSettings.LAUNCHER_THEME.COMMON.name : this.runtimeSettings.theme.name;
      enfsDirectory = EnFSHelper.initEnFSDirectory(this.config, str, runtimeDirectory);
    } 
  }
  
  private void registerCommands() {
    this.runtimeCategory = (CommandCategory)new BaseCommandCategory();
    this.runtimeCategory.registerCommand("version", (Command)new VersionCommand());
    if (ConsoleManager.isConsoleUnlock)
      registerPrivateCommands(); 
    ConsoleManager.handler.registerCategory(new CommandHandler.Category(this.runtimeCategory, "runtime"));
  }
  
  public void registerPrivateCommands() {
    if (this.runtimeCategory == null)
      return; 
    this.runtimeCategory.registerCommand("runtime", (Command)new RuntimeCommand(this));
  }
  
  public boolean isThemeSupport() {
    return (enfsDirectory != null);
  }
  
  public void stop() {
    LogHelper.debug("JavaFX method stop invoked");
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientExitPhase(0));
  }
  
  public boolean isDebugMode() {
    return this.debugMode;
  }
  
  private InputStream getResource(String paramString) throws IOException {
    return IOHelper.newInput(getResourceURL(paramString));
  }
  
  public static URL getResourceURL(String paramString) throws IOException {
    if (enfsDirectory != null)
      return getResourceEnFs(paramString); 
    if (runtimeDirectory != null) {
      Path path = runtimeDirectory.resolve(paramString);
      if (!Files.exists(path, new java.nio.file.LinkOption[0]))
        throw new FileNotFoundException("File runtime/%s not found".formatted(new Object[] { paramString })); 
      return path.toUri().toURL();
    } 
    return Launcher.getResourceURL(paramString);
  }
  
  private static URL getResourceEnFs(String paramString) throws IOException {
    return EnFSHelper.getURL(enfsDirectory.resolve(paramString).toString().replaceAll("\\\\", "/"));
  }
  
  public URL tryResource(String paramString) {
    try {
      return getResourceURL(paramString);
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public RuntimeSettings.ProfileSettings getProfileSettings() {
    return getProfileSettings(this.profilesService.getProfile());
  }
  
  public RuntimeSettings.ProfileSettings getProfileSettings(ClientProfile paramClientProfile) {
    if (paramClientProfile == null)
      throw new NullPointerException("ClientProfile not selected"); 
    UUID uUID = paramClientProfile.getUUID();
    RuntimeSettings.ProfileSettings profileSettings = (RuntimeSettings.ProfileSettings)this.runtimeSettings.profileSettings.get(uUID);
    if (profileSettings == null) {
      profileSettings = RuntimeSettings.ProfileSettings.getDefault(this.javaService, paramClientProfile);
      this.runtimeSettings.profileSettings.put(uUID, profileSettings);
    } 
    return profileSettings;
  }
  
  public void setMainScene(AbstractScene paramAbstractScene) throws Exception {
    this.mainStage.setScene((AbstractVisualComponent)paramAbstractScene, true);
  }
  
  public Stage newStage() {
    return newStage(StageStyle.TRANSPARENT);
  }
  
  public Stage newStage(StageStyle paramStageStyle) {
    Stage stage = new Stage();
    stage.initStyle(paramStageStyle);
    stage.setResizable(false);
    return stage;
  }
  
  public final String getTranslation(String paramString) {
    return getTranslation(paramString, "'%s'".formatted(new Object[] { paramString }));
  }
  
  public final String getTranslation(String paramString1, String paramString2) {
    try {
      return this.resources.getString(paramString1);
    } catch (Throwable throwable) {
      return paramString2;
    } 
  }
  
  public boolean openURL(String paramString) {
    try {
      getHostServices().showDocument(paramString);
      return true;
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      return false;
    } 
  }
  
  public void saveSettings() throws IOException {
    this.settingsManager.saveConfig();
    if (this.profilesService != null)
      try {
        this.profilesService.saveAll();
      } catch (Throwable throwable) {
        LogHelper.error(throwable);
      }  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\JavaFXApplication.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */