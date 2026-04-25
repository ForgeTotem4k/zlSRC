package oshi.hardware.platform.unix;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class BsdNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(BsdNetworkIF.class);
  
  private long bytesRecv;
  
  private long bytesSent;
  
  private long packetsRecv;
  
  private long packetsSent;
  
  private long inErrors;
  
  private long outErrors;
  
  private long inDrops;
  
  private long collisions;
  
  private long timeStamp;
  
  public BsdNetworkIF(NetworkInterface paramNetworkInterface) throws InstantiationException {
    super(paramNetworkInterface);
    updateAttributes();
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    ArrayList<BsdNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new BsdNetworkIF(networkInterface));
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
    return 0L;
  }
  
  public long getTimeStamp() {
    return this.timeStamp;
  }
  
  public boolean updateAttributes() {
    String str = ExecutingCommand.getAnswerAt("netstat -bI " + getName(), 1);
    this.timeStamp = System.currentTimeMillis();
    String[] arrayOfString = ParseUtil.whitespaces.split(str);
    if (arrayOfString.length < 12)
      return false; 
    this.bytesSent = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[10], 0L);
    this.bytesRecv = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[7], 0L);
    this.packetsSent = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[8], 0L);
    this.packetsRecv = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[4], 0L);
    this.outErrors = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[9], 0L);
    this.inErrors = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[5], 0L);
    this.collisions = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[11], 0L);
    this.inDrops = ParseUtil.parseUnsignedLongOrDefault(arrayOfString[6], 0L);
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\BsdNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */