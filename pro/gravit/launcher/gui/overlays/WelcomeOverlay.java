package pro.gravit.launcher.gui.overlays;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.utils.JavaFxUtils;
import pro.gravit.utils.helper.LogHelper;

public class WelcomeOverlay extends AbstractOverlay {
  private Image originalImage;
  
  public WelcomeOverlay(JavaFXApplication paramJavaFXApplication) {
    super("overlay/welcome/welcome.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "welcome";
  }
  
  protected void doInit() {
    reset();
  }
  
  public void reset() {
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#playerName" }).ifPresent(paramLabel -> paramLabel.setText(this.application.authService.getUsername()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#playerHead" }).ifPresent(paramImageView -> {
          try {
            JavaFxUtils.setStaticRadius(paramImageView, 8.0D);
            Image image = this.application.skinManager.getScaledFxSkinHead(this.application.authService.getUsername(), (int)paramImageView.getFitWidth(), (int)paramImageView.getFitHeight());
            if (image != null) {
              if (this.originalImage == null)
                this.originalImage = paramImageView.getImage(); 
              paramImageView.setImage(image);
            } else if (this.originalImage != null) {
              paramImageView.setImage(this.originalImage);
            } 
          } catch (Throwable throwable) {
            LogHelper.warning("Skin head error");
          } 
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\WelcomeOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */