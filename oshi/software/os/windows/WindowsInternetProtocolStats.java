package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ParseUtil;

@ThreadSafe
public class WindowsInternetProtocolStats extends AbstractInternetProtocolStats {
  private static final IPHlpAPI IPHLP = IPHlpAPI.INSTANCE;
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  public InternetProtocolStats.TcpStats getTCPv4Stats() {
    Struct.CloseableMibTcpStats closeableMibTcpStats = new Struct.CloseableMibTcpStats();
    try {
      IPHLP.GetTcpStatisticsEx((IPHlpAPI.MIB_TCPSTATS)closeableMibTcpStats, 2);
      InternetProtocolStats.TcpStats tcpStats = new InternetProtocolStats.TcpStats(closeableMibTcpStats.dwCurrEstab, closeableMibTcpStats.dwActiveOpens, closeableMibTcpStats.dwPassiveOpens, closeableMibTcpStats.dwAttemptFails, closeableMibTcpStats.dwEstabResets, closeableMibTcpStats.dwOutSegs, closeableMibTcpStats.dwInSegs, closeableMibTcpStats.dwRetransSegs, closeableMibTcpStats.dwInErrs, closeableMibTcpStats.dwOutRsts);
      closeableMibTcpStats.close();
      return tcpStats;
    } catch (Throwable throwable) {
      try {
        closeableMibTcpStats.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public InternetProtocolStats.TcpStats getTCPv6Stats() {
    Struct.CloseableMibTcpStats closeableMibTcpStats = new Struct.CloseableMibTcpStats();
    try {
      IPHLP.GetTcpStatisticsEx((IPHlpAPI.MIB_TCPSTATS)closeableMibTcpStats, 23);
      InternetProtocolStats.TcpStats tcpStats = new InternetProtocolStats.TcpStats(closeableMibTcpStats.dwCurrEstab, closeableMibTcpStats.dwActiveOpens, closeableMibTcpStats.dwPassiveOpens, closeableMibTcpStats.dwAttemptFails, closeableMibTcpStats.dwEstabResets, closeableMibTcpStats.dwOutSegs, closeableMibTcpStats.dwInSegs, closeableMibTcpStats.dwRetransSegs, closeableMibTcpStats.dwInErrs, closeableMibTcpStats.dwOutRsts);
      closeableMibTcpStats.close();
      return tcpStats;
    } catch (Throwable throwable) {
      try {
        closeableMibTcpStats.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public InternetProtocolStats.UdpStats getUDPv4Stats() {
    Struct.CloseableMibUdpStats closeableMibUdpStats = new Struct.CloseableMibUdpStats();
    try {
      IPHLP.GetUdpStatisticsEx((IPHlpAPI.MIB_UDPSTATS)closeableMibUdpStats, 2);
      InternetProtocolStats.UdpStats udpStats = new InternetProtocolStats.UdpStats(closeableMibUdpStats.dwOutDatagrams, closeableMibUdpStats.dwInDatagrams, closeableMibUdpStats.dwNoPorts, closeableMibUdpStats.dwInErrors);
      closeableMibUdpStats.close();
      return udpStats;
    } catch (Throwable throwable) {
      try {
        closeableMibUdpStats.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public InternetProtocolStats.UdpStats getUDPv6Stats() {
    Struct.CloseableMibUdpStats closeableMibUdpStats = new Struct.CloseableMibUdpStats();
    try {
      IPHLP.GetUdpStatisticsEx((IPHlpAPI.MIB_UDPSTATS)closeableMibUdpStats, 23);
      InternetProtocolStats.UdpStats udpStats = new InternetProtocolStats.UdpStats(closeableMibUdpStats.dwOutDatagrams, closeableMibUdpStats.dwInDatagrams, closeableMibUdpStats.dwNoPorts, closeableMibUdpStats.dwInErrors);
      closeableMibUdpStats.close();
      return udpStats;
    } catch (Throwable throwable) {
      try {
        closeableMibUdpStats.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public List<InternetProtocolStats.IPConnection> getConnections() {
    if (IS_VISTA_OR_GREATER) {
      ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
      arrayList.addAll(queryTCPv4Connections());
      arrayList.addAll(queryTCPv6Connections());
      arrayList.addAll(queryUDPv4Connections());
      arrayList.addAll(queryUDPv6Connections());
      return arrayList;
    } 
    return Collections.emptyList();
  }
  
  private static List<InternetProtocolStats.IPConnection> queryTCPv4Connections() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      int i = IPHLP.GetExtendedTcpTable(null, (IntByReference)closeableIntByReference, false, 2, 5, 0);
      int j = closeableIntByReference.getValue();
      Memory memory = new Memory(j);
      while (true) {
        i = IPHLP.GetExtendedTcpTable((Pointer)memory, (IntByReference)closeableIntByReference, false, 2, 5, 0);
        if (i == 122) {
          j = closeableIntByReference.getValue();
          memory.close();
          memory = new Memory(j);
        } 
        if (i != 122) {
          IPHlpAPI.MIB_TCPTABLE_OWNER_PID mIB_TCPTABLE_OWNER_PID = new IPHlpAPI.MIB_TCPTABLE_OWNER_PID((Pointer)memory);
          for (byte b = 0; b < mIB_TCPTABLE_OWNER_PID.dwNumEntries; b++) {
            IPHlpAPI.MIB_TCPROW_OWNER_PID mIB_TCPROW_OWNER_PID = mIB_TCPTABLE_OWNER_PID.table[b];
            arrayList.add(new InternetProtocolStats.IPConnection("tcp4", ParseUtil.parseIntToIP(mIB_TCPROW_OWNER_PID.dwLocalAddr), ParseUtil.bigEndian16ToLittleEndian(mIB_TCPROW_OWNER_PID.dwLocalPort), ParseUtil.parseIntToIP(mIB_TCPROW_OWNER_PID.dwRemoteAddr), ParseUtil.bigEndian16ToLittleEndian(mIB_TCPROW_OWNER_PID.dwRemotePort), stateLookup(mIB_TCPROW_OWNER_PID.dwState), 0, 0, mIB_TCPROW_OWNER_PID.dwOwningPid));
          } 
          memory.close();
          closeableIntByReference.close();
          return arrayList;
        } 
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
  
  private static List<InternetProtocolStats.IPConnection> queryTCPv6Connections() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      int i = IPHLP.GetExtendedTcpTable(null, (IntByReference)closeableIntByReference, false, 23, 5, 0);
      int j = closeableIntByReference.getValue();
      Memory memory = new Memory(j);
      while (true) {
        i = IPHLP.GetExtendedTcpTable((Pointer)memory, (IntByReference)closeableIntByReference, false, 23, 5, 0);
        if (i == 122) {
          j = closeableIntByReference.getValue();
          memory.close();
          memory = new Memory(j);
        } 
        if (i != 122) {
          IPHlpAPI.MIB_TCP6TABLE_OWNER_PID mIB_TCP6TABLE_OWNER_PID = new IPHlpAPI.MIB_TCP6TABLE_OWNER_PID((Pointer)memory);
          for (byte b = 0; b < mIB_TCP6TABLE_OWNER_PID.dwNumEntries; b++) {
            IPHlpAPI.MIB_TCP6ROW_OWNER_PID mIB_TCP6ROW_OWNER_PID = mIB_TCP6TABLE_OWNER_PID.table[b];
            arrayList.add(new InternetProtocolStats.IPConnection("tcp6", mIB_TCP6ROW_OWNER_PID.LocalAddr, ParseUtil.bigEndian16ToLittleEndian(mIB_TCP6ROW_OWNER_PID.dwLocalPort), mIB_TCP6ROW_OWNER_PID.RemoteAddr, ParseUtil.bigEndian16ToLittleEndian(mIB_TCP6ROW_OWNER_PID.dwRemotePort), stateLookup(mIB_TCP6ROW_OWNER_PID.State), 0, 0, mIB_TCP6ROW_OWNER_PID.dwOwningPid));
          } 
          memory.close();
          closeableIntByReference.close();
          return arrayList;
        } 
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
  
  private static List<InternetProtocolStats.IPConnection> queryUDPv4Connections() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      int i = IPHLP.GetExtendedUdpTable(null, (IntByReference)closeableIntByReference, false, 2, 1, 0);
      int j = closeableIntByReference.getValue();
      Memory memory = new Memory(j);
      while (true) {
        i = IPHLP.GetExtendedUdpTable((Pointer)memory, (IntByReference)closeableIntByReference, false, 2, 1, 0);
        if (i == 122) {
          j = closeableIntByReference.getValue();
          memory.close();
          memory = new Memory(j);
        } 
        if (i != 122) {
          IPHlpAPI.MIB_UDPTABLE_OWNER_PID mIB_UDPTABLE_OWNER_PID = new IPHlpAPI.MIB_UDPTABLE_OWNER_PID((Pointer)memory);
          for (byte b = 0; b < mIB_UDPTABLE_OWNER_PID.dwNumEntries; b++) {
            IPHlpAPI.MIB_UDPROW_OWNER_PID mIB_UDPROW_OWNER_PID = mIB_UDPTABLE_OWNER_PID.table[b];
            arrayList.add(new InternetProtocolStats.IPConnection("udp4", ParseUtil.parseIntToIP(mIB_UDPROW_OWNER_PID.dwLocalAddr), ParseUtil.bigEndian16ToLittleEndian(mIB_UDPROW_OWNER_PID.dwLocalPort), new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0, mIB_UDPROW_OWNER_PID.dwOwningPid));
          } 
          memory.close();
          closeableIntByReference.close();
          return arrayList;
        } 
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
  
  private static List<InternetProtocolStats.IPConnection> queryUDPv6Connections() {
    ArrayList<InternetProtocolStats.IPConnection> arrayList = new ArrayList();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      int i = IPHLP.GetExtendedUdpTable(null, (IntByReference)closeableIntByReference, false, 23, 1, 0);
      int j = closeableIntByReference.getValue();
      Memory memory = new Memory(j);
      while (true) {
        i = IPHLP.GetExtendedUdpTable((Pointer)memory, (IntByReference)closeableIntByReference, false, 23, 1, 0);
        if (i == 122) {
          j = closeableIntByReference.getValue();
          memory.close();
          memory = new Memory(j);
        } 
        if (i != 122) {
          IPHlpAPI.MIB_UDP6TABLE_OWNER_PID mIB_UDP6TABLE_OWNER_PID = new IPHlpAPI.MIB_UDP6TABLE_OWNER_PID((Pointer)memory);
          for (byte b = 0; b < mIB_UDP6TABLE_OWNER_PID.dwNumEntries; b++) {
            IPHlpAPI.MIB_UDP6ROW_OWNER_PID mIB_UDP6ROW_OWNER_PID = mIB_UDP6TABLE_OWNER_PID.table[b];
            arrayList.add(new InternetProtocolStats.IPConnection("udp6", mIB_UDP6ROW_OWNER_PID.ucLocalAddr, ParseUtil.bigEndian16ToLittleEndian(mIB_UDP6ROW_OWNER_PID.dwLocalPort), new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0, mIB_UDP6ROW_OWNER_PID.dwOwningPid));
          } 
          closeableIntByReference.close();
          return arrayList;
        } 
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
  
  private static InternetProtocolStats.TcpState stateLookup(int paramInt) {
    switch (paramInt) {
      case 1:
      case 12:
        return InternetProtocolStats.TcpState.CLOSED;
      case 2:
        return InternetProtocolStats.TcpState.LISTEN;
      case 3:
        return InternetProtocolStats.TcpState.SYN_SENT;
      case 4:
        return InternetProtocolStats.TcpState.SYN_RECV;
      case 5:
        return InternetProtocolStats.TcpState.ESTABLISHED;
      case 6:
        return InternetProtocolStats.TcpState.FIN_WAIT_1;
      case 7:
        return InternetProtocolStats.TcpState.FIN_WAIT_2;
      case 8:
        return InternetProtocolStats.TcpState.CLOSE_WAIT;
      case 9:
        return InternetProtocolStats.TcpState.CLOSING;
      case 10:
        return InternetProtocolStats.TcpState.LAST_ACK;
      case 11:
        return InternetProtocolStats.TcpState.TIME_WAIT;
    } 
    return InternetProtocolStats.TcpState.UNKNOWN;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */