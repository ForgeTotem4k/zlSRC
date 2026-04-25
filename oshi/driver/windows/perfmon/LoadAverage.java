package oshi.driver.windows.perfmon;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class LoadAverage {
  private static Thread loadAvgThread = null;
  
  private static double[] loadAverages = new double[] { -1.0D, -1.0D, -1.0D };
  
  private static final double[] EXP_WEIGHT = new double[] { Math.exp(-0.08333333333333333D), Math.exp(-0.016666666666666666D), Math.exp(-0.005555555555555556D) };
  
  public static double[] queryLoadAverage(int paramInt) {
    synchronized (loadAverages) {
      return Arrays.copyOf(loadAverages, paramInt);
    } 
  }
  
  public static synchronized void stopDaemon() {
    if (loadAvgThread != null) {
      loadAvgThread.interrupt();
      loadAvgThread = null;
    } 
  }
  
  public static synchronized void startDaemon() {
    if (loadAvgThread != null)
      return; 
    loadAvgThread = new Thread("OSHI Load Average daemon") {
        public void run() {
          Pair pair = LoadAverage.queryNonIdleTicks();
          long l1 = ((Long)pair.getA()).longValue();
          long l2 = ((Long)pair.getB()).longValue();
          long l3 = System.nanoTime();
          try {
            Thread.sleep(2500L);
          } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
          } 
          while (!Thread.currentThread().isInterrupted()) {
            double d;
            pair = LoadAverage.queryNonIdleTicks();
            long l4 = ((Long)pair.getA()).longValue() - l1;
            long l5 = ((Long)pair.getB()).longValue() - l2;
            if (l5 > 0L && l4 > 0L) {
              d = l4 / l5;
            } else {
              d = 0.0D;
            } 
            l1 = ((Long)pair.getA()).longValue();
            l2 = ((Long)pair.getB()).longValue();
            long l7 = ((Long)SystemInformation.queryProcessorQueueLength().getOrDefault(SystemInformation.ProcessorQueueLengthProperty.PROCESSORQUEUELENGTH, Long.valueOf(0L))).longValue();
            synchronized (LoadAverage.loadAverages) {
              if (LoadAverage.loadAverages[0] < 0.0D)
                Arrays.fill(LoadAverage.loadAverages, d); 
              for (byte b = 0; b < LoadAverage.loadAverages.length; b++) {
                LoadAverage.loadAverages[b] = LoadAverage.loadAverages[b] * LoadAverage.EXP_WEIGHT[b];
                LoadAverage.loadAverages[b] = LoadAverage.loadAverages[b] + (d + l7) * (1.0D - LoadAverage.EXP_WEIGHT[b]);
              } 
            } 
            long l6 = 5000L - (System.nanoTime() - l3) % 5000000000L / 1000000L;
            if (l6 < 500L)
              l6 += 5000L; 
            try {
              Thread.sleep(l6);
            } catch (InterruptedException interruptedException) {
              Thread.currentThread().interrupt();
            } 
          } 
        }
      };
    loadAvgThread.setDaemon(true);
    loadAvgThread.start();
  }
  
  private static Pair<Long, Long> queryNonIdleTicks() {
    Pair<List<String>, Map<ProcessInformation.IdleProcessorTimeProperty, List<Long>>> pair = ProcessInformation.queryIdleProcessCounters();
    List list = (List)pair.getA();
    Map map = (Map)pair.getB();
    List<Long> list1 = (List)map.get(ProcessInformation.IdleProcessorTimeProperty.PERCENTPROCESSORTIME);
    List<Long> list2 = (List)map.get(ProcessInformation.IdleProcessorTimeProperty.ELAPSEDTIME);
    long l1 = 0L;
    long l2 = 0L;
    for (byte b = 0; b < list.size(); b++) {
      if ("_Total".equals(list.get(b))) {
        l1 += ((Long)list1.get(b)).longValue();
        l2 += ((Long)list2.get(b)).longValue();
      } else if ("Idle".equals(list.get(b))) {
        l1 -= ((Long)list1.get(b)).longValue();
      } 
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\LoadAverage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */