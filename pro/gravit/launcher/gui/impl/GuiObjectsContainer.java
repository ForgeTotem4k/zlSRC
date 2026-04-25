package pro.gravit.launcher.gui.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.overlays.ProcessingOverlay;
import pro.gravit.launcher.gui.overlays.UploadAssetOverlay;
import pro.gravit.launcher.gui.overlays.WelcomeOverlay;
import pro.gravit.launcher.gui.scenes.console.ConsoleScene;
import pro.gravit.launcher.gui.scenes.debug.DebugScene;
import pro.gravit.launcher.gui.scenes.internal.BrowserScene;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.launcher.gui.scenes.options.OptionsScene;
import pro.gravit.launcher.gui.scenes.serverinfo.ServerInfoScene;
import pro.gravit.launcher.gui.scenes.servermenu.ServerMenuScene;
import pro.gravit.launcher.gui.scenes.settings.GlobalSettingsScene;
import pro.gravit.launcher.gui.scenes.settings.SettingsScene;
import pro.gravit.launcher.gui.scenes.update.UpdateScene;
import pro.gravit.launcher.gui.stage.ConsoleStage;
import pro.gravit.utils.helper.LogHelper;

public class GuiObjectsContainer {
  private final JavaFXApplication application;
  
  private final Map<String, AbstractVisualComponent> components = new HashMap<>();
  
  public ProcessingOverlay processingOverlay;
  
  public WelcomeOverlay welcomeOverlay;
  
  public UploadAssetOverlay uploadAssetOverlay;
  
  public UpdateScene updateScene;
  
  public DebugScene debugScene;
  
  public ServerMenuScene serverMenuScene;
  
  public ServerInfoScene serverInfoScene;
  
  public LoginScene loginScene;
  
  public OptionsScene optionsScene;
  
  public SettingsScene settingsScene;
  
  public GlobalSettingsScene globalSettingsScene;
  
  public ConsoleScene consoleScene;
  
  public ConsoleStage consoleStage;
  
  public BrowserScene browserScene;
  
  public BackgroundComponent background;
  
  public GuiObjectsContainer(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public void init() {
    this.background = registerComponent(BackgroundComponent.class);
    this.loginScene = registerComponent(LoginScene.class);
    this.processingOverlay = registerComponent(ProcessingOverlay.class);
    this.welcomeOverlay = registerComponent(WelcomeOverlay.class);
    this.uploadAssetOverlay = registerComponent(UploadAssetOverlay.class);
    this.serverMenuScene = registerComponent(ServerMenuScene.class);
    this.serverInfoScene = registerComponent(ServerInfoScene.class);
    this.optionsScene = registerComponent(OptionsScene.class);
    this.settingsScene = registerComponent(SettingsScene.class);
    this.globalSettingsScene = registerComponent(GlobalSettingsScene.class);
    this.consoleScene = registerComponent(ConsoleScene.class);
    this.updateScene = registerComponent(UpdateScene.class);
    this.debugScene = registerComponent(DebugScene.class);
    this.browserScene = registerComponent(BrowserScene.class);
  }
  
  public Collection<AbstractVisualComponent> getComponents() {
    return this.components.values();
  }
  
  public void reload() throws Exception {
    String str = this.application.getCurrentScene().getName();
    ContextHelper.runInFxThreadStatic(() -> {
          this.application.getMainStage().setScene(null, false);
          this.application.getMainStage().pullBackground(this.background);
          this.application.resetDirectory();
          this.components.clear();
          this.application.getMainStage().resetStyles();
          init();
          this.application.getMainStage().pushBackground(this.background);
          for (AbstractVisualComponent abstractVisualComponent : this.components.values()) {
            if (paramString.equals(abstractVisualComponent.getName()))
              this.application.getMainStage().setScene(abstractVisualComponent, false); 
          } 
        }).get();
  }
  
  public AbstractVisualComponent getByName(String paramString) {
    return this.components.get(paramString);
  }
  
  public <T extends AbstractVisualComponent> T registerComponent(Class<T> paramClass) {
    try {
      AbstractVisualComponent abstractVisualComponent = (AbstractVisualComponent)MethodHandles.publicLookup().findConstructor(paramClass, MethodType.methodType(void.class, JavaFXApplication.class)).invokeWithArguments(new Object[] { this.application });
      this.components.put(abstractVisualComponent.getName(), abstractVisualComponent);
      return (T)abstractVisualComponent;
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      throw new RuntimeException(throwable);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\GuiObjectsContainer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */