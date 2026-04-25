package oshi.software.os;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface InternetProtocolStats {
  TcpStats getTCPv4Stats();
  
  TcpStats getTCPv6Stats();
  
  UdpStats getUDPv4Stats();
  
  UdpStats getUDPv6Stats();
  
  List<IPConnection> getConnections();
  
  @Immutable
  public static final class IPConnection {
    private final String type;
    
    private final byte[] localAddress;
    
    private final int localPort;
    
    private final byte[] foreignAddress;
    
    private final int foreignPort;
    
    private final InternetProtocolStats.TcpState state;
    
    private final int transmitQueue;
    
    private final int receiveQueue;
    
    private int owningProcessId;
    
    public IPConnection(String param1String, byte[] param1ArrayOfbyte1, int param1Int1, byte[] param1ArrayOfbyte2, int param1Int2, InternetProtocolStats.TcpState param1TcpState, int param1Int3, int param1Int4, int param1Int5) {
      this.type = param1String;
      this.localAddress = Arrays.copyOf(param1ArrayOfbyte1, param1ArrayOfbyte1.length);
      this.localPort = param1Int1;
      this.foreignAddress = Arrays.copyOf(param1ArrayOfbyte2, param1ArrayOfbyte2.length);
      this.foreignPort = param1Int2;
      this.state = param1TcpState;
      this.transmitQueue = param1Int3;
      this.receiveQueue = param1Int4;
      this.owningProcessId = param1Int5;
    }
    
    public String getType() {
      return this.type;
    }
    
    public byte[] getLocalAddress() {
      return Arrays.copyOf(this.localAddress, this.localAddress.length);
    }
    
    public int getLocalPort() {
      return this.localPort;
    }
    
    public byte[] getForeignAddress() {
      return Arrays.copyOf(this.foreignAddress, this.foreignAddress.length);
    }
    
    public int getForeignPort() {
      return this.foreignPort;
    }
    
    public InternetProtocolStats.TcpState getState() {
      return this.state;
    }
    
    public int getTransmitQueue() {
      return this.transmitQueue;
    }
    
    public int getReceiveQueue() {
      return this.receiveQueue;
    }
    
    public int getowningProcessId() {
      return this.owningProcessId;
    }
    
    public String toString() {
      String str1 = "*";
      try {
        str1 = InetAddress.getByAddress(this.localAddress).toString();
      } catch (UnknownHostException unknownHostException) {}
      String str2 = "*";
      try {
        str2 = InetAddress.getByAddress(this.foreignAddress).toString();
      } catch (UnknownHostException unknownHostException) {}
      return "IPConnection [type=" + this.type + ", localAddress=" + str1 + ", localPort=" + this.localPort + ", foreignAddress=" + str2 + ", foreignPort=" + this.foreignPort + ", state=" + this.state + ", transmitQueue=" + this.transmitQueue + ", receiveQueue=" + this.receiveQueue + ", owningProcessId=" + this.owningProcessId + "]";
    }
  }
  
  public enum TcpState {
    UNKNOWN, CLOSED, LISTEN, SYN_SENT, SYN_RECV, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT, NONE;
  }
  
  @Immutable
  public static final class UdpStats {
    private final long datagramsSent;
    
    private final long datagramsReceived;
    
    private final long datagramsNoPort;
    
    private final long datagramsReceivedErrors;
    
    public UdpStats(long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.datagramsSent = param1Long1;
      this.datagramsReceived = param1Long2;
      this.datagramsNoPort = param1Long3;
      this.datagramsReceivedErrors = param1Long4;
    }
    
    public long getDatagramsSent() {
      return this.datagramsSent;
    }
    
    public long getDatagramsReceived() {
      return this.datagramsReceived;
    }
    
    public long getDatagramsNoPort() {
      return this.datagramsNoPort;
    }
    
    public long getDatagramsReceivedErrors() {
      return this.datagramsReceivedErrors;
    }
    
    public String toString() {
      return "UdpStats [datagramsSent=" + this.datagramsSent + ", datagramsReceived=" + this.datagramsReceived + ", datagramsNoPort=" + this.datagramsNoPort + ", datagramsReceivedErrors=" + this.datagramsReceivedErrors + "]";
    }
  }
  
  @Immutable
  public static final class TcpStats {
    private final long connectionsEstablished;
    
    private final long connectionsActive;
    
    private final long connectionsPassive;
    
    private final long connectionFailures;
    
    private final long connectionsReset;
    
    private final long segmentsSent;
    
    private final long segmentsReceived;
    
    private final long segmentsRetransmitted;
    
    private final long inErrors;
    
    private final long outResets;
    
    public TcpStats(long param1Long1, long param1Long2, long param1Long3, long param1Long4, long param1Long5, long param1Long6, long param1Long7, long param1Long8, long param1Long9, long param1Long10) {
      this.connectionsEstablished = param1Long1;
      this.connectionsActive = param1Long2;
      this.connectionsPassive = param1Long3;
      this.connectionFailures = param1Long4;
      this.connectionsReset = param1Long5;
      this.segmentsSent = param1Long6;
      this.segmentsReceived = param1Long7;
      this.segmentsRetransmitted = param1Long8;
      this.inErrors = param1Long9;
      this.outResets = param1Long10;
    }
    
    public long getConnectionsEstablished() {
      return this.connectionsEstablished;
    }
    
    public long getConnectionsActive() {
      return this.connectionsActive;
    }
    
    public long getConnectionsPassive() {
      return this.connectionsPassive;
    }
    
    public long getConnectionFailures() {
      return this.connectionFailures;
    }
    
    public long getConnectionsReset() {
      return this.connectionsReset;
    }
    
    public long getSegmentsSent() {
      return this.segmentsSent;
    }
    
    public long getSegmentsReceived() {
      return this.segmentsReceived;
    }
    
    public long getSegmentsRetransmitted() {
      return this.segmentsRetransmitted;
    }
    
    public long getInErrors() {
      return this.inErrors;
    }
    
    public long getOutResets() {
      return this.outResets;
    }
    
    public String toString() {
      return "TcpStats [connectionsEstablished=" + this.connectionsEstablished + ", connectionsActive=" + this.connectionsActive + ", connectionsPassive=" + this.connectionsPassive + ", connectionFailures=" + this.connectionFailures + ", connectionsReset=" + this.connectionsReset + ", segmentsSent=" + this.segmentsSent + ", segmentsReceived=" + this.segmentsReceived + ", segmentsRetransmitted=" + this.segmentsRetransmitted + ", inErrors=" + this.inErrors + ", outResets=" + this.outResets + "]";
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\InternetProtocolStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */