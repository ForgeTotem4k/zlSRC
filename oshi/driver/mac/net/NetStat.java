package oshi.driver.mac.net;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.unix.LibCAPI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;

@ThreadSafe
public final class NetStat {
  private static final Logger LOG = LoggerFactory.getLogger(NetStat.class);
  
  private static final int CTL_NET = 4;
  
  private static final int PF_ROUTE = 17;
  
  private static final int NET_RT_IFLIST2 = 6;
  
  private static final int RTM_IFINFO2 = 18;
  
  public static Map<Integer, IFdata> queryIFdata(int paramInt) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    int[] arrayOfInt = { 4, 17, 0, 0, 6, 0 };
    ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference();
    try {
      if (0 != SystemB.INSTANCE.sysctl(arrayOfInt, 6, null, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
        LOG.error("Didn't get buffer length for IFLIST2");
        HashMap<Object, Object> hashMap1 = hashMap;
        closeableSizeTByReference.close();
        return (Map)hashMap1;
      } 
      Memory memory = new Memory(closeableSizeTByReference.longValue());
      try {
        if (0 != SystemB.INSTANCE.sysctl(arrayOfInt, 6, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          LOG.error("Didn't get buffer for IFLIST2");
          HashMap<Object, Object> hashMap1 = hashMap;
          memory.close();
          closeableSizeTByReference.close();
          return (Map)hashMap1;
        } 
        long l = System.currentTimeMillis();
        int i = (int)(memory.size() - (new SystemB.IFmsgHdr()).size());
        int j = 0;
        while (j < i) {
          Pointer pointer = memory.share(j);
          SystemB.IFmsgHdr iFmsgHdr = new SystemB.IFmsgHdr(pointer);
          iFmsgHdr.read();
          j += iFmsgHdr.ifm_msglen;
          if (iFmsgHdr.ifm_type == 18) {
            SystemB.IFmsgHdr2 iFmsgHdr2 = new SystemB.IFmsgHdr2(pointer);
            iFmsgHdr2.read();
            if (paramInt < 0 || paramInt == iFmsgHdr2.ifm_index) {
              hashMap.put(Integer.valueOf(iFmsgHdr2.ifm_index), new IFdata(0xFF & iFmsgHdr2.ifm_data.ifi_type, iFmsgHdr2.ifm_data.ifi_opackets, iFmsgHdr2.ifm_data.ifi_ipackets, iFmsgHdr2.ifm_data.ifi_obytes, iFmsgHdr2.ifm_data.ifi_ibytes, iFmsgHdr2.ifm_data.ifi_oerrors, iFmsgHdr2.ifm_data.ifi_ierrors, iFmsgHdr2.ifm_data.ifi_collisions, iFmsgHdr2.ifm_data.ifi_iqdrops, iFmsgHdr2.ifm_data.ifi_baudrate, l));
              if (paramInt >= 0) {
                HashMap<Object, Object> hashMap1 = hashMap;
                memory.close();
                closeableSizeTByReference.close();
                return (Map)hashMap1;
              } 
            } 
          } 
        } 
        memory.close();
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      closeableSizeTByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableSizeTByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return (Map)hashMap;
  }
  
  @Immutable
  public static class IFdata {
    private final int ifType;
    
    private final long oPackets;
    
    private final long iPackets;
    
    private final long oBytes;
    
    private final long iBytes;
    
    private final long oErrors;
    
    private final long iErrors;
    
    private final long collisions;
    
    private final long iDrops;
    
    private final long speed;
    
    private final long timeStamp;
    
    IFdata(int param1Int, long param1Long1, long param1Long2, long param1Long3, long param1Long4, long param1Long5, long param1Long6, long param1Long7, long param1Long8, long param1Long9, long param1Long10) {
      this.ifType = param1Int;
      this.oPackets = param1Long1 & 0xFFFFFFFFL;
      this.iPackets = param1Long2 & 0xFFFFFFFFL;
      this.oBytes = param1Long3 & 0xFFFFFFFFL;
      this.iBytes = param1Long4 & 0xFFFFFFFFL;
      this.oErrors = param1Long5 & 0xFFFFFFFFL;
      this.iErrors = param1Long6 & 0xFFFFFFFFL;
      this.collisions = param1Long7 & 0xFFFFFFFFL;
      this.iDrops = param1Long8 & 0xFFFFFFFFL;
      this.speed = param1Long9 & 0xFFFFFFFFL;
      this.timeStamp = param1Long10;
    }
    
    public int getIfType() {
      return this.ifType;
    }
    
    public long getOPackets() {
      return this.oPackets;
    }
    
    public long getIPackets() {
      return this.iPackets;
    }
    
    public long getOBytes() {
      return this.oBytes;
    }
    
    public long getIBytes() {
      return this.iBytes;
    }
    
    public long getOErrors() {
      return this.oErrors;
    }
    
    public long getIErrors() {
      return this.iErrors;
    }
    
    public long getCollisions() {
      return this.collisions;
    }
    
    public long getIDrops() {
      return this.iDrops;
    }
    
    public long getSpeed() {
      return this.speed;
    }
    
    public long getTimeStamp() {
      return this.timeStamp;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\mac\net\NetStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */