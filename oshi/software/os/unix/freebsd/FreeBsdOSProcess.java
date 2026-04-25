package oshi.software.os.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.unix.Resource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.platform.unix.freebsd.ProcstatUtil;

@ThreadSafe
public class FreeBsdOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(FreeBsdOSProcess.class);
  
  private static final int ARGMAX = BsdSysctlUtil.sysctl("kern.argmax", 0);
  
  private final FreeBsdOperatingSystem os;
  
  static final String PS_THREAD_COLUMNS;
  
  private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
  
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
  
  private String commandLineBackup;
  
  public FreeBsdOSProcess(int paramInt, Map<FreeBsdOperatingSystem.PsKeywords, String> paramMap, FreeBsdOperatingSystem paramFreeBsdOperatingSystem) {
    super(paramInt);
    this.os = paramFreeBsdOperatingSystem;
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
      arrayOfInt[1] = 14;
      arrayOfInt[2] = 7;
      arrayOfInt[3] = getProcessID();
      Memory memory = new Memory(ARGMAX);
      try {
        ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(ARGMAX);
        try {
          if (FreeBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO) == 0) {
            List<?> list = Collections.unmodifiableList(ParseUtil.parseByteArrayToStrings(memory.getByteArray(0L, closeableSizeTByReference.getValue().intValue())));
            closeableSizeTByReference.close();
            memory.close();
            return (List)list;
          } 
          LOG.warn("Failed sysctl call for process arguments (kern.proc.args), process {} may not exist. Error code: {}", Integer.valueOf(getProcessID()), Integer.valueOf(Native.getLastError()));
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
    if (ARGMAX > 0) {
      int[] arrayOfInt = new int[4];
      arrayOfInt[0] = 1;
      arrayOfInt[1] = 14;
      arrayOfInt[2] = 35;
      arrayOfInt[3] = getProcessID();
      Memory memory = new Memory(ARGMAX);
      try {
        ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(ARGMAX);
        try {
          if (FreeBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO) == 0) {
            Map<?, ?> map = Collections.unmodifiableMap(ParseUtil.parseByteArrayToStringMap(memory.getByteArray(0L, closeableSizeTByReference.getValue().intValue())));
            closeableSizeTByReference.close();
            memory.close();
            return (Map)map;
          } 
          LOG.warn("Failed sysctl call for process environment variables (kern.proc.env), process {} may not exist. Error code: {}", Integer.valueOf(getProcessID()), Integer.valueOf(Native.getLastError()));
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
    return Collections.emptyMap();
  }
  
  public String getCurrentWorkingDirectory() {
    return ProcstatUtil.getCwd(getProcessID());
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
    return ProcstatUtil.getOpenFiles(getProcessID());
  }
  
  public long getSoftOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      FreeBsdLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_cur;
    } 
    return getProcessOpenFileLimit(getProcessID(), 1);
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      FreeBsdLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_max;
    } 
    return getProcessOpenFileLimit(getProcessID(), 2);
  }
  
  public int getBitness() {
    return ((Integer)this.bitness.get()).intValue();
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
  
  private int queryBitness() {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 14;
    arrayOfInt[2] = 9;
    arrayOfInt[3] = getProcessID();
    Memory memory = new Memory(32L);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(32L);
      try {
        if (0 == FreeBsdLibc.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          String str = memory.getString(0L);
          if (str.contains("ELF32")) {
            byte b = 32;
            closeableSizeTByReference.close();
            memory.close();
            return b;
          } 
          if (str.contains("ELF64")) {
            byte b = 64;
            closeableSizeTByReference.close();
            memory.close();
            return b;
          } 
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
    return 0;
  }
  
  public List<OSThread> getThreadDetails() {
    String str = "ps -awwxo " + PS_THREAD_COLUMNS + " -H";
    if (getProcessID() >= 0)
      str = str + " -p " + getProcessID(); 
    Predicate predicate = paramMap -> paramMap.containsKey(PsThreadColumns.PRI);
    return (List<OSThread>)ExecutingCommand.runNative(str).stream().skip(1L).parallel().map(paramString -> ParseUtil.stringToEnumMap(PsThreadColumns.class, paramString.trim(), ' ')).filter(predicate).map(paramMap -> new FreeBsdOSThread(getProcessID(), paramMap)).filter(OSThread.ThreadFiltering.VALID_THREAD).collect(Collectors.toList());
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
    String str = "ps -awwxo " + FreeBsdOperatingSystem.PS_COMMAND_ARGS + " -p " + getProcessID();
    List<String> list = ExecutingCommand.runNative(str);
    if (list.size() > 1) {
      Map<FreeBsdOperatingSystem.PsKeywords, String> map = ParseUtil.stringToEnumMap(FreeBsdOperatingSystem.PsKeywords.class, ((String)list.get(1)).trim(), ' ');
      if (map.containsKey(FreeBsdOperatingSystem.PsKeywords.ARGS))
        return updateAttributes(map); 
    } 
    this.state = OSProcess.State.INVALID;
    return false;
  }
  
  private boolean updateAttributes(Map<FreeBsdOperatingSystem.PsKeywords, String> paramMap) {
    long l1 = System.currentTimeMillis();
    switch (((String)paramMap.get(FreeBsdOperatingSystem.PsKeywords.STATE)).charAt(0)) {
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
    this.parentProcessID = ParseUtil.parseIntOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.PPID), 0);
    this.user = paramMap.get(FreeBsdOperatingSystem.PsKeywords.USER);
    this.userID = paramMap.get(FreeBsdOperatingSystem.PsKeywords.UID);
    this.group = paramMap.get(FreeBsdOperatingSystem.PsKeywords.GROUP);
    this.groupID = paramMap.get(FreeBsdOperatingSystem.PsKeywords.GID);
    this.threadCount = ParseUtil.parseIntOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.NLWP), 0);
    this.priority = ParseUtil.parseIntOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.PRI), 0);
    this.virtualSize = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.VSZ), 0L) * 1024L;
    this.residentSetSize = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.RSS), 0L) * 1024L;
    long l2 = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.ETIMES), 0L);
    this.upTime = (l2 < 1L) ? 1L : l2;
    this.startTime = l1 - this.upTime;
    this.kernelTime = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.SYSTIME), 0L);
    this.userTime = ParseUtil.parseDHMSOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.TIME), 0L) - this.kernelTime;
    this.path = paramMap.get(FreeBsdOperatingSystem.PsKeywords.COMM);
    this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
    this.minorFaults = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.MAJFLT), 0L);
    this.majorFaults = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.MINFLT), 0L);
    long l3 = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.NVCSW), 0L);
    long l4 = ParseUtil.parseLongOrDefault(paramMap.get(FreeBsdOperatingSystem.PsKeywords.NIVCSW), 0L);
    this.contextSwitches = l4 + l3;
    this.commandLineBackup = paramMap.get(FreeBsdOperatingSystem.PsKeywords.ARGS);
    return true;
  }
  
  private long getProcessOpenFileLimit(long paramLong, int paramInt) {
    String str = String.format(Locale.ROOT, "/proc/%d/limits", new Object[] { Long.valueOf(paramLong) });
    if (!Files.exists(Paths.get(str, new String[0]), new java.nio.file.LinkOption[0]))
      return -1L; 
    List list = FileUtil.readFile(str);
    Optional<String> optional = list.stream().filter(paramString -> paramString.startsWith("Max open files")).findFirst();
    if (!optional.isPresent())
      return -1L; 
    String[] arrayOfString = ((String)optional.get()).split("\\D+");
    return ParseUtil.parseLongOrDefault(arrayOfString[paramInt], -1L);
  }
  
  static {
    PS_THREAD_COLUMNS = Arrays.<PsThreadColumns>stream(PsThreadColumns.values()).map(Enum::name).map(paramString -> paramString.toLowerCase(Locale.ROOT)).collect(Collectors.joining(","));
  }
  
  enum PsThreadColumns {
    TDNAME, LWP, STATE, ETIMES, SYSTIME, TIME, TDADDR, NIVCSW, NVCSW, MAJFLT, MINFLT, PRI;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\freebsd\FreeBsdOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */