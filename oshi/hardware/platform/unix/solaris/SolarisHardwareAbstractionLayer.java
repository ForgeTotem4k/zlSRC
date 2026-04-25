package oshi.hardware.platform.unix.solaris;

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
import oshi.hardware.platform.unix.UnixDisplay;

@ThreadSafe
public final class SolarisHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new SolarisComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new SolarisGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new SolarisCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new SolarisSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return SolarisPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return SolarisHWDiskStore.getDisks();
  }
  
  public List<Display> getDisplays() {
    return UnixDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean paramBoolean) {
    return SolarisNetworkIF.getNetworks(paramBoolean);
  }
  
  public List<UsbDevice> getUsbDevices(boolean paramBoolean) {
    return SolarisUsbDevice.getUsbDevices(paramBoolean);
  }
  
  public List<SoundCard> getSoundCards() {
    return SolarisSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return SolarisGraphicsCard.getGraphicsCards();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */