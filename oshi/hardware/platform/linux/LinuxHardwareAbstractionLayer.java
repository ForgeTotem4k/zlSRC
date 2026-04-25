package oshi.hardware.platform.linux;

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
import oshi.hardware.platform.unix.UnixDisplay;

@ThreadSafe
public final class LinuxHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new LinuxComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new LinuxGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new LinuxCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new LinuxSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return LinuxPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return LinuxHWDiskStore.getDisks();
  }
  
  public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    return LinuxLogicalVolumeGroup.getLogicalVolumeGroups();
  }
  
  public List<Display> getDisplays() {
    return UnixDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean paramBoolean) {
    return LinuxNetworkIF.getNetworks(paramBoolean);
  }
  
  public List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    return LinuxUsbDevice.getUsbDevices(paramBoolean);
  }
  
  public List<SoundCard> getSoundCards() {
    return LinuxSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return LinuxGraphicsCard.getGraphicsCards();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */