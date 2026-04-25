package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.ptr.PointerByReference;

public interface Kstat2 extends Library {
  public static final Kstat2 INSTANCE = (Kstat2)Native.load("kstat2", Kstat2.class);
  
  public static final int KSTAT2_S_OK = 0;
  
  public static final int KSTAT2_S_NO_PERM = 1;
  
  public static final int KSTAT2_S_NO_MEM = 2;
  
  public static final int KSTAT2_S_NO_SPACE = 3;
  
  public static final int KSTAT2_S_INVAL_ARG = 4;
  
  public static final int KSTAT2_S_INVAL_STATE = 5;
  
  public static final int KSTAT2_S_INVAL_TYPE = 6;
  
  public static final int KSTAT2_S_NOT_FOUND = 7;
  
  public static final int KSTAT2_S_CONC_MOD = 8;
  
  public static final int KSTAT2_S_DEL_MAP = 9;
  
  public static final int KSTAT2_S_SYS_FAIL = 10;
  
  public static final int KSTAT2_M_STRING = 0;
  
  public static final int KSTAT2_M_GLOB = 1;
  
  public static final int KSTAT2_M_RE = 2;
  
  public static final byte KSTAT2_NVVT_MAP = 0;
  
  public static final byte KSTAT2_NVVT_INT = 1;
  
  public static final byte KSTAT2_NVVT_INTS = 2;
  
  public static final byte KSTAT2_NVVT_STR = 3;
  
  public static final byte KSTAT2_NVVT_STRS = 4;
  
  public static final byte KSTAT2_NVK_SYS = 1;
  
  public static final byte KSTAT2_NVK_USR = 2;
  
  public static final byte KSTAT2_NVK_MAP = 4;
  
  public static final byte KSTAT2_NVK_ALL = 7;
  
  public static final short KSTAT2_NVF_NONE = 0;
  
  public static final short KSTAT2_NVF_INVAL = 1;
  
  int kstat2_open(PointerByReference paramPointerByReference, Kstat2MatcherList paramKstat2MatcherList);
  
  int kstat2_update(Kstat2Handle paramKstat2Handle);
  
  int kstat2_close(PointerByReference paramPointerByReference);
  
  int kstat2_alloc_matcher_list(PointerByReference paramPointerByReference);
  
  int kstat2_free_matcher_list(PointerByReference paramPointerByReference);
  
  int kstat2_add_matcher(int paramInt, String paramString, Kstat2MatcherList paramKstat2MatcherList);
  
  int kstat2_lookup_map(Kstat2Handle paramKstat2Handle, String paramString, PointerByReference paramPointerByReference);
  
  int kstat2_map_get(Kstat2Map paramKstat2Map, String paramString, PointerByReference paramPointerByReference);
  
  String kstat2_status_string(int paramInt);
  
  @FieldOrder({"name", "type", "kind", "flags", "data"})
  public static class Kstat2NV extends Structure {
    public String name;
    
    public byte type;
    
    public byte kind;
    
    public short flags;
    
    public UNION data;
    
    public Kstat2NV() {}
    
    public Kstat2NV(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public void read() {
      super.read();
      switch (this.type) {
        case 0:
          this.data.setType(Kstat2.Kstat2Map.class);
          break;
        case 1:
          this.data.setType(long.class);
          break;
        case 2:
          this.data.setType(UNION.IntegersArr.class);
          break;
        case 3:
        case 4:
          this.data.setType(UNION.StringsArr.class);
          break;
      } 
      this.data.read();
    }
    
    public static class UNION extends Union {
      public Kstat2.Kstat2Map map;
      
      public long integerVal;
      
      public IntegersArr integers;
      
      public StringsArr strings;
      
      @FieldOrder({"addr", "len"})
      public static class StringsArr extends Structure {
        public Pointer addr;
        
        public int len;
      }
      
      @FieldOrder({"addr", "len"})
      public static class IntegersArr extends Structure {
        public Pointer addr;
        
        public int len;
      }
    }
    
    @FieldOrder({"addr", "len"})
    public static class IntegersArr extends Structure {
      public Pointer addr;
      
      public int len;
    }
    
    @FieldOrder({"addr", "len"})
    public static class StringsArr extends Structure {
      public Pointer addr;
      
      public int len;
    }
  }
  
  public static class Kstat2Map extends PointerType {
    public Kstat2Map() {}
    
    public Kstat2Map(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public Kstat2.Kstat2NV mapGet(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      int i = Kstat2.INSTANCE.kstat2_map_get(this, param1String, pointerByReference);
      if (i != 0)
        throw new Kstat2StatusException(i); 
      return new Kstat2.Kstat2NV(pointerByReference.getValue());
    }
    
    public Object getValue(String param1String) {
      try {
        Kstat2.Kstat2NV kstat2NV = mapGet(param1String);
        if (kstat2NV.flags == 1)
          return null; 
        switch (kstat2NV.type) {
          case 0:
            return kstat2NV.data.map;
          case 1:
            return Long.valueOf(kstat2NV.data.integerVal);
          case 2:
            return kstat2NV.data.integers.addr.getLongArray(0L, kstat2NV.data.integers.len);
          case 3:
            return kstat2NV.data.strings.addr.getString(0L);
          case 4:
            return kstat2NV.data.strings.addr.getStringArray(0L, kstat2NV.data.strings.len);
        } 
        return null;
      } catch (Kstat2StatusException kstat2StatusException) {
        return null;
      } 
    }
  }
  
  public static class Kstat2MatcherList extends PointerType {
    private PointerByReference ref = new PointerByReference();
    
    public Kstat2MatcherList() {
      int i = Kstat2.INSTANCE.kstat2_alloc_matcher_list(this.ref);
      if (i != 0)
        throw new Kstat2StatusException(i); 
      setPointer(this.ref.getValue());
    }
    
    public int addMatcher(int param1Int, String param1String) {
      return Kstat2.INSTANCE.kstat2_add_matcher(param1Int, param1String, this);
    }
    
    public int free() {
      return Kstat2.INSTANCE.kstat2_free_matcher_list(this.ref);
    }
  }
  
  public static class Kstat2Handle extends PointerType {
    private PointerByReference ref = new PointerByReference();
    
    public Kstat2Handle() {
      this(null);
    }
    
    public Kstat2Handle(Kstat2.Kstat2MatcherList param1Kstat2MatcherList) {
      int i = Kstat2.INSTANCE.kstat2_open(this.ref, param1Kstat2MatcherList);
      if (i != 0)
        throw new Kstat2StatusException(i); 
      setPointer(this.ref.getValue());
    }
    
    public int update() {
      return Kstat2.INSTANCE.kstat2_update(this);
    }
    
    public Kstat2.Kstat2Map lookupMap(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      int i = Kstat2.INSTANCE.kstat2_lookup_map(this, param1String, pointerByReference);
      if (i != 0)
        throw new Kstat2StatusException(i); 
      return new Kstat2.Kstat2Map(pointerByReference.getValue());
    }
    
    public int close() {
      return Kstat2.INSTANCE.kstat2_close(this.ref);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platfor\\unix\solaris\Kstat2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */