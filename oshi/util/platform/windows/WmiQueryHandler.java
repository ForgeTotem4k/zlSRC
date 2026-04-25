package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public class WmiQueryHandler {
  private static final Logger LOG = LoggerFactory.getLogger(WmiQueryHandler.class);
  
  private static int globalTimeout = GlobalConfig.get("oshi.util.wmi.timeout", -1);
  
  private int wmiTimeout = globalTimeout;
  
  private final Set<String> failedWmiClassNames = new HashSet<>();
  
  private int comThreading = 0;
  
  private boolean securityInitialized = false;
  
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
  
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  
  private static Class<? extends WmiQueryHandler> customClass = null;
  
  public static synchronized WmiQueryHandler createInstance() {
    if (customClass == null)
      return new WmiQueryHandler(); 
    try {
      return customClass.getConstructor(EMPTY_CLASS_ARRAY).newInstance(EMPTY_OBJECT_ARRAY);
    } catch (NoSuchMethodException|SecurityException noSuchMethodException) {
      LOG.error("Failed to find or access a no-arg constructor for {}", customClass);
    } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException instantiationException) {
      LOG.error("Failed to create a new instance of {}", customClass);
    } 
    return null;
  }
  
  public static synchronized void setInstanceClass(Class<? extends WmiQueryHandler> paramClass) {
    customClass = paramClass;
  }
  
  public <T extends Enum<T>> WbemcliUtil.WmiResult<T> queryWMI(WbemcliUtil.WmiQuery<T> paramWmiQuery) {
    return queryWMI(paramWmiQuery, true);
  }
  
  public <T extends Enum<T>> WbemcliUtil.WmiResult<T> queryWMI(WbemcliUtil.WmiQuery<T> paramWmiQuery, boolean paramBoolean) {
    Objects.requireNonNull(WbemcliUtil.INSTANCE);
    WbemcliUtil.WmiResult<T> wmiResult = new WbemcliUtil.WmiResult(WbemcliUtil.INSTANCE, paramWmiQuery.getPropertyEnum());
    if (this.failedWmiClassNames.contains(paramWmiQuery.getWmiClassName()))
      return wmiResult; 
    boolean bool = false;
    try {
      if (paramBoolean)
        bool = initCOM(); 
      wmiResult = paramWmiQuery.execute(this.wmiTimeout);
    } catch (COMException cOMException) {
      if (!"ROOT\\OpenHardwareMonitor".equals(paramWmiQuery.getNameSpace())) {
        boolean bool1 = (cOMException.getHresult() == null) ? true : cOMException.getHresult().intValue();
        switch (bool1) {
          case true:
            LOG.warn("COM exception: Invalid Namespace {}", paramWmiQuery.getNameSpace());
            break;
          case true:
            LOG.warn("COM exception: Invalid Class {}", paramWmiQuery.getWmiClassName());
            break;
          case true:
            LOG.warn("COM exception: Invalid Query: {}", WmiUtil.queryToString(paramWmiQuery));
            break;
          default:
            handleComException(paramWmiQuery, cOMException);
            break;
        } 
        this.failedWmiClassNames.add(paramWmiQuery.getWmiClassName());
      } 
    } catch (TimeoutException timeoutException) {
      LOG.warn("WMI query timed out after {} ms: {}", Integer.valueOf(this.wmiTimeout), WmiUtil.queryToString(paramWmiQuery));
    } finally {
      if (bool)
        unInitCOM(); 
    } 
    return wmiResult;
  }
  
  protected void handleComException(WbemcliUtil.WmiQuery<?> paramWmiQuery, COMException paramCOMException) {
    LOG.warn("COM exception querying {}, which might not be on your system. Will not attempt to query it again. Error was {}: {}", new Object[] { paramWmiQuery.getWmiClassName(), (paramCOMException.getHresult() == null) ? null : Integer.valueOf(paramCOMException.getHresult().intValue()), paramCOMException.getMessage() });
  }
  
  public boolean initCOM() {
    boolean bool = false;
    bool = initCOM(getComThreading());
    if (!bool)
      bool = initCOM(switchComThreading()); 
    if (bool && !isSecurityInitialized()) {
      WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, 0, 3, null, 0, null);
      if (COMUtils.FAILED(hRESULT) && hRESULT.intValue() != -2147417831) {
        Ole32.INSTANCE.CoUninitialize();
        throw new COMException("Failed to initialize security.", hRESULT);
      } 
      this.securityInitialized = true;
    } 
    return bool;
  }
  
  protected boolean initCOM(int paramInt) {
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoInitializeEx(null, paramInt);
    switch (hRESULT.intValue()) {
      case 0:
      case 1:
        return true;
      case -2147417850:
        return false;
    } 
    throw new COMException("Failed to initialize COM library.", hRESULT);
  }
  
  public void unInitCOM() {
    Ole32.INSTANCE.CoUninitialize();
  }
  
  public int getComThreading() {
    return this.comThreading;
  }
  
  public int switchComThreading() {
    if (this.comThreading == 2) {
      this.comThreading = 0;
    } else {
      this.comThreading = 2;
    } 
    return this.comThreading;
  }
  
  public boolean isSecurityInitialized() {
    return this.securityInitialized;
  }
  
  public int getWmiTimeout() {
    return this.wmiTimeout;
  }
  
  public void setWmiTimeout(int paramInt) {
    this.wmiTimeout = paramInt;
  }
  
  static {
    if (globalTimeout == 0 || globalTimeout < -1)
      throw new GlobalConfig.PropertyException("oshi.util.wmi.timeout"); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\WmiQueryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */