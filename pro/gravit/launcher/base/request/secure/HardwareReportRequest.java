package pro.gravit.launcher.base.request.secure;

import java.util.Base64;
import pro.gravit.launcher.base.events.request.HardwareReportRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class HardwareReportRequest extends Request<HardwareReportRequestEvent> {
  public HardwareInfo hardware;
  
  public String getType() {
    return "hardwareReport";
  }
  
  public static class HardwareInfo {
    public int bitness;
    
    public long totalMemory;
    
    public int logicalProcessors;
    
    public int physicalProcessors;
    
    public long processorMaxFreq;
    
    public boolean battery;
    
    public String hwDiskId;
    
    public byte[] displayId;
    
    public String baseboardSerialNumber;
    
    public String graphicCard;
    
    public String toString() {
      return "HardwareInfo{bitness=" + this.bitness + ", totalMemory=" + this.totalMemory + ", logicalProcessors=" + this.logicalProcessors + ", physicalProcessors=" + this.physicalProcessors + ", processorMaxFreq=" + this.processorMaxFreq + ", battery=" + this.battery + ", hwDiskId='" + this.hwDiskId + "', displayId=" + ((this.displayId == null) ? null : new String(Base64.getEncoder().encode(this.displayId))) + ", baseboardSerialNumber='" + this.baseboardSerialNumber + "', graphicCard='" + this.graphicCard + "'}";
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\secure\HardwareReportRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */