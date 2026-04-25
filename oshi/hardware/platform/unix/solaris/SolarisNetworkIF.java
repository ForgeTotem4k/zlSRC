package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(SolarisNetworkIF.class);
  
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
  
  public SolarisNetworkIF(NetworkInterface paramNetworkInterface) throws InstantiationException {
    super(paramNetworkInterface);
    updateAttributes();
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    ArrayList<SolarisNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new SolarisNetworkIF(networkInterface));
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
    this.timeStamp = System.currentTimeMillis();
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return updateAttributes2(); 
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup("link", -1, getName());
      if (kstat == null)
        kstat = kstatChain.lookup(null, -1, getName()); 
      if (kstat != null && kstatChain.read(kstat)) {
        this.bytesSent = KstatUtil.dataLookupLong(kstat, "obytes64");
        this.bytesRecv = KstatUtil.dataLookupLong(kstat, "rbytes64");
        this.packetsSent = KstatUtil.dataLookupLong(kstat, "opackets64");
        this.packetsRecv = KstatUtil.dataLookupLong(kstat, "ipackets64");
        this.outErrors = KstatUtil.dataLookupLong(kstat, "oerrors");
        this.inErrors = KstatUtil.dataLookupLong(kstat, "ierrors");
        this.collisions = KstatUtil.dataLookupLong(kstat, "collisions");
        this.inDrops = KstatUtil.dataLookupLong(kstat, "dl_idrops");
        this.speed = KstatUtil.dataLookupLong(kstat, "ifspeed");
        this.timeStamp = kstat.ks_snaptime / 1000000L;
        boolean bool = true;
        if (kstatChain != null)
          kstatChain.close(); 
        return bool;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return false;
  }
  
  private boolean updateAttributes2() {
    Object[] arrayOfObject = KstatUtil.queryKstat2("kstat:/net/link/" + getName() + "/0", new String[] { "obytes64", "rbytes64", "opackets64", "ipackets64", "oerrors", "ierrors", "collisions", "dl_idrops", "ifspeed", "snaptime" });
    if (arrayOfObject[arrayOfObject.length - 1] == null)
      return false; 
    this.bytesSent = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
    this.bytesRecv = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
    this.packetsSent = (arrayOfObject[2] == null) ? 0L : ((Long)arrayOfObject[2]).longValue();
    this.packetsRecv = (arrayOfObject[3] == null) ? 0L : ((Long)arrayOfObject[3]).longValue();
    this.outErrors = (arrayOfObject[4] == null) ? 0L : ((Long)arrayOfObject[4]).longValue();
    this.collisions = (arrayOfObject[5] == null) ? 0L : ((Long)arrayOfObject[5]).longValue();
    this.inDrops = (arrayOfObject[6] == null) ? 0L : ((Long)arrayOfObject[6]).longValue();
    this.speed = (arrayOfObject[7] == null) ? 0L : ((Long)arrayOfObject[7]).longValue();
    this.timeStamp = ((Long)arrayOfObject[8]).longValue() / 1000000L;
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */