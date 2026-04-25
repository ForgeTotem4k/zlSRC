package oshi.hardware;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public interface CentralProcessor {
  ProcessorIdentifier getProcessorIdentifier();
  
  long getMaxFreq();
  
  long[] getCurrentFreq();
  
  List<LogicalProcessor> getLogicalProcessors();
  
  List<PhysicalProcessor> getPhysicalProcessors();
  
  List<ProcessorCache> getProcessorCaches();
  
  List<String> getFeatureFlags();
  
  double getSystemCpuLoadBetweenTicks(long[] paramArrayOflong);
  
  long[] getSystemCpuLoadTicks();
  
  double[] getSystemLoadAverage(int paramInt);
  
  default double getSystemCpuLoad(long paramLong) {
    long l1 = System.nanoTime();
    long[] arrayOfLong = getSystemCpuLoadTicks();
    long l2 = paramLong - (System.nanoTime() - l1) / 1000000L;
    if (l2 > 0L)
      Util.sleep(paramLong); 
    return getSystemCpuLoadBetweenTicks(arrayOfLong);
  }
  
  default double[] getProcessorCpuLoad(long paramLong) {
    long l1 = System.nanoTime();
    long[][] arrayOfLong = getProcessorCpuLoadTicks();
    long l2 = paramLong - (System.nanoTime() - l1) / 1000000L;
    if (l2 > 0L)
      Util.sleep(paramLong); 
    return getProcessorCpuLoadBetweenTicks(arrayOfLong);
  }
  
  double[] getProcessorCpuLoadBetweenTicks(long[][] paramArrayOflong);
  
  long[][] getProcessorCpuLoadTicks();
  
  int getLogicalProcessorCount();
  
  int getPhysicalProcessorCount();
  
  int getPhysicalPackageCount();
  
  long getContextSwitches();
  
  long getInterrupts();
  
  @Immutable
  public static final class ProcessorIdentifier {
    private static final String OSHI_ARCHITECTURE_PROPERTIES = "oshi.architecture.properties";
    
    private final String cpuVendor;
    
    private final String cpuName;
    
    private final String cpuFamily;
    
    private final String cpuModel;
    
    private final String cpuStepping;
    
    private final String processorID;
    
    private final String cpuIdentifier;
    
    private final boolean cpu64bit;
    
    private final long cpuVendorFreq;
    
    private final Supplier<String> microArchictecture = Memoizer.memoize(this::queryMicroarchitecture);
    
    public ProcessorIdentifier(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5, String param1String6, boolean param1Boolean) {
      this(param1String1, param1String2, param1String3, param1String4, param1String5, param1String6, param1Boolean, -1L);
    }
    
    public ProcessorIdentifier(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5, String param1String6, boolean param1Boolean, long param1Long) {
      this.cpuVendor = param1String1.startsWith("0x") ? queryVendorFromImplementer(param1String1) : param1String1;
      this.cpuName = param1String2;
      this.cpuFamily = param1String3;
      this.cpuModel = param1String4;
      this.cpuStepping = param1String5;
      this.processorID = param1String6;
      this.cpu64bit = param1Boolean;
      StringBuilder stringBuilder = new StringBuilder();
      if (param1String1.contentEquals("GenuineIntel")) {
        stringBuilder.append(param1Boolean ? "Intel64" : "x86");
      } else {
        stringBuilder.append(param1String1);
      } 
      stringBuilder.append(" Family ").append(param1String3);
      stringBuilder.append(" Model ").append(param1String4);
      stringBuilder.append(" Stepping ").append(param1String5);
      this.cpuIdentifier = stringBuilder.toString();
      if (param1Long > 0L) {
        this.cpuVendorFreq = param1Long;
      } else {
        Pattern pattern = Pattern.compile("@ (.*)$");
        Matcher matcher = pattern.matcher(param1String2);
        if (matcher.find()) {
          String str = matcher.group(1);
          this.cpuVendorFreq = ParseUtil.parseHertz(str);
        } else {
          this.cpuVendorFreq = -1L;
        } 
      } 
    }
    
    public String getVendor() {
      return this.cpuVendor;
    }
    
    public String getName() {
      return this.cpuName;
    }
    
    public String getFamily() {
      return this.cpuFamily;
    }
    
    public String getModel() {
      return this.cpuModel;
    }
    
    public String getStepping() {
      return this.cpuStepping;
    }
    
    public String getProcessorID() {
      return this.processorID;
    }
    
    public String getIdentifier() {
      return this.cpuIdentifier;
    }
    
    public boolean isCpu64bit() {
      return this.cpu64bit;
    }
    
    public long getVendorFreq() {
      return this.cpuVendorFreq;
    }
    
    public String getMicroarchitecture() {
      return this.microArchictecture.get();
    }
    
    private String queryMicroarchitecture() {
      String str1 = null;
      Properties properties = FileUtil.readPropertiesFromFilename("oshi.architecture.properties");
      StringBuilder stringBuilder = new StringBuilder();
      String str2 = this.cpuVendor.toUpperCase(Locale.ROOT);
      if (str2.contains("AMD")) {
        stringBuilder.append("amd.");
      } else if (str2.contains("ARM")) {
        stringBuilder.append("arm.");
      } else if (str2.contains("IBM")) {
        int i = this.cpuName.indexOf("_POWER");
        if (i > 0)
          str1 = this.cpuName.substring(i + 1); 
      } else if (str2.contains("APPLE")) {
        stringBuilder.append("apple.");
      } 
      if (Util.isBlank(str1) && !stringBuilder.toString().equals("arm.")) {
        stringBuilder.append(this.cpuFamily);
        str1 = properties.getProperty(stringBuilder.toString());
      } 
      if (Util.isBlank(str1)) {
        stringBuilder.append('.').append(this.cpuModel);
        str1 = properties.getProperty(stringBuilder.toString());
      } 
      if (Util.isBlank(str1)) {
        stringBuilder.append('.').append(this.cpuStepping);
        str1 = properties.getProperty(stringBuilder.toString());
      } 
      return Util.isBlank(str1) ? "unknown" : str1;
    }
    
    private String queryVendorFromImplementer(String param1String) {
      Properties properties = FileUtil.readPropertiesFromFilename("oshi.architecture.properties");
      return properties.getProperty("hw_impl." + param1String, param1String);
    }
    
    public String toString() {
      return getIdentifier();
    }
  }
  
  @Immutable
  public static class ProcessorCache {
    private final byte level;
    
    private final byte associativity;
    
    private final short lineSize;
    
    private final int cacheSize;
    
    private final Type type;
    
    public ProcessorCache(byte param1Byte1, byte param1Byte2, short param1Short, int param1Int, Type param1Type) {
      this.level = param1Byte1;
      this.associativity = param1Byte2;
      this.lineSize = param1Short;
      this.cacheSize = param1Int;
      this.type = param1Type;
    }
    
    public ProcessorCache(int param1Int1, int param1Int2, int param1Int3, long param1Long, Type param1Type) {
      this((byte)param1Int1, (byte)param1Int2, (short)param1Int3, (int)param1Long, param1Type);
    }
    
    public byte getLevel() {
      return this.level;
    }
    
    public byte getAssociativity() {
      return this.associativity;
    }
    
    public short getLineSize() {
      return this.lineSize;
    }
    
    public int getCacheSize() {
      return this.cacheSize;
    }
    
    public Type getType() {
      return this.type;
    }
    
    public String toString() {
      return "ProcessorCache [L" + this.level + " " + this.type + ", cacheSize=" + this.cacheSize + ", " + ((this.associativity > 0) ? (this.associativity + "-way") : "unknown") + " associativity, lineSize=" + this.lineSize + "]";
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null || !(param1Object instanceof ProcessorCache))
        return false; 
      ProcessorCache processorCache = (ProcessorCache)param1Object;
      return (this.associativity == processorCache.associativity && this.cacheSize == processorCache.cacheSize && this.level == processorCache.level && this.lineSize == processorCache.lineSize && this.type == processorCache.type);
    }
    
    public int hashCode() {
      return Objects.hash(new Object[] { Byte.valueOf(this.associativity), Integer.valueOf(this.cacheSize), Byte.valueOf(this.level), Short.valueOf(this.lineSize), this.type });
    }
    
    public enum Type {
      UNIFIED, INSTRUCTION, DATA, TRACE;
      
      public String toString() {
        return name().substring(0, 1) + name().substring(1).toLowerCase(Locale.ROOT);
      }
    }
  }
  
  @Immutable
  public static class PhysicalProcessor {
    private final int physicalPackageNumber;
    
    private final int physicalProcessorNumber;
    
    private final int efficiency;
    
    private final String idString;
    
    public PhysicalProcessor(int param1Int1, int param1Int2) {
      this(param1Int1, param1Int2, 0, "");
    }
    
    public PhysicalProcessor(int param1Int1, int param1Int2, int param1Int3, String param1String) {
      this.physicalPackageNumber = param1Int1;
      this.physicalProcessorNumber = param1Int2;
      this.efficiency = param1Int3;
      this.idString = param1String;
    }
    
    public int getPhysicalPackageNumber() {
      return this.physicalPackageNumber;
    }
    
    public int getPhysicalProcessorNumber() {
      return this.physicalProcessorNumber;
    }
    
    public int getEfficiency() {
      return this.efficiency;
    }
    
    public String getIdString() {
      return this.idString;
    }
    
    public String toString() {
      return "PhysicalProcessor [package/core=" + this.physicalPackageNumber + "/" + this.physicalProcessorNumber + ", efficiency=" + this.efficiency + ", idString=" + this.idString + "]";
    }
  }
  
  @Immutable
  public static class LogicalProcessor {
    private final int processorNumber;
    
    private final int physicalProcessorNumber;
    
    private final int physicalPackageNumber;
    
    private final int numaNode;
    
    private final int processorGroup;
    
    public LogicalProcessor(int param1Int1, int param1Int2, int param1Int3) {
      this(param1Int1, param1Int2, param1Int3, 0, 0);
    }
    
    public LogicalProcessor(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this(param1Int1, param1Int2, param1Int3, param1Int4, 0);
    }
    
    public LogicalProcessor(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      this.processorNumber = param1Int1;
      this.physicalProcessorNumber = param1Int2;
      this.physicalPackageNumber = param1Int3;
      this.numaNode = param1Int4;
      this.processorGroup = param1Int5;
    }
    
    public int getProcessorNumber() {
      return this.processorNumber;
    }
    
    public int getPhysicalProcessorNumber() {
      return this.physicalProcessorNumber;
    }
    
    public int getPhysicalPackageNumber() {
      return this.physicalPackageNumber;
    }
    
    public int getNumaNode() {
      return this.numaNode;
    }
    
    public int getProcessorGroup() {
      return this.processorGroup;
    }
    
    public String toString() {
      return "LogicalProcessor [processorNumber=" + this.processorNumber + ", coreNumber=" + this.physicalProcessorNumber + ", packageNumber=" + this.physicalPackageNumber + ", numaNode=" + this.numaNode + ", processorGroup=" + this.processorGroup + "]";
    }
  }
  
  public enum TickType {
    USER(0),
    NICE(1),
    SYSTEM(2),
    IDLE(3),
    IOWAIT(4),
    IRQ(5),
    SOFTIRQ(6),
    STEAL(7);
    
    private final int index;
    
    TickType(int param1Int1) {
      this.index = param1Int1;
    }
    
    public int getIndex() {
      return this.index;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\CentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */