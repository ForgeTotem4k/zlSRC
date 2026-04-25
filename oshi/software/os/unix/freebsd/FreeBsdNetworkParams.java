package oshi.software.os.unix.freebsd;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.CLibrary;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
final class FreeBsdNetworkParams extends AbstractNetworkParams {
  private static final Logger LOG = LoggerFactory.getLogger(FreeBsdNetworkParams.class);
  
  private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;
  
  public String getDomainName() {
    CLibrary.Addrinfo addrinfo = new CLibrary.Addrinfo();
    try {
      addrinfo.ai_flags = 2;
      String str = getHostName();
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        int i = LIBC.getaddrinfo(str, null, addrinfo, (PointerByReference)closeablePointerByReference);
        if (i > 0) {
          if (LOG.isErrorEnabled())
            LOG.warn("Failed getaddrinfo(): {}", LIBC.gai_strerror(i)); 
          String str3 = "";
          closeablePointerByReference.close();
          addrinfo.close();
          return str3;
        } 
        CLibrary.Addrinfo addrinfo1 = new CLibrary.Addrinfo(closeablePointerByReference.getValue());
        String str1 = addrinfo1.ai_canonname.trim();
        LIBC.freeaddrinfo(closeablePointerByReference.getValue());
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
    return (0 != LIBC.gethostname(arrayOfByte, arrayOfByte.length)) ? super.getHostName() : Native.toString(arrayOfByte);
  }
  
  public String getIpv4DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route -4 get default"));
  }
  
  public String getIpv6DefaultGateway() {
    return searchGateway(ExecutingCommand.runNative("route -6 get default"));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */