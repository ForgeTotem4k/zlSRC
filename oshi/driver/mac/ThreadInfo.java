package oshi.driver.mac;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class ThreadInfo {
  private static final Pattern PS_M = Pattern.compile("\\D+(\\d+).+(\\d+\\.\\d)\\s+(\\w)\\s+(\\d+)\\D+(\\d+:\\d{2}\\.\\d{2})\\s+(\\d+:\\d{2}\\.\\d{2}).+");
  
  public static List<ThreadStats> queryTaskThreads(int paramInt) {
    String str = " " + paramInt + " ";
    ArrayList<ThreadStats> arrayList = new ArrayList();
    List list = (List)ExecutingCommand.runNative("ps -awwxM").stream().filter(paramString2 -> paramString2.contains(paramString1)).collect(Collectors.toList());
    byte b = 0;
    for (String str1 : list) {
      Matcher matcher = PS_M.matcher(str1);
      if (matcher.matches() && paramInt == ParseUtil.parseIntOrDefault(matcher.group(1), -1)) {
        double d = ParseUtil.parseDoubleOrDefault(matcher.group(2), 0.0D);
        char c = matcher.group(3).charAt(0);
        int i = ParseUtil.parseIntOrDefault(matcher.group(4), 0);
        long l1 = ParseUtil.parseDHMSOrDefault(matcher.group(5), 0L);
        long l2 = ParseUtil.parseDHMSOrDefault(matcher.group(6), 0L);
        arrayList.add(new ThreadStats(b++, d, c, l1, l2, i));
      } 
    } 
    return arrayList;
  }
  
  @Immutable
  public static class ThreadStats {
    private final int threadId;
    
    private final long userTime;
    
    private final long systemTime;
    
    private final long upTime;
    
    private final OSProcess.State state;
    
    private final int priority;
    
    public ThreadStats(int param1Int1, double param1Double, char param1Char, long param1Long1, long param1Long2, int param1Int2) {
      this.threadId = param1Int1;
      this.userTime = param1Long2;
      this.systemTime = param1Long1;
      this.upTime = (long)((param1Long2 + param1Long1) / (param1Double / 100.0D + 5.0E-4D));
      switch (param1Char) {
        case 'I':
        case 'S':
          this.state = OSProcess.State.SLEEPING;
          break;
        case 'U':
          this.state = OSProcess.State.WAITING;
          break;
        case 'R':
          this.state = OSProcess.State.RUNNING;
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
      this.priority = param1Int2;
    }
    
    public int getThreadId() {
      return this.threadId;
    }
    
    public long getUserTime() {
      return this.userTime;
    }
    
    public long getSystemTime() {
      return this.systemTime;
    }
    
    public long getUpTime() {
      return this.upTime;
    }
    
    public OSProcess.State getState() {
      return this.state;
    }
    
    public int getPriority() {
      return this.priority;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\mac\ThreadInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */