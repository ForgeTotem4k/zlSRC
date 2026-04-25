package oshi.software.os;

import java.util.HashMap;
import java.util.Map;

public class ApplicationInfo {
  private final String name;
  
  private final String version;
  
  private final String vendor;
  
  private final long timestamp;
  
  private final Map<String, String> additionalInfo;
  
  public ApplicationInfo(String paramString1, String paramString2, String paramString3, long paramLong, Map<String, String> paramMap) {
    this.name = paramString1;
    this.version = paramString2;
    this.vendor = paramString3;
    this.timestamp = paramLong;
    this.additionalInfo = (paramMap != null) ? new HashMap<>(paramMap) : new HashMap<>();
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public String getVendor() {
    return this.vendor;
  }
  
  public long getTimestamp() {
    return this.timestamp;
  }
  
  public Map<String, String> getAdditionalInfo() {
    return this.additionalInfo;
  }
  
  public String toString() {
    return "AppInfo{name=" + this.name + ", version=" + this.version + ", vendor=" + this.vendor + ", timestamp=" + this.timestamp + ", additionalInfo=" + this.additionalInfo + '}';
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\ApplicationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */