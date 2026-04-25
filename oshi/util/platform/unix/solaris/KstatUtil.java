package oshi.util.platform.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.solaris.Kstat2;
import com.sun.jna.platform.unix.solaris.Kstat2StatusException;
import com.sun.jna.platform.unix.solaris.LibKstat;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

@ThreadSafe
public final class KstatUtil {
  private static final Logger LOG = LoggerFactory.getLogger(KstatUtil.class);
  
  private static final Lock CHAIN = new ReentrantLock();
  
  @GuardedBy("CHAIN")
  private static LibKstat.KstatCtl kstatCtl = null;
  
  public static synchronized KstatChain openChain() {
    CHAIN.lock();
    if (kstatCtl == null)
      kstatCtl = LibKstat.INSTANCE.kstat_open(); 
    return new KstatChain(kstatCtl);
  }
  
  public static String dataLookupString(LibKstat.Kstat paramKstat, String paramString) {
    if (paramKstat.ks_type != 1 && paramKstat.ks_type != 4)
      throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat."); 
    Pointer pointer = LibKstat.INSTANCE.kstat_data_lookup(paramKstat, paramString);
    if (pointer == null) {
      LOG.debug("Failed to lookup kstat value for key {}", paramString);
      return "";
    } 
    LibKstat.KstatNamed kstatNamed = new LibKstat.KstatNamed(pointer);
    switch (kstatNamed.data_type) {
      case 0:
        return Native.toString(kstatNamed.value.charc, StandardCharsets.UTF_8);
      case 1:
        return Integer.toString(kstatNamed.value.i32);
      case 2:
        return FormatUtil.toUnsignedString(kstatNamed.value.ui32);
      case 3:
        return Long.toString(kstatNamed.value.i64);
      case 4:
        return FormatUtil.toUnsignedString(kstatNamed.value.ui64);
      case 9:
        return kstatNamed.value.str.addr.getString(0L);
    } 
    LOG.error("Unimplemented kstat data type {}", Byte.valueOf(kstatNamed.data_type));
    return "";
  }
  
  public static long dataLookupLong(LibKstat.Kstat paramKstat, String paramString) {
    if (paramKstat.ks_type != 1 && paramKstat.ks_type != 4)
      throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat."); 
    Pointer pointer = LibKstat.INSTANCE.kstat_data_lookup(paramKstat, paramString);
    if (pointer == null) {
      if (LOG.isDebugEnabled())
        LOG.debug("Failed lo lookup kstat value on {}:{}:{} for key {}", new Object[] { Native.toString(paramKstat.ks_module, StandardCharsets.US_ASCII), Integer.valueOf(paramKstat.ks_instance), Native.toString(paramKstat.ks_name, StandardCharsets.US_ASCII), paramString }); 
      return 0L;
    } 
    LibKstat.KstatNamed kstatNamed = new LibKstat.KstatNamed(pointer);
    switch (kstatNamed.data_type) {
      case 1:
        return kstatNamed.value.i32;
      case 2:
        return FormatUtil.getUnsignedInt(kstatNamed.value.ui32);
      case 3:
        return kstatNamed.value.i64;
      case 4:
        return kstatNamed.value.ui64;
    } 
    LOG.error("Unimplemented or non-numeric kstat data type {}", Byte.valueOf(kstatNamed.data_type));
    return 0L;
  }
  
  public static Object[] queryKstat2(String paramString, String... paramVarArgs) {
    if (!SolarisOperatingSystem.HAS_KSTAT2)
      throw new UnsupportedOperationException("Kstat2 requires Solaris 11.4+. Use SolarisOperatingSystem#HAS_KSTAT2 to test this."); 
    Object[] arrayOfObject = new Object[paramVarArgs.length];
    Kstat2.Kstat2MatcherList kstat2MatcherList = new Kstat2.Kstat2MatcherList();
    CHAIN.lock();
    try {
      kstat2MatcherList.addMatcher(0, paramString);
      Kstat2.Kstat2Handle kstat2Handle = new Kstat2.Kstat2Handle();
      try {
        Kstat2.Kstat2Map kstat2Map = kstat2Handle.lookupMap(paramString);
        for (byte b = 0; b < paramVarArgs.length; b++)
          arrayOfObject[b] = kstat2Map.getValue(paramVarArgs[b]); 
      } finally {
        kstat2Handle.close();
      } 
    } catch (Kstat2StatusException kstat2StatusException) {
      LOG.debug("Failed to get stats on {} for names {}: {}", new Object[] { paramString, Arrays.toString((Object[])paramVarArgs), kstat2StatusException.getMessage() });
    } finally {
      CHAIN.unlock();
      kstat2MatcherList.free();
    } 
    return arrayOfObject;
  }
  
  public static List<Object[]> queryKstat2List(String paramString1, String paramString2, String... paramVarArgs) {
    if (!SolarisOperatingSystem.HAS_KSTAT2)
      throw new UnsupportedOperationException("Kstat2 requires Solaris 11.4+. Use SolarisOperatingSystem#HAS_KSTAT2 to test this."); 
    ArrayList<Object[]> arrayList = new ArrayList();
    byte b = 0;
    Kstat2.Kstat2MatcherList kstat2MatcherList = new Kstat2.Kstat2MatcherList();
    CHAIN.lock();
    try {
      kstat2MatcherList.addMatcher(1, paramString1 + "*" + paramString2);
      Kstat2.Kstat2Handle kstat2Handle = new Kstat2.Kstat2Handle();
      try {
        for (b = 0; b < Integer.MAX_VALUE; b++) {
          Object[] arrayOfObject = new Object[paramVarArgs.length];
          Kstat2.Kstat2Map kstat2Map = kstat2Handle.lookupMap(paramString1 + b + paramString2);
          for (byte b1 = 0; b1 < paramVarArgs.length; b1++)
            arrayOfObject[b1] = kstat2Map.getValue(paramVarArgs[b1]); 
          arrayList.add(arrayOfObject);
        } 
      } finally {
        kstat2Handle.close();
      } 
    } catch (Kstat2StatusException kstat2StatusException) {
      LOG.debug("Failed to get stats on {}{}{} for names {}: {}", new Object[] { paramString1, Integer.valueOf(b), paramString2, Arrays.toString((Object[])paramVarArgs), kstat2StatusException.getMessage() });
    } finally {
      CHAIN.unlock();
      kstat2MatcherList.free();
    } 
    return arrayList;
  }
  
  public static final class KstatChain implements AutoCloseable {
    private final LibKstat.KstatCtl localCtlRef;
    
    private KstatChain(LibKstat.KstatCtl param1KstatCtl) {
      this.localCtlRef = param1KstatCtl;
      update();
    }
    
    @GuardedBy("CHAIN")
    public boolean read(LibKstat.Kstat param1Kstat) {
      byte b = 0;
      while (0 > LibKstat.INSTANCE.kstat_read(this.localCtlRef, param1Kstat, null)) {
        if (11 != Native.getLastError() || 5 <= ++b) {
          if (KstatUtil.LOG.isDebugEnabled())
            KstatUtil.LOG.debug("Failed to read kstat {}:{}:{}", new Object[] { Native.toString(param1Kstat.ks_module, StandardCharsets.US_ASCII), Integer.valueOf(param1Kstat.ks_instance), Native.toString(param1Kstat.ks_name, StandardCharsets.US_ASCII) }); 
          return false;
        } 
        Util.sleep((8 << b));
      } 
      return true;
    }
    
    @GuardedBy("CHAIN")
    public LibKstat.Kstat lookup(String param1String1, int param1Int, String param1String2) {
      return LibKstat.INSTANCE.kstat_lookup(this.localCtlRef, param1String1, param1Int, param1String2);
    }
    
    @GuardedBy("CHAIN")
    public List<LibKstat.Kstat> lookupAll(String param1String1, int param1Int, String param1String2) {
      ArrayList<LibKstat.Kstat> arrayList = new ArrayList();
      for (LibKstat.Kstat kstat = LibKstat.INSTANCE.kstat_lookup(this.localCtlRef, param1String1, param1Int, param1String2); kstat != null; kstat = kstat.next()) {
        if ((param1String1 == null || param1String1.equals(Native.toString(kstat.ks_module, StandardCharsets.US_ASCII))) && (param1Int < 0 || param1Int == kstat.ks_instance) && (param1String2 == null || param1String2.equals(Native.toString(kstat.ks_name, StandardCharsets.US_ASCII))))
          arrayList.add(kstat); 
      } 
      return arrayList;
    }
    
    @GuardedBy("CHAIN")
    public int update() {
      return LibKstat.INSTANCE.kstat_chain_update(this.localCtlRef);
    }
    
    public void close() {
      KstatUtil.CHAIN.unlock();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platfor\\unix\solaris\KstatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */