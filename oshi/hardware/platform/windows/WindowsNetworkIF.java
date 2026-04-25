package oshi.hardware.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.VersionHelpers;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.hardware.common.AbstractNetworkIF;
import oshi.jna.Struct;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WindowsNetworkIF extends AbstractNetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkIF.class);
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  private static final byte CONNECTOR_PRESENT_BIT = 4;
  
  private int ifType;
  
  private int ndisPhysicalMediumType;
  
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
  
  private String ifAlias;
  
  private NetworkIF.IfOperStatus ifOperStatus;
  
  public WindowsNetworkIF(NetworkInterface paramNetworkInterface) throws InstantiationException {
    super(paramNetworkInterface);
    updateAttributes();
  }
  
  public static List<NetworkIF> getNetworks(boolean paramBoolean) {
    ArrayList<WindowsNetworkIF> arrayList = new ArrayList();
    for (NetworkInterface networkInterface : getNetworkInterfaces(paramBoolean)) {
      try {
        arrayList.add(new WindowsNetworkIF(networkInterface));
      } catch (InstantiationException instantiationException) {
        LOG.debug("Network Interface Instantiation failed: {}", instantiationException.getMessage());
      } 
    } 
    return (List)arrayList;
  }
  
  public int getIfType() {
    return this.ifType;
  }
  
  public int getNdisPhysicalMediumType() {
    return this.ndisPhysicalMediumType;
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
    if (IS_VISTA_OR_GREATER) {
      Struct.CloseableMibIfRow2 closeableMibIfRow2 = new Struct.CloseableMibIfRow2();
      try {
        closeableMibIfRow2.InterfaceIndex = queryNetworkInterface().getIndex();
        if (0 != IPHlpAPI.INSTANCE.GetIfEntry2((IPHlpAPI.MIB_IF_ROW2)closeableMibIfRow2)) {
          LOG.error("Failed to retrieve data for interface {}, {}", Integer.valueOf(queryNetworkInterface().getIndex()), getName());
          boolean bool = false;
          closeableMibIfRow2.close();
          return bool;
        } 
        this.ifType = closeableMibIfRow2.Type;
        this.ndisPhysicalMediumType = closeableMibIfRow2.PhysicalMediumType;
        this.connectorPresent = ((closeableMibIfRow2.InterfaceAndOperStatusFlags & 0x4) > 0);
        this.bytesSent = closeableMibIfRow2.OutOctets;
        this.bytesRecv = closeableMibIfRow2.InOctets;
        this.packetsSent = closeableMibIfRow2.OutUcastPkts;
        this.packetsRecv = closeableMibIfRow2.InUcastPkts;
        this.outErrors = closeableMibIfRow2.OutErrors;
        this.inErrors = closeableMibIfRow2.InErrors;
        this.collisions = closeableMibIfRow2.OutDiscards;
        this.inDrops = closeableMibIfRow2.InDiscards;
        this.speed = closeableMibIfRow2.ReceiveLinkSpeed;
        this.ifAlias = Native.toString(closeableMibIfRow2.Alias);
        this.ifOperStatus = NetworkIF.IfOperStatus.byValue(closeableMibIfRow2.OperStatus);
        closeableMibIfRow2.close();
      } catch (Throwable throwable) {
        try {
          closeableMibIfRow2.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } else {
      Struct.CloseableMibIfRow closeableMibIfRow = new Struct.CloseableMibIfRow();
      try {
        closeableMibIfRow.dwIndex = queryNetworkInterface().getIndex();
        if (0 != IPHlpAPI.INSTANCE.GetIfEntry((IPHlpAPI.MIB_IFROW)closeableMibIfRow)) {
          LOG.error("Failed to retrieve data for interface {}, {}", Integer.valueOf(queryNetworkInterface().getIndex()), getName());
          boolean bool = false;
          closeableMibIfRow.close();
          return bool;
        } 
        this.ifType = closeableMibIfRow.dwType;
        this.bytesSent = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwOutOctets);
        this.bytesRecv = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwInOctets);
        this.packetsSent = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwOutUcastPkts);
        this.packetsRecv = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwInUcastPkts);
        this.outErrors = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwOutErrors);
        this.inErrors = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwInErrors);
        this.collisions = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwOutDiscards);
        this.inDrops = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwInDiscards);
        this.speed = ParseUtil.unsignedIntToLong(closeableMibIfRow.dwSpeed);
        this.ifAlias = "";
        this.ifOperStatus = NetworkIF.IfOperStatus.UNKNOWN;
        closeableMibIfRow.close();
      } catch (Throwable throwable) {
        try {
          closeableMibIfRow.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
    this.timeStamp = System.currentTimeMillis();
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */