package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  public CollectionTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor) {
    this.constructorConstructor = paramConstructorConstructor;
  }
  
  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken) {
    Type type1 = paramTypeToken.getType();
    Class<?> clazz = paramTypeToken.getRawType();
    if (!Collection.class.isAssignableFrom(clazz))
      return null; 
    Type type2 = .Gson.Types.getCollectionElementType(type1, clazz);
    TypeAdapter<?> typeAdapter = paramGson.getAdapter(TypeToken.get(type2));
    TypeAdapterRuntimeTypeWrapper<?> typeAdapterRuntimeTypeWrapper = new TypeAdapterRuntimeTypeWrapper(paramGson, typeAdapter, type2);
    ObjectConstructor<? extends Collection<?>> objectConstructor = this.constructorConstructor.get(paramTypeToken);
    return new Adapter(typeAdapterRuntimeTypeWrapper, objectConstructor);
  }
  
  private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
    private final TypeAdapter<E> elementTypeAdapter;
    
    private final ObjectConstructor<? extends Collection<E>> constructor;
    
    public Adapter(TypeAdapter<E> param1TypeAdapter, ObjectConstructor<? extends Collection<E>> param1ObjectConstructor) {
      this.elementTypeAdapter = param1TypeAdapter;
      this.constructor = param1ObjectConstructor;
    }
    
    public Collection<E> read(JsonReader param1JsonReader) throws IOException {
      if (param1JsonReader.peek() == JsonToken.NULL) {
        param1JsonReader.nextNull();
        return null;
      } 
      Collection<Object> collection = (Collection)this.constructor.construct();
      param1JsonReader.beginArray();
      while (param1JsonReader.hasNext()) {
        Object object = this.elementTypeAdapter.read(param1JsonReader);
        collection.add(object);
      } 
      param1JsonReader.endArray();
      return (Collection)collection;
    }
    
    public void write(JsonWriter param1JsonWriter, Collection<E> param1Collection) throws IOException {
      // Byte code:
      //   0: aload_2
      //   1: ifnonnull -> 10
      //   4: aload_1
      //   5: invokevirtual nullValue : ()Lcom/google/gson/stream/JsonWriter;
      //   8: pop
      //   9: return
      //   10: aload_1
      //   11: invokevirtual beginArray : ()Lcom/google/gson/stream/JsonWriter;
      //   14: pop
      //   15: aload_2
      //   16: invokeinterface iterator : ()Ljava/util/Iterator;
      //   21: astore_3
      //   22: aload_3
      //   23: invokeinterface hasNext : ()Z
      //   28: ifeq -> 52
      //   31: aload_3
      //   32: invokeinterface next : ()Ljava/lang/Object;
      //   37: astore #4
      //   39: aload_0
      //   40: getfield elementTypeAdapter : Lcom/google/gson/TypeAdapter;
      //   43: aload_1
      //   44: aload #4
      //   46: invokevirtual write : (Lcom/google/gson/stream/JsonWriter;Ljava/lang/Object;)V
      //   49: goto -> 22
      //   52: aload_1
      //   53: invokevirtual endArray : ()Lcom/google/gson/stream/JsonWriter;
      //   56: pop
      //   57: return
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\CollectionTypeAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */