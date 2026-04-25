package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class WindowsNetworkParams extends AbstractNetworkParams {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkParams.class);
  
  private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;
  
  public String getDomainName() {
    char[] arrayOfChar = new char[256];
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(arrayOfChar.length);
    try {
      if (!Kernel32.INSTANCE.GetComputerNameEx(3, arrayOfChar, (IntByReference)closeableIntByReference)) {
        LOG.error("Failed to get dns domain name. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
        String str = "";
        closeableIntByReference.close();
        return str;
      } 
      closeableIntByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableIntByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return Native.toString(arrayOfChar);
  }
  
  public String[] getDnsServers() {
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      int i = IPHlpAPI.INSTANCE.GetNetworkParams(null, (IntByReference)closeableIntByReference);
      if (i != 111) {
        LOG.error("Failed to get network parameters buffer size. Error code: {}", Integer.valueOf(i));
        String[] arrayOfString = new String[0];
        closeableIntByReference.close();
        return arrayOfString;
      } 
      Memory memory = new Memory(closeableIntByReference.getValue());
      try {
        i = IPHlpAPI.INSTANCE.GetNetworkParams((Pointer)memory, (IntByReference)closeableIntByReference);
        if (i != 0) {
          LOG.error("Failed to get network parameters. Error code: {}", Integer.valueOf(i));
          String[] arrayOfString1 = new String[0];
          memory.close();
          closeableIntByReference.close();
          return arrayOfString1;
        } 
        IPHlpAPI.FIXED_INFO fIXED_INFO = new IPHlpAPI.FIXED_INFO((Pointer)memory);
        ArrayList<String> arrayList = new ArrayList();
        IPHlpAPI.IP_ADDR_STRING iP_ADDR_STRING = fIXED_INFO.DnsServerList;
        while (iP_ADDR_STRING != null) {
          String str = Native.toString(iP_ADDR_STRING.IpAddress.String, StandardCharsets.US_ASCII);
          int j = str.indexOf(false);
          if (j != -1)
            str = str.substring(0, j); 
          arrayList.add(str);
          IPHlpAPI.IP_ADDR_STRING.ByReference byReference = iP_ADDR_STRING.Next;
        } 
        String[] arrayOfString = arrayList.<String>toArray(new String[0]);
        memory.close();
        closeableIntByReference.close();
        return arrayOfString;
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        closeableIntByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public String getHostName() {
    try {
      return Kernel32Util.getComputerName();
    } catch (Win32Exception win32Exception) {
      return super.getHostName();
    } 
  }
  
  public String getIpv4DefaultGateway() {
    return parseIpv4Route();
  }
  
  public String getIpv6DefaultGateway() {
    return parseIpv6Route();
  }
  
  private static String parseIpv4Route() {
    List list = ExecutingCommand.runNative("route print -4 0.0.0.0");
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
      if (arrayOfString.length > 2 && "0.0.0.0".equals(arrayOfString[0]))
        return arrayOfString[2]; 
    } 
    return "";
  }
  
  private static String parseIpv6Route() {
    List list = ExecutingCommand.runNative("route print -6 ::/0");
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
      if (arrayOfString.length > 3 && "::/0".equals(arrayOfString[2]))
        return arrayOfString[3]; 
    } 
    return "";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */