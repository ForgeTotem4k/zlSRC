package pro.gravit.launcher.runtime.console;

import pro.gravit.launcher.base.events.request.GetConnectUUIDRequestEvent;
import pro.gravit.launcher.base.request.management.GetConnectUUIDRequest;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class GetConnectUUIDCommand extends Command {
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return "Get your connectUUID";
  }
  
  public void invoke(String... paramVarArgs) throws Exception {
    GetConnectUUIDRequestEvent getConnectUUIDRequestEvent = (GetConnectUUIDRequestEvent)(new GetConnectUUIDRequest()).request();
    LogHelper.info("Your connectUUID: %s | shardId %d", new Object[] { getConnectUUIDRequestEvent.connectUUID.toString(), Integer.valueOf(getConnectUUIDRequestEvent.shardId) });
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\GetConnectUUIDCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */