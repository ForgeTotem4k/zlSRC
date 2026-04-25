package pro.gravit.launcher.runtime.console;

import java.util.Base64;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class GetPublicKeyCommand extends Command {
  private final LauncherEngine engine;
  
  public GetPublicKeyCommand(LauncherEngine paramLauncherEngine) {
    this.engine = paramLauncherEngine;
  }
  
  public String getArgsDescription() {
    return "[]";
  }
  
  public String getUsageDescription() {
    return "print public key in base64 format";
  }
  
  public void invoke(String... paramVarArgs) {
    LogHelper.info("PublicKey: %s", new Object[] { Base64.getEncoder().encodeToString(this.engine.getClientPublicKey().getEncoded()) });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\GetPublicKeyCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */