package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.FileUtil;
import oshi.util.Util;
import oshi.util.platform.linux.SysPath;

@ThreadSafe
public final class LinuxNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxNetworkIF.class);
  
  private int ifType;
  
  private boolean connectorPresent;
  
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
  
  private String ifAlias = "";
  
  private NetworkIF.IfOperStatus ifOperStatus = NetworkIF.IfOperStatus.UNKNOWN;
  
  public LinuxNetworkIF(NetworkInterface paramNetworkInterface) throws InstantiationException {
    super(paramNetworkInterface, queryIfModel(paramNetworkInterface));
    updateAttributes();
  }
  
  private static String queryIfModel(NetworkInterface paramNetworkInterface) {
    String str = paramNetworkInterface.getName();
    if (!LinuxOperatingSystem.HAS_UDEV)
      return queryIfModelFromSysfs(str); 
    Udev.UdevContext udevContext = Udev.INSTANCE.udev_new();
    if (udevContext != null)
      try {
        Udev.UdevDevice udevDevice = udevContext.deviceNewFromSyspath(SysPath.NET + str);
        if (udevDevice != null)
          try {
            String str1 = udevDevice.getPropertyValue("ID_VENDOR_FROM_DATABASE");
            String str2 = udevDevice.getPropertyValue("ID_MODEL_FROM_DATABASE");
            if (!Util.isBlank(str2)) {
              if (!Util.isBlank(str1))
                return str1 + " " + str2; 
              return str2;
            } 
          } finally {
            udevDevice.unref();
          }  
      } finally {
        udevContext.unref();
      }  
    return str;
  }
  
  private static String queryIfModelFromSysfs(String paramString) {
    Map map = FileUtil.getKeyValueMapFromFile(SysPath.NET + paramString + "/uevent", "=");
    String str1 = (String)map.get("ID_VENDOR_FROM_DATABASE");
    String str2 = (String)map.get("ID_MODEL_FROM_DATABASE");
    return !Util.isBlank(str2) ? (!Util.isBlank(str1) ? (str1 + " " + str2) : str2) : paramString;
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    ArrayList<LinuxNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new LinuxNetworkIF(networkInterface));
      } catch (InstantiationException instantiationException) {
        LOG.debug("Network Interface Instantiation failed: {}", instantiationException.getMessage());
      } 
    } 
    return (List)arrayList;
  }
  
  public int getIfType() {
    return this.ifType;
  }
  
  public boolean isConnectorPresent() {
    return this.connectorPresent;
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
  
  public String getIfAlias() {
    return this.ifAlias;
  }
  
  public NetworkIF.IfOperStatus getIfOperStatus() {
    return this.ifOperStatus;
  }
  
  public boolean updateAttributes() {
    String str = SysPath.NET + getName();
    try {
      File file = new File(str + "/statistics");
      if (!file.isDirectory())
        return false; 
    } catch (SecurityException securityException) {
      return false;
    } 
    this.timeStamp = System.currentTimeMillis();
    this.ifType = FileUtil.getIntFromFile(str + "/type");
    this.connectorPresent = (FileUtil.getIntFromFile(str + "/carrier") > 0);
    this.bytesSent = FileUtil.getUnsignedLongFromFile(str + "/statistics/tx_bytes");
    this.bytesRecv = FileUtil.getUnsignedLongFromFile(str + "/statistics/rx_bytes");
    this.packetsSent = FileUtil.getUnsignedLongFromFile(str + "/statistics/tx_packets");
    this.packetsRecv = FileUtil.getUnsignedLongFromFile(str + "/statistics/rx_packets");
    this.outErrors = FileUtil.getUnsignedLongFromFile(str + "/statistics/tx_errors");
    this.inErrors = FileUtil.getUnsignedLongFromFile(str + "/statistics/rx_errors");
    this.collisions = FileUtil.getUnsignedLongFromFile(str + "/statistics/collisions");
    this.inDrops = FileUtil.getUnsignedLongFromFile(str + "/statistics/rx_dropped");
    long l = FileUtil.getUnsignedLongFromFile(str + "/speed");
    this.speed = (l < 0L) ? 0L : (l * 1000000L);
    this.ifAlias = FileUtil.getStringFromFile(str + "/ifalias");
    this.ifOperStatus = parseIfOperStatus(FileUtil.getStringFromFile(str + "/operstate"));
    return true;
  }
  
  private static NetworkIF.IfOperStatus parseIfOperStatus(String paramString) {
    switch (paramString) {
      case "up":
        return NetworkIF.IfOperStatus.UP;
      case "down":
        return NetworkIF.IfOperStatus.DOWN;
      case "testing":
        return NetworkIF.IfOperStatus.TESTING;
      case "dormant":
        return NetworkIF.IfOperStatus.DORMANT;
      case "notpresent":
        return NetworkIF.IfOperStatus.NOT_PRESENT;
      case "lowerlayerdown":
        return NetworkIF.IfOperStatus.LOWER_LAYER_DOWN;
    } 
    return NetworkIF.IfOperStatus.UNKNOWN;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */