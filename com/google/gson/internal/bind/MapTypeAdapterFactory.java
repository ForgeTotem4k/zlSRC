package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public final class MapTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  final boolean complexMapKeySerialization;
  
  public MapTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor, boolean paramBoolean) {
    this.constructorConstructor = paramConstructorConstructor;
    this.complexMapKeySerialization = paramBoolean;
  }
  
  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken) {
    Type type1 = paramTypeToken.getType();
    Class<?> clazz = paramTypeToken.getRawType();
    if (!Map.class.isAssignableFrom(clazz))
      return null; 
    Type[] arrayOfType = .Gson.Types.getMapKeyAndValueTypes(type1, clazz);
    Type type2 = arrayOfType[0];
    Type type3 = arrayOfType[1];
    TypeAdapter<?> typeAdapter1 = getKeyAdapter(paramGson, type2);
    TypeAdapterRuntimeTypeWrapper<?> typeAdapterRuntimeTypeWrapper1 = new TypeAdapterRuntimeTypeWrapper(paramGson, typeAdapter1, type2);
    TypeAdapter<?> typeAdapter2 = paramGson.getAdapter(TypeToken.get(type3));
    TypeAdapterRuntimeTypeWrapper<?> typeAdapterRuntimeTypeWrapper2 = new TypeAdapterRuntimeTypeWrapper(paramGson, typeAdapter2, type3);
    ObjectConstructor<? extends Map<?, ?>> objectConstructor = this.constructorConstructor.get(paramTypeToken);
    return (TypeAdapter)new Adapter<>(typeAdapterRuntimeTypeWrapper1, typeAdapterRuntimeTypeWrapper2, objectConstructor);
  }
  
  private TypeAdapter<?> getKeyAdapter(Gson paramGson, Type paramType) {
    return (paramType == boolean.class || paramType == Boolean.class) ? TypeAdapters.BOOLEAN_AS_STRING : paramGson.getAdapter(TypeToken.get(paramType));
  }
  
  private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
    private final TypeAdapter<K> keyTypeAdapter;
    
    private final TypeAdapter<V> valueTypeAdapter;
    
    private final ObjectConstructor<? extends Map<K, V>> constructor;
    
    public Adapter(TypeAdapter<K> param1TypeAdapter, TypeAdapter<V> param1TypeAdapter1, ObjectConstructor<? extends Map<K, V>> param1ObjectConstructor) {
      this.keyTypeAdapter = param1TypeAdapter;
      this.valueTypeAdapter = param1TypeAdapter1;
      this.constructor = param1ObjectConstructor;
    }
    
    public Map<K, V> read(JsonReader param1JsonReader) throws IOException {
      JsonToken jsonToken = param1JsonReader.peek();
      if (jsonToken == JsonToken.NULL) {
        param1JsonReader.nextNull();
        return null;
      } 
      Map<Object, Object> map = (Map)this.constructor.construct();
      if (jsonToken == JsonToken.BEGIN_ARRAY) {
        param1JsonReader.beginArray();
        while (param1JsonReader.hasNext()) {
          param1JsonReader.beginArray();
          Object object1 = this.keyTypeAdapter.read(param1JsonReader);
          Object object2 = this.valueTypeAdapter.read(param1JsonReader);
          Object object = map.put(object1, object2);
          if (object != null)
            throw new JsonSyntaxException("duplicate key: " + object1); 
          param1JsonReader.endArray();
        } 
        param1JsonReader.endArray();
      } else {
        param1JsonReader.beginObject();
        while (param1JsonReader.hasNext()) {
          JsonReaderInternalAccess.INSTANCE.promoteNameToValue(param1JsonReader);
          Object object1 = this.keyTypeAdapter.read(param1JsonReader);
          Object object2 = this.valueTypeAdapter.read(param1JsonReader);
          Object object = map.put(object1, object2);
          if (object != null)
            throw new JsonSyntaxException("duplicate key: " + object1); 
        } 
        param1JsonReader.endObject();
      } 
      return (Map)map;
    }
    
    public void write(JsonWriter param1JsonWriter, Map<K, V> param1Map) throws IOException {
      if (param1Map == null) {
        param1JsonWriter.nullValue();
        return;
      } 
      if (!MapTypeAdapterFactory.this.complexMapKeySerialization) {
        param1JsonWriter.beginObject();
        for (Map.Entry<K, V> entry : param1Map.entrySet()) {
          param1JsonWriter.name(String.valueOf(entry.getKey()));
          this.valueTypeAdapter.write(param1JsonWriter, entry.getValue());
        } 
        param1JsonWriter.endObject();
        return;
      } 
      int i = 0;
      ArrayList<JsonElement> arrayList = new ArrayList(param1Map.size());
      ArrayList arrayList1 = new ArrayList(param1Map.size());
      for (Map.Entry<K, V> entry : param1Map.entrySet()) {
        JsonElement jsonElement = this.keyTypeAdapter.toJsonTree(entry.getKey());
        arrayList.add(jsonElement);
        arrayList1.add(entry.getValue());
        i |= (jsonElement.isJsonArray() || jsonElement.isJsonObject()) ? 1 : 0;
      } 
      if (i != 0) {
        param1JsonWriter.beginArray();
        byte b = 0;
        int j = arrayList.size();
        while (b < j) {
          param1JsonWriter.beginArray();
          Streams.write(arrayList.get(b), param1JsonWriter);
          this.valueTypeAdapter.write(param1JsonWriter, arrayList1.get(b));
          param1JsonWriter.endArray();
          b++;
        } 
        param1JsonWriter.endArray();
      } else {
        param1JsonWriter.beginObject();
        byte b = 0;
        int j = arrayList.size();
        while (b < j) {
          JsonElement jsonElement = arrayList.get(b);
          param1JsonWriter.name(keyToString(jsonElement));
          this.valueTypeAdapter.write(param1JsonWriter, arrayList1.get(b));
          b++;
        } 
        param1JsonWriter.endObject();
      } 
    }
    
    private String keyToString(JsonElement param1JsonElement) {
      if (param1JsonElement.isJsonPrimitive()) {
        JsonPrimitive jsonPrimitive = param1JsonElement.getAsJsonPrimitive();
        if (jsonPrimitive.isNumber())
          return String.valueOf(jsonPrimitive.getAsNumber()); 
        if (jsonPrimitive.isBoolean())
          return Boolean.toString(jsonPrimitive.getAsBoolean()); 
        if (jsonPrimitive.isString())
          return jsonPrimitive.getAsString(); 
        throw new AssertionError();
      } 
      if (param1JsonElement.isJsonNull())
        return "null"; 
      throw new AssertionError();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\MapTypeAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */