package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public interface CoreFoundation extends Library {
  public static final CoreFoundation INSTANCE = (CoreFoundation)Native.load("CoreFoundation", CoreFoundation.class);
  
  public static final int kCFNotFound = -1;
  
  public static final int kCFStringEncodingASCII = 1536;
  
  public static final int kCFStringEncodingUTF8 = 134217984;
  
  public static final CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
  
  public static final CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
  
  public static final CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
  
  public static final CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
  
  public static final CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
  
  public static final CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID();
  
  public static final CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID();
  
  CFStringRef CFStringCreateWithCharacters(CFAllocatorRef paramCFAllocatorRef, char[] paramArrayOfchar, CFIndex paramCFIndex);
  
  CFNumberRef CFNumberCreate(CFAllocatorRef paramCFAllocatorRef, CFIndex paramCFIndex, com.sun.jna.ptr.ByReference paramByReference);
  
  CFArrayRef CFArrayCreate(CFAllocatorRef paramCFAllocatorRef, Pointer paramPointer1, CFIndex paramCFIndex, Pointer paramPointer2);
  
  CFDataRef CFDataCreate(CFAllocatorRef paramCFAllocatorRef, Pointer paramPointer, CFIndex paramCFIndex);
  
  CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef paramCFAllocatorRef, CFIndex paramCFIndex, Pointer paramPointer1, Pointer paramPointer2);
  
  CFStringRef CFCopyDescription(CFTypeRef paramCFTypeRef);
  
  void CFRelease(CFTypeRef paramCFTypeRef);
  
  CFTypeRef CFRetain(CFTypeRef paramCFTypeRef);
  
  CFIndex CFGetRetainCount(CFTypeRef paramCFTypeRef);
  
  CFIndex CFDictionaryGetCount(CFDictionaryRef paramCFDictionaryRef);
  
  Pointer CFDictionaryGetValue(CFDictionaryRef paramCFDictionaryRef, PointerType paramPointerType);
  
  byte CFDictionaryGetValueIfPresent(CFDictionaryRef paramCFDictionaryRef, PointerType paramPointerType, PointerByReference paramPointerByReference);
  
  void CFDictionarySetValue(CFMutableDictionaryRef paramCFMutableDictionaryRef, PointerType paramPointerType1, PointerType paramPointerType2);
  
  byte CFStringGetCString(CFStringRef paramCFStringRef, Pointer paramPointer, CFIndex paramCFIndex, int paramInt);
  
  byte CFBooleanGetValue(CFBooleanRef paramCFBooleanRef);
  
  CFIndex CFArrayGetCount(CFArrayRef paramCFArrayRef);
  
  Pointer CFArrayGetValueAtIndex(CFArrayRef paramCFArrayRef, CFIndex paramCFIndex);
  
  CFIndex CFNumberGetType(CFNumberRef paramCFNumberRef);
  
  byte CFNumberGetValue(CFNumberRef paramCFNumberRef, CFIndex paramCFIndex, com.sun.jna.ptr.ByReference paramByReference);
  
  CFIndex CFStringGetLength(CFStringRef paramCFStringRef);
  
  CFIndex CFStringGetMaximumSizeForEncoding(CFIndex paramCFIndex, int paramInt);
  
  boolean CFEqual(CFTypeRef paramCFTypeRef1, CFTypeRef paramCFTypeRef2);
  
  CFAllocatorRef CFAllocatorGetDefault();
  
  CFIndex CFDataGetLength(CFDataRef paramCFDataRef);
  
  Pointer CFDataGetBytePtr(CFDataRef paramCFDataRef);
  
  CFTypeID CFGetTypeID(CFTypeRef paramCFTypeRef);
  
  CFTypeID CFGetTypeID(Pointer paramPointer);
  
  CFTypeID CFArrayGetTypeID();
  
  CFTypeID CFBooleanGetTypeID();
  
  CFTypeID CFDateGetTypeID();
  
  CFTypeID CFDataGetTypeID();
  
  CFTypeID CFDictionaryGetTypeID();
  
  CFTypeID CFNumberGetTypeID();
  
  CFTypeID CFStringGetTypeID();
  
  public static class CFTypeID extends NativeLong {
    private static final long serialVersionUID = 1L;
    
    public CFTypeID() {}
    
    public CFTypeID(long param1Long) {
      super(param1Long);
    }
    
    public String toString() {
      return equals(CoreFoundation.ARRAY_TYPE_ID) ? "CFArray" : (equals(CoreFoundation.BOOLEAN_TYPE_ID) ? "CFBoolean" : (equals(CoreFoundation.DATA_TYPE_ID) ? "CFData" : (equals(CoreFoundation.DATE_TYPE_ID) ? "CFDate" : (equals(CoreFoundation.DICTIONARY_TYPE_ID) ? "CFDictionary" : (equals(CoreFoundation.NUMBER_TYPE_ID) ? "CFNumber" : (equals(CoreFoundation.STRING_TYPE_ID) ? "CFString" : super.toString()))))));
    }
  }
  
  public static class CFIndex extends NativeLong {
    private static final long serialVersionUID = 1L;
    
    public CFIndex() {}
    
    public CFIndex(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class CFStringRef extends CFTypeRef {
    public CFStringRef() {}
    
    public CFStringRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.STRING_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFString. Type ID: " + getTypeID()); 
    }
    
    public static CFStringRef createCFString(String param1String) {
      char[] arrayOfChar = param1String.toCharArray();
      return CoreFoundation.INSTANCE.CFStringCreateWithCharacters(null, arrayOfChar, new CoreFoundation.CFIndex(arrayOfChar.length));
    }
    
    public String stringValue() {
      CoreFoundation.CFIndex cFIndex1 = CoreFoundation.INSTANCE.CFStringGetLength(this);
      if (cFIndex1.longValue() == 0L)
        return ""; 
      CoreFoundation.CFIndex cFIndex2 = CoreFoundation.INSTANCE.CFStringGetMaximumSizeForEncoding(cFIndex1, 134217984);
      if (cFIndex2.intValue() == -1)
        throw new StringIndexOutOfBoundsException("CFString maximum number of bytes exceeds LONG_MAX."); 
      cFIndex2.setValue(cFIndex2.longValue() + 1L);
      Memory memory = new Memory(cFIndex2.longValue());
      if (0 != CoreFoundation.INSTANCE.CFStringGetCString(this, (Pointer)memory, cFIndex2, 134217984))
        return memory.getString(0L, "UTF8"); 
      throw new IllegalArgumentException("CFString conversion fails or the provided buffer is too small.");
    }
    
    public static class ByReference extends PointerByReference {
      public ByReference() {
        this(null);
      }
      
      public ByReference(CoreFoundation.CFStringRef param2CFStringRef) {
        super((param2CFStringRef != null) ? param2CFStringRef.getPointer() : null);
      }
      
      public void setValue(Pointer param2Pointer) {
        if (param2Pointer != null) {
          CoreFoundation.CFTypeID cFTypeID = CoreFoundation.INSTANCE.CFGetTypeID(param2Pointer);
          if (!CoreFoundation.STRING_TYPE_ID.equals(cFTypeID))
            throw new ClassCastException("Unable to cast to CFString. Type ID: " + cFTypeID); 
        } 
        super.setValue(param2Pointer);
      }
      
      public CoreFoundation.CFStringRef getStringRefValue() {
        Pointer pointer = getValue();
        return (pointer == null) ? null : new CoreFoundation.CFStringRef(pointer);
      }
    }
  }
  
  public static class CFMutableDictionaryRef extends CFDictionaryRef {
    public CFMutableDictionaryRef() {}
    
    public CFMutableDictionaryRef(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public void setValue(PointerType param1PointerType1, PointerType param1PointerType2) {
      CoreFoundation.INSTANCE.CFDictionarySetValue(this, param1PointerType1, param1PointerType2);
    }
  }
  
  public static class CFDictionaryRef extends CFTypeRef {
    public CFDictionaryRef() {}
    
    public CFDictionaryRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.DICTIONARY_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + getTypeID()); 
    }
    
    public Pointer getValue(PointerType param1PointerType) {
      return CoreFoundation.INSTANCE.CFDictionaryGetValue(this, param1PointerType);
    }
    
    public long getCount() {
      return CoreFoundation.INSTANCE.CFDictionaryGetCount(this).longValue();
    }
    
    public boolean getValueIfPresent(PointerType param1PointerType, PointerByReference param1PointerByReference) {
      return (CoreFoundation.INSTANCE.CFDictionaryGetValueIfPresent(this, param1PointerType, param1PointerByReference) > 0);
    }
    
    public static class ByReference extends PointerByReference {
      public ByReference() {
        this(null);
      }
      
      public ByReference(CoreFoundation.CFDictionaryRef param2CFDictionaryRef) {
        super((param2CFDictionaryRef != null) ? param2CFDictionaryRef.getPointer() : null);
      }
      
      public void setValue(Pointer param2Pointer) {
        if (param2Pointer != null) {
          CoreFoundation.CFTypeID cFTypeID = CoreFoundation.INSTANCE.CFGetTypeID(param2Pointer);
          if (!CoreFoundation.DICTIONARY_TYPE_ID.equals(cFTypeID))
            throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + cFTypeID); 
        } 
        super.setValue(param2Pointer);
      }
      
      public CoreFoundation.CFDictionaryRef getDictionaryRefValue() {
        Pointer pointer = getValue();
        return (pointer == null) ? null : new CoreFoundation.CFDictionaryRef(pointer);
      }
    }
  }
  
  public static class CFDataRef extends CFTypeRef {
    public CFDataRef() {}
    
    public CFDataRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.DATA_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFData. Type ID: " + getTypeID()); 
    }
    
    public int getLength() {
      return CoreFoundation.INSTANCE.CFDataGetLength(this).intValue();
    }
    
    public Pointer getBytePtr() {
      return CoreFoundation.INSTANCE.CFDataGetBytePtr(this);
    }
  }
  
  public static class CFArrayRef extends CFTypeRef {
    public CFArrayRef() {}
    
    public CFArrayRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.ARRAY_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFArray. Type ID: " + getTypeID()); 
    }
    
    public int getCount() {
      return CoreFoundation.INSTANCE.CFArrayGetCount(this).intValue();
    }
    
    public Pointer getValueAtIndex(int param1Int) {
      return CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(this, new CoreFoundation.CFIndex(param1Int));
    }
  }
  
  public static class CFBooleanRef extends CFTypeRef {
    public CFBooleanRef() {}
    
    public CFBooleanRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.BOOLEAN_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + getTypeID()); 
    }
    
    public boolean booleanValue() {
      return (0 != CoreFoundation.INSTANCE.CFBooleanGetValue(this));
    }
  }
  
  public enum CFNumberType {
    unusedZero, kCFNumberSInt8Type, kCFNumberSInt16Type, kCFNumberSInt32Type, kCFNumberSInt64Type, kCFNumberFloat32Type, kCFNumberFloat64Type, kCFNumberCharType, kCFNumberShortType, kCFNumberIntType, kCFNumberLongType, kCFNumberLongLongType, kCFNumberFloatType, kCFNumberDoubleType, kCFNumberCFIndexType, kCFNumberNSIntegerType, kCFNumberCGFloatType, kCFNumberMaxType;
    
    public CoreFoundation.CFIndex typeIndex() {
      return new CoreFoundation.CFIndex(ordinal());
    }
  }
  
  public static class CFNumberRef extends CFTypeRef {
    public CFNumberRef() {}
    
    public CFNumberRef(Pointer param1Pointer) {
      super(param1Pointer);
      if (!isTypeID(CoreFoundation.NUMBER_TYPE_ID))
        throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + getTypeID()); 
    }
    
    public long longValue() {
      LongByReference longByReference = new LongByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberLongLongType.typeIndex(), (com.sun.jna.ptr.ByReference)longByReference);
      return longByReference.getValue();
    }
    
    public int intValue() {
      IntByReference intByReference = new IntByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberIntType.typeIndex(), (com.sun.jna.ptr.ByReference)intByReference);
      return intByReference.getValue();
    }
    
    public short shortValue() {
      ShortByReference shortByReference = new ShortByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberShortType.typeIndex(), (com.sun.jna.ptr.ByReference)shortByReference);
      return shortByReference.getValue();
    }
    
    public byte byteValue() {
      ByteByReference byteByReference = new ByteByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberCharType.typeIndex(), (com.sun.jna.ptr.ByReference)byteByReference);
      return byteByReference.getValue();
    }
    
    public double doubleValue() {
      DoubleByReference doubleByReference = new DoubleByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberDoubleType.typeIndex(), (com.sun.jna.ptr.ByReference)doubleByReference);
      return doubleByReference.getValue();
    }
    
    public float floatValue() {
      FloatByReference floatByReference = new FloatByReference();
      CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberFloatType.typeIndex(), (com.sun.jna.ptr.ByReference)floatByReference);
      return floatByReference.getValue();
    }
  }
  
  public static class CFAllocatorRef extends CFTypeRef {}
  
  public static class CFTypeRef extends PointerType {
    public CFTypeRef() {}
    
    public CFTypeRef(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public CoreFoundation.CFTypeID getTypeID() {
      return (getPointer() == null) ? new CoreFoundation.CFTypeID(0L) : CoreFoundation.INSTANCE.CFGetTypeID(this);
    }
    
    public boolean isTypeID(CoreFoundation.CFTypeID param1CFTypeID) {
      return getTypeID().equals(param1CFTypeID);
    }
    
    public void retain() {
      CoreFoundation.INSTANCE.CFRetain(this);
    }
    
    public void release() {
      CoreFoundation.INSTANCE.CFRelease(this);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\mac\CoreFoundation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */