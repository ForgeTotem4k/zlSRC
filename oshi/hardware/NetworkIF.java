package oshi.hardware;

import java.net.NetworkInterface;
import java.util.Arrays;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface NetworkIF {
  NetworkInterface queryNetworkInterface();
  
  String getName();
  
  int getIndex();
  
  String getDisplayName();
  
  default String getIfAlias() {
    return "";
  }
  
  default IfOperStatus getIfOperStatus() {
    return IfOperStatus.UNKNOWN;
  }
  
  long getMTU();
  
  String getMacaddr();
  
  String[] getIPv4addr();
  
  Short[] getSubnetMasks();
  
  String[] getIPv6addr();
  
  Short[] getPrefixLengths();
  
  default int getIfType() {
    return 0;
  }
  
  default int getNdisPhysicalMediumType() {
    return 0;
  }
  
  default boolean isConnectorPresent() {
    return false;
  }
  
  long getBytesRecv();
  
  long getBytesSent();
  
  long getPacketsRecv();
  
  long getPacketsSent();
  
  long getInErrors();
  
  long getOutErrors();
  
  long getInDrops();
  
  long getCollisions();
  
  long getSpeed();
  
  long getTimeStamp();
  
  boolean isKnownVmMacAddr();
  
  boolean updateAttributes();
  
  public enum IfOperStatus {
    UP(1),
    DOWN(2),
    TESTING(3),
    UNKNOWN(4),
    DORMANT(5),
    NOT_PRESENT(6),
    LOWER_LAYER_DOWN(7);
    
    private final int value;
    
    IfOperStatus(int param1Int1) {
      this.value = param1Int1;
    }
    
    public int getValue() {
      return this.value;
    }
    
    public static IfOperStatus byValue(int param1Int) {
      return Arrays.<IfOperStatus>stream(values()).filter(param1IfOperStatus -> (param1IfOperStatus.getValue() == param1Int)).findFirst().orElse(UNKNOWN);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\NetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */