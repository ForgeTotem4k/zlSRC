package com.sun.jna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTypeMapper implements TypeMapper {
  private List<Entry> toNativeConverters = new ArrayList<>();
  
  private List<Entry> fromNativeConverters = new ArrayList<>();
  
  private Class<?> getAltClass(Class<?> paramClass) {
    return (Class<?>)((paramClass == Boolean.class) ? boolean.class : ((paramClass == boolean.class) ? Boolean.class : ((paramClass == Byte.class) ? byte.class : ((paramClass == byte.class) ? Byte.class : ((paramClass == Character.class) ? char.class : ((paramClass == char.class) ? Character.class : ((paramClass == Short.class) ? short.class : ((paramClass == short.class) ? Short.class : ((paramClass == Integer.class) ? int.class : ((paramClass == int.class) ? Integer.class : ((paramClass == Long.class) ? long.class : ((paramClass == long.class) ? Long.class : ((paramClass == Float.class) ? float.class : ((paramClass == float.class) ? Float.class : ((paramClass == Double.class) ? double.class : ((paramClass == double.class) ? Double.class : null))))))))))))))));
  }
  
  public void addToNativeConverter(Class<?> paramClass, ToNativeConverter paramToNativeConverter) {
    this.toNativeConverters.add(new Entry(paramClass, paramToNativeConverter));
    Class<?> clazz = getAltClass(paramClass);
    if (clazz != null)
      this.toNativeConverters.add(new Entry(clazz, paramToNativeConverter)); 
  }
  
  public void addFromNativeConverter(Class<?> paramClass, FromNativeConverter paramFromNativeConverter) {
    this.fromNativeConverters.add(new Entry(paramClass, paramFromNativeConverter));
    Class<?> clazz = getAltClass(paramClass);
    if (clazz != null)
      this.fromNativeConverters.add(new Entry(clazz, paramFromNativeConverter)); 
  }
  
  public void addTypeConverter(Class<?> paramClass, TypeConverter paramTypeConverter) {
    addFromNativeConverter(paramClass, paramTypeConverter);
    addToNativeConverter(paramClass, paramTypeConverter);
  }
  
  private Object lookupConverter(Class<?> paramClass, Collection<? extends Entry> paramCollection) {
    for (Entry entry : paramCollection) {
      if (entry.type.isAssignableFrom(paramClass))
        return entry.converter; 
    } 
    return null;
  }
  
  public FromNativeConverter getFromNativeConverter(Class<?> paramClass) {
    return (FromNativeConverter)lookupConverter(paramClass, this.fromNativeConverters);
  }
  
  public ToNativeConverter getToNativeConverter(Class<?> paramClass) {
    return (ToNativeConverter)lookupConverter(paramClass, this.toNativeConverters);
  }
  
  private static class Entry {
    public Class<?> type;
    
    public Object converter;
    
    public Entry(Class<?> param1Class, Object param1Object) {
      this.type = param1Class;
      this.converter = param1Object;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\DefaultTypeMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */