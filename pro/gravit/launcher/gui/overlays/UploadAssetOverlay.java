package pro.gravit.launcher.gui.overlays;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.AssetUploadInfoRequestEvent;
import pro.gravit.launcher.base.events.request.GetAssetUploadUrlRequestEvent;
import pro.gravit.launcher.base.profiles.Texture;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.cabinet.GetAssetUploadUrl;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public class UploadAssetOverlay extends CenterOverlay {
  private static final HttpClient client = HttpClient.newBuilder().build();
  
  private Button uploadSkin;
  
  private Button uploadCape;
  
  private CheckBox useSlim;
  
  private AssetUploadInfoRequestEvent.SlimSupportConf slimSupportConf;
  
  public UploadAssetOverlay(JavaFXApplication paramJavaFXApplication) {
    super("overlay/uploadasset/uploadasset.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "uploadasset";
  }
  
  protected void doInit() {
    this.uploadSkin = (Button)LookupHelper.lookup((Node)this.layout, new String[] { "#uploadskin" });
    this.uploadCape = (Button)LookupHelper.lookup((Node)this.layout, new String[] { "#uploadcape" });
    this.useSlim = (CheckBox)LookupHelper.lookup((Node)this.layout, new String[] { "#useslim" });
    this.uploadSkin.setOnAction(paramActionEvent -> {
          switch (this.slimSupportConf) {
            default:
              throw new IncompatibleClassChangeError();
            case USER:
            
            case UNSUPPORTED:
            case SERVER:
              break;
          } 
          uploadAsset("SKIN", (AssetOptions)null);
        });
    this.uploadCape.setOnAction(paramActionEvent -> uploadAsset("CAPE", (AssetOptions)null));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#close" }).ifPresent(paramButton -> paramButton.setOnAction(()));
  }
  
  public void onAssetUploadInfo(AssetUploadInfoRequestEvent paramAssetUploadInfoRequestEvent) {
    boolean bool1 = paramAssetUploadInfoRequestEvent.available.contains("SKIN");
    boolean bool2 = paramAssetUploadInfoRequestEvent.available.contains("CAPE");
    this.uploadSkin.setVisible(bool1);
    this.uploadCape.setVisible(bool2);
    this.slimSupportConf = paramAssetUploadInfoRequestEvent.slimSupportConf;
    if (bool1)
      switch (paramAssetUploadInfoRequestEvent.slimSupportConf) {
        case USER:
          this.useSlim.setVisible(true);
          break;
        case UNSUPPORTED:
        case SERVER:
          this.useSlim.setVisible(false);
          break;
      }  
  }
  
  public void uploadAsset(String paramString, AssetOptions paramAssetOptions) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", new String[] { "*.png" }));
    File file = fileChooser.showOpenDialog((Window)this.currentStage.getStage());
    if (file == null)
      return; 
    this.application.gui.processingOverlay.processRequest(this.currentStage, this.application.getTranslation("runtime.overlay.processing.text.uploadasset"), (Request<WebSocketEvent>)new GetAssetUploadUrl(paramString), paramGetAssetUploadUrlRequestEvent -> {
          byte[] arrayOfByte1;
          byte[] arrayOfByte2;
          String str1 = (paramGetAssetUploadUrlRequestEvent.token == null) ? Request.getAccessToken() : paramGetAssetUploadUrlRequestEvent.token.accessToken;
          String str2 = SecurityHelper.toHex(SecurityHelper.randomBytes(32));
          String str3 = (paramAssetOptions != null) ? Launcher.gsonManager.gson.toJson(paramAssetOptions) : "{}";
          try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(256);
            try {
              byteArrayOutputStream.write("--".getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write(str2.getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write("\r\nContent-Disposition: form-data; name=\"options\"\r\nContent-Type: application/json\r\n\r\n".getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write(str3.getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write("\r\n--".getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write(str2.getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write("\r\nContent-Disposition: form-data; name=\"file\"; filename=\"file\"\r\nContent-Type: image/png\r\n\r\n".getBytes(StandardCharsets.UTF_8));
              arrayOfByte1 = byteArrayOutputStream.toByteArray();
              byteArrayOutputStream.close();
            } catch (Throwable throwable) {
              try {
                byteArrayOutputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (IOException iOException) {
            errorHandle(iOException);
            return;
          } 
          try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
            try {
              byteArrayOutputStream.write("\r\n--".getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write(str2.getBytes(StandardCharsets.UTF_8));
              byteArrayOutputStream.write("--\r\n".getBytes(StandardCharsets.UTF_8));
              arrayOfByte2 = byteArrayOutputStream.toByteArray();
              byteArrayOutputStream.close();
            } catch (Throwable throwable) {
              try {
                byteArrayOutputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (IOException iOException) {
            errorHandle(iOException);
            return;
          } 
          LogHelper.dev("%s<DATA>%s", new Object[] { new String(arrayOfByte1), new String(arrayOfByte2) });
          try {
            client.<byte[]>sendAsync(HttpRequest.newBuilder().uri(URI.create(paramGetAssetUploadUrlRequestEvent.url)).POST(HttpRequest.BodyPublishers.concat(new HttpRequest.BodyPublisher[] { HttpRequest.BodyPublishers.ofByteArray(arrayOfByte1), HttpRequest.BodyPublishers.ofFile(paramFile.toPath()), HttpRequest.BodyPublishers.ofByteArray(arrayOfByte2) }, )).header("Authorization", "Bearer " + str1).header("Content-Type", "multipart/form-data; boundary=\"" + str2 + "\"").header("Accept", "application/json").build(), (HttpResponse.BodyHandler)HttpResponse.BodyHandlers.ofByteArray()).thenAccept(()).exceptionally(());
          } catch (Throwable throwable) {
            errorHandle(throwable);
          } 
        }this::errorHandle, paramActionEvent -> {
        
        });
  }
  
  public void reset() {}
  
  public static final class AssetOptions {
    @LauncherNetworkAPI
    private final boolean modelSlim;
    
    public AssetOptions(boolean param1Boolean) {
      this.modelSlim = param1Boolean;
    }
    
    public boolean modelSlim() {
      return this.modelSlim;
    }
  }
  
  public static final class UserTexture extends Record {
    @LauncherNetworkAPI
    private final String url;
    
    @LauncherNetworkAPI
    private final String digest;
    
    @LauncherNetworkAPI
    private final Map<String, String> metadata;
    
    public UserTexture(String param1String1, String param1String2, Map<String, String> param1Map) {
      this.url = param1String1;
      this.digest = param1String2;
      this.metadata = param1Map;
    }
    
    Texture toLauncherTexture() {
      return new Texture(this.url, SecurityHelper.fromHex(this.digest), this.metadata);
    }
    
    public final String toString() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;)Ljava/lang/String;
      //   6: areturn
    }
    
    public final int hashCode() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;)I
      //   6: ireturn
    }
    
    public final boolean equals(Object param1Object) {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;Ljava/lang/Object;)Z
      //   7: ireturn
    }
    
    @LauncherNetworkAPI
    public String url() {
      return this.url;
    }
    
    @LauncherNetworkAPI
    public String digest() {
      return this.digest;
    }
    
    @LauncherNetworkAPI
    public Map<String, String> metadata() {
      return this.metadata;
    }
  }
  
  public static final class UploadError extends Record {
    @LauncherNetworkAPI
    private final String error;
    
    public UploadError(String param1String) {
      this.error = param1String;
    }
    
    public final String toString() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;)Ljava/lang/String;
      //   6: areturn
    }
    
    public final int hashCode() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;)I
      //   6: ireturn
    }
    
    public final boolean equals(Object param1Object) {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;Ljava/lang/Object;)Z
      //   7: ireturn
    }
    
    @LauncherNetworkAPI
    public String error() {
      return this.error;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\UploadAssetOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */