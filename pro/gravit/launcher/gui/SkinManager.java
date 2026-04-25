package pro.gravit.launcher.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javax.imageio.ImageIO;
import pro.gravit.launcher.base.Downloader;
import pro.gravit.utils.helper.LogHelper;

public class SkinManager {
  private static final HttpClient client = Downloader.newHttpClientBuilder().build();
  
  private final JavaFXApplication application;
  
  private final Map<String, SkinEntry> map = new ConcurrentHashMap<>();
  
  public SkinManager(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public void addSkin(String paramString, URI paramURI) {
    this.map.put(paramString, new SkinEntry(paramURI));
  }
  
  public void addOrReplaceSkin(String paramString, URI paramURI) {
    SkinEntry skinEntry = this.map.get(paramString);
    if (skinEntry == null) {
      this.map.put(paramString, new SkinEntry(paramURI));
    } else {
      this.map.put(paramString, new SkinEntry(paramURI, skinEntry.avatarUrl));
    } 
  }
  
  public void addSkinWithAvatar(String paramString, URI paramURI1, URI paramURI2) {
    this.map.put(paramString, new SkinEntry(paramURI1, paramURI2));
  }
  
  public BufferedImage getSkin(String paramString) {
    SkinEntry skinEntry = this.map.get(paramString);
    return (skinEntry == null) ? null : skinEntry.getFullImage();
  }
  
  public BufferedImage getSkinHead(String paramString) {
    SkinEntry skinEntry = this.map.get(paramString);
    return (skinEntry == null) ? null : skinEntry.getHeadImage();
  }
  
  public Image getFxSkin(String paramString) {
    SkinEntry skinEntry = this.map.get(paramString);
    return (skinEntry == null) ? null : skinEntry.getFullFxImage();
  }
  
  public Image getFxSkinHead(String paramString) {
    SkinEntry skinEntry = this.map.get(paramString);
    return (skinEntry == null) ? null : skinEntry.getHeadFxImage();
  }
  
  public BufferedImage getScaledSkin(String paramString, int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = getSkin(paramString);
    return scaleImage(bufferedImage, paramInt1, paramInt2);
  }
  
  public BufferedImage getScaledSkinHead(String paramString, int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = getSkinHead(paramString);
    return scaleImage(bufferedImage, paramInt1, paramInt2);
  }
  
  public Image getScaledFxSkin(String paramString, int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = getSkin(paramString);
    return convertToFxImage(scaleImage(bufferedImage, paramInt1, paramInt2));
  }
  
  public static BufferedImage sumBufferedImage(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    int i = Math.max(paramBufferedImage1.getWidth(), paramBufferedImage2.getWidth());
    int j = Math.max(paramBufferedImage1.getHeight(), paramBufferedImage2.getHeight());
    BufferedImage bufferedImage = new BufferedImage(i, j, 2);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    Color color = graphics2D.getColor();
    graphics2D.setPaint(Color.WHITE);
    graphics2D.fillRect(0, 0, i, j);
    graphics2D.setColor(color);
    graphics2D.drawImage(paramBufferedImage1, (BufferedImageOp)null, 0, 0);
    graphics2D.drawImage(paramBufferedImage2, (BufferedImageOp)null, 0, 0);
    graphics2D.dispose();
    return bufferedImage;
  }
  
  public Image getScaledFxSkinHead(String paramString, int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = getSkinHead(paramString);
    return (bufferedImage == null) ? null : convertToFxImage(scaleImage(bufferedImage, paramInt1, paramInt2));
  }
  
  private static BufferedImage scaleImage(BufferedImage paramBufferedImage, int paramInt1, int paramInt2) {
    if (paramBufferedImage == null)
      return null; 
    Image image = paramBufferedImage.getScaledInstance(paramInt1, paramInt2, 2);
    BufferedImage bufferedImage = new BufferedImage(paramInt1, paramInt2, 3);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(image, 0, 0, (ImageObserver)null);
    graphics2D.dispose();
    return bufferedImage;
  }
  
  private static BufferedImage downloadSkin(URI paramURI) {
    if (paramURI == null)
      return null; 
    try {
      HttpResponse<InputStream> httpResponse = client.send(HttpRequest.newBuilder().uri(paramURI).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36").timeout(Duration.of(10L, ChronoUnit.SECONDS)).build(), HttpResponse.BodyHandlers.ofInputStream());
      if (httpResponse.statusCode() >= 300 || httpResponse.statusCode() < 200) {
        LogHelper.error("Skin %s not found (error %d)", new Object[] { paramURI.toString(), Integer.valueOf(httpResponse.statusCode()) });
        return null;
      } 
      InputStream inputStream = httpResponse.body();
      try {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        if (inputStream != null)
          inputStream.close(); 
        return bufferedImage;
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException|InterruptedException iOException) {
      LogHelper.error(iOException);
      return null;
    } 
  }
  
  private static BufferedImage getHeadLayerFromSkinImage(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = i / 64;
    int k = 8 * j;
    int m = 40 * j;
    int n = 8 * j;
    LogHelper.debug("ShinHead debug: W: %d Scale: %d Offset: %d", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) });
    return paramBufferedImage.getSubimage(m, n, k, k);
  }
  
  private static BufferedImage getHeadFromSkinImage(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = i / 64;
    int k = 8 * j;
    LogHelper.debug("ShinHead debug: W: %d Scale: %d Offset: %d", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) });
    return paramBufferedImage.getSubimage(k, k, k, k);
  }
  
  private static Image convertToFxImage(BufferedImage paramBufferedImage) {
    return (paramBufferedImage == null) ? null : convertToFxImageJava8(paramBufferedImage);
  }
  
  private static Image convertToFxImageJava8(BufferedImage paramBufferedImage) {
    BufferedImage bufferedImage;
    Graphics2D graphics2D;
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    switch (paramBufferedImage.getType()) {
      case 2:
      case 3:
        break;
      default:
        bufferedImage = new BufferedImage(i, j, 3);
        graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(paramBufferedImage, 0, 0, (ImageObserver)null);
        graphics2D.dispose();
        paramBufferedImage = bufferedImage;
        break;
    } 
    WritableImage writableImage = new WritableImage(i, j);
    DataBufferInt dataBufferInt = (DataBufferInt)paramBufferedImage.getRaster().getDataBuffer();
    SampleModel sampleModel = paramBufferedImage.getRaster().getSampleModel();
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
    boolean bool = (sampleModel instanceof SinglePixelPackedSampleModel) ? singlePixelPackedSampleModel.getScanlineStride() : false;
    WritablePixelFormat writablePixelFormat = paramBufferedImage.isAlphaPremultiplied() ? PixelFormat.getIntArgbPreInstance() : PixelFormat.getIntArgbInstance();
    writableImage.getPixelWriter().setPixels(0, 0, i, j, (PixelFormat)writablePixelFormat, dataBufferInt.getData(), dataBufferInt.getOffset(), bool);
    return (Image)writableImage;
  }
  
  private static class SkinEntry {
    final URI url;
    
    final URI avatarUrl;
    
    SoftReference<Optional<BufferedImage>> imageRef = new SoftReference<>(null);
    
    SoftReference<Optional<BufferedImage>> avatarRef = new SoftReference<>(null);
    
    SoftReference<Optional<Image>> fxImageRef = new SoftReference<>(null);
    
    SoftReference<Optional<Image>> fxAvatarRef = new SoftReference<>(null);
    
    private SkinEntry(URI param1URI) {
      this.url = param1URI;
      this.avatarUrl = null;
    }
    
    public SkinEntry(URI param1URI1, URI param1URI2) {
      this.url = param1URI1;
      this.avatarUrl = param1URI2;
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\SkinManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */