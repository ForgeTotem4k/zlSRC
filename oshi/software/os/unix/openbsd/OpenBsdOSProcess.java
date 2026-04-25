package oshi.software.os.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.unix.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.FstatUtil;

@ThreadSafe
public class OpenBsdOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(OpenBsdOSProcess.class);
  
  static final String PS_THREAD_COLUMNS;
  
  private static final int ARGMAX;
  
  private final OpenBsdOperatingSystem os;
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<List<String>> arguments = Memoizer.memoize(this::queryArguments);
  
  private Supplier<Map<String, String>> environmentVariables = Memoizer.memoize(this::queryEnvironmentVariables);
  
  private String name;
  
  private String path = "";
  
  private String user;
  
  private String userID;
  
  private String group;
  
  private String groupID;
  
  private OSProcess.State state = OSProcess.State.INVALID;
  
  private int parentProcessID;
  
  private int threadCount;
  
  private int priority;
  
  private long virtualSize;
  
  private long residentSetSize;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private long bytesRead;
  
  private long bytesWritten;
  
  private long minorFaults;
  
  private long majorFaults;
  
  private long contextSwitches;
  
  private int bitness;
  
  private String commandLineBackup;
  
  public OpenBsdOSProcess(int paramInt, Map<OpenBsdOperatingSystem.PsKeywords, String> paramMap, OpenBsdOperatingSystem paramOpenBsdOperatingSystem) {
    super(paramInt);
    this.os = paramOpenBsdOperatingSystem;
    this.bitness = Native.LONG_SIZE * 8;
    updateThreadCount();
    updateAttributes(paramMap);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public String getCommandLine() {
    return this.commandLine.get();
  }
  
  private String queryCommandLine() {
    String str = String.join(" ", (Iterable)getArguments());
    return str.isEmpty() ? this.commandLineBackup : str;
  }
  
  public List<String> getArguments() {
    return this.arguments.get();
  }
  
  private List<String> queryArguments() {
    if (ARGMAX > 0) {
      int[] arrayOfInt = new int[4];
      arrayOfInt[0] = 1;
      arrayOfInt[1] = 55;
      arrayOfInt[2] = getProcessID();
      arrayOfInt[3] = 1;
      Memory memory = new Memory(ARGMAX);
      try {
        ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(ARGMAX);
        try {
          if (OpenBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO) == 0) {
            ArrayList<String> arrayList = new ArrayList();
            long l1 = 0L;
            long l2 = Pointer.nativeValue((Pointer)memory);
            long l3 = l2 + closeableSizeTByReference.getValue().longValue();
            long l4;
            for (l4 = Pointer.nativeValue(memory.getPointer(l1)); l4 > l2 && l4 < l3; l4 = Pointer.nativeValue(memory.getPointer(l1))) {
              arrayList.add(memory.getString(l4 - l2));
              l1 += Native.POINTER_SIZE;
            } 
            List<String> list = Collections.unmodifiableList(arrayList);
            closeableSizeTByReference.close();
            memory.close();
            return list;
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
        memory.close();
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
    return Collections.emptyList();
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables.get();
  }
  
  private Map<String, String> queryEnvironmentVariables() {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 55;
    arrayOfInt[2] = getProcessID();
    arrayOfInt[3] = 3;
    Memory memory = new Memory(ARGMAX);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(ARGMAX);
      try {
        if (OpenBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO) == 0) {
          LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
          long l1 = 0L;
          long l2 = Pointer.nativeValue((Pointer)memory);
          long l3 = l2 + closeableSizeTByReference.longValue();
          long l4;
          for (l4 = Pointer.nativeValue(memory.getPointer(l1)); l4 > l2 && l4 < l3; l4 = Pointer.nativeValue(memory.getPointer(l1))) {
            String str = memory.getString(l4 - l2);
            int i = str.indexOf('=');
            if (i > 0)
              linkedHashMap.put(str.substring(0, i), str.substring(i + 1)); 
            l1 += Native.POINTER_SIZE;
          } 
          Map<Object, Object> map = Collections.unmodifiableMap(linkedHashMap);
          closeableSizeTByReference.close();
          memory.close();
          return (Map)map;
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
      memory.close();
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return Collections.emptyMap();
  }
  
  public String getCurrentWorkingDirectory() {
    return FstatUtil.getCwd(getProcessID());
  }
  
  public String getUser() {
    return this.user;
  }
  
  public String getUserID() {
    return this.userID;
  }
  
  public String getGroup() {
    return this.group;
  }
  
  public String getGroupID() {
    return this.groupID;
  }
  
  public OSProcess.State getState() {
    return this.state;
  }
  
  public int getParentProcessID() {
    return this.parentProcessID;
  }
  
  public int getThreadCount() {
    return this.threadCount;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public long getVirtualSize() {
    return this.virtualSize;
  }
  
  public long getResidentSetSize() {
    return this.residentSetSize;
  }
  
  public long getKernelTime() {
    return this.kernelTime;
  }
  
  public long getUserTime() {
    return this.userTime;
  }
  
  public long getUpTime() {
    return this.upTime;
  }
  
  public long getStartTime() {
    return this.startTime;
  }
  
  public long getBytesRead() {
    return this.bytesRead;
  }
  
  public long getBytesWritten() {
    return this.bytesWritten;
  }
  
  public long getOpenFiles() {
    return FstatUtil.getOpenFiles(getProcessID());
  }
  
  public long getSoftOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      OpenBsdLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_cur;
    } 
    return -1L;
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      OpenBsdLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_max;
    } 
    return -1L;
  }
  
  public int getBitness() {
    return this.bitness;
  }
  
  public long getAffinityMask() {
    long l = 0L;
    String str = ExecutingCommand.getFirstAnswer("cpuset -gp " + getProcessID());
    String[] arrayOfString = str.split(":");
    if (arrayOfString.length > 1) {
      String[] arrayOfString1 = arrayOfString[1].split(",");
      for (String str1 : arrayOfString1) {
        int i = ParseUtil.parseIntOrDefault(str1.trim(), -1);
        if (i >= 0)
          l |= 1L << i; 
      } 
    } 
    return l;
  }
  
  public List<OSThread> getThreadDetails() {
    String str = "ps -aHwwxo " + PS_THREAD_COLUMNS;
    if (getProcessID() >= 0)
      str = str + " -p " + getProcessID(); 
    Predicate predicate = paramMap -> paramMap.containsKey(PsThreadColumns.ARGS);
    return (List<OSThread>)ExecutingCommand.runNative(str).stream().skip(1L).map(paramString -> ParseUtil.stringToEnumMap(PsThreadColumns.class, paramString.trim(), ' ')).filter(predicate).map(paramMap -> new OpenBsdOSThread(getProcessID(), paramMap)).filter(OSThread.ThreadFiltering.VALID_THREAD).collect(Collectors.toList());
  }
  
  public long getMinorFaults() {
    return this.minorFaults;
  }
  
  public long getMajorFaults() {
    return this.majorFaults;
  }
  
  public long getContextSwitches() {
    return this.contextSwitches;
  }
  
  public boolean updateAttributes() {
    String str = "ps -awwxo " + OpenBsdOperatingSystem.PS_COMMAND_ARGS + " -p " + getProcessID();
    List<String> list = ExecutingCommand.runNative(str);
    if (list.size() > 1) {
      Map<OpenBsdOperatingSystem.PsKeywords, String> map = ParseUtil.stringToEnumMap(OpenBsdOperatingSystem.PsKeywords.class, ((String)list.get(1)).trim(), ' ');
      if (map.containsKey(OpenBsdOperatingSystem.PsKeywords.ARGS)) {
        updateThreadCount();
        return updateAttributes(map);
      } 
    } 
    this.state = OSProcess.State.INVALID;
    return false;
  }
  
  private boolean updateAttributes(Map<OpenBsdOperatingSystem.PsKeywords, String> paramMap) {
    long l1 = System.currentTimeMillis();
    switch (((String)paramMap.get(OpenBsdOperatingSystem.PsKeywords.STATE)).charAt(0)) {
      case 'R':
        this.state = OSProcess.State.RUNNING;
        break;
      case 'I':
      case 'S':
        this.state = OSProcess.State.SLEEPING;
        break;
      case 'D':
      case 'L':
      case 'U':
        this.state = OSProcess.State.WAITING;
        break;
      case 'Z':
        this.state = OSProcess.State.ZOMBIE;
        break;
      case 'T':
        this.state = OSProcess.State.STOPPED;
        break;
      default:
        this.state = OSProcess.State.OTHER;
        break;
    } 
    this.parentProcessID = ParseUtil.parseIntOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.PPID), 0);
    this.user = paramMap.get(OpenBsdOperatingSystem.PsKeywords.USER);
    this.userID = paramMap.get(OpenBsdOperatingSystem.PsKeywords.UID);
    this.group = paramMap.get(OpenBsdOperatingSystem.PsKeywords.GROUP);
    this.groupID = paramMap.get(OpenBsdOperatingSystem.PsKeywords.GID);
    this.priority = ParseUtil.parseIntOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.PRI), 0);
    this.virtualSize = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.VSZ), 0L) * 1024L;
    this.residentSetSize = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.RSS), 0L) * 1024L;
    long l2 = ParseUtil.parseDHMSOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.ETIME), 0L);
    this.upTime = (l2 < 1L) ? 1L : l2;
    this.startTime = l1 - this.upTime;
    this.userTime = ParseUtil.parseDHMSOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.CPUTIME), 0L);
    this.kernelTime = 0L;
    this.path = paramMap.get(OpenBsdOperatingSystem.PsKeywords.COMM);
    this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
    this.minorFaults = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.MINFLT), 0L);
    this.majorFaults = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.MAJFLT), 0L);
    long l3 = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.NIVCSW), 0L);
    long l4 = ParseUtil.parseLongOrDefault(paramMap.get(OpenBsdOperatingSystem.PsKeywords.NVCSW), 0L);
    this.contextSwitches = l4 + l3;
    this.commandLineBackup = paramMap.get(OpenBsdOperatingSystem.PsKeywords.ARGS);
    return true;
  }
  
  private void updateThreadCount() {
    List list = ExecutingCommand.runNative("ps -axHo tid -p " + getProcessID());
    if (!list.isEmpty())
      this.threadCount = list.size() - 1; 
    this.threadCount = 1;
  }
  
  static {
    PS_THREAD_COLUMNS = Arrays.<PsThreadColumns>stream(PsThreadColumns.values()).map(Enum::name).map(paramString -> paramString.toLowerCase(Locale.ROOT)).collect(Collectors.joining(","));
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 8;
    Memory memory = new Memory(4L);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(4L);
      try {
        if (OpenBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO) == 0) {
          ARGMAX = memory.getInt(0L);
        } else {
          LOG.warn("Failed sysctl call for process arguments max size (kern.argmax). Error code: {}", Integer.valueOf(Native.getLastError()));
          ARGMAX = 0;
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
      memory.close();
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  enum PsThreadColumns {
    TID, STATE, ETIME, CPUTIME, NIVCSW, NVCSW, MAJFLT, MINFLT, PRI, ARGS;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\openbsd\OpenBsdOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */