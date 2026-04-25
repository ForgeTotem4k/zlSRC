package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.TypeComp;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Calendar;
import java.util.Date;

public interface OaIdl {
  public static final long DATE_OFFSET = (new Date(-1, 11, 30, 0, 0, 0)).getTime();
  
  public static final DISPID DISPID_COLLECT = new DISPID(-8);
  
  public static final DISPID DISPID_CONSTRUCTOR = new DISPID(-6);
  
  public static final DISPID DISPID_DESTRUCTOR = new DISPID(-7);
  
  public static final DISPID DISPID_EVALUATE = new DISPID(-5);
  
  public static final DISPID DISPID_NEWENUM = new DISPID(-4);
  
  public static final DISPID DISPID_PROPERTYPUT = new DISPID(-3);
  
  public static final DISPID DISPID_UNKNOWN = new DISPID(-1);
  
  public static final DISPID DISPID_VALUE = new DISPID(0);
  
  public static final MEMBERID MEMBERID_NIL = new MEMBERID(DISPID_UNKNOWN.intValue());
  
  public static final int FADF_AUTO = 1;
  
  public static final int FADF_STATIC = 2;
  
  public static final int FADF_EMBEDDED = 4;
  
  public static final int FADF_FIXEDSIZE = 16;
  
  public static final int FADF_RECORD = 32;
  
  public static final int FADF_HAVEIID = 64;
  
  public static final int FADF_HAVEVARTYPE = 128;
  
  public static final int FADF_BSTR = 256;
  
  public static final int FADF_UNKNOWN = 512;
  
  public static final int FADF_DISPATCH = 1024;
  
  public static final int FADF_VARIANT = 2048;
  
  public static final int FADF_RESERVED = 61448;
  
  public static class DISPID extends WinDef.LONG {
    private static final long serialVersionUID = 1L;
    
    public DISPID() {
      this(0);
    }
    
    public DISPID(int param1Int) {
      super(param1Int);
    }
  }
  
  public static class MEMBERID extends DISPID {
    private static final long serialVersionUID = 1L;
    
    public MEMBERID() {
      this(0);
    }
    
    public MEMBERID(int param1Int) {
      super(param1Int);
    }
  }
  
  @FieldOrder({"guid", "lcid", "dwReserved", "memidConstructor", "memidDestructor", "lpstrSchema", "cbSizeInstance", "typekind", "cFuncs", "cVars", "cImplTypes", "cbSizeVft", "cbAlignment", "wTypeFlags", "wMajorVerNum", "wMinorVerNum", "tdescAlias", "idldescType"})
  public static class TYPEATTR extends Structure {
    public Guid.GUID guid;
    
    public WinDef.LCID lcid;
    
    public WinDef.DWORD dwReserved;
    
    public OaIdl.MEMBERID memidConstructor;
    
    public OaIdl.MEMBERID memidDestructor;
    
    public WTypes.LPOLESTR lpstrSchema;
    
    public WinDef.ULONG cbSizeInstance;
    
    public OaIdl.TYPEKIND typekind;
    
    public WinDef.WORD cFuncs;
    
    public WinDef.WORD cVars;
    
    public WinDef.WORD cImplTypes;
    
    public WinDef.WORD cbSizeVft;
    
    public WinDef.WORD cbAlignment;
    
    public WinDef.WORD wTypeFlags;
    
    public WinDef.WORD wMajorVerNum;
    
    public WinDef.WORD wMinorVerNum;
    
    public OaIdl.TYPEDESC tdescAlias;
    
    public OaIdl.IDLDESC idldescType;
    
    public static final int TYPEFLAGS_FAPPOBJECT = 1;
    
    public static final int TYPEFLAGS_FCANCREATE = 2;
    
    public static final int TYPEFLAGS_FLICENSED = 4;
    
    public static final int TYPEFLAGS_FPREDECLID = 8;
    
    public static final int TYPEFLAGS_FHIDDEN = 16;
    
    public static final int TYPEFLAGS_FCONTROL = 32;
    
    public static final int TYPEFLAGS_FDUAL = 64;
    
    public static final int TYPEFLAGS_FNONEXTENSIBLE = 128;
    
    public static final int TYPEFLAGS_FOLEAUTOMATION = 256;
    
    public static final int TYPEFLAGS_FRESTRICTED = 512;
    
    public static final int TYPEFLAGS_FAGGREGATABLE = 1024;
    
    public static final int TYPEFLAGS_FREPLACEABLE = 2048;
    
    public static final int TYPEFLAGS_FDISPATCHABLE = 4096;
    
    public static final int TYPEFLAGS_FREVERSEBIND = 8192;
    
    public static final int TYPEFLAGS_FPROXY = 16384;
    
    public TYPEATTR() {}
    
    public TYPEATTR(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends TYPEATTR implements Structure.ByReference {}
  }
  
  public static class HREFTYPEByReference extends WinDef.DWORDByReference {
    public HREFTYPEByReference() {
      this(new OaIdl.HREFTYPE(0L));
    }
    
    public HREFTYPEByReference(WinDef.DWORD param1DWORD) {
      super(param1DWORD);
    }
    
    public void setValue(OaIdl.HREFTYPE param1HREFTYPE) {
      getPointer().setInt(0L, param1HREFTYPE.intValue());
    }
    
    public OaIdl.HREFTYPE getValue() {
      return new OaIdl.HREFTYPE(getPointer().getInt(0L));
    }
  }
  
  public static class HREFTYPE extends WinDef.DWORD {
    private static final long serialVersionUID = 1L;
    
    public HREFTYPE() {}
    
    public HREFTYPE(long param1Long) {
      super(param1Long);
    }
  }
  
  @FieldOrder({"cBytes", "varDefaultValue"})
  public static class PARAMDESCEX extends Structure {
    public WinDef.ULONG cBytes;
    
    public Variant.VariantArg varDefaultValue;
    
    public PARAMDESCEX() {}
    
    public PARAMDESCEX(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends PARAMDESCEX implements Structure.ByReference {}
  }
  
  @FieldOrder({"pparamdescex", "wParamFlags"})
  public static class PARAMDESC extends Structure {
    public Pointer pparamdescex;
    
    public WinDef.USHORT wParamFlags;
    
    public PARAMDESC() {}
    
    public PARAMDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends PARAMDESC implements Structure.ByReference {}
  }
  
  @FieldOrder({"tdescElem", "cDims", "rgbounds"})
  public static class ARRAYDESC extends Structure {
    public OaIdl.TYPEDESC tdescElem;
    
    public short cDims;
    
    public OaIdl.SAFEARRAYBOUND[] rgbounds = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
    
    public ARRAYDESC() {}
    
    public ARRAYDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public ARRAYDESC(OaIdl.TYPEDESC param1TYPEDESC, short param1Short, OaIdl.SAFEARRAYBOUND[] param1ArrayOfSAFEARRAYBOUND) {
      this.tdescElem = param1TYPEDESC;
      this.cDims = param1Short;
      if (param1ArrayOfSAFEARRAYBOUND.length != this.rgbounds.length)
        throw new IllegalArgumentException("Wrong array size !"); 
      this.rgbounds = param1ArrayOfSAFEARRAYBOUND;
    }
    
    public static class ByReference extends ARRAYDESC implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwReserved", "wIDLFlags"})
  public static class IDLDESC extends Structure {
    public BaseTSD.ULONG_PTR dwReserved;
    
    public WinDef.USHORT wIDLFlags;
    
    public IDLDESC() {}
    
    public IDLDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public IDLDESC(BaseTSD.ULONG_PTR param1ULONG_PTR, WinDef.USHORT param1USHORT) {
      this.dwReserved = param1ULONG_PTR;
      this.wIDLFlags = param1USHORT;
    }
    
    public static class ByReference extends IDLDESC implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(OaIdl.IDLDESC param2IDLDESC) {
        super(param2IDLDESC.dwReserved, param2IDLDESC.wIDLFlags);
      }
    }
  }
  
  @FieldOrder({"_typedesc", "vt"})
  public static class TYPEDESC extends Structure {
    public _TYPEDESC _typedesc;
    
    public WTypes.VARTYPE vt;
    
    public TYPEDESC() {
      read();
    }
    
    public TYPEDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public TYPEDESC(_TYPEDESC param1_TYPEDESC, WTypes.VARTYPE param1VARTYPE) {
      this._typedesc = param1_TYPEDESC;
      this.vt = param1VARTYPE;
    }
    
    public static class _TYPEDESC extends Union {
      public OaIdl.TYPEDESC.ByReference lptdesc;
      
      public OaIdl.ARRAYDESC.ByReference lpadesc;
      
      public OaIdl.HREFTYPE hreftype;
      
      public _TYPEDESC() {
        setType("hreftype");
        read();
      }
      
      public _TYPEDESC(Pointer param2Pointer) {
        super(param2Pointer);
        setType("hreftype");
        read();
      }
      
      public OaIdl.TYPEDESC.ByReference getLptdesc() {
        setType("lptdesc");
        read();
        return this.lptdesc;
      }
      
      public OaIdl.ARRAYDESC.ByReference getLpadesc() {
        setType("lpadesc");
        read();
        return this.lpadesc;
      }
      
      public OaIdl.HREFTYPE getHreftype() {
        setType("hreftype");
        read();
        return this.hreftype;
      }
    }
    
    public static class ByReference extends TYPEDESC implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class VARKIND extends Structure {
    public static final int VAR_PERINSTANCE = 0;
    
    public static final int VAR_STATIC = 1;
    
    public static final int VAR_CONST = 2;
    
    public static final int VAR_DISPATCH = 3;
    
    public int value;
    
    public VARKIND() {}
    
    public VARKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public static class ByReference extends VARKIND implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class CALLCONV extends Structure {
    public static final int CC_FASTCALL = 0;
    
    public static final int CC_CDECL = 1;
    
    public static final int CC_MSCPASCAL = 2;
    
    public static final int CC_PASCAL = 2;
    
    public static final int CC_MACPASCAL = 3;
    
    public static final int CC_STDCALL = 4;
    
    public static final int CC_FPFASTCALL = 5;
    
    public static final int CC_SYSCALL = 6;
    
    public static final int CC_MPWCDECL = 7;
    
    public static final int CC_MPWPASCAL = 8;
    
    public static final int CC_MAX = 9;
    
    public int value;
    
    public CALLCONV() {}
    
    public CALLCONV(int param1Int) {
      this.value = param1Int;
    }
    
    public static class ByReference extends CALLCONV implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class INVOKEKIND extends Structure {
    public static final INVOKEKIND INVOKE_FUNC = new INVOKEKIND(1);
    
    public static final INVOKEKIND INVOKE_PROPERTYGET = new INVOKEKIND(2);
    
    public static final INVOKEKIND INVOKE_PROPERTYPUT = new INVOKEKIND(4);
    
    public static final INVOKEKIND INVOKE_PROPERTYPUTREF = new INVOKEKIND(8);
    
    public int value;
    
    public INVOKEKIND() {}
    
    public INVOKEKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public static class ByReference extends INVOKEKIND implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class FUNCKIND extends Structure {
    public static final int FUNC_VIRTUAL = 0;
    
    public static final int FUNC_PUREVIRTUAL = 1;
    
    public static final int FUNC_NONVIRTUAL = 2;
    
    public static final int FUNC_STATIC = 3;
    
    public static final int FUNC_DISPATCH = 4;
    
    public int value;
    
    public FUNCKIND() {}
    
    public FUNCKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public static class ByReference extends FUNCKIND implements Structure.ByReference {}
  }
  
  @FieldOrder({"tdesc", "_elemdesc"})
  public static class ELEMDESC extends Structure {
    public OaIdl.TYPEDESC tdesc;
    
    public _ELEMDESC _elemdesc;
    
    public ELEMDESC() {}
    
    public ELEMDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class _ELEMDESC extends Union {
      public OaIdl.IDLDESC idldesc;
      
      public OaIdl.PARAMDESC paramdesc;
      
      public _ELEMDESC() {}
      
      public _ELEMDESC(Pointer param2Pointer) {
        super(param2Pointer);
        setType("paramdesc");
        read();
      }
      
      public _ELEMDESC(OaIdl.PARAMDESC param2PARAMDESC) {
        this.paramdesc = param2PARAMDESC;
        setType("paramdesc");
      }
      
      public _ELEMDESC(OaIdl.IDLDESC param2IDLDESC) {
        this.idldesc = param2IDLDESC;
        setType("idldesc");
      }
      
      public static class ByReference extends _ELEMDESC implements Structure.ByReference {}
    }
    
    public static class ByReference extends ELEMDESC implements Structure.ByReference {}
  }
  
  @FieldOrder({"memid", "lpstrSchema", "_vardesc", "elemdescVar", "wVarFlags", "varkind"})
  public static class VARDESC extends Structure {
    public OaIdl.MEMBERID memid;
    
    public WTypes.LPOLESTR lpstrSchema;
    
    public _VARDESC _vardesc;
    
    public OaIdl.ELEMDESC elemdescVar;
    
    public WinDef.WORD wVarFlags;
    
    public OaIdl.VARKIND varkind;
    
    public VARDESC() {}
    
    public VARDESC(Pointer param1Pointer) {
      super(param1Pointer);
      this._vardesc.setType("lpvarValue");
      read();
    }
    
    public static class _VARDESC extends Union {
      public NativeLong oInst;
      
      public Variant.VARIANT.ByReference lpvarValue;
      
      public _VARDESC() {
        setType("lpvarValue");
        read();
      }
      
      public _VARDESC(Pointer param2Pointer) {
        super(param2Pointer);
        setType("lpvarValue");
        read();
      }
      
      public _VARDESC(Variant.VARIANT.ByReference param2ByReference) {
        this.lpvarValue = param2ByReference;
        setType("lpvarValue");
      }
      
      public _VARDESC(NativeLong param2NativeLong) {
        this.oInst = param2NativeLong;
        setType("oInst");
      }
      
      public static class ByReference extends _VARDESC implements Structure.ByReference {}
    }
    
    public static class ByReference extends VARDESC implements Structure.ByReference {}
  }
  
  @FieldOrder({"scodeArg"})
  public static class ScodeArg extends Structure {
    public WinDef.SCODE[] scodeArg = new WinDef.SCODE[] { new WinDef.SCODE() };
    
    public ScodeArg() {}
    
    public ScodeArg(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends ScodeArg implements Structure.ByReference {}
  }
  
  @FieldOrder({"elemDescArg"})
  public static class ElemDescArg extends Structure {
    public OaIdl.ELEMDESC[] elemDescArg = new OaIdl.ELEMDESC[] { new OaIdl.ELEMDESC() };
    
    public ElemDescArg() {}
    
    public ElemDescArg(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends ElemDescArg implements Structure.ByReference {}
  }
  
  @FieldOrder({"memid", "lprgscode", "lprgelemdescParam", "funckind", "invkind", "callconv", "cParams", "cParamsOpt", "oVft", "cScodes", "elemdescFunc", "wFuncFlags"})
  public static class FUNCDESC extends Structure {
    public OaIdl.MEMBERID memid;
    
    public OaIdl.ScodeArg.ByReference lprgscode;
    
    public OaIdl.ElemDescArg.ByReference lprgelemdescParam;
    
    public OaIdl.FUNCKIND funckind;
    
    public OaIdl.INVOKEKIND invkind;
    
    public OaIdl.CALLCONV callconv;
    
    public WinDef.SHORT cParams;
    
    public WinDef.SHORT cParamsOpt;
    
    public WinDef.SHORT oVft;
    
    public WinDef.SHORT cScodes;
    
    public OaIdl.ELEMDESC elemdescFunc;
    
    public WinDef.WORD wFuncFlags;
    
    public FUNCDESC() {}
    
    public FUNCDESC(Pointer param1Pointer) {
      super(param1Pointer);
      read();
      if (this.cParams.shortValue() > 1) {
        this.lprgelemdescParam.elemDescArg = new OaIdl.ELEMDESC[this.cParams.shortValue()];
        this.lprgelemdescParam.read();
      } 
    }
    
    public static class ByReference extends FUNCDESC implements Structure.ByReference {}
  }
  
  public static class BINDPTR extends Union {
    public OaIdl.FUNCDESC lpfuncdesc;
    
    public OaIdl.VARDESC lpvardesc;
    
    public TypeComp lptcomp;
    
    public BINDPTR() {}
    
    public BINDPTR(OaIdl.VARDESC param1VARDESC) {
      this.lpvardesc = param1VARDESC;
      setType(OaIdl.VARDESC.class);
    }
    
    public BINDPTR(TypeComp param1TypeComp) {
      this.lptcomp = param1TypeComp;
      setType(TypeComp.class);
    }
    
    public BINDPTR(OaIdl.FUNCDESC param1FUNCDESC) {
      this.lpfuncdesc = param1FUNCDESC;
      setType(OaIdl.FUNCDESC.class);
    }
    
    public static class ByReference extends BINDPTR implements Structure.ByReference {}
  }
  
  @FieldOrder({"guid", "lcid", "syskind", "wMajorVerNum", "wMinorVerNum", "wLibFlags"})
  public static class TLIBATTR extends Structure {
    public Guid.GUID guid;
    
    public WinDef.LCID lcid;
    
    public OaIdl.SYSKIND syskind;
    
    public WinDef.WORD wMajorVerNum;
    
    public WinDef.WORD wMinorVerNum;
    
    public WinDef.WORD wLibFlags;
    
    public TLIBATTR() {}
    
    public TLIBATTR(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends TLIBATTR implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
        read();
      }
    }
  }
  
  @FieldOrder({"value"})
  public static class LIBFLAGS extends Structure {
    public int value;
    
    public static final int LIBFLAG_FRESTRICTED = 1;
    
    public static final int LIBFLAG_FCONTROL = 2;
    
    public static final int LIBFLAG_FHIDDEN = 4;
    
    public static final int LIBFLAG_FHASDISKIMAGE = 8;
    
    public LIBFLAGS() {}
    
    public LIBFLAGS(int param1Int) {
      this.value = param1Int;
    }
    
    public LIBFLAGS(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends LIBFLAGS implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class SYSKIND extends Structure {
    public int value;
    
    public static final int SYS_WIN16 = 0;
    
    public static final int SYS_WIN32 = 1;
    
    public static final int SYS_MAC = 2;
    
    public static final int SYS_WIN64 = 3;
    
    public SYSKIND() {}
    
    public SYSKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public SYSKIND(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends SYSKIND implements Structure.ByReference {}
  }
  
  @FieldOrder({"wReserved", "decimal1", "Hi32", "decimal2"})
  public static class DECIMAL extends Structure {
    public short wReserved;
    
    public _DECIMAL1 decimal1;
    
    public NativeLong Hi32;
    
    public _DECIMAL2 decimal2;
    
    public DECIMAL() {}
    
    public DECIMAL(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public static class _DECIMAL2 extends Union {
      public WinDef.ULONGLONG Lo64;
      
      public _DECIMAL2_DECIMAL decimal2_DECIMAL;
      
      public _DECIMAL2() {
        setType("Lo64");
      }
      
      public _DECIMAL2(Pointer param2Pointer) {
        super(param2Pointer);
        setType("Lo64");
        read();
      }
      
      @FieldOrder({"Lo32", "Mid32"})
      public static class _DECIMAL2_DECIMAL extends Structure {
        public WinDef.BYTE Lo32;
        
        public WinDef.BYTE Mid32;
        
        public _DECIMAL2_DECIMAL() {}
        
        public _DECIMAL2_DECIMAL(Pointer param3Pointer) {
          super(param3Pointer);
        }
      }
    }
    
    public static class _DECIMAL1 extends Union {
      public WinDef.USHORT signscale;
      
      public _DECIMAL1_DECIMAL decimal1_DECIMAL;
      
      public _DECIMAL1() {
        setType("signscale");
      }
      
      public _DECIMAL1(Pointer param2Pointer) {
        super(param2Pointer);
        setType("signscale");
        read();
      }
      
      @FieldOrder({"scale", "sign"})
      public static class _DECIMAL1_DECIMAL extends Structure {
        public WinDef.BYTE scale;
        
        public WinDef.BYTE sign;
        
        public _DECIMAL1_DECIMAL() {}
        
        public _DECIMAL1_DECIMAL(Pointer param3Pointer) {
          super(param3Pointer);
        }
      }
    }
    
    public static class ByReference extends DECIMAL implements Structure.ByReference {}
  }
  
  public static class CURRENCY extends Union {
    public _CURRENCY currency;
    
    public WinDef.LONGLONG int64;
    
    public CURRENCY() {}
    
    public CURRENCY(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    @FieldOrder({"Lo", "Hi"})
    public static class _CURRENCY extends Structure {
      public WinDef.ULONG Lo;
      
      public WinDef.LONG Hi;
      
      public _CURRENCY() {}
      
      public _CURRENCY(Pointer param2Pointer) {
        super(param2Pointer);
        read();
      }
    }
    
    public static class ByReference extends CURRENCY implements Structure.ByReference {}
  }
  
  @FieldOrder({"cElements", "lLbound"})
  public static class SAFEARRAYBOUND extends Structure {
    public WinDef.ULONG cElements;
    
    public WinDef.LONG lLbound;
    
    public SAFEARRAYBOUND() {}
    
    public SAFEARRAYBOUND(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public SAFEARRAYBOUND(int param1Int1, int param1Int2) {
      this.cElements = new WinDef.ULONG(param1Int1);
      this.lLbound = new WinDef.LONG(param1Int2);
      write();
    }
    
    public static class ByReference extends SAFEARRAYBOUND implements Structure.ByReference {}
  }
  
  @FieldOrder({"pSAFEARRAY"})
  public static class SAFEARRAYByReference extends Structure implements Structure.ByReference {
    public OaIdl.SAFEARRAY.ByReference pSAFEARRAY;
    
    public SAFEARRAYByReference() {}
    
    public SAFEARRAYByReference(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public SAFEARRAYByReference(OaIdl.SAFEARRAY.ByReference param1ByReference) {
      this.pSAFEARRAY = param1ByReference;
    }
  }
  
  @FieldOrder({"cDims", "fFeatures", "cbElements", "cLocks", "pvData", "rgsabound"})
  public static class SAFEARRAY extends Structure implements Closeable {
    public WinDef.USHORT cDims;
    
    public WinDef.USHORT fFeatures;
    
    public WinDef.ULONG cbElements;
    
    public WinDef.ULONG cLocks;
    
    public WinDef.PVOID pvData;
    
    public OaIdl.SAFEARRAYBOUND[] rgsabound = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
    
    public SAFEARRAY() {}
    
    public SAFEARRAY(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public void read() {
      super.read();
      if (this.cDims.intValue() > 0) {
        this.rgsabound = (OaIdl.SAFEARRAYBOUND[])this.rgsabound[0].toArray(this.cDims.intValue());
      } else {
        this.rgsabound = new OaIdl.SAFEARRAYBOUND[] { new OaIdl.SAFEARRAYBOUND() };
      } 
    }
    
    public static ByReference createSafeArray(int... param1VarArgs) {
      return createSafeArray(new WTypes.VARTYPE(12), param1VarArgs);
    }
    
    public static ByReference createSafeArray(WTypes.VARTYPE param1VARTYPE, int... param1VarArgs) {
      OaIdl.SAFEARRAYBOUND[] arrayOfSAFEARRAYBOUND = (OaIdl.SAFEARRAYBOUND[])(new OaIdl.SAFEARRAYBOUND()).toArray(param1VarArgs.length);
      for (byte b = 0; b < param1VarArgs.length; b++) {
        (arrayOfSAFEARRAYBOUND[b]).lLbound = new WinDef.LONG(0L);
        (arrayOfSAFEARRAYBOUND[b]).cElements = new WinDef.ULONG(param1VarArgs[param1VarArgs.length - b - 1]);
      } 
      return OleAuto.INSTANCE.SafeArrayCreate(param1VARTYPE, new WinDef.UINT(param1VarArgs.length), arrayOfSAFEARRAYBOUND);
    }
    
    public void putElement(Object param1Object, int... param1VarArgs) {
      WinNT.HRESULT hRESULT;
      Memory memory;
      WinDef.LONG[] arrayOfLONG = new WinDef.LONG[param1VarArgs.length];
      for (byte b = 0; b < param1VarArgs.length; b++)
        arrayOfLONG[b] = new WinDef.LONG(param1VarArgs[param1VarArgs.length - b - 1]); 
      switch (getVarType().intValue()) {
        case 11:
          memory = new Memory(2L);
          if (param1Object instanceof Boolean) {
            memory.setShort(0L, (short)(((Boolean)param1Object).booleanValue() ? 65535 : 0));
          } else {
            memory.setShort(0L, (short)((((Number)param1Object).intValue() > 0) ? 65535 : 0));
          } 
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 16:
        case 17:
          memory = new Memory(1L);
          memory.setByte(0L, ((Number)param1Object).byteValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 2:
        case 18:
          memory = new Memory(2L);
          memory.setShort(0L, ((Number)param1Object).shortValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 3:
        case 19:
        case 22:
        case 23:
          memory = new Memory(4L);
          memory.setInt(0L, ((Number)param1Object).intValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 10:
          memory = new Memory(4L);
          memory.setInt(0L, ((Number)param1Object).intValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 4:
          memory = new Memory(4L);
          memory.setFloat(0L, ((Number)param1Object).floatValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 5:
          memory = new Memory(8L);
          memory.setDouble(0L, ((Number)param1Object).doubleValue());
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 7:
          memory = new Memory(8L);
          memory.setDouble(0L, ((OaIdl.DATE)param1Object).date);
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return;
        case 8:
          if (param1Object instanceof String) {
            WTypes.BSTR bSTR = OleAuto.INSTANCE.SysAllocString((String)param1Object);
            hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, bSTR.getPointer());
            OleAuto.INSTANCE.SysFreeString(bSTR);
            COMUtils.checkRC(hRESULT);
          } else {
            hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((WTypes.BSTR)param1Object).getPointer());
            COMUtils.checkRC(hRESULT);
          } 
          return;
        case 12:
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((Variant.VARIANT)param1Object).getPointer());
          COMUtils.checkRC(hRESULT);
          return;
        case 13:
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((Unknown)param1Object).getPointer());
          COMUtils.checkRC(hRESULT);
          return;
        case 9:
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((Dispatch)param1Object).getPointer());
          COMUtils.checkRC(hRESULT);
          return;
        case 6:
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((OaIdl.CURRENCY)param1Object).getPointer());
          COMUtils.checkRC(hRESULT);
          return;
        case 14:
          hRESULT = OleAuto.INSTANCE.SafeArrayPutElement(this, arrayOfLONG, ((OaIdl.DECIMAL)param1Object).getPointer());
          COMUtils.checkRC(hRESULT);
          return;
      } 
      throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
    }
    
    public Object getElement(int... param1VarArgs) {
      String str;
      WinNT.HRESULT hRESULT;
      Memory memory;
      PointerByReference pointerByReference;
      WTypes.BSTR bSTR;
      Variant.VARIANT vARIANT;
      OaIdl.CURRENCY cURRENCY;
      OaIdl.DECIMAL dECIMAL;
      WinDef.LONG[] arrayOfLONG = new WinDef.LONG[param1VarArgs.length];
      for (byte b = 0; b < param1VarArgs.length; b++)
        arrayOfLONG[b] = new WinDef.LONG(param1VarArgs[param1VarArgs.length - b - 1]); 
      switch (getVarType().intValue()) {
        case 11:
          memory = new Memory(2L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Boolean.valueOf((memory.getShort(0L) != 0));
        case 16:
        case 17:
          memory = new Memory(1L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Byte.valueOf(memory.getByte(0L));
        case 2:
        case 18:
          memory = new Memory(2L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Short.valueOf(memory.getShort(0L));
        case 3:
        case 19:
        case 22:
        case 23:
          memory = new Memory(4L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Integer.valueOf(memory.getInt(0L));
        case 10:
          memory = new Memory(4L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return new WinDef.SCODE(memory.getInt(0L));
        case 4:
          memory = new Memory(4L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Float.valueOf(memory.getFloat(0L));
        case 5:
          memory = new Memory(8L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return Double.valueOf(memory.getDouble(0L));
        case 7:
          memory = new Memory(8L);
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, (Pointer)memory);
          COMUtils.checkRC(hRESULT);
          return new OaIdl.DATE(memory.getDouble(0L));
        case 8:
          pointerByReference = new PointerByReference();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, pointerByReference.getPointer());
          COMUtils.checkRC(hRESULT);
          bSTR = new WTypes.BSTR(pointerByReference.getValue());
          str = bSTR.getValue();
          OleAuto.INSTANCE.SysFreeString(bSTR);
          return str;
        case 12:
          vARIANT = new Variant.VARIANT();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, vARIANT.getPointer());
          COMUtils.checkRC(hRESULT);
          return vARIANT;
        case 13:
          pointerByReference = new PointerByReference();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, pointerByReference.getPointer());
          COMUtils.checkRC(hRESULT);
          return new Unknown(pointerByReference.getValue());
        case 9:
          pointerByReference = new PointerByReference();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, pointerByReference.getPointer());
          COMUtils.checkRC(hRESULT);
          return new Dispatch(pointerByReference.getValue());
        case 6:
          cURRENCY = new OaIdl.CURRENCY();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, cURRENCY.getPointer());
          COMUtils.checkRC(hRESULT);
          return cURRENCY;
        case 14:
          dECIMAL = new OaIdl.DECIMAL();
          hRESULT = OleAuto.INSTANCE.SafeArrayGetElement(this, arrayOfLONG, dECIMAL.getPointer());
          COMUtils.checkRC(hRESULT);
          return dECIMAL;
      } 
      throw new IllegalStateException("Can't parse array content - type not supported: " + getVarType().intValue());
    }
    
    public Pointer ptrOfIndex(int... param1VarArgs) {
      WinDef.LONG[] arrayOfLONG = new WinDef.LONG[param1VarArgs.length];
      for (byte b = 0; b < param1VarArgs.length; b++)
        arrayOfLONG[b] = new WinDef.LONG(param1VarArgs[param1VarArgs.length - b - 1]); 
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayPtrOfIndex(this, arrayOfLONG, pointerByReference);
      COMUtils.checkRC(hRESULT);
      return pointerByReference.getValue();
    }
    
    public void destroy() {
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayDestroy(this);
      COMUtils.checkRC(hRESULT);
    }
    
    public void close() {
      destroy();
    }
    
    public int getLBound(int param1Int) {
      int i = getDimensionCount() - param1Int;
      WinDef.LONGByReference lONGByReference = new WinDef.LONGByReference();
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayGetLBound(this, new WinDef.UINT(i), lONGByReference);
      COMUtils.checkRC(hRESULT);
      return lONGByReference.getValue().intValue();
    }
    
    public int getUBound(int param1Int) {
      int i = getDimensionCount() - param1Int;
      WinDef.LONGByReference lONGByReference = new WinDef.LONGByReference();
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayGetUBound(this, new WinDef.UINT(i), lONGByReference);
      COMUtils.checkRC(hRESULT);
      return lONGByReference.getValue().intValue();
    }
    
    public int getDimensionCount() {
      return OleAuto.INSTANCE.SafeArrayGetDim(this).intValue();
    }
    
    public Pointer accessData() {
      PointerByReference pointerByReference = new PointerByReference();
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayAccessData(this, pointerByReference);
      COMUtils.checkRC(hRESULT);
      return pointerByReference.getValue();
    }
    
    public void unaccessData() {
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayUnaccessData(this);
      COMUtils.checkRC(hRESULT);
    }
    
    public void lock() {
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayLock(this);
      COMUtils.checkRC(hRESULT);
    }
    
    public void unlock() {
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayUnlock(this);
      COMUtils.checkRC(hRESULT);
    }
    
    public void redim(int param1Int1, int param1Int2) {
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayRedim(this, new OaIdl.SAFEARRAYBOUND(param1Int1, param1Int2));
      COMUtils.checkRC(hRESULT);
    }
    
    public WTypes.VARTYPE getVarType() {
      WTypes.VARTYPEByReference vARTYPEByReference = new WTypes.VARTYPEByReference();
      WinNT.HRESULT hRESULT = OleAuto.INSTANCE.SafeArrayGetVartype(this, vARTYPEByReference);
      COMUtils.checkRC(hRESULT);
      return vARTYPEByReference.getValue();
    }
    
    public long getElemsize() {
      return OleAuto.INSTANCE.SafeArrayGetElemsize(this).longValue();
    }
    
    public static class ByReference extends SAFEARRAY implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class DESCKIND extends Structure {
    public int value;
    
    public static final int DESCKIND_NONE = 0;
    
    public static final int DESCKIND_FUNCDESC = 1;
    
    public static final int DESCKIND_VARDESC = 2;
    
    public static final int DESCKIND_TYPECOMP = 3;
    
    public static final int DESCKIND_IMPLICITAPPOBJ = 4;
    
    public static final int DESCKIND_MAX = 5;
    
    public DESCKIND() {}
    
    public DESCKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public DESCKIND(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends DESCKIND implements Structure.ByReference {}
  }
  
  @FieldOrder({"value"})
  public static class TYPEKIND extends Structure {
    public int value;
    
    public static final int TKIND_ENUM = 0;
    
    public static final int TKIND_RECORD = 1;
    
    public static final int TKIND_MODULE = 2;
    
    public static final int TKIND_INTERFACE = 3;
    
    public static final int TKIND_DISPATCH = 4;
    
    public static final int TKIND_COCLASS = 5;
    
    public static final int TKIND_ALIAS = 6;
    
    public static final int TKIND_UNION = 7;
    
    public static final int TKIND_MAX = 8;
    
    public TYPEKIND() {}
    
    public TYPEKIND(int param1Int) {
      this.value = param1Int;
    }
    
    public TYPEKIND(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends TYPEKIND implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(int param2Int) {
        super(param2Int);
      }
      
      public ByReference(OaIdl.TYPEKIND param2TYPEKIND) {
        super(param2TYPEKIND.getPointer());
        this.value = param2TYPEKIND.value;
      }
    }
  }
  
  public static class MEMBERIDByReference extends com.sun.jna.ptr.ByReference {
    public MEMBERIDByReference() {
      this(new OaIdl.MEMBERID(0));
    }
    
    public MEMBERIDByReference(OaIdl.MEMBERID param1MEMBERID) {
      super(OaIdl.MEMBERID.SIZE);
      setValue(param1MEMBERID);
    }
    
    public void setValue(OaIdl.MEMBERID param1MEMBERID) {
      getPointer().setInt(0L, param1MEMBERID.intValue());
    }
    
    public OaIdl.MEMBERID getValue() {
      return new OaIdl.MEMBERID(getPointer().getInt(0L));
    }
  }
  
  public static class DISPIDByReference extends com.sun.jna.ptr.ByReference {
    public DISPIDByReference() {
      this(new OaIdl.DISPID(0));
    }
    
    public DISPIDByReference(OaIdl.DISPID param1DISPID) {
      super(OaIdl.DISPID.SIZE);
      setValue(param1DISPID);
    }
    
    public void setValue(OaIdl.DISPID param1DISPID) {
      getPointer().setInt(0L, param1DISPID.intValue());
    }
    
    public OaIdl.DISPID getValue() {
      return new OaIdl.DISPID(getPointer().getInt(0L));
    }
  }
  
  @FieldOrder({"date"})
  public static class DATE extends Structure {
    private static final double MILLISECONDS_PER_DAY = 8.64E7D;
    
    public double date;
    
    public DATE() {}
    
    public DATE(double param1Double) {
      this.date = param1Double;
    }
    
    public DATE(Date param1Date) {
      setFromJavaDate(param1Date);
    }
    
    public Date getAsJavaDate() {
      WinBase.SYSTEMTIME sYSTEMTIME = new WinBase.SYSTEMTIME();
      OleAuto.INSTANCE.VariantTimeToSystemTime(this.date, sYSTEMTIME);
      Calendar calendar = sYSTEMTIME.toCalendar();
      int i = (int)((long)(Math.abs(this.date) * 8.64E7D + 0.5D) % 1000L);
      if ((this.date > 0.0D && i > 500) || (this.date < 0.0D && i > 499))
        i -= 1000; 
      calendar.set(14, i);
      return calendar.getTime();
    }
    
    public void setFromJavaDate(Date param1Date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(param1Date);
      DoubleByReference doubleByReference = new DoubleByReference();
      OleAuto.INSTANCE.SystemTimeToVariantTime(new WinBase.SYSTEMTIME(calendar), doubleByReference);
      double d = doubleByReference.getValue();
      this.date = d + Math.signum(d) * calendar.get(14) / 8.64E7D;
    }
    
    public static class ByReference extends DATE implements Structure.ByReference {}
  }
  
  public static class _VARIANT_BOOLByReference extends com.sun.jna.ptr.ByReference {
    public _VARIANT_BOOLByReference() {
      this(new OaIdl.VARIANT_BOOL(0L));
    }
    
    public _VARIANT_BOOLByReference(OaIdl.VARIANT_BOOL param1VARIANT_BOOL) {
      super(2);
      setValue(param1VARIANT_BOOL);
    }
    
    public void setValue(OaIdl.VARIANT_BOOL param1VARIANT_BOOL) {
      getPointer().setShort(0L, param1VARIANT_BOOL.shortValue());
    }
    
    public OaIdl.VARIANT_BOOL getValue() {
      return new OaIdl.VARIANT_BOOL(getPointer().getShort(0L));
    }
  }
  
  public static class VARIANT_BOOLByReference extends com.sun.jna.ptr.ByReference {
    public VARIANT_BOOLByReference() {
      this(new OaIdl.VARIANT_BOOL(0L));
    }
    
    public VARIANT_BOOLByReference(OaIdl.VARIANT_BOOL param1VARIANT_BOOL) {
      super(2);
      setValue(param1VARIANT_BOOL);
    }
    
    public void setValue(OaIdl.VARIANT_BOOL param1VARIANT_BOOL) {
      getPointer().setShort(0L, param1VARIANT_BOOL.shortValue());
    }
    
    public OaIdl.VARIANT_BOOL getValue() {
      return new OaIdl.VARIANT_BOOL(getPointer().getShort(0L));
    }
  }
  
  public static class _VARIANT_BOOL extends VARIANT_BOOL {
    private static final long serialVersionUID = 1L;
    
    public _VARIANT_BOOL() {
      this(0L);
    }
    
    public _VARIANT_BOOL(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class VARIANT_BOOL extends IntegerType {
    private static final long serialVersionUID = 1L;
    
    public static final int SIZE = 2;
    
    public VARIANT_BOOL() {
      this(0L);
    }
    
    public VARIANT_BOOL(long param1Long) {
      super(2, param1Long);
    }
    
    public VARIANT_BOOL(boolean param1Boolean) {
      this(param1Boolean ? 65535L : 0L);
    }
    
    public boolean booleanValue() {
      return (shortValue() != 0);
    }
  }
  
  @FieldOrder({"wCode", "wReserved", "bstrSource", "bstrDescription", "bstrHelpFile", "dwHelpContext", "pvReserved", "pfnDeferredFillIn", "scode"})
  public static class EXCEPINFO extends Structure {
    public WinDef.WORD wCode;
    
    public WinDef.WORD wReserved;
    
    public WTypes.BSTR bstrSource;
    
    public WTypes.BSTR bstrDescription;
    
    public WTypes.BSTR bstrHelpFile;
    
    public WinDef.DWORD dwHelpContext;
    
    public WinDef.PVOID pvReserved;
    
    public ByReference pfnDeferredFillIn;
    
    public WinDef.SCODE scode;
    
    public EXCEPINFO() {}
    
    public EXCEPINFO(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public static class ByReference extends EXCEPINFO implements Structure.ByReference {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\OaIdl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */