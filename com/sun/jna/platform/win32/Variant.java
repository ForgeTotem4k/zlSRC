package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import java.util.Date;

public interface Variant {
  public static final int VT_EMPTY = 0;
  
  public static final int VT_NULL = 1;
  
  public static final int VT_I2 = 2;
  
  public static final int VT_I4 = 3;
  
  public static final int VT_R4 = 4;
  
  public static final int VT_R8 = 5;
  
  public static final int VT_CY = 6;
  
  public static final int VT_DATE = 7;
  
  public static final int VT_BSTR = 8;
  
  public static final int VT_DISPATCH = 9;
  
  public static final int VT_ERROR = 10;
  
  public static final int VT_BOOL = 11;
  
  public static final int VT_VARIANT = 12;
  
  public static final int VT_UNKNOWN = 13;
  
  public static final int VT_DECIMAL = 14;
  
  public static final int VT_I1 = 16;
  
  public static final int VT_UI1 = 17;
  
  public static final int VT_UI2 = 18;
  
  public static final int VT_UI4 = 19;
  
  public static final int VT_I8 = 20;
  
  public static final int VT_UI8 = 21;
  
  public static final int VT_INT = 22;
  
  public static final int VT_UINT = 23;
  
  public static final int VT_VOID = 24;
  
  public static final int VT_HRESULT = 25;
  
  public static final int VT_PTR = 26;
  
  public static final int VT_SAFEARRAY = 27;
  
  public static final int VT_CARRAY = 28;
  
  public static final int VT_USERDEFINED = 29;
  
  public static final int VT_LPSTR = 30;
  
  public static final int VT_LPWSTR = 31;
  
  public static final int VT_RECORD = 36;
  
  public static final int VT_INT_PTR = 37;
  
  public static final int VT_UINT_PTR = 38;
  
  public static final int VT_FILETIME = 64;
  
  public static final int VT_BLOB = 65;
  
  public static final int VT_STREAM = 66;
  
  public static final int VT_STORAGE = 67;
  
  public static final int VT_STREAMED_OBJECT = 68;
  
  public static final int VT_STORED_OBJECT = 69;
  
  public static final int VT_BLOB_OBJECT = 70;
  
  public static final int VT_CF = 71;
  
  public static final int VT_CLSID = 72;
  
  public static final int VT_VERSIONED_STREAM = 73;
  
  public static final int VT_BSTR_BLOB = 4095;
  
  public static final int VT_VECTOR = 4096;
  
  public static final int VT_ARRAY = 8192;
  
  public static final int VT_BYREF = 16384;
  
  public static final int VT_RESERVED = 32768;
  
  public static final int VT_ILLEGAL = 65535;
  
  public static final int VT_ILLEGALMASKED = 4095;
  
  public static final int VT_TYPEMASK = 4095;
  
  public static final OaIdl.VARIANT_BOOL VARIANT_TRUE = new OaIdl.VARIANT_BOOL(65535L);
  
  public static final OaIdl.VARIANT_BOOL VARIANT_FALSE = new OaIdl.VARIANT_BOOL(0L);
  
  @FieldOrder({"variantArg"})
  public static class VariantArg extends Structure {
    public Variant.VARIANT[] variantArg = new Variant.VARIANT[1];
    
    public VariantArg() {}
    
    public VariantArg(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public VariantArg(Variant.VARIANT[] param1ArrayOfVARIANT) {}
    
    public void setArraySize(int param1Int) {
      this.variantArg = new Variant.VARIANT[param1Int];
      read();
    }
    
    public static class ByReference extends VariantArg implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Variant.VARIANT[] param2ArrayOfVARIANT) {
        super(param2ArrayOfVARIANT);
      }
    }
  }
  
  public static class VARIANT extends Union {
    public static final VARIANT VARIANT_MISSING = new VARIANT();
    
    public _VARIANT _variant;
    
    public OaIdl.DECIMAL decVal;
    
    public VARIANT() {
      setType("_variant");
      read();
    }
    
    public VARIANT(Pointer param1Pointer) {
      super(param1Pointer);
      setType("_variant");
      read();
    }
    
    public VARIANT(WTypes.BSTR param1BSTR) {
      this();
      setValue(8, param1BSTR);
    }
    
    public VARIANT(WTypes.BSTRByReference param1BSTRByReference) {
      this();
      setValue(16392, param1BSTRByReference);
    }
    
    public VARIANT(OaIdl.VARIANT_BOOL param1VARIANT_BOOL) {
      this();
      setValue(11, param1VARIANT_BOOL);
    }
    
    public VARIANT(WinDef.BOOL param1BOOL) {
      this(param1BOOL.booleanValue());
    }
    
    public VARIANT(WinDef.LONG param1LONG) {
      this();
      setValue(3, param1LONG);
    }
    
    public VARIANT(WinDef.SHORT param1SHORT) {
      this();
      setValue(2, param1SHORT);
    }
    
    public VARIANT(OaIdl.DATE param1DATE) {
      this();
      setValue(7, param1DATE);
    }
    
    public VARIANT(byte param1Byte) {
      this(new WinDef.BYTE(param1Byte));
    }
    
    public VARIANT(WinDef.BYTE param1BYTE) {
      this();
      setValue(17, param1BYTE);
    }
    
    public VARIANT(char param1Char) {
      this();
      setValue(18, new WinDef.USHORT(param1Char));
    }
    
    public VARIANT(WinDef.CHAR param1CHAR) {
      this();
      setValue(16, param1CHAR);
    }
    
    public VARIANT(short param1Short) {
      this();
      setValue(2, new WinDef.SHORT(param1Short));
    }
    
    public VARIANT(int param1Int) {
      this();
      setValue(3, new WinDef.LONG(param1Int));
    }
    
    public VARIANT(IntByReference param1IntByReference) {
      this();
      setValue(16406, param1IntByReference);
    }
    
    public VARIANT(long param1Long) {
      this();
      setValue(20, new WinDef.LONGLONG(param1Long));
    }
    
    public VARIANT(float param1Float) {
      this();
      setValue(4, Float.valueOf(param1Float));
    }
    
    public VARIANT(double param1Double) {
      this();
      setValue(5, Double.valueOf(param1Double));
    }
    
    public VARIANT(String param1String) {
      this();
      WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString(param1String);
      setValue(8, bSTR);
    }
    
    public VARIANT(boolean param1Boolean) {
      this();
      setValue(11, new OaIdl.VARIANT_BOOL(param1Boolean));
    }
    
    @Deprecated
    public VARIANT(IDispatch param1IDispatch) {
      this();
      setValue(9, param1IDispatch);
    }
    
    public VARIANT(Dispatch param1Dispatch) {
      this();
      setValue(9, param1Dispatch);
    }
    
    public VARIANT(Date param1Date) {
      this();
      setValue(7, new OaIdl.DATE(param1Date));
    }
    
    public VARIANT(OaIdl.SAFEARRAY param1SAFEARRAY) {
      this();
      setValue(param1SAFEARRAY);
    }
    
    public VARIANT(OaIdl.SAFEARRAYByReference param1SAFEARRAYByReference) {
      this();
      setValue(param1SAFEARRAYByReference);
    }
    
    public WTypes.VARTYPE getVarType() {
      read();
      return this._variant.vt;
    }
    
    public void setVarType(short param1Short) {
      this._variant.vt = new WTypes.VARTYPE(param1Short);
    }
    
    public void setValue(int param1Int, Object param1Object) {
      setValue(new WTypes.VARTYPE(param1Int), param1Object);
    }
    
    public void setValue(OaIdl.SAFEARRAY param1SAFEARRAY) {
      setValue(param1SAFEARRAY.getVarType().intValue() | 0x2000, param1SAFEARRAY);
    }
    
    public void setValue(OaIdl.SAFEARRAYByReference param1SAFEARRAYByReference) {
      setValue(param1SAFEARRAYByReference.pSAFEARRAY.getVarType().intValue() | 0x2000 | 0x4000, param1SAFEARRAYByReference);
    }
    
    public void setValue(WTypes.VARTYPE param1VARTYPE, Object param1Object) {
      int i = param1VARTYPE.intValue();
      switch (i) {
        case 17:
          this._variant.__variant.writeField("bVal", param1Object);
          break;
        case 2:
          this._variant.__variant.writeField("iVal", param1Object);
          break;
        case 3:
          this._variant.__variant.writeField("lVal", param1Object);
          break;
        case 20:
          this._variant.__variant.writeField("llVal", param1Object);
          break;
        case 4:
          this._variant.__variant.writeField("fltVal", param1Object);
          break;
        case 5:
          this._variant.__variant.writeField("dblVal", param1Object);
          break;
        case 11:
          this._variant.__variant.writeField("boolVal", param1Object);
          break;
        case 10:
          this._variant.__variant.writeField("scode", param1Object);
          break;
        case 6:
          this._variant.__variant.writeField("cyVal", param1Object);
          break;
        case 7:
          this._variant.__variant.writeField("date", param1Object);
          break;
        case 8:
          this._variant.__variant.writeField("bstrVal", param1Object);
          break;
        case 13:
          this._variant.__variant.writeField("punkVal", param1Object);
          break;
        case 9:
          this._variant.__variant.writeField("pdispVal", param1Object);
          break;
        case 16401:
          this._variant.__variant.writeField("pbVal", param1Object);
          break;
        case 16386:
          this._variant.__variant.writeField("piVal", param1Object);
          break;
        case 16387:
          this._variant.__variant.writeField("plVal", param1Object);
          break;
        case 16404:
          this._variant.__variant.writeField("pllVal", param1Object);
          break;
        case 16388:
          this._variant.__variant.writeField("pfltVal", param1Object);
          break;
        case 16389:
          this._variant.__variant.writeField("pdblVal", param1Object);
          break;
        case 16395:
          this._variant.__variant.writeField("pboolVal", param1Object);
          break;
        case 16394:
          this._variant.__variant.writeField("pscode", param1Object);
          break;
        case 16390:
          this._variant.__variant.writeField("pcyVal", param1Object);
          break;
        case 16391:
          this._variant.__variant.writeField("pdate", param1Object);
          break;
        case 16392:
          this._variant.__variant.writeField("pbstrVal", param1Object);
          break;
        case 16397:
          this._variant.__variant.writeField("ppunkVal", param1Object);
          break;
        case 16393:
          this._variant.__variant.writeField("ppdispVal", param1Object);
          break;
        case 16396:
          this._variant.__variant.writeField("pvarVal", param1Object);
          break;
        case 16384:
          this._variant.__variant.writeField("byref", param1Object);
          break;
        case 16:
          this._variant.__variant.writeField("cVal", param1Object);
          break;
        case 18:
          this._variant.__variant.writeField("uiVal", param1Object);
          break;
        case 19:
          this._variant.__variant.writeField("ulVal", param1Object);
          break;
        case 21:
          this._variant.__variant.writeField("ullVal", param1Object);
          break;
        case 22:
          this._variant.__variant.writeField("intVal", param1Object);
          break;
        case 23:
          this._variant.__variant.writeField("uintVal", param1Object);
          break;
        case 16398:
          this._variant.__variant.writeField("pdecVal", param1Object);
          break;
        case 16400:
          this._variant.__variant.writeField("pcVal", param1Object);
          break;
        case 16402:
          this._variant.__variant.writeField("puiVal", param1Object);
          break;
        case 16403:
          this._variant.__variant.writeField("pulVal", param1Object);
          break;
        case 16405:
          this._variant.__variant.writeField("pullVal", param1Object);
          break;
        case 16406:
          this._variant.__variant.writeField("pintVal", param1Object);
          break;
        case 16407:
          this._variant.__variant.writeField("puintVal", param1Object);
          break;
        case 36:
          this._variant.__variant.writeField("pvRecord", param1Object);
          break;
        default:
          if ((i & 0x2000) > 0) {
            if ((i & 0x4000) > 0) {
              this._variant.__variant.writeField("pparray", param1Object);
              break;
            } 
            this._variant.__variant.writeField("parray", param1Object);
          } 
          break;
      } 
      this._variant.writeField("vt", param1VARTYPE);
      write();
    }
    
    public Object getValue() {
      int i = getVarType().intValue();
      switch (i) {
        case 17:
          return this._variant.__variant.readField("bVal");
        case 2:
          return this._variant.__variant.readField("iVal");
        case 3:
          return this._variant.__variant.readField("lVal");
        case 20:
          return this._variant.__variant.readField("llVal");
        case 4:
          return this._variant.__variant.readField("fltVal");
        case 5:
          return this._variant.__variant.readField("dblVal");
        case 11:
          return this._variant.__variant.readField("boolVal");
        case 10:
          return this._variant.__variant.readField("scode");
        case 6:
          return this._variant.__variant.readField("cyVal");
        case 7:
          return this._variant.__variant.readField("date");
        case 8:
          return this._variant.__variant.readField("bstrVal");
        case 13:
          return this._variant.__variant.readField("punkVal");
        case 9:
          return this._variant.__variant.readField("pdispVal");
        case 16401:
          return this._variant.__variant.readField("pbVal");
        case 16386:
          return this._variant.__variant.readField("piVal");
        case 16387:
          return this._variant.__variant.readField("plVal");
        case 16404:
          return this._variant.__variant.readField("pllVal");
        case 16388:
          return this._variant.__variant.readField("pfltVal");
        case 16389:
          return this._variant.__variant.readField("pdblVal");
        case 16395:
          return this._variant.__variant.readField("pboolVal");
        case 16394:
          return this._variant.__variant.readField("pscode");
        case 16390:
          return this._variant.__variant.readField("pcyVal");
        case 16391:
          return this._variant.__variant.readField("pdate");
        case 16392:
          return this._variant.__variant.readField("pbstrVal");
        case 16397:
          return this._variant.__variant.readField("ppunkVal");
        case 16393:
          return this._variant.__variant.readField("ppdispVal");
        case 16396:
          return this._variant.__variant.readField("pvarVal");
        case 16384:
          return this._variant.__variant.readField("byref");
        case 16:
          return this._variant.__variant.readField("cVal");
        case 18:
          return this._variant.__variant.readField("uiVal");
        case 19:
          return this._variant.__variant.readField("ulVal");
        case 21:
          return this._variant.__variant.readField("ullVal");
        case 22:
          return this._variant.__variant.readField("intVal");
        case 23:
          return this._variant.__variant.readField("uintVal");
        case 16398:
          return this._variant.__variant.readField("pdecVal");
        case 16400:
          return this._variant.__variant.readField("pcVal");
        case 16402:
          return this._variant.__variant.readField("puiVal");
        case 16403:
          return this._variant.__variant.readField("pulVal");
        case 16405:
          return this._variant.__variant.readField("pullVal");
        case 16406:
          return this._variant.__variant.readField("pintVal");
        case 16407:
          return this._variant.__variant.readField("puintVal");
        case 36:
          return this._variant.__variant.readField("pvRecord");
      } 
      return ((i & 0x2000) > 0) ? (((i & 0x4000) > 0) ? this._variant.__variant.readField("pparray") : this._variant.__variant.readField("parray")) : null;
    }
    
    public byte byteValue() {
      return ((Number)getValue()).byteValue();
    }
    
    public short shortValue() {
      return ((Number)getValue()).shortValue();
    }
    
    public int intValue() {
      return ((Number)getValue()).intValue();
    }
    
    public long longValue() {
      return ((Number)getValue()).longValue();
    }
    
    public float floatValue() {
      return ((Number)getValue()).floatValue();
    }
    
    public double doubleValue() {
      return ((Number)getValue()).doubleValue();
    }
    
    public String stringValue() {
      WTypes.BSTR bSTR = (WTypes.BSTR)getValue();
      return (bSTR == null) ? null : bSTR.getValue();
    }
    
    public boolean booleanValue() {
      return ((OaIdl.VARIANT_BOOL)getValue()).booleanValue();
    }
    
    public Date dateValue() {
      OaIdl.DATE dATE = (OaIdl.DATE)getValue();
      return (dATE == null) ? null : dATE.getAsJavaDate();
    }
    
    static {
      VARIANT_MISSING.setValue(10, new WinDef.SCODE(-2147352572L));
    }
    
    @FieldOrder({"vt", "wReserved1", "wReserved2", "wReserved3", "__variant"})
    public static class _VARIANT extends Structure {
      public WTypes.VARTYPE vt;
      
      public short wReserved1;
      
      public short wReserved2;
      
      public short wReserved3;
      
      public __VARIANT __variant;
      
      public _VARIANT() {}
      
      public _VARIANT(Pointer param2Pointer) {
        super(param2Pointer);
        read();
      }
      
      public static class __VARIANT extends Union {
        public WinDef.LONGLONG llVal;
        
        public WinDef.LONG lVal;
        
        public WinDef.BYTE bVal;
        
        public WinDef.SHORT iVal;
        
        public Float fltVal;
        
        public Double dblVal;
        
        public OaIdl.VARIANT_BOOL boolVal;
        
        public WinDef.SCODE scode;
        
        public OaIdl.CURRENCY cyVal;
        
        public OaIdl.DATE date;
        
        public WTypes.BSTR bstrVal;
        
        public Unknown punkVal;
        
        public Dispatch pdispVal;
        
        public OaIdl.SAFEARRAY.ByReference parray;
        
        public ByteByReference pbVal;
        
        public ShortByReference piVal;
        
        public WinDef.LONGByReference plVal;
        
        public WinDef.LONGLONGByReference pllVal;
        
        public FloatByReference pfltVal;
        
        public DoubleByReference pdblVal;
        
        public OaIdl.VARIANT_BOOLByReference pboolVal;
        
        public OaIdl._VARIANT_BOOLByReference pbool;
        
        public WinDef.SCODEByReference pscode;
        
        public OaIdl.CURRENCY.ByReference pcyVal;
        
        public OaIdl.DATE.ByReference pdate;
        
        public WTypes.BSTRByReference pbstrVal;
        
        public Unknown.ByReference ppunkVal;
        
        public Dispatch.ByReference ppdispVal;
        
        public OaIdl.SAFEARRAYByReference pparray;
        
        public Variant.VARIANT.ByReference pvarVal;
        
        public WinDef.PVOID byref;
        
        public WinDef.CHAR cVal;
        
        public WinDef.USHORT uiVal;
        
        public WinDef.ULONG ulVal;
        
        public WinDef.ULONGLONG ullVal;
        
        public Integer intVal;
        
        public WinDef.UINT uintVal;
        
        public OaIdl.DECIMAL.ByReference pdecVal;
        
        public WinDef.CHARByReference pcVal;
        
        public WinDef.USHORTByReference puiVal;
        
        public WinDef.ULONGByReference pulVal;
        
        public WinDef.ULONGLONGByReference pullVal;
        
        public IntByReference pintVal;
        
        public WinDef.UINTByReference puintVal;
        
        public BRECORD pvRecord;
        
        public __VARIANT() {
          read();
        }
        
        public __VARIANT(Pointer param3Pointer) {
          super(param3Pointer);
          read();
        }
        
        @FieldOrder({"pvRecord", "pRecInfo"})
        public static class BRECORD extends Structure {
          public WinDef.PVOID pvRecord;
          
          public Pointer pRecInfo;
          
          public BRECORD() {}
          
          public BRECORD(Pointer param4Pointer) {
            super(param4Pointer);
          }
          
          public static class ByReference extends BRECORD implements Structure.ByReference {}
        }
      }
    }
    
    public static class __VARIANT extends Union {
      public WinDef.LONGLONG llVal;
      
      public WinDef.LONG lVal;
      
      public WinDef.BYTE bVal;
      
      public WinDef.SHORT iVal;
      
      public Float fltVal;
      
      public Double dblVal;
      
      public OaIdl.VARIANT_BOOL boolVal;
      
      public WinDef.SCODE scode;
      
      public OaIdl.CURRENCY cyVal;
      
      public OaIdl.DATE date;
      
      public WTypes.BSTR bstrVal;
      
      public Unknown punkVal;
      
      public Dispatch pdispVal;
      
      public OaIdl.SAFEARRAY.ByReference parray;
      
      public ByteByReference pbVal;
      
      public ShortByReference piVal;
      
      public WinDef.LONGByReference plVal;
      
      public WinDef.LONGLONGByReference pllVal;
      
      public FloatByReference pfltVal;
      
      public DoubleByReference pdblVal;
      
      public OaIdl.VARIANT_BOOLByReference pboolVal;
      
      public OaIdl._VARIANT_BOOLByReference pbool;
      
      public WinDef.SCODEByReference pscode;
      
      public OaIdl.CURRENCY.ByReference pcyVal;
      
      public OaIdl.DATE.ByReference pdate;
      
      public WTypes.BSTRByReference pbstrVal;
      
      public Unknown.ByReference ppunkVal;
      
      public Dispatch.ByReference ppdispVal;
      
      public OaIdl.SAFEARRAYByReference pparray;
      
      public Variant.VARIANT.ByReference pvarVal;
      
      public WinDef.PVOID byref;
      
      public WinDef.CHAR cVal;
      
      public WinDef.USHORT uiVal;
      
      public WinDef.ULONG ulVal;
      
      public WinDef.ULONGLONG ullVal;
      
      public Integer intVal;
      
      public WinDef.UINT uintVal;
      
      public OaIdl.DECIMAL.ByReference pdecVal;
      
      public WinDef.CHARByReference pcVal;
      
      public WinDef.USHORTByReference puiVal;
      
      public WinDef.ULONGByReference pulVal;
      
      public WinDef.ULONGLONGByReference pullVal;
      
      public IntByReference pintVal;
      
      public WinDef.UINTByReference puintVal;
      
      public BRECORD pvRecord;
      
      public __VARIANT() {
        read();
      }
      
      public __VARIANT(Pointer param2Pointer) {
        super(param2Pointer);
        read();
      }
      
      @FieldOrder({"pvRecord", "pRecInfo"})
      public static class BRECORD extends Structure {
        public WinDef.PVOID pvRecord;
        
        public Pointer pRecInfo;
        
        public BRECORD() {}
        
        public BRECORD(Pointer param4Pointer) {
          super(param4Pointer);
        }
        
        public static class ByReference extends BRECORD implements Structure.ByReference {}
      }
    }
    
    public static class ByValue extends VARIANT implements Structure.ByValue {
      public ByValue(Variant.VARIANT param2VARIANT) {
        setValue(param2VARIANT.getVarType(), param2VARIANT.getValue());
      }
      
      public ByValue(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByValue() {}
    }
    
    public static class ByReference extends VARIANT implements Structure.ByReference {
      public ByReference(Variant.VARIANT param2VARIANT) {
        setValue(param2VARIANT.getVarType(), param2VARIANT.getValue());
      }
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByReference() {}
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Variant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */