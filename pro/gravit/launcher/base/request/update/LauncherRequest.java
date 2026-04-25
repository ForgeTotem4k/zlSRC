package pro.gravit.launcher.base.request.update;

import java.io.IOException;
import java.nio.file.Path;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.LauncherRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public final class LauncherRequest extends Request<LauncherRequestEvent> implements WebSocketRequest {
  public static final Path BINARY_PATH = IOHelper.getCodeSource(Launcher.class);
  
  public static final boolean EXE_BINARY = IOHelper.hasExtension(BINARY_PATH, "exe");
  
  @LauncherNetworkAPI
  public final String secureHash;
  
  @LauncherNetworkAPI
  public final String secureSalt;
  
  @LauncherNetworkAPI
  public byte[] digest;
  
  @LauncherNetworkAPI
  public int launcher_type = EXE_BINARY ? 2 : 1;
  
  public LauncherRequest() {
    Path path = IOHelper.getCodeSource(LauncherRequest.class);
    try {
      this.digest = SecurityHelper.digest(SecurityHelper.DigestAlgorithm.SHA512, path);
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
    this.secureHash = (Launcher.getConfig()).secureCheckHash;
    this.secureSalt = (Launcher.getConfig()).secureCheckSalt;
  }
  
  public LauncherRequestEvent requestDo(RequestService paramRequestService) throws Exception {
    return (LauncherRequestEvent)request(paramRequestService);
  }
  
  public String getType() {
    return "launcher";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\update\LauncherRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */