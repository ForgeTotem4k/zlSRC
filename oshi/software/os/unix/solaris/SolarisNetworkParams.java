package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
final class SolarisNetworkParams extends AbstractNetworkParams {
  private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
  
  public String getHostName() {
    byte[] arrayOfByte = new byte[256];
    return (0 != LIBC.gethostname(arrayOfByte, arrayOfByte.length)) ? super.getHostName() : Native.toString(arrayOfByte);
  }
  
  public String getIpv4DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route get -inet default"));
  }
  
  public String getIpv6DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route get -inet6 default"));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */