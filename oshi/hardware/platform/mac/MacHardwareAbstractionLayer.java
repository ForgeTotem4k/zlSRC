package oshi.hardware.platform.mac;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;

@ThreadSafe
public final class MacHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new MacComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new MacGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new MacCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new MacSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return MacPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return MacHWDiskStore.getDisks();
  }
  
  public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    return MacLogicalVolumeGroup.getLogicalVolumeGroups();
  }
  
  public List<Display> getDisplays() {
    return MacDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean paramBoolean) {
    return MacNetworkIF.getNetworks(paramBoolean);
  }
  
  public List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    return MacUsbDevice.getUsbDevices(paramBoolean);
  }
  
  public List<SoundCard> getSoundCards() {
    return MacSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return MacGraphicsCard.getGraphicsCards();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */