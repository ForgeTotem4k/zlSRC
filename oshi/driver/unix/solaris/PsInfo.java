package oshi.driver.unix.solaris;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Quartet;

@ThreadSafe
public final class PsInfo {
  private static final Logger LOG = LoggerFactory.getLogger(PsInfo.class);
  
  private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
  
  private static final long PAGE_SIZE = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("pagesize"), 4096L);
  
  public static SolarisLibc.SolarisPsInfo queryPsInfo(int paramInt) {
    return new SolarisLibc.SolarisPsInfo(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/psinfo", new Object[] { Integer.valueOf(paramInt) })));
  }
  
  public static SolarisLibc.SolarisLwpsInfo queryLwpsInfo(int paramInt1, int paramInt2) {
    return new SolarisLibc.SolarisLwpsInfo(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/lwp/%d/lwpsinfo", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })));
  }
  
  public static SolarisLibc.SolarisPrUsage queryPrUsage(int paramInt) {
    return new SolarisLibc.SolarisPrUsage(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/usage", new Object[] { Integer.valueOf(paramInt) })));
  }
  
  public static SolarisLibc.SolarisPrUsage queryPrUsage(int paramInt1, int paramInt2) {
    return new SolarisLibc.SolarisPrUsage(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/lwp/%d/usage", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })));
  }
  
  public static Quartet<Integer, Long, Long, Byte> queryArgsEnvAddrs(int paramInt, SolarisLibc.SolarisPsInfo paramSolarisPsInfo) {
    if (paramSolarisPsInfo != null) {
      int i = paramSolarisPsInfo.pr_argc;
      if (i > 0) {
        long l1 = Pointer.nativeValue(paramSolarisPsInfo.pr_argv);
        long l2 = Pointer.nativeValue(paramSolarisPsInfo.pr_envp);
        byte b = paramSolarisPsInfo.pr_dmodel;
        if ((b * 4) == (l2 - l1) / (i + 1))
          return new Quartet(Integer.valueOf(i), Long.valueOf(l1), Long.valueOf(l2), Byte.valueOf(b)); 
        LOG.trace("Failed data model and offset increment sanity check: dm={} diff={}", Byte.valueOf(b), Long.valueOf(l2 - l1));
        return null;
      } 
      LOG.trace("Failed argc sanity check: argc={}", Integer.valueOf(i));
      return null;
    } 
    LOG.trace("Failed to read psinfo file for pid: {} ", Integer.valueOf(paramInt));
    return null;
  }
  
  public static Pair<List<String>, Map<String, String>> queryArgsEnv(int paramInt, SolarisLibc.SolarisPsInfo paramSolarisPsInfo) {
    ArrayList<String> arrayList = new ArrayList();
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    Quartet<Integer, Long, Long, Byte> quartet = queryArgsEnvAddrs(paramInt, paramSolarisPsInfo);
    if (quartet != null) {
      String str = "/proc/" + paramInt + "/as";
      int i = LIBC.open(str, 0);
      if (i < 0) {
        LOG.trace("No permission to read file: {} ", str);
        return new Pair(arrayList, linkedHashMap);
      } 
      try {
        int j = ((Integer)quartet.getA()).intValue();
        long l1 = ((Long)quartet.getB()).longValue();
        long l2 = ((Long)quartet.getC()).longValue();
        long l3 = ((Byte)quartet.getD()).byteValue() * 4L;
        long l4 = 0L;
        Memory memory = new Memory(PAGE_SIZE * 2L);
        try {
          LibCAPI.size_t size_t = new LibCAPI.size_t(memory.size());
          long[] arrayOfLong = new long[j];
          long l5 = l1;
          for (byte b1 = 0; b1 < j; b1++) {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, l5);
            arrayOfLong[b1] = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l5 - l4, l3);
            l5 += l3;
          } 
          ArrayList<Long> arrayList1 = new ArrayList();
          l5 = l2;
          long l6 = 0L;
          char c = 'Ǵ';
          do {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, l5);
            l6 = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l5 - l4, l3);
            if (l6 != 0L)
              arrayList1.add(Long.valueOf(l6)); 
            l5 += l3;
          } while (l6 != 0L && --c > '\000');
          for (byte b2 = 0; b2 < arrayOfLong.length && arrayOfLong[b2] != 0L; b2++) {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, arrayOfLong[b2]);
            if (l4 != 0L) {
              String str1 = memory.getString(arrayOfLong[b2] - l4);
              if (!str1.isEmpty())
                arrayList.add(str1); 
            } 
          } 
          for (Long long_ : arrayList1) {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, long_.longValue());
            if (l4 != 0L) {
              String str1 = memory.getString(long_.longValue() - l4);
              int k = str1.indexOf('=');
              if (k > 0)
                linkedHashMap.put(str1.substring(0, k), str1.substring(k + 1)); 
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
      } finally {
        LIBC.close(i);
      } 
    } 
    return new Pair(arrayList, linkedHashMap);
  }
  
  private static long conditionallyReadBufferFromStartOfPage(int paramInt, Memory paramMemory, LibCAPI.size_t paramsize_t, long paramLong1, long paramLong2) {
    if (paramLong2 < paramLong1 || paramLong2 - paramLong1 > PAGE_SIZE) {
      long l = Math.floorDiv(paramLong2, PAGE_SIZE) * PAGE_SIZE;
      LibCAPI.ssize_t ssize_t = LIBC.pread(paramInt, (Pointer)paramMemory, paramsize_t, new NativeLong(l));
      if (ssize_t.longValue() < PAGE_SIZE) {
        LOG.debug("Failed to read page from address space: {} bytes read", Long.valueOf(ssize_t.longValue()));
        return 0L;
      } 
      return l;
    } 
    return paramLong1;
  }
  
  private static long getOffsetFromBuffer(Memory paramMemory, long paramLong1, long paramLong2) {
    return (paramLong2 == 8L) ? paramMemory.getLong(paramLong1) : paramMemory.getInt(paramLong1);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\PsInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */