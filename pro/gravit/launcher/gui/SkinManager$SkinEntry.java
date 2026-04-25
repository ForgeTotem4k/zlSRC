package pro.gravit.launcher.gui;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.Optional;
import javafx.scene.image.Image;

class SkinEntry {
  final URI url;
  
  final URI avatarUrl;
  
  SoftReference<Optional<BufferedImage>> imageRef = new SoftReference<>(null);
  
  SoftReference<Optional<BufferedImage>> avatarRef = new SoftReference<>(null);
  
  SoftReference<Optional<Image>> fxImageRef = new SoftReference<>(null);
  
  SoftReference<Optional<Image>> fxAvatarRef = new SoftReference<>(null);
  
  private SkinEntry(URI paramURI) {
    this.url = paramURI;
    this.avatarUrl = null;
  }
  
  public SkinEntry(URI paramURI1, URI paramURI2) {
    this.url = paramURI1;
    this.avatarUrl = paramURI2;
  }
  
  synchronized BufferedImage getFullImage() {
    Optional<BufferedImage> optional = this.imageRef.get();
    if (optional == null) {
      optional = Optional.ofNullable(SkinManager.downloadSkin(this.url));
      this.imageRef = new SoftReference<>(optional);
    } 
    return optional.orElse(null);
  }
  
  synchronized Image getFullFxImage() {
    Optional<Image> optional = this.fxImageRef.get();
    if (optional == null) {
      BufferedImage bufferedImage = getFullImage();
      if (bufferedImage == null)
        return null; 
      optional = Optional.ofNullable(SkinManager.convertToFxImage(bufferedImage));
      this.fxImageRef = new SoftReference<>(optional);
    } 
    return optional.orElse(null);
  }
  
  synchronized BufferedImage getHeadImage() {
    Optional<BufferedImage> optional = this.avatarRef.get();
    if (optional == null) {
      if (this.avatarUrl != null) {
        optional = Optional.ofNullable(SkinManager.downloadSkin(this.avatarUrl));
      } else {
        BufferedImage bufferedImage = getFullImage();
        if (bufferedImage == null)
          return null; 
        optional = Optional.of(SkinManager.sumBufferedImage(SkinManager.getHeadFromSkinImage(bufferedImage), SkinManager.getHeadLayerFromSkinImage(bufferedImage)));
      } 
      this.avatarRef = new SoftReference<>(optional);
    } 
    return optional.orElse(null);
  }
  
  synchronized Image getHeadFxImage() {
    Optional<Image> optional = this.fxAvatarRef.get();
    if (optional == null) {
      BufferedImage bufferedImage = getHeadImage();
      if (bufferedImage == null)
        return null; 
      optional = Optional.ofNullable(SkinManager.convertToFxImage(bufferedImage));
      this.fxAvatarRef = new SoftReference<>(optional);
    } 
    return optional.orElse(null);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\SkinManager$SkinEntry.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */