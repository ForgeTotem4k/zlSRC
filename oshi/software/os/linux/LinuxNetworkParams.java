package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class LinuxNetworkParams extends AbstractNetworkParams {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxNetworkParams.class);
  
  private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
  
  private static final String IPV4_DEFAULT_DEST = "0.0.0.0";
  
  private static final String IPV6_DEFAULT_DEST = "::/0";
  
  public String getDomainName() {
    CLibrary.Addrinfo addrinfo = new CLibrary.Addrinfo();
    try {
      addrinfo.ai_flags = 2;
      String str = "";
      try {
        str = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException unknownHostException) {
        LOG.warn("Unknown host exception when getting address of local host: {}", unknownHostException.getMessage());
        String str1 = "";
        addrinfo.close();
        return str1;
      } 
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        int i = LIBC.getaddrinfo(str, null, addrinfo, (PointerByReference)closeablePointerByReference);
        if (i > 0) {
          if (LOG.isErrorEnabled())
            LOG.error("Failed getaddrinfo(): {}", LIBC.gai_strerror(i)); 
          String str1 = "";
          closeablePointerByReference.close();
          addrinfo.close();
          return str1;
        } 
        CLibrary.Addrinfo addrinfo1 = new CLibrary.Addrinfo(closeablePointerByReference.getValue());
        try {
          String str1 = (addrinfo1.ai_canonname == null) ? str : addrinfo1.ai_canonname.trim();
          addrinfo1.close();
          closeablePointerByReference.close();
          addrinfo.close();
          return str1;
        } catch (Throwable throwable) {
          try {
            addrinfo1.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
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
    return (0 != LibC.INSTANCE.gethostname(arrayOfByte, arrayOfByte.length)) ? super.getHostName() : Native.toString(arrayOfByte);
  }
  
  public String getIpv4DefaultGateway() {
    List<CharSequence> list = ExecutingCommand.runNative("route -A inet -n");
    if (list.size() <= 2)
      return ""; 
    String str = "";
    int i = Integer.MAX_VALUE;
    for (byte b = 2; b < list.size(); b++) {
      String[] arrayOfString = ParseUtil.whitespaces.split(list.get(b));
      if (arrayOfString.length > 4 && arrayOfString[0].equals("0.0.0.0")) {
        boolean bool = (arrayOfString[3].indexOf('G') != -1) ? true : false;
        int j = ParseUtil.parseIntOrDefault(arrayOfString[4], 2147483647);
        if (bool && j < i) {
          i = j;
          str = arrayOfString[1];
        } 
      } 
    } 
    return str;
  }
  
  public String getIpv6DefaultGateway() {
    List<CharSequence> list = ExecutingCommand.runNative("route -A inet6 -n");
    if (list.size() <= 2)
      return ""; 
    String str = "";
    int i = Integer.MAX_VALUE;
    for (byte b = 2; b < list.size(); b++) {
      String[] arrayOfString = ParseUtil.whitespaces.split(list.get(b));
      if (arrayOfString.length > 3 && arrayOfString[0].equals("::/0")) {
        boolean bool = (arrayOfString[2].indexOf('G') != -1) ? true : false;
        int j = ParseUtil.parseIntOrDefault(arrayOfString[3], 2147483647);
        if (bool && j < i) {
          i = j;
          str = arrayOfString[1];
        } 
      } 
    } 
    return str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */