package pro.gravit.launcher.runtime.utils;

import java.util.Iterator;
import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.Display;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import pro.gravit.launcher.base.request.secure.HardwareReportRequest;

public class HWIDProvider {
  public final SystemInfo systemInfo = new SystemInfo();
  
  public final OperatingSystem system = this.systemInfo.getOperatingSystem();
  
  public final HardwareAbstractionLayer hardware = this.systemInfo.getHardware();
  
  public int getBitness() {
    return this.system.getBitness();
  }
  
  public long getTotalMemory() {
    return this.hardware.getMemory().getTotal();
  }
  
  public long getProcessorMaxFreq() {
    return this.hardware.getProcessor().getMaxFreq();
  }
  
  public int getProcessorPhysicalCount() {
    return this.hardware.getProcessor().getPhysicalProcessorCount();
  }
  
  public int getProcessorLogicalCount() {
    return this.hardware.getProcessor().getLogicalProcessorCount();
  }
  
  public boolean isBattery() {
    List list = this.hardware.getPowerSources();
    return (list != null && !list.isEmpty());
  }
  
  public String getHWDiskID() {
    List list = this.hardware.getDiskStores();
    long l = 0L;
    HWDiskStore hWDiskStore = null;
    for (HWDiskStore hWDiskStore1 : list) {
      if (hWDiskStore1.getSize() > l) {
        hWDiskStore = hWDiskStore1;
        l = hWDiskStore1.getSize();
      } 
    } 
    return (hWDiskStore != null) ? hWDiskStore.getSerial() : null;
  }
  
  public GraphicsCard getGraphicCard() {
    List list = this.hardware.getGraphicsCards();
    GraphicsCard graphicsCard = null;
    long l = 0L;
    for (GraphicsCard graphicsCard1 : list) {
      long l1 = graphicsCard1.getVRam();
      if (l1 > l) {
        graphicsCard = graphicsCard1;
        l = l1;
      } 
    } 
    return graphicsCard;
  }
  
  public String getGraphicCardName() {
    GraphicsCard graphicsCard = getGraphicCard();
    return (graphicsCard == null) ? null : graphicsCard.getName();
  }
  
  public long getGraphicCardMemory() {
    GraphicsCard graphicsCard = getGraphicCard();
    return (graphicsCard == null) ? 0L : graphicsCard.getVRam();
  }
  
  public byte[] getDisplayID() {
    List list = this.hardware.getDisplays();
    if (list == null || list.isEmpty())
      return null; 
    Iterator<Display> iterator = list.iterator();
    if (iterator.hasNext()) {
      Display display = iterator.next();
      return display.getEdid();
    } 
    return null;
  }
  
  public String getBaseboardSerialNumber() {
    return this.hardware.getComputerSystem().getBaseboard().getSerialNumber();
  }
  
  public HardwareReportRequest.HardwareInfo getHardwareInfo(boolean paramBoolean) {
    HardwareReportRequest.HardwareInfo hardwareInfo = new HardwareReportRequest.HardwareInfo();
    hardwareInfo.bitness = getBitness();
    hardwareInfo.logicalProcessors = getProcessorLogicalCount();
    hardwareInfo.physicalProcessors = getProcessorPhysicalCount();
    hardwareInfo.processorMaxFreq = getProcessorMaxFreq();
    hardwareInfo.totalMemory = getTotalMemory();
    hardwareInfo.battery = isBattery();
    hardwareInfo.graphicCard = getGraphicCardName();
    if (paramBoolean) {
      hardwareInfo.hwDiskId = getHWDiskID();
      hardwareInfo.displayId = getDisplayID();
      hardwareInfo.baseboardSerialNumber = getBaseboardSerialNumber();
    } 
    return hardwareInfo;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtim\\utils\HWIDProvider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */