package pro.gravit.launcher.gui.scenes.update;

import java.nio.file.Path;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.core.hasher.FileNameMatcher;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.utils.helper.LogHelper;

public class UpdateScene extends AbstractScene {
  private ProgressBar progressBar;
  
  private Label speed;
  
  private Label volume;
  
  private TextArea logOutput;
  
  private Button cancel;
  
  private Label speedtext;
  
  private Label speederr;
  
  private Pane speedon;
  
  private VisualDownloader downloader;
  
  private volatile DownloadStatus downloadStatus = DownloadStatus.COMPLETE;
  
  public UpdateScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/update/update.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.progressBar = (ProgressBar)LookupHelper.lookup((Node)this.layout, new String[] { "#progress" });
    this.speed = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#speed" });
    this.speederr = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#speedErr" });
    this.speedon = (Pane)LookupHelper.lookup((Node)this.layout, new String[] { "#speedOn" });
    this.speedtext = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#speed-text" });
    this.volume = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#volume" });
    this.logOutput = (TextArea)LookupHelper.lookup((Node)this.layout, new String[] { "#outputUpdate" });
    this.downloader = new VisualDownloader(this.application, this.progressBar, this.speed, this.volume, this::errorHandle, paramString -> this.contextHelper.runInFxThread(()), this::onUpdateStatus);
  }
  
  private void onUpdateStatus(DownloadStatus paramDownloadStatus) {
    this.downloadStatus = paramDownloadStatus;
    LogHelper.debug("Update download status: %s", new Object[] { paramDownloadStatus.toString() });
  }
  
  public void sendUpdateAssetRequest(String paramString1, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, String paramString2, boolean paramBoolean2, Consumer<HashedDir> paramConsumer) {
    this.downloader.sendUpdateAssetRequest(paramString1, paramPath, paramFileNameMatcher, paramBoolean1, paramString2, paramBoolean2, paramConsumer);
  }
  
  public void sendUpdateRequest(String paramString, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, OptionalView paramOptionalView, boolean paramBoolean2, boolean paramBoolean3, Consumer<HashedDir> paramConsumer) {
    this.downloader.sendUpdateRequest(paramString, paramPath, paramFileNameMatcher, paramBoolean1, paramOptionalView, paramBoolean2, paramBoolean3, paramConsumer);
  }
  
  public void addLog(String paramString) {
    LogHelper.dev("Update event %s", new Object[] { paramString });
    this.logOutput.appendText(paramString.concat("\n"));
  }
  
  public void reset() {
    this.progressBar.progressProperty().setValue(Integer.valueOf(0));
    this.logOutput.setText("");
    this.volume.setText("");
    this.speed.setText("0");
    this.progressBar.getStyleClass().removeAll((Object[])new String[] { "progress" });
    this.speederr.setVisible(false);
    this.speedon.setVisible(true);
  }
  
  public void errorHandle(Throwable paramThrowable) {
    if (paramThrowable instanceof java.util.concurrent.CompletionException)
      paramThrowable = paramThrowable.getCause(); 
    addLog("Exception %s: %s".formatted(new Object[] { paramThrowable.getClass(), (paramThrowable.getMessage() == null) ? "" : paramThrowable.getMessage() }));
    this.progressBar.getStyleClass().add("progressError");
    this.speederr.setVisible(true);
    this.speedon.setVisible(false);
    LogHelper.error(paramThrowable);
  }
  
  public boolean isDisableReturnBack() {
    return true;
  }
  
  public String getName() {
    return "update";
  }
  
  public enum DownloadStatus {
    ERROR, HASHING, REQUEST, DOWNLOAD, COMPLETE, DELETE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scene\\update\UpdateScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */