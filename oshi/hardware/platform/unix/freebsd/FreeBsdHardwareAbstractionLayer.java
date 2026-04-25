package oshi.hardware.platform.unix.freebsd;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;
import oshi.hardware.platform.unix.BsdNetworkIF;
import oshi.hardware.platform.unix.UnixDisplay;

@ThreadSafe
public final class FreeBsdHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new FreeBsdComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new FreeBsdGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new FreeBsdCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new FreeBsdSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return FreeBsdPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return FreeBsdHWDiskStore.getDisks();
  }
  
  public List<Display> getDisplays() {
    return UnixDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean paramBoolean) {
    return BsdNetworkIF.getNetworks(paramBoolean);
  }
  
  public List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    return FreeBsdUsbDevice.getUsbDevices(paramBoolean);
  }
  
  public List<SoundCard> getSoundCards() {
    return FreeBsdSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return FreeBsdGraphicsCard.getGraphicsCards();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */