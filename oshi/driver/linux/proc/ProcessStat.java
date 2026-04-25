package oshi.driver.linux.proc;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSProcess;
import oshi.util.Constants;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessStat {
  private static final Pattern SOCKET = Pattern.compile("socket:\\[(\\d+)\\]");
  
  public static final int PROC_PID_STAT_LENGTH;
  
  public static Triplet<String, Character, Map<PidStat, Long>> getPidStats(int paramInt) {
    String str1 = FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.PID_STAT, new Object[] { Integer.valueOf(paramInt) }));
    if (str1.isEmpty())
      return null; 
    int i = str1.indexOf('(') + 1;
    int j = str1.indexOf(')');
    String str2 = str1.substring(i, j);
    Character character = Character.valueOf(str1.charAt(j + 2));
    String[] arrayOfString = ParseUtil.whitespaces.split(str1.substring(j + 4).trim());
    EnumMap<PidStat, Object> enumMap = new EnumMap<>(PidStat.class);
    PidStat[] arrayOfPidStat = PidStat.class.getEnumConstants();
    for (byte b = 3; b < arrayOfPidStat.length && b - 3 < arrayOfString.length; b++)
      enumMap.put(arrayOfPidStat[b], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[b - 3], 0L))); 
    return new Triplet(str2, character, enumMap);
  }
  
  public static Map<PidStatM, Long> getPidStatM(int paramInt) {
    String str = FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.PID_STATM, new Object[] { Integer.valueOf(paramInt) }));
    if (str.isEmpty())
      return null; 
    String[] arrayOfString = ParseUtil.whitespaces.split(str);
    EnumMap<PidStatM, Object> enumMap = new EnumMap<>(PidStatM.class);
    PidStatM[] arrayOfPidStatM = PidStatM.class.getEnumConstants();
    for (byte b = 0; b < arrayOfPidStatM.length && b < arrayOfString.length; b++)
      enumMap.put(arrayOfPidStatM[b], Long.valueOf(ParseUtil.parseLongOrDefault(arrayOfString[b], 0L))); 
    return (Map)enumMap;
  }
  
  public static File[] getFileDescriptorFiles(int paramInt) {
    return listNumericFiles(String.format(Locale.ROOT, ProcPath.PID_FD, new Object[] { Integer.valueOf(paramInt) }));
  }
  
  public static File[] getPidFiles() {
    return listNumericFiles(ProcPath.PROC);
  }
  
  public static Map<Long, Integer> querySocketToPidMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (File file : getPidFiles()) {
      int i = ParseUtil.parseIntOrDefault(file.getName(), -1);
      File[] arrayOfFile = getFileDescriptorFiles(i);
      for (File file1 : arrayOfFile) {
        String str = FileUtil.readSymlinkTarget(file1);
        if (str != null) {
          Matcher matcher = SOCKET.matcher(str);
          if (matcher.matches())
            hashMap.put(Long.valueOf(ParseUtil.parseLongOrDefault(matcher.group(1), -1L)), Integer.valueOf(i)); 
        } 
      } 
    } 
    return (Map)hashMap;
  }
  
  public static List<Integer> getThreadIds(int paramInt) {
    File[] arrayOfFile = listNumericFiles(String.format(Locale.ROOT, ProcPath.TASK_PATH, new Object[] { Integer.valueOf(paramInt) }));
    return (List<Integer>)Arrays.<File>stream(arrayOfFile).map(paramFile -> Integer.valueOf(ParseUtil.parseIntOrDefault(paramFile.getName(), 0))).filter(paramInteger -> (paramInteger.intValue() != paramInt)).collect(Collectors.toList());
  }
  
  private static File[] listNumericFiles(String paramString) {
    File file = new File(paramString);
    File[] arrayOfFile = file.listFiles(paramFile -> Constants.DIGITS.matcher(paramFile.getName()).matches());
    return (arrayOfFile == null) ? new File[0] : arrayOfFile;
  }
  
  public static OSProcess.State getState(char paramChar) {
    switch (paramChar) {
      case 'R':
        return OSProcess.State.RUNNING;
      case 'S':
        return OSProcess.State.SLEEPING;
      case 'D':
        return OSProcess.State.WAITING;
      case 'Z':
        return OSProcess.State.ZOMBIE;
      case 'T':
        return OSProcess.State.STOPPED;
    } 
    return OSProcess.State.OTHER;
  }
  
  static {
    String str = FileUtil.getStringFromFile(ProcPath.SELF_STAT);
    if (str.contains(")")) {
      PROC_PID_STAT_LENGTH = ParseUtil.countStringToLongArray(str, ' ') + 3;
    } else {
      PROC_PID_STAT_LENGTH = 52;
    } 
  }
  
  public enum PidStat {
    PID, COMM, STATE, PPID, PGRP, SESSION, TTY_NR, PTGID, FLAGS, MINFLT, CMINFLT, MAJFLT, CMAJFLT, UTIME, STIME, CUTIME, CSTIME, PRIORITY, NICE, NUM_THREADS, ITREALVALUE, STARTTIME, VSIZE, RSS, RSSLIM, STARTCODE, ENDCODE, STARTSTACK, KSTKESP, KSTKEIP, SIGNAL, BLOCKED, SIGIGNORE, SIGCATCH, WCHAN, NSWAP, CNSWAP, EXIT_SIGNAL, PROCESSOR, RT_PRIORITY, POLICY, DELAYACCT_BLKIO_TICKS, GUEST_TIME, CGUEST_TIME, START_DATA, END_DATA, START_BRK, ARG_START, ARG_END, ENV_START, ENV_END, EXIT_CODE;
  }
  
  public enum PidStatM {
    SIZE, RESIDENT, SHARED, TEXT, LIB, DATA, DT;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\proc\ProcessStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */