package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;

public class W32APITypeMapper extends DefaultTypeMapper {
  public static final TypeMapper UNICODE = (TypeMapper)new W32APITypeMapper(true);
  
  public static final TypeMapper ASCII = (TypeMapper)new W32APITypeMapper(false);
  
  public static final TypeMapper DEFAULT = Boolean.getBoolean("w32.ascii") ? ASCII : UNICODE;
  
  protected W32APITypeMapper(boolean paramBoolean) {
    if (paramBoolean) {
      TypeConverter typeConverter1 = new TypeConverter() {
          public Object toNative(Object param1Object, ToNativeContext param1ToNativeContext) {
            return (param1Object == null) ? null : ((param1Object instanceof String[]) ? new StringArray((String[])param1Object, true) : new WString(param1Object.toString()));
          }
          
          public Object fromNative(Object param1Object, FromNativeContext param1FromNativeContext) {
            return (param1Object == null) ? null : param1Object.toString();
          }
          
          public Class<?> nativeType() {
            return WString.class;
          }
        };
      addTypeConverter(String.class, typeConverter1);
      addToNativeConverter(String[].class, (ToNativeConverter)typeConverter1);
    } 
    TypeConverter typeConverter = new TypeConverter() {
        public Object toNative(Object param1Object, ToNativeContext param1ToNativeContext) {
          return Integer.valueOf(Boolean.TRUE.equals(param1Object) ? 1 : 0);
        }
        
        public Object fromNative(Object param1Object, FromNativeContext param1FromNativeContext) {
          return (((Integer)param1Object).intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
        }
        
        public Class<?> nativeType() {
          return Integer.class;
        }
      };
    addTypeConverter(Boolean.class, typeConverter);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\win32\W32APITypeMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */