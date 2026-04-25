package oshi.software.os.linux;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public class LinuxInternetProtocolStats extends AbstractInternetProtocolStats {
  private final String tcpColon = "Tcp:";
  
  private final String udpColon = "Udp:";
  
  private final String udp6 = "Udp6";
  
  public InternetProtocolStats.TcpStats getTCPv4Stats() {
    byte[] arrayOfByte = FileUtil.readAllBytes(ProcPath.SNMP, true);
    List<String> list = ParseUtil.parseByteArrayToStrings(arrayOfByte);
    EnumMap<TcpStat, Object> enumMap = new EnumMap<>(TcpStat.class);
    for (byte b = 0; b < list.size() - 1; b += 2) {
      if (((String)list.get(b)).startsWith("Tcp:") && ((String)list.get(b + 1)).startsWith("Tcp:")) {
        Map map = ParseUtil.stringToEnumMap(TcpStat.class, ((String)list.get(b + 1)).substring("Tcp:".length()).trim(), ' ');
        for (Map.Entry entry : map.entrySet())
          enumMap.put((TcpStat)entry.getKey(), Long.valueOf(ParseUtil.parseLongOrDefault((String)entry.getValue(), 0L))); 
        break;
      } 
    } 
    return new InternetProtocolStats.TcpStats(((Long)enumMap.getOrDefault(TcpStat.CurrEstab, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.ActiveOpens, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.PassiveOpens, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.AttemptFails, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.EstabResets, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.OutSegs, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.InSegs, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.RetransSegs, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.InErrs, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(TcpStat.OutRsts, Long.valueOf(0L))).longValue());
  }
  
  public InternetProtocolStats.UdpStats getUDPv4Stats() {
    byte[] arrayOfByte = FileUtil.readAllBytes(ProcPath.SNMP, true);
    List<String> list = ParseUtil.parseByteArrayToStrings(arrayOfByte);
    EnumMap<UdpStat, Object> enumMap = new EnumMap<>(UdpStat.class);
    for (byte b = 0; b < list.size() - 1; b += 2) {
      if (((String)list.get(b)).startsWith("Udp:") && ((String)list.get(b + 1)).startsWith("Udp:")) {
        Map map = ParseUtil.stringToEnumMap(UdpStat.class, ((String)list.get(b + 1)).substring("Udp:".length()).trim(), ' ');
        for (Map.Entry entry : map.entrySet())
          enumMap.put((UdpStat)entry.getKey(), Long.valueOf(ParseUtil.parseLongOrDefault((String)entry.getValue(), 0L))); 
        break;
      } 
    } 
    return new InternetProtocolStats.UdpStats(((Long)enumMap.getOrDefault(UdpStat.OutDatagrams, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(UdpStat.InDatagrams, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(UdpStat.NoPorts, Long.valueOf(0L))).longValue(), ((Long)enumMap.getOrDefault(UdpStat.InErrors, Long.valueOf(0L))).longValue());
  }
  
  public InternetProtocolStats.UdpStats getUDPv6Stats() {
    byte[] arrayOfByte = FileUtil.readAllBytes(ProcPath.SNMP6, true);
    List<String> list = ParseUtil.parseByteArrayToStrings(arrayOfByte);
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    byte b = 0;
    for (int i = list.size() - 1; i >= 0 && b < 4; i--) {
      if (((String)list.get(i)).startsWith("Udp6")) {
        String[] arrayOfString = ((String)list.get(i)).split("\\s+");
        switch (arrayOfString[0]) {
          case "Udp6InDatagrams":
            l1 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
            b++;
            break;
          case "Udp6NoPorts":
            l2 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
            b++;
            break;
          case "Udp6InErrors":
            l3 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
            b++;
            break;
          case "Udp6OutDatagrams":
            l4 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
            b++;
            break;
        } 
      } 
    } 
    return new InternetProtocolStats.UdpStats(l1, l2, l3, l4);
  }
  
  public List<InternetProtocolStats.IPConnection> getConnections() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    Map<Long, Integer> map = ProcessStat.querySocketToPidMap();
    arrayList.addAll(queryConnections("tcp", 4, map));
    arrayList.addAll(queryConnections("tcp", 6, map));
    arrayList.addAll(queryConnections("udp", 4, map));
    arrayList.addAll(queryConnections("udp", 6, map));
    return arrayList;
  }
  
  private static List<InternetProtocolStats.IPConnection> queryConnections(String paramString, int paramInt, Map<Long, Integer> paramMap) {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    for (String str : FileUtil.readFile(ProcPath.NET + "/" + paramString + ((paramInt == 6) ? "6" : ""))) {
      if (str.indexOf(':') >= 0) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str.trim());
        if (arrayOfString.length > 9) {
          Pair<byte[], Integer> pair1 = parseIpAddr(arrayOfString[1]);
          Pair<byte[], Integer> pair2 = parseIpAddr(arrayOfString[2]);
          InternetProtocolStats.TcpState tcpState = stateLookup(ParseUtil.hexStringToInt(arrayOfString[3], 0));
          Pair<Integer, Integer> pair = parseHexColonHex(arrayOfString[4]);
          long l = ParseUtil.parseLongOrDefault(arrayOfString[9], 0L);
          arrayList.add(new InternetProtocolStats.IPConnection(paramString + paramInt, (byte[])pair1.getA(), ((Integer)pair1.getB()).intValue(), (byte[])pair2.getA(), ((Integer)pair2.getB()).intValue(), tcpState, ((Integer)pair.getA()).intValue(), ((Integer)pair.getB()).intValue(), ((Integer)paramMap.getOrDefault(Long.valueOf(l), Integer.valueOf(-1))).intValue()));
        } 
      } 
    } 
    return arrayList;
  }
  
  private static Pair<byte[], Integer> parseIpAddr(String paramString) {
    int i = paramString.indexOf(':');
    if (i > 0 && i < paramString.length()) {
      byte[] arrayOfByte = ParseUtil.hexStringToByteArray(paramString.substring(0, i));
      int j;
      for (j = 0; j + 3 < arrayOfByte.length; j += 4) {
        byte b = arrayOfByte[j];
        arrayOfByte[j] = arrayOfByte[j + 3];
        arrayOfByte[j + 3] = b;
        b = arrayOfByte[j + 1];
        arrayOfByte[j + 1] = arrayOfByte[j + 2];
        arrayOfByte[j + 2] = b;
      } 
      j = ParseUtil.hexStringToInt(paramString.substring(i + 1), 0);
      return new Pair(arrayOfByte, Integer.valueOf(j));
    } 
    return new Pair(new byte[0], Integer.valueOf(0));
  }
  
  private static Pair<Integer, Integer> parseHexColonHex(String paramString) {
    int i = paramString.indexOf(':');
    if (i > 0 && i < paramString.length()) {
      int j = ParseUtil.hexStringToInt(paramString.substring(0, i), 0);
      int k = ParseUtil.hexStringToInt(paramString.substring(i + 1), 0);
      return new Pair(Integer.valueOf(j), Integer.valueOf(k));
    } 
    return new Pair(Integer.valueOf(0), Integer.valueOf(0));
  }
  
  private static InternetProtocolStats.TcpState stateLookup(int paramInt) {
    switch (paramInt) {
      case 1:
        return InternetProtocolStats.TcpState.ESTABLISHED;
      case 2:
        return InternetProtocolStats.TcpState.SYN_SENT;
      case 3:
        return InternetProtocolStats.TcpState.SYN_RECV;
      case 4:
        return InternetProtocolStats.TcpState.FIN_WAIT_1;
      case 5:
        return InternetProtocolStats.TcpState.FIN_WAIT_2;
      case 6:
        return InternetProtocolStats.TcpState.TIME_WAIT;
      case 7:
        return InternetProtocolStats.TcpState.CLOSED;
      case 8:
        return InternetProtocolStats.TcpState.CLOSE_WAIT;
      case 9:
        return InternetProtocolStats.TcpState.LAST_ACK;
      case 10:
        return InternetProtocolStats.TcpState.LISTEN;
      case 11:
        return InternetProtocolStats.TcpState.CLOSING;
    } 
    return InternetProtocolStats.TcpState.UNKNOWN;
  }
  
  private enum TcpStat {
    RtoAlgorithm, RtoMin, RtoMax, MaxConn, ActiveOpens, PassiveOpens, AttemptFails, EstabResets, CurrEstab, InSegs, OutSegs, RetransSegs, InErrs, OutRsts, InCsumErrors;
  }
  
  private enum UdpStat {
    OutDatagrams, InDatagrams, NoPorts, InErrors, RcvbufErrors, SndbufErrors, InCsumErrors, IgnoredMulti, MemErrors;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */