package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {
  private static final TypeAdapterFactory DOUBLE_FACTORY = newFactory((ToNumberStrategy)ToNumberPolicy.DOUBLE);
  
  private final Gson gson;
  
  private final ToNumberStrategy toNumberStrategy;
  
  private ObjectTypeAdapter(Gson paramGson, ToNumberStrategy paramToNumberStrategy) {
    this.gson = paramGson;
    this.toNumberStrategy = paramToNumberStrategy;
  }
  
  private static TypeAdapterFactory newFactory(final ToNumberStrategy toNumberStrategy) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          return (param1TypeToken.getRawType() == Object.class) ? new ObjectTypeAdapter(param1Gson, toNumberStrategy) : null;
        }
      };
  }
  
  public static TypeAdapterFactory getFactory(ToNumberStrategy paramToNumberStrategy) {
    return (paramToNumberStrategy == ToNumberPolicy.DOUBLE) ? DOUBLE_FACTORY : newFactory(paramToNumberStrategy);
  }
  
  private Object tryBeginNesting(JsonReader paramJsonReader, JsonToken paramJsonToken) throws IOException {
    switch (paramJsonToken) {
      case BEGIN_ARRAY:
        paramJsonReader.beginArray();
        return new ArrayList();
      case BEGIN_OBJECT:
        paramJsonReader.beginObject();
        return new LinkedTreeMap();
    } 
    return null;
  }
  
  private Object readTerminal(JsonReader paramJsonReader, JsonToken paramJsonToken) throws IOException {
    switch (paramJsonToken) {
      case STRING:
        return paramJsonReader.nextString();
      case NUMBER:
        return this.toNumberStrategy.readNumber(paramJsonReader);
      case BOOLEAN:
        return Boolean.valueOf(paramJsonReader.nextBoolean());
      case NULL:
        paramJsonReader.nextNull();
        return null;
    } 
    throw new IllegalStateException("Unexpected token: " + paramJsonToken);
  }
  
  public Object read(JsonReader paramJsonReader) throws IOException {
    JsonToken jsonToken = paramJsonReader.peek();
    Object object = tryBeginNesting(paramJsonReader, jsonToken);
    if (object == null)
      return readTerminal(paramJsonReader, jsonToken); 
    ArrayDeque<Object> arrayDeque = new ArrayDeque();
    while (true) {
      while (paramJsonReader.hasNext()) {
        String str = null;
        if (object instanceof Map)
          str = paramJsonReader.nextName(); 
        jsonToken = paramJsonReader.peek();
        Object object1 = tryBeginNesting(paramJsonReader, jsonToken);
        boolean bool = (object1 != null) ? true : false;
        if (object1 == null)
          object1 = readTerminal(paramJsonReader, jsonToken); 
        if (object instanceof List) {
          List<Object> list = (List)object;
          list.add(object1);
        } else {
          Map<String, Object> map = (Map)object;
          map.put(str, object1);
        } 
        if (bool) {
          arrayDeque.addLast(object);
          object = object1;
        } 
      } 
      if (object instanceof List) {
        paramJsonReader.endArray();
      } else {
        paramJsonReader.endObject();
      } 
      if (arrayDeque.isEmpty())
        return object; 
      object = arrayDeque.removeLast();
    } 
  }
  
  public void write(JsonWriter paramJsonWriter, Object paramObject) throws IOException {
    if (paramObject == null) {
      paramJsonWriter.nullValue();
      return;
    } 
    TypeAdapter typeAdapter = this.gson.getAdapter(paramObject.getClass());
    if (typeAdapter instanceof ObjectTypeAdapter) {
      paramJsonWriter.beginObject();
      paramJsonWriter.endObject();
      return;
    } 
    typeAdapter.write(paramJsonWriter, paramObject);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\ObjectTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */