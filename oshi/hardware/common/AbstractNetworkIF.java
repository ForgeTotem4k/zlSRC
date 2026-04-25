package oshi.hardware.common;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;
import oshi.util.FileUtil;
import oshi.util.FormatUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractNetworkIF implements NetworkIF {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkIF.class);
  
  private static final String OSHI_VM_MAC_ADDR_PROPERTIES = "oshi.vmmacaddr.properties";
  
  private NetworkInterface networkInterface;
  
  private String name;
  
  private String displayName;
  
  private int index;
  
  private long mtu;
  
  private String mac;
  
  private String[] ipv4;
  
  private Short[] subnetMasks;
  
  private String[] ipv6;
  
  private Short[] prefixLengths;
  
  private final Supplier<Properties> vmMacAddrProps = Memoizer.memoize(AbstractNetworkIF::queryVmMacAddrProps);
  
  protected AbstractNetworkIF(NetworkInterface paramNetworkInterface) throws InstantiationException {
    this(paramNetworkInterface, paramNetworkInterface.getDisplayName());
  }
  
  protected AbstractNetworkIF(NetworkInterface paramNetworkInterface, String paramString) throws InstantiationException {
    this.networkInterface = paramNetworkInterface;
    try {
      this.name = this.networkInterface.getName();
      this.displayName = paramString;
      this.index = this.networkInterface.getIndex();
      this.mtu = ParseUtil.unsignedIntToLong(this.networkInterface.getMTU());
      byte[] arrayOfByte = this.networkInterface.getHardwareAddress();
      if (arrayOfByte != null) {
        ArrayList<String> arrayList = new ArrayList(6);
        for (byte b : arrayOfByte) {
          arrayList.add(String.format(Locale.ROOT, "%02x", new Object[] { Byte.valueOf(b) }));
        } 
        this.mac = String.join(":", (Iterable)arrayList);
      } else {
        this.mac = "unknown";
      } 
      ArrayList<String> arrayList1 = new ArrayList();
      ArrayList<Short> arrayList2 = new ArrayList();
      ArrayList<String> arrayList3 = new ArrayList();
      ArrayList<Short> arrayList4 = new ArrayList();
      for (InterfaceAddress interfaceAddress : this.networkInterface.getInterfaceAddresses()) {
        InetAddress inetAddress = interfaceAddress.getAddress();
        if (inetAddress.getHostAddress().length() > 0) {
          if (inetAddress.getHostAddress().contains(":")) {
            arrayList3.add(inetAddress.getHostAddress().split("%")[0]);
            arrayList4.add(Short.valueOf(interfaceAddress.getNetworkPrefixLength()));
            continue;
          } 
          arrayList1.add(inetAddress.getHostAddress());
          arrayList2.add(Short.valueOf(interfaceAddress.getNetworkPrefixLength()));
        } 
      } 
      this.ipv4 = arrayList1.<String>toArray(new String[0]);
      this.subnetMasks = arrayList2.<Short>toArray(new Short[0]);
      this.ipv6 = arrayList3.<String>toArray(new String[0]);
      this.prefixLengths = arrayList4.<Short>toArray(new Short[0]);
    } catch (SocketException socketException) {
      throw new InstantiationException(socketException.getMessage());
    } 
  }
  
  protected static List<NetworkInterface> getNetworkInterfaces(boolean paramBoolean) {
    List<NetworkInterface> list = getAllNetworkInterfaces();
    return paramBoolean ? list : (List<NetworkInterface>)getAllNetworkInterfaces().stream().parallel().filter(paramNetworkInterface -> !isLocalInterface(paramNetworkInterface)).collect(Collectors.toList());
  }
  
  private static List<NetworkInterface> getAllNetworkInterfaces() {
    try {
      Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
      return (enumeration == null) ? Collections.<NetworkInterface>emptyList() : Collections.<NetworkInterface>list(enumeration);
    } catch (SocketException socketException) {
      LOG.error("Socket exception when retrieving interfaces: {}", socketException.getMessage());
      return Collections.emptyList();
    } 
  }
  
  private static boolean isLocalInterface(NetworkInterface paramNetworkInterface) {
    try {
      return (paramNetworkInterface.getHardwareAddress() == null);
    } catch (SocketException socketException) {
      LOG.error("Socket exception when retrieving interface information for {}: {}", paramNetworkInterface, socketException.getMessage());
      return false;
    } 
  }
  
  public NetworkInterface queryNetworkInterface() {
    return this.networkInterface;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getIndex() {
    return this.index;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public long getMTU() {
    return this.mtu;
  }
  
  public String getMacaddr() {
    return this.mac;
  }
  
  public String[] getIPv4addr() {
    return Arrays.<String>copyOf(this.ipv4, this.ipv4.length);
  }
  
  public Short[] getSubnetMasks() {
    return Arrays.<Short>copyOf(this.subnetMasks, this.subnetMasks.length);
  }
  
  public String[] getIPv6addr() {
    return Arrays.<String>copyOf(this.ipv6, this.ipv6.length);
  }
  
  public Short[] getPrefixLengths() {
    return Arrays.<Short>copyOf(this.prefixLengths, this.prefixLengths.length);
  }
  
  public boolean isKnownVmMacAddr() {
    String str = (getMacaddr().length() > 7) ? getMacaddr().substring(0, 8) : getMacaddr();
    return ((Properties)this.vmMacAddrProps.get()).containsKey(str.toUpperCase(Locale.ROOT));
  }
  
  private static Properties queryVmMacAddrProps() {
    return FileUtil.readPropertiesFromFilename("oshi.vmmacaddr.properties");
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Name: ").append(getName());
    if (!getName().equals(getDisplayName()))
      stringBuilder.append(" (").append(getDisplayName()).append(")"); 
    if (!getIfAlias().isEmpty())
      stringBuilder.append(" [IfAlias=").append(getIfAlias()).append("]"); 
    stringBuilder.append("\n");
    stringBuilder.append("  MAC Address: ").append(getMacaddr()).append("\n");
    stringBuilder.append("  MTU: ").append(getMTU()).append(", ").append("Speed: ").append(getSpeed()).append("\n");
    String[] arrayOfString1 = getIPv4addr();
    if (this.ipv4.length == this.subnetMasks.length)
      for (byte b = 0; b < this.subnetMasks.length; b++)
        arrayOfString1[b] = arrayOfString1[b] + "/" + this.subnetMasks[b];  
    stringBuilder.append("  IPv4: ").append(Arrays.toString((Object[])arrayOfString1)).append("\n");
    String[] arrayOfString2 = getIPv6addr();
    if (this.ipv6.length == this.prefixLengths.length)
      for (byte b = 0; b < this.prefixLengths.length; b++)
        arrayOfString2[b] = arrayOfString2[b] + "/" + this.prefixLengths[b];  
    stringBuilder.append("  IPv6: ").append(Arrays.toString((Object[])arrayOfString2)).append("\n");
    stringBuilder.append("  Traffic: received ").append(getPacketsRecv()).append(" packets/").append(FormatUtil.formatBytes(getBytesRecv())).append(" (" + getInErrors() + " err, ").append(getInDrops() + " drop);");
    stringBuilder.append(" transmitted ").append(getPacketsSent()).append(" packets/").append(FormatUtil.formatBytes(getBytesSent())).append(" (" + getOutErrors() + " err, ").append(getCollisions() + " coll);");
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\common\AbstractNetworkIF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */