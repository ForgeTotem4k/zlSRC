package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.mac.SystemB;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class MacNetworkParams extends AbstractNetworkParams {
  private static final Logger LOG = LoggerFactory.getLogger(MacNetworkParams.class);
  
  private static final SystemB SYS = SystemB.INSTANCE;
  
  private static final String IPV6_ROUTE_HEADER = "Internet6:";
  
  private static final String DEFAULT_GATEWAY = "default";
  
  public String getDomainName() {
    String str = "";
    try {
      str = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException unknownHostException) {
      LOG.error("Unknown host exception when getting address of local host: {}", unknownHostException.getMessage());
      return "";
    } 
    CLibrary.Addrinfo addrinfo = new CLibrary.Addrinfo();
    try {
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        addrinfo.ai_flags = 2;
        int i = SYS.getaddrinfo(str, null, addrinfo, (PointerByReference)closeablePointerByReference);
        if (i > 0) {
          if (LOG.isErrorEnabled())
            LOG.error("Failed getaddrinfo(): {}", SYS.gai_strerror(i)); 
          String str3 = "";
          closeablePointerByReference.close();
          addrinfo.close();
          return str3;
        } 
        CLibrary.Addrinfo addrinfo1 = new CLibrary.Addrinfo(closeablePointerByReference.getValue());
        String str1 = addrinfo1.ai_canonname.trim();
        SYS.freeaddrinfo(closeablePointerByReference.getValue());
        String str2 = str1;
        closeablePointerByReference.close();
        addrinfo.close();
        return str2;
      } catch (Throwable throwable) {
        try {
          closeablePointerByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        addrinfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public String getHostName() {
    byte[] arrayOfByte = new byte[256];
    return (0 != SYS.gethostname(arrayOfByte, arrayOfByte.length)) ? super.getHostName() : Native.toString(arrayOfByte);
  }
  
  public String getIpv4DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route -n get default"));
  }
  
  public String getIpv6DefaultGateway() {
    List list = ExecutingCommand.runNative("netstat -nr");
    boolean bool = false;
    for (String str : list) {
      if (bool && str.startsWith("default")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str);
        if (arrayOfString.length > 2 && arrayOfString[2].contains("G"))
          return arrayOfString[1].split("%")[0]; 
        continue;
      } 
      if (str.startsWith("Internet6:"))
        bool = true; 
    } 
    return "";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */