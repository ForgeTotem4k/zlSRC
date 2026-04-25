package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IOKit extends Library {
  public static final IOKit INSTANCE = (IOKit)Native.load("IOKit", IOKit.class);
  
  public static final int kIORegistryIterateRecursively = 1;
  
  public static final int kIORegistryIterateParents = 2;
  
  public static final int kIOReturnNoDevice = -536870208;
  
  public static final double kIOPSTimeRemainingUnlimited = -2.0D;
  
  public static final double kIOPSTimeRemainingUnknown = -1.0D;
  
  int IOMasterPort(int paramInt, IntByReference paramIntByReference);
  
  CoreFoundation.CFMutableDictionaryRef IOServiceMatching(String paramString);
  
  CoreFoundation.CFMutableDictionaryRef IOServiceNameMatching(String paramString);
  
  CoreFoundation.CFMutableDictionaryRef IOBSDNameMatching(int paramInt1, int paramInt2, String paramString);
  
  IOService IOServiceGetMatchingService(int paramInt, CoreFoundation.CFDictionaryRef paramCFDictionaryRef);
  
  int IOServiceGetMatchingServices(int paramInt, CoreFoundation.CFDictionaryRef paramCFDictionaryRef, PointerByReference paramPointerByReference);
  
  IORegistryEntry IOIteratorNext(IOIterator paramIOIterator);
  
  CoreFoundation.CFTypeRef IORegistryEntryCreateCFProperty(IORegistryEntry paramIORegistryEntry, CoreFoundation.CFStringRef paramCFStringRef, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
  
  int IORegistryEntryCreateCFProperties(IORegistryEntry paramIORegistryEntry, PointerByReference paramPointerByReference, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
  
  CoreFoundation.CFTypeRef IORegistryEntrySearchCFProperty(IORegistryEntry paramIORegistryEntry, String paramString, CoreFoundation.CFStringRef paramCFStringRef, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
  
  int IORegistryEntryGetRegistryEntryID(IORegistryEntry paramIORegistryEntry, LongByReference paramLongByReference);
  
  int IORegistryEntryGetName(IORegistryEntry paramIORegistryEntry, Pointer paramPointer);
  
  int IORegistryEntryGetChildIterator(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
  
  int IORegistryEntryGetChildEntry(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
  
  int IORegistryEntryGetParentEntry(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
  
  IORegistryEntry IORegistryGetRootEntry(int paramInt);
  
  boolean IOObjectConformsTo(IOObject paramIOObject, String paramString);
  
  int IOObjectRelease(IOObject paramIOObject);
  
  int IOServiceOpen(IOService paramIOService, int paramInt1, int paramInt2, PointerByReference paramPointerByReference);
  
  int IOServiceGetBusyState(IOService paramIOService, IntByReference paramIntByReference);
  
  int IOServiceClose(IOConnect paramIOConnect);
  
  CoreFoundation.CFTypeRef IOPSCopyPowerSourcesInfo();
  
  CoreFoundation.CFArrayRef IOPSCopyPowerSourcesList(CoreFoundation.CFTypeRef paramCFTypeRef);
  
  CoreFoundation.CFDictionaryRef IOPSGetPowerSourceDescription(CoreFoundation.CFTypeRef paramCFTypeRef1, CoreFoundation.CFTypeRef paramCFTypeRef2);
  
  double IOPSGetTimeRemainingEstimate();
  
  public static class IOConnect extends IOService {
    public IOConnect() {}
    
    public IOConnect(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class IOService extends IORegistryEntry {
    public IOService() {}
    
    public IOService(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class IORegistryEntry extends IOObject {
    public IORegistryEntry() {}
    
    public IORegistryEntry(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public long getRegistryEntryID() {
      LongByReference longByReference = new LongByReference();
      int i = IOKit.INSTANCE.IORegistryEntryGetRegistryEntryID(this, longByReference);
      if (i != 0)
        throw new IOReturnException(i); 
      return longByReference.getValue();
    }
    
    public String getName() {
      Memory memory = new Memory(128L);
      int i = IOKit.INSTANCE.IORegistryEntryGetName(this, (Pointer)memory);
      if (i != 0)
        throw new IOReturnException(i); 
      return memory.getString(0L);
    }
    
    public IOKit.IOIterator getChildIterator(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      int i = IOKit.INSTANCE.IORegistryEntryGetChildIterator(this, param1String, pointerByReference);
      if (i != 0)
        throw new IOReturnException(i); 
      return new IOKit.IOIterator(pointerByReference.getValue());
    }
    
    public IORegistryEntry getChildEntry(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      int i = IOKit.INSTANCE.IORegistryEntryGetChildEntry(this, param1String, pointerByReference);
      if (i == -536870208)
        return null; 
      if (i != 0)
        throw new IOReturnException(i); 
      return new IORegistryEntry(pointerByReference.getValue());
    }
    
    public IORegistryEntry getParentEntry(String param1String) {
      PointerByReference pointerByReference = new PointerByReference();
      int i = IOKit.INSTANCE.IORegistryEntryGetParentEntry(this, param1String, pointerByReference);
      if (i == -536870208)
        return null; 
      if (i != 0)
        throw new IOReturnException(i); 
      return new IORegistryEntry(pointerByReference.getValue());
    }
    
    public CoreFoundation.CFTypeRef createCFProperty(CoreFoundation.CFStringRef param1CFStringRef) {
      return IOKit.INSTANCE.IORegistryEntryCreateCFProperty(this, param1CFStringRef, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
    }
    
    public CoreFoundation.CFMutableDictionaryRef createCFProperties() {
      PointerByReference pointerByReference = new PointerByReference();
      int i = IOKit.INSTANCE.IORegistryEntryCreateCFProperties(this, pointerByReference, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
      if (i != 0)
        throw new IOReturnException(i); 
      return new CoreFoundation.CFMutableDictionaryRef(pointerByReference.getValue());
    }
    
    CoreFoundation.CFTypeRef searchCFProperty(String param1String, CoreFoundation.CFStringRef param1CFStringRef, int param1Int) {
      return IOKit.INSTANCE.IORegistryEntrySearchCFProperty(this, param1String, param1CFStringRef, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), param1Int);
    }
    
    public String getStringProperty(String param1String) {
      String str = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFStringRef cFStringRef1 = new CoreFoundation.CFStringRef(cFTypeRef.getPointer());
        str = cFStringRef1.stringValue();
        cFTypeRef.release();
      } 
      return str;
    }
    
    public Long getLongProperty(String param1String) {
      Long long_ = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(cFTypeRef.getPointer());
        long_ = Long.valueOf(cFNumberRef.longValue());
        cFTypeRef.release();
      } 
      return long_;
    }
    
    public Integer getIntegerProperty(String param1String) {
      Integer integer = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(cFTypeRef.getPointer());
        integer = Integer.valueOf(cFNumberRef.intValue());
        cFTypeRef.release();
      } 
      return integer;
    }
    
    public Double getDoubleProperty(String param1String) {
      Double double_ = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(cFTypeRef.getPointer());
        double_ = Double.valueOf(cFNumberRef.doubleValue());
        cFTypeRef.release();
      } 
      return double_;
    }
    
    public Boolean getBooleanProperty(String param1String) {
      Boolean bool = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFBooleanRef cFBooleanRef = new CoreFoundation.CFBooleanRef(cFTypeRef.getPointer());
        bool = Boolean.valueOf(cFBooleanRef.booleanValue());
        cFTypeRef.release();
      } 
      return bool;
    }
    
    public byte[] getByteArrayProperty(String param1String) {
      byte[] arrayOfByte = null;
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString(param1String);
      CoreFoundation.CFTypeRef cFTypeRef = createCFProperty(cFStringRef);
      cFStringRef.release();
      if (cFTypeRef != null) {
        CoreFoundation.CFDataRef cFDataRef = new CoreFoundation.CFDataRef(cFTypeRef.getPointer());
        int i = cFDataRef.getLength();
        Pointer pointer = cFDataRef.getBytePtr();
        arrayOfByte = pointer.getByteArray(0L, i);
        cFTypeRef.release();
      } 
      return arrayOfByte;
    }
  }
  
  public static class IOIterator extends IOObject {
    public IOIterator() {}
    
    public IOIterator(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public IOKit.IORegistryEntry next() {
      return IOKit.INSTANCE.IOIteratorNext(this);
    }
  }
  
  public static class IOObject extends PointerType {
    public IOObject() {}
    
    public IOObject(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public boolean conformsTo(String param1String) {
      return IOKit.INSTANCE.IOObjectConformsTo(this, param1String);
    }
    
    public int release() {
      return IOKit.INSTANCE.IOObjectRelease(this);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\mac\IOKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */