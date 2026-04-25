package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class WbemcliUtil {
  public static final WbemcliUtil INSTANCE = new WbemcliUtil();
  
  public static final String DEFAULT_NAMESPACE = "ROOT\\CIMV2";
  
  public static boolean hasNamespace(String paramString) {
    String str = paramString;
    if (paramString.toUpperCase().startsWith("ROOT\\"))
      str = paramString.substring(5); 
    WmiQuery<NamespaceProperty> wmiQuery = new WmiQuery<>("ROOT", "__NAMESPACE", NamespaceProperty.class);
    WmiResult<NamespaceProperty> wmiResult = wmiQuery.execute();
    for (byte b = 0; b < wmiResult.getResultCount(); b++) {
      if (str.equalsIgnoreCase((String)wmiResult.getValue(NamespaceProperty.NAME, b)))
        return true; 
    } 
    return false;
  }
  
  public static Wbemcli.IWbemServices connectServer(String paramString) {
    Wbemcli.IWbemLocator iWbemLocator = Wbemcli.IWbemLocator.create();
    if (iWbemLocator == null)
      throw new COMException("Failed to create WbemLocator object."); 
    Wbemcli.IWbemServices iWbemServices = iWbemLocator.ConnectServer(paramString, (String)null, (String)null, (String)null, 0, (String)null, (Wbemcli.IWbemContext)null);
    iWbemLocator.Release();
    WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoSetProxyBlanket(iWbemServices, 10, 0, null, 3, 3, null, 0);
    if (COMUtils.FAILED(hRESULT)) {
      iWbemServices.Release();
      throw new COMException("Could not set proxy blanket.", hRESULT);
    } 
    return iWbemServices;
  }
  
  public static class WmiQuery<T extends Enum<T>> {
    private String nameSpace;
    
    private String wmiClassName;
    
    private Class<T> propertyEnum;
    
    public WmiQuery(String param1String1, String param1String2, Class<T> param1Class) {
      this.nameSpace = param1String1;
      this.wmiClassName = param1String2;
      this.propertyEnum = param1Class;
    }
    
    public WmiQuery(String param1String, Class<T> param1Class) {
      this("ROOT\\CIMV2", param1String, param1Class);
    }
    
    public Class<T> getPropertyEnum() {
      return this.propertyEnum;
    }
    
    public String getNameSpace() {
      return this.nameSpace;
    }
    
    public void setNameSpace(String param1String) {
      this.nameSpace = param1String;
    }
    
    public String getWmiClassName() {
      return this.wmiClassName;
    }
    
    public void setWmiClassName(String param1String) {
      this.wmiClassName = param1String;
    }
    
    public WbemcliUtil.WmiResult<T> execute() {
      try {
        return execute(-1);
      } catch (TimeoutException timeoutException) {
        throw new COMException("Got a WMI timeout when infinite wait was specified. This should never happen.");
      } 
    }
    
    public WbemcliUtil.WmiResult<T> execute(int param1Int) throws TimeoutException {
      if (((Enum[])getPropertyEnum().getEnumConstants()).length < 1)
        throw new IllegalArgumentException("The query's property enum has no values."); 
      Wbemcli.IWbemServices iWbemServices = WbemcliUtil.connectServer(getNameSpace());
      try {
        Wbemcli.IEnumWbemClassObject iEnumWbemClassObject = selectProperties(iWbemServices, this);
      } finally {
        iWbemServices.Release();
      } 
    }
    
    private static <T extends Enum<T>> Wbemcli.IEnumWbemClassObject selectProperties(Wbemcli.IWbemServices param1IWbemServices, WmiQuery<T> param1WmiQuery) {
      Enum[] arrayOfEnum = param1WmiQuery.getPropertyEnum().getEnumConstants();
      StringBuilder stringBuilder = new StringBuilder("SELECT ");
      stringBuilder.append(arrayOfEnum[0].name());
      for (byte b = 1; b < arrayOfEnum.length; b++)
        stringBuilder.append(',').append(arrayOfEnum[b].name()); 
      stringBuilder.append(" FROM ").append(param1WmiQuery.getWmiClassName());
      return param1IWbemServices.ExecQuery("WQL", stringBuilder.toString().replaceAll("\\\\", "\\\\\\\\"), 48, null);
    }
    
    private static <T extends Enum<T>> WbemcliUtil.WmiResult<T> enumerateProperties(Wbemcli.IEnumWbemClassObject param1IEnumWbemClassObject, Class<T> param1Class, int param1Int) throws TimeoutException {
      Objects.requireNonNull(WbemcliUtil.INSTANCE);
      WbemcliUtil.WmiResult<T> wmiResult = new WbemcliUtil.WmiResult<>(param1Class);
      Pointer[] arrayOfPointer = new Pointer[1];
      IntByReference intByReference = new IntByReference(0);
      HashMap<Object, Object> hashMap = new HashMap<>();
      WinNT.HRESULT hRESULT = null;
      for (Enum enum_ : (Enum[])param1Class.getEnumConstants())
        hashMap.put(enum_, new WString(enum_.name())); 
      while (param1IEnumWbemClassObject.getPointer() != Pointer.NULL) {
        hRESULT = param1IEnumWbemClassObject.Next(param1Int, arrayOfPointer.length, arrayOfPointer, intByReference);
        if (hRESULT.intValue() == 1 || hRESULT.intValue() == 262149)
          break; 
        if (hRESULT.intValue() == 262148)
          throw new TimeoutException("No results after " + param1Int + " ms."); 
        if (COMUtils.FAILED(hRESULT))
          throw new COMException("Failed to enumerate results.", hRESULT); 
        Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
        IntByReference intByReference1 = new IntByReference();
        Wbemcli.IWbemClassObject iWbemClassObject = new Wbemcli.IWbemClassObject(arrayOfPointer[0]);
        for (Enum enum_ : (Enum[])param1Class.getEnumConstants()) {
          iWbemClassObject.Get((WString)hashMap.get(enum_), 0, byReference, intByReference1, (IntByReference)null);
          int i = ((byReference.getValue() == null) ? Integer.valueOf(1) : byReference.getVarType()).intValue();
          int j = intByReference1.getValue();
          switch (i) {
            case 8:
              wmiResult.add(i, j, (T)enum_, byReference.stringValue());
              break;
            case 3:
              wmiResult.add(i, j, (T)enum_, Integer.valueOf(byReference.intValue()));
              break;
            case 17:
              wmiResult.add(i, j, (T)enum_, Byte.valueOf(byReference.byteValue()));
              break;
            case 2:
              wmiResult.add(i, j, (T)enum_, Short.valueOf(byReference.shortValue()));
              break;
            case 11:
              wmiResult.add(i, j, (T)enum_, Boolean.valueOf(byReference.booleanValue()));
              break;
            case 4:
              wmiResult.add(i, j, (T)enum_, Float.valueOf(byReference.floatValue()));
              break;
            case 5:
              wmiResult.add(i, j, (T)enum_, Double.valueOf(byReference.doubleValue()));
              break;
            case 0:
            case 1:
              wmiResult.add(i, j, (T)enum_, null);
              break;
            default:
              if ((i & 0x2000) == 8192 || (i & 0xD) == 13 || (i & 0x9) == 9 || (i & 0x1000) == 4096) {
                wmiResult.add(i, j, (T)enum_, null);
                break;
              } 
              wmiResult.add(i, j, (T)enum_, byReference.getValue());
              break;
          } 
          OleAuto.INSTANCE.VariantClear((Variant.VARIANT)byReference);
        } 
        iWbemClassObject.Release();
        wmiResult.incrementResultCount();
      } 
      return wmiResult;
    }
  }
  
  private enum NamespaceProperty {
    NAME;
  }
  
  public class WmiResult<T extends Enum<T>> {
    private Map<T, List<Object>> propertyMap;
    
    private Map<T, Integer> vtTypeMap;
    
    private Map<T, Integer> cimTypeMap;
    
    private int resultCount = 0;
    
    public WmiResult(Class<T> param1Class) {
      this.propertyMap = new EnumMap<>(param1Class);
      this.vtTypeMap = new EnumMap<>(param1Class);
      this.cimTypeMap = new EnumMap<>(param1Class);
      for (Enum enum_ : (Enum[])param1Class.getEnumConstants()) {
        this.propertyMap.put((T)enum_, new ArrayList());
        this.vtTypeMap.put((T)enum_, Integer.valueOf(1));
        this.cimTypeMap.put((T)enum_, Integer.valueOf(0));
      } 
    }
    
    public Object getValue(T param1T, int param1Int) {
      return ((List)this.propertyMap.get(param1T)).get(param1Int);
    }
    
    public int getVtType(T param1T) {
      return ((Integer)this.vtTypeMap.get(param1T)).intValue();
    }
    
    public int getCIMType(T param1T) {
      return ((Integer)this.cimTypeMap.get(param1T)).intValue();
    }
    
    private void add(int param1Int1, int param1Int2, T param1T, Object param1Object) {
      ((List<Object>)this.propertyMap.get(param1T)).add(param1Object);
      if (param1Int1 != 1 && ((Integer)this.vtTypeMap.get(param1T)).equals(Integer.valueOf(1)))
        this.vtTypeMap.put(param1T, Integer.valueOf(param1Int1)); 
      if (((Integer)this.cimTypeMap.get(param1T)).equals(Integer.valueOf(0)))
        this.cimTypeMap.put(param1T, Integer.valueOf(param1Int2)); 
    }
    
    public int getResultCount() {
      return this.resultCount;
    }
    
    private void incrementResultCount() {
      this.resultCount++;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\WbemcliUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */