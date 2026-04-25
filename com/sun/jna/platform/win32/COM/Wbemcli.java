package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdlUtil;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface Wbemcli {
  public static final int WBEM_FLAG_RETURN_WBEM_COMPLETE = 0;
  
  public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 16;
  
  public static final int WBEM_FLAG_FORWARD_ONLY = 32;
  
  public static final int WBEM_FLAG_NO_ERROR_OBJECT = 64;
  
  public static final int WBEM_FLAG_SEND_STATUS = 128;
  
  public static final int WBEM_FLAG_ENSURE_LOCATABLE = 256;
  
  public static final int WBEM_FLAG_DIRECT_READ = 512;
  
  public static final int WBEM_MASK_RESERVED_FLAGS = 126976;
  
  public static final int WBEM_FLAG_USE_AMENDED_QUALIFIERS = 131072;
  
  public static final int WBEM_FLAG_STRONG_VALIDATION = 1048576;
  
  public static final int WBEM_INFINITE = -1;
  
  public static final int WBEM_S_NO_ERROR = 0;
  
  public static final int WBEM_S_FALSE = 1;
  
  public static final int WBEM_S_TIMEDOUT = 262148;
  
  public static final int WBEM_S_NO_MORE_DATA = 262149;
  
  public static final int WBEM_E_INVALID_NAMESPACE = -2147217394;
  
  public static final int WBEM_E_INVALID_CLASS = -2147217392;
  
  public static final int WBEM_E_INVALID_QUERY = -2147217385;
  
  public static final int CIM_ILLEGAL = 4095;
  
  public static final int CIM_EMPTY = 0;
  
  public static final int CIM_SINT8 = 16;
  
  public static final int CIM_UINT8 = 17;
  
  public static final int CIM_SINT16 = 2;
  
  public static final int CIM_UINT16 = 18;
  
  public static final int CIM_SINT32 = 3;
  
  public static final int CIM_UINT32 = 19;
  
  public static final int CIM_SINT64 = 20;
  
  public static final int CIM_UINT64 = 21;
  
  public static final int CIM_REAL32 = 4;
  
  public static final int CIM_REAL64 = 5;
  
  public static final int CIM_BOOLEAN = 11;
  
  public static final int CIM_STRING = 8;
  
  public static final int CIM_DATETIME = 101;
  
  public static final int CIM_REFERENCE = 102;
  
  public static final int CIM_CHAR16 = 103;
  
  public static final int CIM_OBJECT = 13;
  
  public static final int CIM_FLAG_ARRAY = 8192;
  
  public static class IWbemContext extends Unknown {
    public static final Guid.CLSID CLSID_WbemContext = new Guid.CLSID("674B6698-EE92-11D0-AD71-00C04FD8FDFF");
    
    public static final Guid.GUID IID_IWbemContext = new Guid.GUID("44aca674-e8fc-11d0-a07c-00c04fb68820");
    
    public IWbemContext() {}
    
    public static IWbemContext create() {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoCreateInstance((Guid.GUID)CLSID_WbemContext, null, 1, IID_IWbemContext, pointerByReference);
      return COMUtils.FAILED(hRESULT) ? null : new IWbemContext(pointerByReference.getValue());
    }
    
    public IWbemContext(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public void SetValue(String param1String, int param1Int, Variant.VARIANT param1VARIANT) {
      WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString(param1String);
      try {
        WinNT.HRESULT hRESULT = (WinNT.HRESULT)_invokeNativeObject(8, new Object[] { getPointer(), bSTR, Integer.valueOf(param1Int), param1VARIANT }, WinNT.HRESULT.class);
        COMUtils.checkRC(hRESULT);
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR);
      } 
    }
    
    public void SetValue(String param1String, int param1Int, boolean param1Boolean) {
      Variant.VARIANT vARIANT = new Variant.VARIANT();
      vARIANT.setValue(11, param1Boolean ? Variant.VARIANT_TRUE : Variant.VARIANT_FALSE);
      SetValue(param1String, param1Int, vARIANT);
      OleAuto.INSTANCE.VariantClear(vARIANT);
    }
    
    public void SetValue(String param1String1, int param1Int, String param1String2) {
      Variant.VARIANT vARIANT = new Variant.VARIANT();
      WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString(param1String2);
      try {
        vARIANT.setValue(30, bSTR);
        SetValue(param1String1, param1Int, vARIANT);
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR);
      } 
    }
  }
  
  public static class IWbemServices extends Unknown {
    public IWbemServices() {}
    
    public IWbemServices(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public WinNT.HRESULT ExecMethod(WTypes.BSTR param1BSTR1, WTypes.BSTR param1BSTR2, int param1Int, Wbemcli.IWbemContext param1IWbemContext, Pointer param1Pointer, PointerByReference param1PointerByReference1, PointerByReference param1PointerByReference2) {
      return (WinNT.HRESULT)_invokeNativeObject(24, new Object[] { getPointer(), param1BSTR1, param1BSTR2, Integer.valueOf(param1Int), param1IWbemContext, param1Pointer, param1PointerByReference1, param1PointerByReference2 }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemClassObject ExecMethod(String param1String1, String param1String2, int param1Int, Wbemcli.IWbemContext param1IWbemContext, Wbemcli.IWbemClassObject param1IWbemClassObject) {
      WTypes.BSTR bSTR1 = OleAuto.INSTANCE.SysAllocString(param1String1);
      WTypes.BSTR bSTR2 = OleAuto.INSTANCE.SysAllocString(param1String2);
      try {
        PointerByReference pointerByReference = new PointerByReference();
        WinNT.HRESULT hRESULT = ExecMethod(bSTR1, bSTR2, param1Int, param1IWbemContext, param1IWbemClassObject.getPointer(), pointerByReference, (PointerByReference)null);
        COMUtils.checkRC(hRESULT);
        return new Wbemcli.IWbemClassObject(pointerByReference.getValue());
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR1);
        OleAuto.INSTANCE.SysFreeString(bSTR2);
      } 
    }
    
    public WinNT.HRESULT ExecQuery(WTypes.BSTR param1BSTR1, WTypes.BSTR param1BSTR2, int param1Int, Wbemcli.IWbemContext param1IWbemContext, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(20, new Object[] { getPointer(), param1BSTR1, param1BSTR2, Integer.valueOf(param1Int), param1IWbemContext, param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IEnumWbemClassObject ExecQuery(String param1String1, String param1String2, int param1Int, Wbemcli.IWbemContext param1IWbemContext) {
      WTypes.BSTR bSTR1 = OleAuto.INSTANCE.SysAllocString(param1String1);
      WTypes.BSTR bSTR2 = OleAuto.INSTANCE.SysAllocString(param1String2);
      try {
        PointerByReference pointerByReference = new PointerByReference();
        WinNT.HRESULT hRESULT = ExecQuery(bSTR1, bSTR2, param1Int, param1IWbemContext, pointerByReference);
        COMUtils.checkRC(hRESULT);
        return new Wbemcli.IEnumWbemClassObject(pointerByReference.getValue());
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR1);
        OleAuto.INSTANCE.SysFreeString(bSTR2);
      } 
    }
    
    public WinNT.HRESULT GetObject(WTypes.BSTR param1BSTR, int param1Int, Wbemcli.IWbemContext param1IWbemContext, PointerByReference param1PointerByReference1, PointerByReference param1PointerByReference2) {
      return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), param1BSTR, Integer.valueOf(param1Int), param1IWbemContext, param1PointerByReference1, param1PointerByReference2 }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemClassObject GetObject(String param1String, int param1Int, Wbemcli.IWbemContext param1IWbemContext) {
      WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString(param1String);
      try {
        PointerByReference pointerByReference = new PointerByReference();
        WinNT.HRESULT hRESULT = GetObject(bSTR, param1Int, param1IWbemContext, pointerByReference, (PointerByReference)null);
        COMUtils.checkRC(hRESULT);
        return new Wbemcli.IWbemClassObject(pointerByReference.getValue());
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR);
      } 
    }
  }
  
  public static class IWbemLocator extends Unknown {
    public static final Guid.CLSID CLSID_WbemLocator = new Guid.CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
    
    public static final Guid.GUID IID_IWbemLocator = new Guid.GUID("dc12a687-737f-11cf-884d-00aa004b2e24");
    
    public IWbemLocator() {}
    
    private IWbemLocator(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public static IWbemLocator create() {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = Ole32.INSTANCE.CoCreateInstance((Guid.GUID)CLSID_WbemLocator, null, 1, IID_IWbemLocator, pointerByReference);
      return COMUtils.FAILED(hRESULT) ? null : new IWbemLocator(pointerByReference.getValue());
    }
    
    public WinNT.HRESULT ConnectServer(WTypes.BSTR param1BSTR1, WTypes.BSTR param1BSTR2, WTypes.BSTR param1BSTR3, WTypes.BSTR param1BSTR4, int param1Int, WTypes.BSTR param1BSTR5, Wbemcli.IWbemContext param1IWbemContext, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), param1BSTR1, param1BSTR2, param1BSTR3, param1BSTR4, Integer.valueOf(param1Int), param1BSTR5, param1IWbemContext, param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemServices ConnectServer(String param1String1, String param1String2, String param1String3, String param1String4, int param1Int, String param1String5, Wbemcli.IWbemContext param1IWbemContext) {
      WTypes.BSTR bSTR1 = OleAuto.INSTANCE.SysAllocString(param1String1);
      WTypes.BSTR bSTR2 = OleAuto.INSTANCE.SysAllocString(param1String2);
      WTypes.BSTR bSTR3 = OleAuto.INSTANCE.SysAllocString(param1String3);
      WTypes.BSTR bSTR4 = OleAuto.INSTANCE.SysAllocString(param1String4);
      WTypes.BSTR bSTR5 = OleAuto.INSTANCE.SysAllocString(param1String5);
      PointerByReference pointerByReference = new PointerByReference();
      try {
        WinNT.HRESULT hRESULT = ConnectServer(bSTR1, bSTR2, bSTR3, bSTR4, param1Int, bSTR5, param1IWbemContext, pointerByReference);
        COMUtils.checkRC(hRESULT);
        return new Wbemcli.IWbemServices(pointerByReference.getValue());
      } finally {
        OleAuto.INSTANCE.SysFreeString(bSTR1);
        OleAuto.INSTANCE.SysFreeString(bSTR2);
        OleAuto.INSTANCE.SysFreeString(bSTR3);
        OleAuto.INSTANCE.SysFreeString(bSTR4);
        OleAuto.INSTANCE.SysFreeString(bSTR5);
      } 
    }
  }
  
  public static class IEnumWbemClassObject extends Unknown {
    public IEnumWbemClassObject() {}
    
    public IEnumWbemClassObject(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public WinNT.HRESULT Next(int param1Int1, int param1Int2, Pointer[] param1ArrayOfPointer, IntByReference param1IntByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), Integer.valueOf(param1Int1), Integer.valueOf(param1Int2), param1ArrayOfPointer, param1IntByReference }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemClassObject[] Next(int param1Int1, int param1Int2) {
      Pointer[] arrayOfPointer = new Pointer[param1Int2];
      IntByReference intByReference = new IntByReference();
      WinNT.HRESULT hRESULT = Next(param1Int1, param1Int2, arrayOfPointer, intByReference);
      COMUtils.checkRC(hRESULT);
      Wbemcli.IWbemClassObject[] arrayOfIWbemClassObject = new Wbemcli.IWbemClassObject[intByReference.getValue()];
      for (byte b = 0; b < intByReference.getValue(); b++)
        arrayOfIWbemClassObject[b] = new Wbemcli.IWbemClassObject(arrayOfPointer[b]); 
      return arrayOfIWbemClassObject;
    }
  }
  
  public static class IWbemQualifierSet extends Unknown {
    public IWbemQualifierSet(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public WinNT.HRESULT Get(WString param1WString, int param1Int, Variant.VARIANT.ByReference param1ByReference, IntByReference param1IntByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), param1WString, Integer.valueOf(param1Int), param1ByReference, param1IntByReference }, WinNT.HRESULT.class);
    }
    
    public String Get(String param1String) {
      WString wString = new WString(param1String);
      Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
      WinNT.HRESULT hRESULT = Get(wString, 0, byReference, (IntByReference)null);
      if (hRESULT.intValue() == -2147217406)
        return null; 
      int i = byReference.getVarType().intValue();
      switch (i) {
        case 11:
          return String.valueOf(byReference.booleanValue());
        case 8:
          return byReference.stringValue();
      } 
      return null;
    }
    
    public WinNT.HRESULT GetNames(int param1Int, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] { getPointer(), Integer.valueOf(param1Int), param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public String[] GetNames() {
      PointerByReference pointerByReference = new PointerByReference();
      COMUtils.checkRC(GetNames(0, pointerByReference));
      Object[] arrayOfObject = (Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pointerByReference.getValue()), true);
      String[] arrayOfString = new String[arrayOfObject.length];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfString[b] = (String)arrayOfObject[b]; 
      return arrayOfString;
    }
  }
  
  public static class IWbemClassObject extends Unknown {
    public IWbemClassObject() {}
    
    public IWbemClassObject(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public WinNT.HRESULT Get(WString param1WString, int param1Int, Variant.VARIANT.ByReference param1ByReference, IntByReference param1IntByReference1, IntByReference param1IntByReference2) {
      return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), param1WString, Integer.valueOf(param1Int), param1ByReference, param1IntByReference1, param1IntByReference2 }, WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT Get(String param1String, int param1Int, Variant.VARIANT.ByReference param1ByReference, IntByReference param1IntByReference1, IntByReference param1IntByReference2) {
      return Get((param1String == null) ? null : new WString(param1String), param1Int, param1ByReference, param1IntByReference1, param1IntByReference2);
    }
    
    public WinNT.HRESULT GetMethod(String param1String, int param1Int, PointerByReference param1PointerByReference1, PointerByReference param1PointerByReference2) {
      return GetMethod((param1String == null) ? null : new WString(param1String), param1Int, param1PointerByReference1, param1PointerByReference2);
    }
    
    public WinNT.HRESULT GetMethod(WString param1WString, int param1Int, PointerByReference param1PointerByReference1, PointerByReference param1PointerByReference2) {
      return (WinNT.HRESULT)_invokeNativeObject(19, new Object[] { getPointer(), param1WString, Integer.valueOf(param1Int), param1PointerByReference1, param1PointerByReference2 }, WinNT.HRESULT.class);
    }
    
    public IWbemClassObject GetMethod(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = GetMethod(param1String, 0, pointerByReference, (PointerByReference)null);
      COMUtils.checkRC(hRESULT);
      return new IWbemClassObject(pointerByReference.getValue());
    }
    
    public WinNT.HRESULT GetNames(String param1String, int param1Int, Variant.VARIANT.ByReference param1ByReference, PointerByReference param1PointerByReference) {
      return GetNames((param1String == null) ? null : new WString(param1String), param1Int, param1ByReference, param1PointerByReference);
    }
    
    public WinNT.HRESULT GetNames(WString param1WString, int param1Int, Variant.VARIANT.ByReference param1ByReference, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] { getPointer(), param1WString, Integer.valueOf(param1Int), param1ByReference, param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public String[] GetNames(String param1String, int param1Int, Variant.VARIANT.ByReference param1ByReference) {
      PointerByReference pointerByReference = new PointerByReference();
      COMUtils.checkRC(GetNames(param1String, param1Int, param1ByReference, pointerByReference));
      Object[] arrayOfObject = (Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pointerByReference.getValue()), true);
      String[] arrayOfString = new String[arrayOfObject.length];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfString[b] = (String)arrayOfObject[b]; 
      return arrayOfString;
    }
    
    public WinNT.HRESULT GetQualifierSet(PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemQualifierSet GetQualifierSet() {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = GetQualifierSet(pointerByReference);
      COMUtils.checkRC(hRESULT);
      return new Wbemcli.IWbemQualifierSet(pointerByReference.getValue());
    }
    
    public WinNT.HRESULT GetPropertyQualifierSet(WString param1WString, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(11, new Object[] { getPointer(), param1WString, param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public Wbemcli.IWbemQualifierSet GetPropertyQualifierSet(String param1String) {
      WString wString = new WString(param1String);
      PointerByReference pointerByReference = new PointerByReference();
      COMUtils.checkRC(GetPropertyQualifierSet(wString, pointerByReference));
      return new Wbemcli.IWbemQualifierSet(pointerByReference.getValue());
    }
    
    public WinNT.HRESULT Put(String param1String, int param1Int1, Variant.VARIANT param1VARIANT, int param1Int2) {
      return Put((param1String == null) ? null : new WString(param1String), param1Int1, param1VARIANT, param1Int2);
    }
    
    public WinNT.HRESULT Put(WString param1WString, int param1Int1, Variant.VARIANT param1VARIANT, int param1Int2) {
      return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer(), param1WString, Integer.valueOf(param1Int1), param1VARIANT, Integer.valueOf(param1Int2) }, WinNT.HRESULT.class);
    }
    
    public void Put(String param1String1, String param1String2) {
      Variant.VARIANT vARIANT = new Variant.VARIANT();
      WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString(param1String2);
      try {
        vARIANT.setValue(8, bSTR);
        WinNT.HRESULT hRESULT = Put(param1String1, 0, vARIANT, 0);
        COMUtils.checkRC(hRESULT);
      } finally {
        OleAuto.INSTANCE.VariantClear(vARIANT);
      } 
    }
    
    public WinNT.HRESULT SpawnInstance(int param1Int, PointerByReference param1PointerByReference) {
      return (WinNT.HRESULT)_invokeNativeObject(15, new Object[] { getPointer(), Integer.valueOf(param1Int), param1PointerByReference }, WinNT.HRESULT.class);
    }
    
    public IWbemClassObject SpawnInstance() {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = SpawnInstance(0, pointerByReference);
      COMUtils.checkRC(hRESULT);
      return new IWbemClassObject(pointerByReference.getValue());
    }
  }
  
  public static interface WBEM_CONDITION_FLAG_TYPE {
    public static final int WBEM_FLAG_ALWAYS = 0;
    
    public static final int WBEM_FLAG_ONLY_IF_TRUE = 1;
    
    public static final int WBEM_FLAG_ONLY_IF_FALSE = 2;
    
    public static final int WBEM_FLAG_ONLY_IF_IDENTICAL = 3;
    
    public static final int WBEM_MASK_PRIMARY_CONDITION = 3;
    
    public static final int WBEM_FLAG_KEYS_ONLY = 4;
    
    public static final int WBEM_FLAG_REFS_ONLY = 8;
    
    public static final int WBEM_FLAG_LOCAL_ONLY = 16;
    
    public static final int WBEM_FLAG_PROPAGATED_ONLY = 32;
    
    public static final int WBEM_FLAG_SYSTEM_ONLY = 48;
    
    public static final int WBEM_FLAG_NONSYSTEM_ONLY = 64;
    
    public static final int WBEM_MASK_CONDITION_ORIGIN = 112;
    
    public static final int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 256;
    
    public static final int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 512;
    
    public static final int WBEM_MASK_CLASS_CONDITION = 768;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\Wbemcli.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */