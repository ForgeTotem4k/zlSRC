package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public final class TreeTypeAdapter<T> extends SerializationDelegatingTypeAdapter<T> {
  private final JsonSerializer<T> serializer;
  
  private final JsonDeserializer<T> deserializer;
  
  final Gson gson;
  
  private final TypeToken<T> typeToken;
  
  private final TypeAdapterFactory skipPastForGetDelegateAdapter;
  
  private final GsonContextImpl context = new GsonContextImpl();
  
  private final boolean nullSafe;
  
  private volatile TypeAdapter<T> delegate;
  
  public TreeTypeAdapter(JsonSerializer<T> paramJsonSerializer, JsonDeserializer<T> paramJsonDeserializer, Gson paramGson, TypeToken<T> paramTypeToken, TypeAdapterFactory paramTypeAdapterFactory, boolean paramBoolean) {
    this.serializer = paramJsonSerializer;
    this.deserializer = paramJsonDeserializer;
    this.gson = paramGson;
    this.typeToken = paramTypeToken;
    this.skipPastForGetDelegateAdapter = paramTypeAdapterFactory;
    this.nullSafe = paramBoolean;
  }
  
  public TreeTypeAdapter(JsonSerializer<T> paramJsonSerializer, JsonDeserializer<T> paramJsonDeserializer, Gson paramGson, TypeToken<T> paramTypeToken, TypeAdapterFactory paramTypeAdapterFactory) {
    this(paramJsonSerializer, paramJsonDeserializer, paramGson, paramTypeToken, paramTypeAdapterFactory, true);
  }
  
  public T read(JsonReader paramJsonReader) throws IOException {
    if (this.deserializer == null)
      return (T)delegate().read(paramJsonReader); 
    JsonElement jsonElement = Streams.parse(paramJsonReader);
    return (T)((this.nullSafe && jsonElement.isJsonNull()) ? null : this.deserializer.deserialize(jsonElement, this.typeToken.getType(), this.context));
  }
  
  public void write(JsonWriter paramJsonWriter, T paramT) throws IOException {
    if (this.serializer == null) {
      delegate().write(paramJsonWriter, paramT);
      return;
    } 
    if (this.nullSafe && paramT == null) {
      paramJsonWriter.nullValue();
      return;
    } 
    JsonElement jsonElement = this.serializer.serialize(paramT, this.typeToken.getType(), this.context);
    Streams.write(jsonElement, paramJsonWriter);
  }
  
  private TypeAdapter<T> delegate() {
    TypeAdapter<T> typeAdapter = this.delegate;
    return (typeAdapter != null) ? typeAdapter : (this.delegate = this.gson.getDelegateAdapter(this.skipPastForGetDelegateAdapter, this.typeToken));
  }
  
  public TypeAdapter<T> getSerializationDelegate() {
    return (this.serializer != null) ? this : delegate();
  }
  
  public static TypeAdapterFactory newFactory(TypeToken<?> paramTypeToken, Object paramObject) {
    return new SingleTypeFactory(paramObject, paramTypeToken, false, null);
  }
  
  public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> paramTypeToken, Object paramObject) {
    boolean bool = (paramTypeToken.getType() == paramTypeToken.getRawType()) ? true : false;
    return new SingleTypeFactory(paramObject, paramTypeToken, bool, null);
  }
  
  public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> paramClass, Object paramObject) {
    return new SingleTypeFactory(paramObject, null, false, paramClass);
  }
  
  private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {
    private GsonContextImpl() {}
    
    public JsonElement serialize(Object param1Object) {
      return TreeTypeAdapter.this.gson.toJsonTree(param1Object);
    }
    
    public JsonElement serialize(Object param1Object, Type param1Type) {
      return TreeTypeAdapter.this.gson.toJsonTree(param1Object, param1Type);
    }
    
    public <R> R deserialize(JsonElement param1JsonElement, Type param1Type) throws JsonParseException {
      return (R)TreeTypeAdapter.this.gson.fromJson(param1JsonElement, param1Type);
    }
  }
  
  private static final class SingleTypeFactory implements TypeAdapterFactory {
    private final TypeToken<?> exactType;
    
    private final boolean matchRawType;
    
    private final Class<?> hierarchyType;
    
    private final JsonSerializer<?> serializer;
    
    private final JsonDeserializer<?> deserializer;
    
    SingleTypeFactory(Object param1Object, TypeToken<?> param1TypeToken, boolean param1Boolean, Class<?> param1Class) {
      this.serializer = (param1Object instanceof JsonSerializer) ? (JsonSerializer)param1Object : null;
      this.deserializer = (param1Object instanceof JsonDeserializer) ? (JsonDeserializer)param1Object : null;
      .Gson.Preconditions.checkArgument((this.serializer != null || this.deserializer != null));
      this.exactType = param1TypeToken;
      this.matchRawType = param1Boolean;
      this.hierarchyType = param1Class;
    }
    
    public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
      boolean bool = (this.exactType != null) ? ((this.exactType.equals(param1TypeToken) || (this.matchRawType && this.exactType.getType() == param1TypeToken.getRawType())) ? true : false) : this.hierarchyType.isAssignableFrom(param1TypeToken.getRawType());
      return bool ? new TreeTypeAdapter<>((JsonSerializer)this.serializer, (JsonDeserializer)this.deserializer, param1Gson, param1TypeToken, this) : null;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\TreeTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */