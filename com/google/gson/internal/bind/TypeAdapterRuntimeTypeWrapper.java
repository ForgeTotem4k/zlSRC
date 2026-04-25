package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
  private final Gson context;
  
  private final TypeAdapter<T> delegate;
  
  private final Type type;
  
  TypeAdapterRuntimeTypeWrapper(Gson paramGson, TypeAdapter<T> paramTypeAdapter, Type paramType) {
    this.context = paramGson;
    this.delegate = paramTypeAdapter;
    this.type = paramType;
  }
  
  public T read(JsonReader paramJsonReader) throws IOException {
    return (T)this.delegate.read(paramJsonReader);
  }
  
  public void write(JsonWriter paramJsonWriter, T paramT) throws IOException {
    TypeAdapter<T> typeAdapter = this.delegate;
    Type type = getRuntimeTypeIfMoreSpecific(this.type, paramT);
    if (type != this.type) {
      TypeAdapter<T> typeAdapter1 = this.context.getAdapter(TypeToken.get(type));
      if (!(typeAdapter1 instanceof ReflectiveTypeAdapterFactory.Adapter)) {
        typeAdapter = typeAdapter1;
      } else if (!isReflective(this.delegate)) {
        typeAdapter = this.delegate;
      } else {
        typeAdapter = typeAdapter1;
      } 
    } 
    typeAdapter.write(paramJsonWriter, paramT);
  }
  
  private static boolean isReflective(TypeAdapter<?> paramTypeAdapter) {
    while (paramTypeAdapter instanceof SerializationDelegatingTypeAdapter) {
      TypeAdapter<?> typeAdapter = ((SerializationDelegatingTypeAdapter)paramTypeAdapter).getSerializationDelegate();
      if (typeAdapter == paramTypeAdapter)
        break; 
      paramTypeAdapter = typeAdapter;
    } 
    return paramTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter;
  }
  
  private static Type getRuntimeTypeIfMoreSpecific(Type<?> paramType, Object paramObject) {
    if (paramObject != null && (paramType instanceof Class || paramType instanceof java.lang.reflect.TypeVariable))
      paramType = paramObject.getClass(); 
    return paramType;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\TypeAdapterRuntimeTypeWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */