package pro.gravit.launcher.gui.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import pro.gravit.launcher.gui.JavaFXApplication;

public class JavaFxUtils {
  public static boolean putAvatarToImageView(JavaFXApplication paramJavaFXApplication, String paramString, ImageView paramImageView) {
    int i = (int)paramImageView.getFitWidth();
    int j = (int)paramImageView.getFitHeight();
    Image image = paramJavaFXApplication.skinManager.getScaledFxSkinHead(paramString, i, j);
    if (image == null)
      return false; 
    paramImageView.setImage(image);
    return true;
  }
  
  public static void setRadius(Region paramRegion, double paramDouble) {
    setRadius(paramRegion, paramDouble, paramDouble);
  }
  
  public static void setRadius(Region paramRegion, double paramDouble1, double paramDouble2) {
    Rectangle rectangle = new Rectangle(30.0D, 30.0D);
    rectangle.setArcWidth(paramDouble1);
    rectangle.setArcHeight(paramDouble2);
    paramRegion.setClip((Node)rectangle);
    paramRegion.widthProperty().addListener(paramObservable -> paramRectangle.setWidth(paramRegion.getWidth()));
    paramRegion.heightProperty().addListener(paramObservable -> paramRectangle.setHeight(paramRegion.getHeight()));
  }
  
  public static void setStaticRadius(ImageView paramImageView, double paramDouble) {
    setStaticRadius(paramImageView, paramDouble, paramDouble);
  }
  
  public static void setStaticRadius(ImageView paramImageView, double paramDouble1, double paramDouble2) {
    Rectangle rectangle = new Rectangle(paramImageView.getFitWidth(), paramImageView.getFitHeight());
    rectangle.setArcWidth(paramDouble1);
    rectangle.setArcHeight(paramDouble2);
    paramImageView.setClip((Node)rectangle);
  }
  
  public static URL getStyleUrl(String paramString) throws IOException {
    URL uRL;
    try {
      uRL = JavaFXApplication.getResourceURL(paramString + ".bss");
    } catch (FileNotFoundException|java.nio.file.NoSuchFileException fileNotFoundException) {
      uRL = JavaFXApplication.getResourceURL(paramString + ".css");
    } 
    return uRL;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\JavaFxUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */