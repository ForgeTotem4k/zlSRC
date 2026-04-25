package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.perfstat.PerfstatNetInterface;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.Memoizer;

@ThreadSafe
public final class AixNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(AixNetworkIF.class);
  
  private long bytesRecv;
  
  private long bytesSent;
  
  private long packetsRecv;
  
  private long packetsSent;
  
  private long inErrors;
  
  private long outErrors;
  
  private long inDrops;
  
  private long collisions;
  
  private long speed;
  
  private long timeStamp;
  
  private Supplier<Perfstat.perfstat_netinterface_t[]> netstats;
  
  public AixNetworkIF(NetworkInterface paramNetworkInterface, Supplier<Perfstat.perfstat_netinterface_t[]> paramSupplier) throws InstantiationException {
    super(paramNetworkInterface);
    this.netstats = paramSupplier;
    updateAttributes();
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    Supplier<Perfstat.perfstat_netinterface_t[]> supplier = Memoizer.memoize(PerfstatNetInterface::queryNetInterfaces, Memoizer.defaultExpiration());
    ArrayList<AixNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new AixNetworkIF(networkInterface, supplier));
      } catch (InstantiationException instantiationException) {
        LOG.debug("Network Interface Instantiation failed: {}", instantiationException.getMessage());
      } 
    } 
    return (List)arrayList;
  }
  
  public long getBytesRecv() {
    return this.bytesRecv;
  }
  
  public long getBytesSent() {
    return this.bytesSent;
  }
  
  public long getPacketsRecv() {
    return this.packetsRecv;
  }
  
  public long getPacketsSent() {
    return this.packetsSent;
  }
  
  public long getInErrors() {
    return this.inErrors;
  }
  
  public long getOutErrors() {
    return this.outErrors;
  }
  
  public long getInDrops() {
    return this.inDrops;
  }
  
  public long getCollisions() {
    return this.collisions;
  }
  
  public long getSpeed() {
    return this.speed;
  }
  
  public long getTimeStamp() {
    return this.timeStamp;
  }
  
  public boolean updateAttributes() {
    Perfstat.perfstat_netinterface_t[] arrayOfPerfstat_netinterface_t = this.netstats.get();
    long l = System.currentTimeMillis();
    for (Perfstat.perfstat_netinterface_t perfstat_netinterface_t : arrayOfPerfstat_netinterface_t) {
      String str = Native.toString(perfstat_netinterface_t.name);
      if (str.equals(getName())) {
        this.bytesSent = perfstat_netinterface_t.obytes;
        this.bytesRecv = perfstat_netinterface_t.ibytes;
        this.packetsSent = perfstat_netinterface_t.opackets;
        this.packetsRecv = perfstat_netinterface_t.ipackets;
        this.outErrors = perfstat_netinterface_t.oerrors;
        this.inErrors = perfstat_netinterface_t.ierrors;
        this.collisions = perfstat_netinterface_t.collisions;
        this.inDrops = perfstat_netinterface_t.if_iqdrops;
        this.speed = perfstat_netinterface_t.bitrate;
        this.timeStamp = l;
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */