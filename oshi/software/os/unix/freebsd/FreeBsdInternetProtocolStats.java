package oshi.software.os.unix.freebsd;

import com.sun.jna.Memory;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.NetStat;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class FreeBsdInternetProtocolStats extends AbstractInternetProtocolStats {
  private Supplier<Pair<Long, Long>> establishedv4v6 = Memoizer.memoize(NetStat::queryTcpnetstat, Memoizer.defaultExpiration());
  
  private Supplier<CLibrary.BsdTcpstat> tcpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
  
  private Supplier<CLibrary.BsdUdpstat> udpstat = Memoizer.memoize(FreeBsdInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());
  
  public InternetProtocolStats.TcpStats getTCPv4Stats() {
    CLibrary.BsdTcpstat bsdTcpstat = this.tcpstat.get();
    return new InternetProtocolStats.TcpStats(((Long)((Pair)this.establishedv4v6.get()).getA()).longValue(), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_connattempt), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_accepts), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_conndrops), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_drops), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_sndpack), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_rcvpack), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_sndrexmitpack), ParseUtil.unsignedIntToLong(bsdTcpstat.tcps_rcvbadsum + bsdTcpstat.tcps_rcvbadoff + bsdTcpstat.tcps_rcvmemdrop + bsdTcpstat.tcps_rcvshort), 0L);
  }
  
  public InternetProtocolStats.UdpStats getUDPv4Stats() {
    CLibrary.BsdUdpstat bsdUdpstat = this.udpstat.get();
    return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(bsdUdpstat.udps_opackets), ParseUtil.unsignedIntToLong(bsdUdpstat.udps_ipackets), ParseUtil.unsignedIntToLong(bsdUdpstat.udps_noportmcast), ParseUtil.unsignedIntToLong(bsdUdpstat.udps_hdrops + bsdUdpstat.udps_badsum + bsdUdpstat.udps_badlen));
  }
  
  public InternetProtocolStats.UdpStats getUDPv6Stats() {
    CLibrary.BsdUdpstat bsdUdpstat = this.udpstat.get();
    return new InternetProtocolStats.UdpStats(ParseUtil.unsignedIntToLong(bsdUdpstat.udps_snd6_swcsum), ParseUtil.unsignedIntToLong(bsdUdpstat.udps_rcv6_swcsum), 0L, 0L);
  }
  
  private static CLibrary.BsdTcpstat queryTcpstat() {
    CLibrary.BsdTcpstat bsdTcpstat = new CLibrary.BsdTcpstat();
    Memory memory = BsdSysctlUtil.sysctl("net.inet.tcp.stats");
    try {
      if (memory != null && memory.size() >= 128L) {
        bsdTcpstat.tcps_connattempt = memory.getInt(0L);
        bsdTcpstat.tcps_accepts = memory.getInt(4L);
        bsdTcpstat.tcps_drops = memory.getInt(12L);
        bsdTcpstat.tcps_conndrops = memory.getInt(16L);
        bsdTcpstat.tcps_sndpack = memory.getInt(64L);
        bsdTcpstat.tcps_sndrexmitpack = memory.getInt(72L);
        bsdTcpstat.tcps_rcvpack = memory.getInt(104L);
        bsdTcpstat.tcps_rcvbadsum = memory.getInt(112L);
        bsdTcpstat.tcps_rcvbadoff = memory.getInt(116L);
        bsdTcpstat.tcps_rcvmemdrop = memory.getInt(120L);
        bsdTcpstat.tcps_rcvshort = memory.getInt(124L);
      } 
      if (memory != null)
        memory.close(); 
    } catch (Throwable throwable) {
      if (memory != null)
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return bsdTcpstat;
  }
  
  private static CLibrary.BsdUdpstat queryUdpstat() {
    CLibrary.BsdUdpstat bsdUdpstat = new CLibrary.BsdUdpstat();
    Memory memory = BsdSysctlUtil.sysctl("net.inet.udp.stats");
    try {
      if (memory != null && memory.size() >= 1644L) {
        bsdUdpstat.udps_ipackets = memory.getInt(0L);
        bsdUdpstat.udps_hdrops = memory.getInt(4L);
        bsdUdpstat.udps_badsum = memory.getInt(8L);
        bsdUdpstat.udps_badlen = memory.getInt(12L);
        bsdUdpstat.udps_opackets = memory.getInt(36L);
        bsdUdpstat.udps_noportmcast = memory.getInt(48L);
        bsdUdpstat.udps_rcv6_swcsum = memory.getInt(64L);
        bsdUdpstat.udps_snd6_swcsum = memory.getInt(80L);
      } 
      if (memory != null)
        memory.close(); 
    } catch (Throwable throwable) {
      if (memory != null)
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return bsdUdpstat;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdInternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */