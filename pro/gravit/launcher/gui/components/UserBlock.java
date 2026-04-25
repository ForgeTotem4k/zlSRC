package pro.gravit.launcher.gui.components;

import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.base.events.request.AssetUploadInfoRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.cabinet.AssetUploadInfoRequest;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.utils.JavaFxUtils;
import pro.gravit.utils.helper.LogHelper;

public class UserBlock {
  private final JavaFXApplication application;
  
  private final Pane layout;
  
  private final AbstractScene.SceneAccessor sceneAccessor;
  
  private final ImageView avatar;
  
  private final Image originalAvatarImage;
  
  public UserBlock(Pane paramPane, AbstractScene.SceneAccessor paramSceneAccessor) {
    this.application = paramSceneAccessor.getApplication();
    this.layout = paramPane;
    this.sceneAccessor = paramSceneAccessor;
    this.avatar = (ImageView)LookupHelper.lookup((Node)paramPane, new String[] { "#avatar" });
    this.originalAvatarImage = this.avatar.getImage();
    LookupHelper.lookupIfPossible((Node)paramPane, new String[] { "#avatar" }).ifPresent(paramImageView -> {
          try {
            JavaFxUtils.setStaticRadius(paramImageView, 8.0D);
            paramImageView.setImage(this.originalAvatarImage);
          } catch (Throwable throwable) {
            LogHelper.warning("Skin head error");
          } 
        });
    reset();
  }
  
  public void reset() {
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#nickname" }).ifPresent(paramLabel -> paramLabel.setText(this.application.authService.getUsername()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#role" }).ifPresent(paramLabel -> paramLabel.setText(this.application.authService.getMainRole()));
    this.avatar.setImage(this.originalAvatarImage);
    resetAvatar();
    if (this.application.authService.isFeatureAvailable("assetupload"))
      LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#customization" }).ifPresent(paramButton -> {
            paramButton.setVisible(true);
            paramButton.setOnAction(());
          }); 
  }
  
  public void resetAvatar() {
    if (this.avatar == null)
      return; 
    JavaFxUtils.putAvatarToImageView(this.application, this.application.authService.getUsername(), this.avatar);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\components\UserBlock.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */