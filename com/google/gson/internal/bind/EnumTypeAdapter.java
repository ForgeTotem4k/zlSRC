package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
        Class<?> clazz = param1TypeToken.getRawType();
        if (!Enum.class.isAssignableFrom(clazz) || clazz == Enum.class)
          return null; 
        if (!clazz.isEnum())
          clazz = clazz.getSuperclass(); 
        return (TypeAdapter)new EnumTypeAdapter<>(clazz);
      }
      
      static {
      
      }
    };
  
  private final Map<String, T> nameToConstant = new HashMap<>();
  
  private final Map<String, T> stringToConstant = new HashMap<>();
  
  private final Map<T, String> constantToName = new HashMap<>();
  
  private EnumTypeAdapter(Class<T> paramClass) {
    try {
      Field[] arrayOfField = paramClass.getDeclaredFields();
      byte b = 0;
      for (Field field : arrayOfField) {
        if (field.isEnumConstant())
          arrayOfField[b++] = field; 
      } 
      arrayOfField = Arrays.<Field>copyOf(arrayOfField, b);
      AccessibleObject.setAccessible((AccessibleObject[])arrayOfField, true);
      for (Field field : arrayOfField) {
        Enum enum_ = (Enum)field.get(null);
        String str1 = enum_.name();
        String str2 = enum_.toString();
        SerializedName serializedName = field.<SerializedName>getAnnotation(SerializedName.class);
        if (serializedName != null) {
          str1 = serializedName.value();
          for (String str : serializedName.alternate())
            this.nameToConstant.put(str, (T)enum_); 
        } 
        this.nameToConstant.put(str1, (T)enum_);
        this.stringToConstant.put(str2, (T)enum_);
        this.constantToName.put((T)enum_, str1);
      } 
    } catch (IllegalAccessException illegalAccessException) {
      throw new AssertionError(illegalAccessException);
    } 
  }
  
  public T read(JsonReader paramJsonReader) throws IOException {
    if (paramJsonReader.peek() == JsonToken.NULL) {
      paramJsonReader.nextNull();
      return null;
    } 
    String str = paramJsonReader.nextString();
    Enum enum_ = (Enum)this.nameToConstant.get(str);
    return (enum_ == null) ? this.stringToConstant.get(str) : (T)enum_;
  }
  
  public void write(JsonWriter paramJsonWriter, T paramT) throws IOException {
    paramJsonWriter.value((paramT == null) ? null : this.constantToName.get(paramT));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\EnumTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */