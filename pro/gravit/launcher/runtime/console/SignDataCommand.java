package pro.gravit.launcher.runtime.console;

import java.util.Base64;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class SignDataCommand extends Command {
  private final LauncherEngine engine;
  
  public SignDataCommand(LauncherEngine paramLauncherEngine) {
    this.engine = paramLauncherEngine;
  }
  
  public String getArgsDescription() {
    return "[base64 data]";
  }
  
  public String getUsageDescription() {
    return "sign any data";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    verifyArgs(paramVarArgs, 1);
    byte[] arrayOfByte1 = Base64.getDecoder().decode(paramVarArgs[0]);
    byte[] arrayOfByte2 = this.engine.sign(arrayOfByte1);
    String str = Base64.getEncoder().encodeToString(arrayOfByte2);
    LogHelper.info("Signature: %s", new Object[] { str });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\SignDataCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */