package pro.gravit.launcher.gui.overlays;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pro.gravit.launcher.gui.JavaFXApplication;

public abstract class CenterOverlay extends AbstractOverlay {
  private volatile Pane overrideFxmlRoot;
  
  public CenterOverlay(String paramString, JavaFXApplication paramJavaFXApplication) {
    super(paramString, paramJavaFXApplication);
  }
  
  protected synchronized Parent getFxmlRoot() {
    if (this.overrideFxmlRoot == null) {
      Parent parent = super.getFxmlRoot();
      HBox hBox = new HBox();
      hBox.getChildren().add(parent);
      hBox.setAlignment(Pos.CENTER);
      VBox vBox = new VBox();
      vBox.setAlignment(Pos.CENTER);
      vBox.getChildren().add(hBox);
      this.overrideFxmlRoot = (Pane)vBox;
    } 
    return (Parent)this.overrideFxmlRoot;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\CenterOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */