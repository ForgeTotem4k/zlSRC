package com.sun.jna.platform;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class EnumConverter<T extends Enum<T>> implements TypeConverter {
  private final Class<T> clazz;
  
  public EnumConverter(Class<T> paramClass) {
    this.clazz = paramClass;
  }
  
  public T fromNative(Object paramObject, FromNativeContext paramFromNativeContext) {
    Integer integer = (Integer)paramObject;
    Enum[] arrayOfEnum = (Enum[])this.clazz.getEnumConstants();
    return (T)arrayOfEnum[integer.intValue()];
  }
  
  public Integer toNative(Object paramObject, ToNativeContext paramToNativeContext) {
    Enum enum_ = (Enum)this.clazz.cast(paramObject);
    return Integer.valueOf(enum_.ordinal());
  }
  
  public Class<Integer> nativeType() {
    return Integer.class;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\EnumConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */