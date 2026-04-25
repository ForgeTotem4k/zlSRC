package oshi.software.os.unix.openbsd;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
public class OpenBsdNetworkParams extends AbstractNetworkParams {
  public String getIpv4DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route -n get default"));
  }
  
  public String getIpv6DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route -n get default"));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\openbsd\OpenBsdNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */