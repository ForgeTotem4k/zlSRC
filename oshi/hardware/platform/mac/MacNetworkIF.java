package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.net.NetStat;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.jna.platform.mac.SystemConfiguration;

@ThreadSafe
public final class MacNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(MacNetworkIF.class);
  
  private int ifType;
  
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
  
  public MacNetworkIF(NetworkInterface paramNetworkInterface, Map<Integer, NetStat.IFdata> paramMap) throws InstantiationException {
    super(paramNetworkInterface, queryIfDisplayName(paramNetworkInterface));
    updateNetworkStats(paramMap);
  }
  
  private static String queryIfDisplayName(NetworkInterface paramNetworkInterface) {
    String str = paramNetworkInterface.getName();
    CoreFoundation.CFArrayRef cFArrayRef = SystemConfiguration.INSTANCE.SCNetworkInterfaceCopyAll();
    if (cFArrayRef != null)
      try {
        int i = cFArrayRef.getCount();
        for (byte b = 0; b < i; b++) {
          Pointer pointer = cFArrayRef.getValueAtIndex(b);
          SystemConfiguration.SCNetworkInterfaceRef sCNetworkInterfaceRef = new SystemConfiguration.SCNetworkInterfaceRef(pointer);
          CoreFoundation.CFStringRef cFStringRef = SystemConfiguration.INSTANCE.SCNetworkInterfaceGetBSDName(sCNetworkInterfaceRef);
          if (cFStringRef != null && str.equals(cFStringRef.stringValue())) {
            CoreFoundation.CFStringRef cFStringRef1 = SystemConfiguration.INSTANCE.SCNetworkInterfaceGetLocalizedDisplayName(sCNetworkInterfaceRef);
            return cFStringRef1.stringValue();
          } 
        } 
      } finally {
        cFArrayRef.release();
      }  
    return str;
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    Map<Integer, NetStat.IFdata> map = NetStat.queryIFdata(-1);
    ArrayList<MacNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new MacNetworkIF(networkInterface, map));
      } catch (InstantiationException instantiationException) {
        LOG.debug("Network Interface Instantiation failed: {}", instantiationException.getMessage());
      } 
    } 
    return (List)arrayList;
  }
  
  public int getIfType() {
    return this.ifType;
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
    int i = queryNetworkInterface().getIndex();
    return updateNetworkStats(NetStat.queryIFdata(i));
  }
  
  private boolean updateNetworkStats(Map<Integer, NetStat.IFdata> paramMap) {
    int i = queryNetworkInterface().getIndex();
    if (paramMap.containsKey(Integer.valueOf(i))) {
      NetStat.IFdata iFdata = paramMap.get(Integer.valueOf(i));
      this.ifType = iFdata.getIfType();
      this.bytesSent = iFdata.getOBytes();
      this.bytesRecv = iFdata.getIBytes();
      this.packetsSent = iFdata.getOPackets();
      this.packetsRecv = iFdata.getIPackets();
      this.outErrors = iFdata.getOErrors();
      this.inErrors = iFdata.getIErrors();
      this.collisions = iFdata.getCollisions();
      this.inDrops = iFdata.getIDrops();
      this.speed = iFdata.getSpeed();
      this.timeStamp = iFdata.getTimeStamp();
      return true;
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */