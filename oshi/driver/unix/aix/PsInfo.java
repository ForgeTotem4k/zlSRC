package oshi.driver.unix.aix;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.AixLibc;
import oshi.util.FileUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class PsInfo {
  private static final Logger LOG = LoggerFactory.getLogger(PsInfo.class);
  
  private static final AixLibc LIBC = AixLibc.INSTANCE;
  
  private static final long PAGE_SIZE = 4096L;
  
  public static AixLibc.AixPsInfo queryPsInfo(int paramInt) {
    return new AixLibc.AixPsInfo(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/psinfo", new Object[] { Integer.valueOf(paramInt) })));
  }
  
  public static AixLibc.AixLwpsInfo queryLwpsInfo(int paramInt1, int paramInt2) {
    return new AixLibc.AixLwpsInfo(FileUtil.readAllBytesAsBuffer(String.format(Locale.ROOT, "/proc/%d/lwp/%d/lwpsinfo", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })));
  }
  
  public static Triplet<Integer, Long, Long> queryArgsEnvAddrs(int paramInt, AixLibc.AixPsInfo paramAixPsInfo) {
    if (paramAixPsInfo != null) {
      int i = paramAixPsInfo.pr_argc;
      if (i > 0) {
        long l1 = paramAixPsInfo.pr_argv;
        long l2 = paramAixPsInfo.pr_envp;
        return new Triplet(Integer.valueOf(i), Long.valueOf(l1), Long.valueOf(l2));
      } 
      LOG.trace("Failed argc sanity check: argc={}", Integer.valueOf(i));
      return null;
    } 
    LOG.trace("Failed to read psinfo file for pid: {} ", Integer.valueOf(paramInt));
    return null;
  }
  
  public static Pair<List<String>, Map<String, String>> queryArgsEnv(int paramInt, AixLibc.AixPsInfo paramAixPsInfo) {
    ArrayList<String> arrayList = new ArrayList();
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    Triplet<Integer, Long, Long> triplet = queryArgsEnvAddrs(paramInt, paramAixPsInfo);
    if (triplet != null) {
      String str = "/proc/" + paramInt + "/as";
      int i = LIBC.open(str, 0);
      if (i < 0) {
        LOG.trace("No permission to read file: {} ", str);
        return new Pair(arrayList, linkedHashMap);
      } 
      try {
        long l3;
        int j = ((Integer)triplet.getA()).intValue();
        long l1 = ((Long)triplet.getB()).longValue();
        long l2 = ((Long)triplet.getC()).longValue();
        Path path = Paths.get("/proc/" + paramInt + "/status", new String[0]);
        try {
          byte[] arrayOfByte = Files.readAllBytes(path);
          if (arrayOfByte[17] == 1) {
            l3 = 8L;
          } else {
            l3 = 4L;
          } 
        } catch (IOException iOException) {
          return new Pair(arrayList, linkedHashMap);
        } 
        Memory memory = new Memory(8192L);
        try {
          LibCAPI.size_t size_t = new LibCAPI.size_t(memory.size());
          long l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, 0L, l1);
          long[] arrayOfLong = new long[j];
          long l5 = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l1 - l4, l3);
          if (l5 > 0L)
            for (byte b1 = 0; b1 < j; b1++) {
              long l = l5 + b1 * l3;
              l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, l);
              arrayOfLong[b1] = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l - l4, l3);
            }  
          l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, l2);
          ArrayList<Long> arrayList1 = new ArrayList();
          long l6 = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l2 - l4, l3);
          char c = 'Ǵ';
          long l7;
          for (l7 = l6; l6 != 0L && --c > '\000'; l7 += l3) {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, l7);
            long l = (l4 == 0L) ? 0L : getOffsetFromBuffer(memory, l7 - l4, l3);
            if (l != 0L)
              arrayList1.add(Long.valueOf(l)); 
          } 
          for (byte b = 0; b < arrayOfLong.length && arrayOfLong[b] != 0L; b++) {
            l4 = conditionallyReadBufferFromStartOfPage(i, memory, size_t, l4, arrayOfLong[b]);
            if (l4 != 0L) {
              String str1 = memory.getString(arrayOfLong[b] - l4);
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
    if (paramLong2 < paramLong1 || paramLong2 - paramLong1 > 4096L) {
      long l = Math.floorDiv(paramLong2, 4096L) * 4096L;
      LibCAPI.ssize_t ssize_t = LIBC.pread(paramInt, (Pointer)paramMemory, paramsize_t, new NativeLong(l));
      if (ssize_t.longValue() < 4096L) {
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\aix\PsInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */