package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public class NativeMappedConverter implements TypeConverter {
  private static final Map<Class<?>, Reference<NativeMappedConverter>> converters = new WeakHashMap<>();
  
  private final Class<?> type;
  
  private final Class<?> nativeType;
  
  private final NativeMapped instance;
  
  public static NativeMappedConverter getInstance(Class<?> paramClass) {
    synchronized (converters) {
      Reference<NativeMappedConverter> reference = converters.get(paramClass);
      NativeMappedConverter nativeMappedConverter = (reference != null) ? reference.get() : null;
      if (nativeMappedConverter == null) {
        nativeMappedConverter = new NativeMappedConverter(paramClass);
        converters.put(paramClass, new SoftReference<>(nativeMappedConverter));
      } 
      return nativeMappedConverter;
    } 
  }
  
  public NativeMappedConverter(Class<?> paramClass) {
    if (!NativeMapped.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException("Type must derive from " + NativeMapped.class); 
    this.type = paramClass;
    this.instance = defaultValue();
    this.nativeType = this.instance.nativeType();
  }
  
  public NativeMapped defaultValue() {
    return this.type.isEnum() ? (NativeMapped)this.type.getEnumConstants()[0] : (NativeMapped)Klass.newInstance(this.type);
  }
  
  public Object fromNative(Object paramObject, FromNativeContext paramFromNativeContext) {
    return this.instance.fromNative(paramObject, paramFromNativeContext);
  }
  
  public Class<?> nativeType() {
    return this.nativeType;
  }
  
  public Object toNative(Object paramObject, ToNativeContext paramToNativeContext) {
    if (paramObject == null) {
      if (Pointer.class.isAssignableFrom(this.nativeType))
        return null; 
      paramObject = defaultValue();
    } 
    return ((NativeMapped)paramObject).toNative();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\NativeMappedConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */