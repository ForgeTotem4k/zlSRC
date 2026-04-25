package oshi.hardware.platform.unix.openbsd;

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
public final class OpenBsdHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new OpenBsdComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new OpenBsdGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new OpenBsdCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new OpenBsdSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return OpenBsdPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return OpenBsdHWDiskStore.getDisks();
  }
  
  public List<Display> getDisplays() {
    return UnixDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean paramBoolean) {
    return BsdNetworkIF.getNetworks(paramBoolean);
  }
  
  public List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    return OpenBsdUsbDevice.getUsbDevices(paramBoolean);
  }
  
  public List<SoundCard> getSoundCards() {
    return OpenBsdSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return OpenBsdGraphicsCard.getGraphicsCards();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */