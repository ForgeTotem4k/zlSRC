package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Processor {
  private static final String WIN32_PROCESSOR = "Win32_Processor";
  
  public static WbemcliUtil.WmiResult<VoltProperty> queryVoltage() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_Processor", VoltProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public static WbemcliUtil.WmiResult<ProcessorIdProperty> queryProcessorId() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_Processor", ProcessorIdProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public static WbemcliUtil.WmiResult<BitnessProperty> queryBitness() {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("Win32_Processor", BitnessProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum VoltProperty {
    CURRENTVOLTAGE, VOLTAGECAPS;
  }
  
  public enum ProcessorIdProperty {
    PROCESSORID;
  }
  
  public enum BitnessProperty {
    ADDRESSWIDTH;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32Processor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */