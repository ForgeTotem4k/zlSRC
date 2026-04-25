package oshi.software.os.unix.solaris;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class SolarisInternetProtocolStats extends AbstractInternetProtocolStats {
  public InternetProtocolStats.TcpStats getTCPv4Stats() {
    return getTcpStats();
  }
  
  public InternetProtocolStats.UdpStats getUDPv4Stats() {
    return getUdpStats();
  }
  
  private static InternetProtocolStats.TcpStats getTcpStats() {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    long l5 = 0L;
    long l6 = 0L;
    long l7 = 0L;
    long l8 = 0L;
    long l9 = 0L;
    long l10 = 0L;
    List list = ExecutingCommand.runNative("netstat -s -P tcp");
    list.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
    for (String str : list) {
      String[] arrayOfString = splitOnPrefix(str, "tcp");
      for (String str1 : arrayOfString) {
        if (str1 != null) {
          String[] arrayOfString1 = str1.split("=");
          if (arrayOfString1.length == 2)
            switch (arrayOfString1[0].trim()) {
              case "tcpCurrEstab":
                l1 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpActiveOpens":
                l2 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpPassiveOpens":
                l3 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpAttemptFails":
                l4 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpEstabResets":
                l5 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpOutSegs":
                l6 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpInSegs":
                l7 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpRetransSegs":
                l8 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "tcpInErr":
                l9 = ParseUtil.getFirstIntValue(arrayOfString1[1].trim());
                break;
              case "tcpOutRsts":
                l10 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
            }  
        } 
      } 
    } 
    return new InternetProtocolStats.TcpStats(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10);
  }
  
  private static InternetProtocolStats.UdpStats getUdpStats() {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    List list = ExecutingCommand.runNative("netstat -s -P udp");
    list.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
    for (String str : list) {
      String[] arrayOfString = splitOnPrefix(str, "udp");
      for (String str1 : arrayOfString) {
        if (str1 != null) {
          String[] arrayOfString1 = str1.split("=");
          if (arrayOfString1.length == 2)
            switch (arrayOfString1[0].trim()) {
              case "udpOutDatagrams":
                l1 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "udpInDatagrams":
                l2 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "udpNoPorts":
                l3 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
              case "udpInErrors":
                l4 = ParseUtil.parseLongOrDefault(arrayOfString1[1].trim(), 0L);
                break;
            }  
        } 
      } 
    } 
    return new InternetProtocolStats.UdpStats(l1, l2, l3, l4);
  }
  
  private static String[] splitOnPrefix(String paramString1, String paramString2) {
    String[] arrayOfString = new String[2];
    int i = paramString1.indexOf(paramString2);
    if (i >= 0) {
      int j = paramString1.indexOf(paramString2, i + 1);
      if (j >= 0) {
        arrayOfString[0] = paramString1.substring(i, j).trim();
        arrayOfString[1] = paramString1.substring(j).trim();
      } else {
        arrayOfString[0] = paramString1.substring(i).trim();
      } 
    } 
    return arrayOfString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */