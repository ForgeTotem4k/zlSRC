package oshi.driver.unix;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class NetStat {
  public static Pair<Long, Long> queryTcpnetstat() {
    long l1 = 0L;
    long l2 = 0L;
    List list = ExecutingCommand.runNative("netstat -n -p tcp");
    for (String str : list) {
      if (str.endsWith("ESTABLISHED")) {
        if (str.startsWith("tcp4")) {
          l1++;
          continue;
        } 
        if (str.startsWith("tcp6"))
          l2++; 
      } 
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  public static List<InternetProtocolStats.IPConnection> queryNetstat() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("netstat -n");
    for (String str : list) {
      String[] arrayOfString = null;
      if (str.startsWith("tcp") || str.startsWith("udp")) {
        arrayOfString = ParseUtil.whitespaces.split(str);
        if (arrayOfString.length >= 5) {
          String str1 = (arrayOfString.length == 6) ? arrayOfString[5] : null;
          if ("SYN_RCVD".equals(str1))
            str1 = "SYN_RECV"; 
          String str2 = arrayOfString[0];
          Pair<byte[], Integer> pair1 = parseIP(arrayOfString[3]);
          Pair<byte[], Integer> pair2 = parseIP(arrayOfString[4]);
          arrayList.add(new InternetProtocolStats.IPConnection(str2, (byte[])pair1.getA(), ((Integer)pair1.getB()).intValue(), (byte[])pair2.getA(), ((Integer)pair2.getB()).intValue(), (str1 == null) ? InternetProtocolStats.TcpState.NONE : InternetProtocolStats.TcpState.valueOf(str1), ParseUtil.parseIntOrDefault(arrayOfString[2], 0), ParseUtil.parseIntOrDefault(arrayOfString[1], 0), -1));
        } 
      } 
    } 
    return arrayList;
  }
  
  private static Pair<byte[], Integer> parseIP(String paramString) {
    int i = paramString.lastIndexOf('.');
    if (i > 0 && paramString.length() > i) {
      int j = ParseUtil.parseIntOrDefault(paramString.substring(i + 1), 0);
      String str = paramString.substring(0, i);
      try {
        return new Pair(InetAddress.getByName(str).getAddress(), Integer.valueOf(j));
      } catch (UnknownHostException unknownHostException) {
        try {
          if (str.endsWith(":") && str.contains("::")) {
            str = str + "0";
          } else if (str.endsWith(":") || str.contains("::")) {
            str = str + ":0";
          } else {
            str = str + "::0";
          } 
          return new Pair(InetAddress.getByName(str).getAddress(), Integer.valueOf(j));
        } catch (UnknownHostException unknownHostException1) {
          return new Pair(new byte[0], Integer.valueOf(j));
        } 
      } 
    } 
    return new Pair(new byte[0], Integer.valueOf(0));
  }
  
  public static InternetProtocolStats.TcpStats queryTcpStats(String paramString) {
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
    List list = ExecutingCommand.runNative(paramString);
    for (String str : list) {
      String[] arrayOfString = str.trim().split(" ", 2);
      if (arrayOfString.length == 2) {
        switch (arrayOfString[1]) {
          case "connections established":
          case "connection established (including accepts)":
          case "connections established (including accepts)":
            l1 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "active connection openings":
            l2 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "passive connection openings":
            l3 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "failed connection attempts":
          case "bad connection attempts":
            l4 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "connection resets received":
          case "dropped due to RST":
            l5 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "segments sent out":
          case "packet sent":
          case "packets sent":
            l6 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "segments received":
          case "packet received":
          case "packets received":
            l7 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "segments retransmitted":
            l8 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "bad segments received":
          case "discarded for bad checksum":
          case "discarded for bad checksums":
          case "discarded for bad header offset field":
          case "discarded for bad header offset fields":
          case "discarded because packet too short":
          case "discarded for missing IPsec protection":
            l9 += ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
          case "resets sent":
            l10 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
            continue;
        } 
        if (arrayOfString[1].contains("retransmitted") && arrayOfString[1].contains("data packet"))
          l8 += ParseUtil.parseLongOrDefault(arrayOfString[0], 0L); 
      } 
    } 
    return new InternetProtocolStats.TcpStats(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10);
  }
  
  public static InternetProtocolStats.UdpStats queryUdpStats(String paramString) {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    List list = ExecutingCommand.runNative(paramString);
    for (String str : list) {
      String[] arrayOfString = str.trim().split(" ", 2);
      if (arrayOfString.length == 2)
        switch (arrayOfString[1]) {
          case "packets sent":
          case "datagram output":
          case "datagrams output":
            l1 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
          case "packets received":
          case "datagram received":
          case "datagrams received":
            l2 = ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
          case "packets to unknown port received":
          case "dropped due to no socket":
          case "broadcast/multicast datagram dropped due to no socket":
          case "broadcast/multicast datagrams dropped due to no socket":
            l3 += ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
          case "packet receive errors":
          case "with incomplete header":
          case "with bad data length field":
          case "with bad checksum":
          case "woth no checksum":
            l4 += ParseUtil.parseLongOrDefault(arrayOfString[0], 0L);
        }  
    } 
    return new InternetProtocolStats.UdpStats(l1, l2, l3, l4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\NetStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */