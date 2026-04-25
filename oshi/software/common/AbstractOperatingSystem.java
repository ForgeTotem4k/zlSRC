package oshi.software.common;

import com.sun.jna.Platform;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.tuples.Pair;

public abstract class AbstractOperatingSystem implements OperatingSystem {
  protected static final boolean USE_WHO_COMMAND = GlobalConfig.get("oshi.os.unix.whoCommand", false);
  
  private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
  
  private final Supplier<Pair<String, OperatingSystem.OSVersionInfo>> familyVersionInfo = Memoizer.memoize(this::queryFamilyVersionInfo);
  
  private final Supplier<Integer> bitness = Memoizer.memoize(this::queryPlatformBitness);
  
  public String getManufacturer() {
    return this.manufacturer.get();
  }
  
  protected abstract String queryManufacturer();
  
  public String getFamily() {
    return (String)((Pair)this.familyVersionInfo.get()).getA();
  }
  
  public OperatingSystem.OSVersionInfo getVersionInfo() {
    return (OperatingSystem.OSVersionInfo)((Pair)this.familyVersionInfo.get()).getB();
  }
  
  protected abstract Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo();
  
  public int getBitness() {
    return ((Integer)this.bitness.get()).intValue();
  }
  
  private int queryPlatformBitness() {
    if (Platform.is64Bit())
      return 64; 
    byte b = System.getProperty("os.arch").contains("64") ? 64 : 32;
    return queryBitness(b);
  }
  
  protected abstract int queryBitness(int paramInt);
  
  public List<OSProcess> getProcesses(Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt) {
    return (List<OSProcess>)queryAllProcesses().stream().filter((paramPredicate == null) ? OperatingSystem.ProcessFiltering.ALL_PROCESSES : paramPredicate).sorted((paramComparator == null) ? OperatingSystem.ProcessSorting.NO_SORTING : paramComparator).limit((paramInt > 0) ? paramInt : Long.MAX_VALUE).collect(Collectors.toList());
  }
  
  protected abstract List<OSProcess> queryAllProcesses();
  
  public List<OSProcess> getChildProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2) {
    List<OSProcess> list = queryChildProcesses(paramInt1);
    OSProcess oSProcess = list.stream().filter(paramOSProcess -> (paramOSProcess.getProcessID() == paramInt)).findAny().orElse(null);
    long l = (oSProcess == null) ? 0L : oSProcess.getStartTime();
    return (List<OSProcess>)queryChildProcesses(paramInt1).stream().filter((paramPredicate == null) ? OperatingSystem.ProcessFiltering.ALL_PROCESSES : paramPredicate).filter(paramOSProcess -> (paramOSProcess.getProcessID() != paramInt && paramOSProcess.getStartTime() >= paramLong)).sorted((paramComparator == null) ? OperatingSystem.ProcessSorting.NO_SORTING : paramComparator).limit((paramInt2 > 0) ? paramInt2 : Long.MAX_VALUE).collect(Collectors.toList());
  }
  
  protected abstract List<OSProcess> queryChildProcesses(int paramInt);
  
  public List<OSProcess> getDescendantProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2) {
    List<OSProcess> list = queryDescendantProcesses(paramInt1);
    OSProcess oSProcess = list.stream().filter(paramOSProcess -> (paramOSProcess.getProcessID() == paramInt)).findAny().orElse(null);
    long l = (oSProcess == null) ? 0L : oSProcess.getStartTime();
    return (List<OSProcess>)queryDescendantProcesses(paramInt1).stream().filter((paramPredicate == null) ? OperatingSystem.ProcessFiltering.ALL_PROCESSES : paramPredicate).filter(paramOSProcess -> (paramOSProcess.getProcessID() != paramInt && paramOSProcess.getStartTime() >= paramLong)).sorted((paramComparator == null) ? OperatingSystem.ProcessSorting.NO_SORTING : paramComparator).limit((paramInt2 > 0) ? paramInt2 : Long.MAX_VALUE).collect(Collectors.toList());
  }
  
  protected abstract List<OSProcess> queryDescendantProcesses(int paramInt);
  
  protected static Set<Integer> getChildrenOrDescendants(Collection<OSProcess> paramCollection, int paramInt, boolean paramBoolean) {
    Map<Integer, Integer> map = (Map)paramCollection.stream().collect(Collectors.toMap(OSProcess::getProcessID, OSProcess::getParentProcessID));
    return getChildrenOrDescendants(map, paramInt, paramBoolean);
  }
  
  protected static Set<Integer> getChildrenOrDescendants(Map<Integer, Integer> paramMap, int paramInt, boolean paramBoolean) {
    HashSet<Integer> hashSet = new HashSet();
    hashSet.add(Integer.valueOf(paramInt));
    ArrayDeque<Integer> arrayDeque = new ArrayDeque();
    arrayDeque.add(Integer.valueOf(paramInt));
    do {
      Iterator<Integer> iterator = getChildren(paramMap, ((Integer)arrayDeque.poll()).intValue()).iterator();
      while (iterator.hasNext()) {
        int i = ((Integer)iterator.next()).intValue();
        if (!hashSet.contains(Integer.valueOf(i))) {
          hashSet.add(Integer.valueOf(i));
          arrayDeque.add(Integer.valueOf(i));
        } 
      } 
    } while (paramBoolean && !arrayDeque.isEmpty());
    return hashSet;
  }
  
  private static Set<Integer> getChildren(Map<Integer, Integer> paramMap, int paramInt) {
    return (Set<Integer>)paramMap.entrySet().stream().filter(paramEntry -> (((Integer)paramEntry.getValue()).equals(Integer.valueOf(paramInt)) && !((Integer)paramEntry.getKey()).equals(Integer.valueOf(paramInt)))).map(Map.Entry::getKey).collect(Collectors.toSet());
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getManufacturer()).append(' ').append(getFamily()).append(' ').append(getVersionInfo());
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */