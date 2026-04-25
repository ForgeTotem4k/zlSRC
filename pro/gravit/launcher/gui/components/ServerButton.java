package pro.gravit.launcher.gui.components;

import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.service.PingService;
import pro.gravit.launcher.gui.utils.JavaFxUtils;
import pro.gravit.utils.helper.LogHelper;

public class ServerButton extends AbstractVisualComponent {
  private static final String SERVER_BUTTON_FXML = "components/serverButton.fxml";
  
  private static final String SERVER_BUTTON_CUSTOM_FXML = "components/serverButton/%s.fxml";
  
  private static final String SERVER_BUTTON_DEFAULT_IMAGE = "images/servers/example.png";
  
  private static final String SERVER_BUTTON_CUSTOM_IMAGE = "images/servers/%s.png";
  
  public ClientProfile profile;
  
  private Button saveButton;
  
  private Button resetButton;
  
  private Region serverLogo;
  
  protected ServerButton(JavaFXApplication paramJavaFXApplication, ClientProfile paramClientProfile) {
    super(getServerButtonFxml(paramJavaFXApplication, paramClientProfile), paramJavaFXApplication);
    this.profile = paramClientProfile;
  }
  
  public static ServerButton createServerButton(JavaFXApplication paramJavaFXApplication, ClientProfile paramClientProfile) {
    return new ServerButton(paramJavaFXApplication, paramClientProfile);
  }
  
  private static String getServerButtonFxml(JavaFXApplication paramJavaFXApplication, ClientProfile paramClientProfile) {
    String str = String.format("components/serverButton/%s.fxml", new Object[] { paramClientProfile.getUUID().toString() });
    URL uRL = paramJavaFXApplication.tryResource(str);
    return (uRL != null) ? str : "components/serverButton.fxml";
  }
  
  public String getName() {
    return "serverButton";
  }
  
  protected void doInit() {
    ((Labeled)LookupHelper.lookup((Node)this.layout, new String[] { "#nameServer" })).setText(this.profile.getTitle());
    ((Labeled)LookupHelper.lookup((Node)this.layout, new String[] { "#genreServer" })).setText(this.profile.getVersion().toString());
    this.serverLogo = (Region)LookupHelper.lookup((Node)this.layout, new String[] { "#serverLogo" });
    URL uRL = this.application.tryResource(String.format("images/servers/%s.png", new Object[] { this.profile.getUUID().toString() }));
    if (uRL == null)
      uRL = this.application.tryResource("images/servers/example.png"); 
    if (uRL != null) {
      this.serverLogo.setBackground(new Background(new BackgroundImage[] { new BackgroundImage(new Image(uRL.toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(0.0D, 0.0D, true, true, false, true)) }));
      JavaFxUtils.setRadius(this.serverLogo, 20.0D);
    } 
    AtomicLong atomicLong1 = new AtomicLong(0L);
    AtomicLong atomicLong2 = new AtomicLong(0L);
    Runnable runnable = () -> this.contextHelper.runInFxThread(());
    for (ClientProfile.ServerProfile serverProfile : this.profile.getServers()) {
      this.application.pingService.getPingReport(serverProfile.name).thenAccept(paramPingServerReport -> {
            if (paramPingServerReport != null) {
              paramAtomicLong1.addAndGet(paramPingServerReport.playersOnline);
              paramAtomicLong2.addAndGet(paramPingServerReport.maxPlayers);
            } 
            paramRunnable.run();
          });
    } 
    this.saveButton = (Button)LookupHelper.lookup((Node)this.layout, new String[] { "#save" });
    this.resetButton = (Button)LookupHelper.lookup((Node)this.layout, new String[] { "#reset" });
  }
  
  protected void doPostInit() {}
  
  public void setOnMouseClicked(EventHandler<? super MouseEvent> paramEventHandler) {
    this.layout.setOnMouseClicked(paramEventHandler);
  }
  
  public void enableSaveButton(String paramString, EventHandler<ActionEvent> paramEventHandler) {
    this.saveButton.setVisible(true);
    if (paramString != null)
      this.saveButton.setText(paramString); 
    this.saveButton.setOnAction(paramEventHandler);
  }
  
  public void enableResetButton(String paramString, EventHandler<ActionEvent> paramEventHandler) {
    this.resetButton.setVisible(true);
    if (paramString != null)
      this.resetButton.setText(paramString); 
    this.resetButton.setOnAction(paramEventHandler);
  }
  
  public void addTo(Pane paramPane) {
    if (!isInit())
      try {
        init();
      } catch (Exception exception) {
        LogHelper.error(exception);
      }  
    paramPane.getChildren().add(this.layout);
  }
  
  public void addTo(Pane paramPane, int paramInt) {
    if (!isInit())
      try {
        init();
      } catch (Exception exception) {
        LogHelper.error(exception);
      }  
    paramPane.getChildren().add(paramInt, this.layout);
  }
  
  public void reset() {}
  
  public void disable() {}
  
  public void enable() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\components\ServerButton.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */