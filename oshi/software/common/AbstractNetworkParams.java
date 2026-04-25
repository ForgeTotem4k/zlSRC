package oshi.software.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.NetworkParams;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractNetworkParams implements NetworkParams {
  private static final String NAMESERVER = "nameserver";
  
  public String getDomainName() {
    InetAddress inetAddress;
    try {
      inetAddress = InetAddress.getLocalHost();
    } catch (UnknownHostException unknownHostException) {
      inetAddress = InetAddress.getLoopbackAddress();
    } 
    return inetAddress.getCanonicalHostName();
  }
  
  public String getHostName() {
    InetAddress inetAddress;
    try {
      inetAddress = InetAddress.getLocalHost();
    } catch (UnknownHostException unknownHostException) {
      inetAddress = InetAddress.getLoopbackAddress();
    } 
    String str = inetAddress.getHostName();
    int i = str.indexOf('.');
    return (i == -1) ? str : str.substring(0, i);
  }
  
  public String[] getDnsServers() {
    List<String> list = FileUtil.readFile("/etc/resolv.conf");
    String str = "nameserver";
    byte b1 = 3;
    ArrayList<String> arrayList = new ArrayList();
    for (byte b2 = 0; b2 < list.size() && arrayList.size() < b1; b2++) {
      String str1 = list.get(b2);
      if (str1.startsWith(str)) {
        String str2 = str1.substring(str.length()).replaceFirst("^[ \t]+", "");
        if (str2.length() != 0 && str2.charAt(0) != '#' && str2.charAt(0) != ';') {
          String str3 = str2.split("[ \t#;]", 2)[0];
          arrayList.add(str3);
        } 
      } 
    } 
    return arrayList.<String>toArray(new String[0]);
  }
  
  protected static String searchGateway(List<String> paramList) {
    for (String str1 : paramList) {
      String str2 = str1.replaceFirst("^\\s+", "");
      if (str2.startsWith("gateway:")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str2);
        return (arrayOfString.length < 2) ? "" : arrayOfString[1].split("%")[0];
      } 
    } 
    return "";
  }
  
  public String toString() {
    return String.format(Locale.ROOT, "Host name: %s, Domain name: %s, DNS servers: %s, IPv4 Gateway: %s, IPv6 Gateway: %s", new Object[] { getHostName(), getDomainName(), Arrays.toString((Object[])getDnsServers()), getIpv4DefaultGateway(), getIpv6DefaultGateway() });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractNetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */