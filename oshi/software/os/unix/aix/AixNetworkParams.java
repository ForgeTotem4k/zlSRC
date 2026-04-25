package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.AixLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class AixNetworkParams extends AbstractNetworkParams {
  private static final AixLibc LIBC = AixLibc.INSTANCE;
  
  public String getHostName() {
    byte[] arrayOfByte = new byte[256];
    return (0 != LIBC.gethostname(arrayOfByte, arrayOfByte.length)) ? super.getHostName() : Native.toString(arrayOfByte);
  }
  
  public String getIpv4DefaultGateway() {
    return getDefaultGateway("netstat -rnf inet");
  }
  
  public String getIpv6DefaultGateway() {
    return getDefaultGateway("netstat -rnf inet6");
  }
  
  private static String getDefaultGateway(String paramString) {
    for (String str : ExecutingCommand.runNative(paramString)) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 7 && "default".equals(arrayOfString[0]))
        return arrayOfString[1]; 
    } 
    return "unknown";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */