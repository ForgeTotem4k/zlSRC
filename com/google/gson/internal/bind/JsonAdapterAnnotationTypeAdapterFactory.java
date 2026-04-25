package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
  private static final TypeAdapterFactory TREE_TYPE_CLASS_DUMMY_FACTORY = new DummyTypeAdapterFactory();
  
  private static final TypeAdapterFactory TREE_TYPE_FIELD_DUMMY_FACTORY = new DummyTypeAdapterFactory();
  
  private final ConstructorConstructor constructorConstructor;
  
  private final ConcurrentMap<Class<?>, TypeAdapterFactory> adapterFactoryMap;
  
  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor) {
    this.constructorConstructor = paramConstructorConstructor;
    this.adapterFactoryMap = new ConcurrentHashMap<>();
  }
  
  private static JsonAdapter getAnnotation(Class<?> paramClass) {
    return paramClass.<JsonAdapter>getAnnotation(JsonAdapter.class);
  }
  
  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken) {
    Class<?> clazz = paramTypeToken.getRawType();
    JsonAdapter jsonAdapter = getAnnotation(clazz);
    return (TypeAdapter)((jsonAdapter == null) ? null : getTypeAdapter(this.constructorConstructor, paramGson, paramTypeToken, jsonAdapter, true));
  }
  
  private static Object createAdapter(ConstructorConstructor paramConstructorConstructor, Class<?> paramClass) {
    return paramConstructorConstructor.get(TypeToken.get(paramClass)).construct();
  }
  
  private TypeAdapterFactory putFactoryAndGetCurrent(Class<?> paramClass, TypeAdapterFactory paramTypeAdapterFactory) {
    TypeAdapterFactory typeAdapterFactory = this.adapterFactoryMap.putIfAbsent(paramClass, paramTypeAdapterFactory);
    return (typeAdapterFactory != null) ? typeAdapterFactory : paramTypeAdapterFactory;
  }
  
  TypeAdapter<?> getTypeAdapter(ConstructorConstructor paramConstructorConstructor, Gson paramGson, TypeToken<?> paramTypeToken, JsonAdapter paramJsonAdapter, boolean paramBoolean) {
    TypeAdapter<?> typeAdapter;
    Object object = createAdapter(paramConstructorConstructor, paramJsonAdapter.value());
    boolean bool = paramJsonAdapter.nullSafe();
    if (object instanceof TypeAdapter) {
      typeAdapter = (TypeAdapter)object;
    } else if (object instanceof TypeAdapterFactory) {
      TypeAdapterFactory typeAdapterFactory = (TypeAdapterFactory)object;
      if (paramBoolean)
        typeAdapterFactory = putFactoryAndGetCurrent(paramTypeToken.getRawType(), typeAdapterFactory); 
      typeAdapter = typeAdapterFactory.create(paramGson, paramTypeToken);
    } else if (object instanceof JsonSerializer || object instanceof JsonDeserializer) {
      TypeAdapterFactory typeAdapterFactory;
      JsonSerializer<?> jsonSerializer = (object instanceof JsonSerializer) ? (JsonSerializer)object : null;
      JsonDeserializer<?> jsonDeserializer = (object instanceof JsonDeserializer) ? (JsonDeserializer)object : null;
      if (paramBoolean) {
        typeAdapterFactory = TREE_TYPE_CLASS_DUMMY_FACTORY;
      } else {
        typeAdapterFactory = TREE_TYPE_FIELD_DUMMY_FACTORY;
      } 
      TreeTypeAdapter<?> treeTypeAdapter = new TreeTypeAdapter(jsonSerializer, jsonDeserializer, paramGson, paramTypeToken, typeAdapterFactory, bool);
      typeAdapter = treeTypeAdapter;
      bool = false;
    } else {
      throw new IllegalArgumentException("Invalid attempt to bind an instance of " + object.getClass().getName() + " as a @JsonAdapter for " + paramTypeToken.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
    } 
    if (typeAdapter != null && bool)
      typeAdapter = typeAdapter.nullSafe(); 
    return typeAdapter;
  }
  
  public boolean isClassJsonAdapterFactory(TypeToken<?> paramTypeToken, TypeAdapterFactory paramTypeAdapterFactory) {
    Objects.requireNonNull(paramTypeToken);
    Objects.requireNonNull(paramTypeAdapterFactory);
    if (paramTypeAdapterFactory == TREE_TYPE_CLASS_DUMMY_FACTORY)
      return true; 
    Class<?> clazz1 = paramTypeToken.getRawType();
    TypeAdapterFactory typeAdapterFactory1 = this.adapterFactoryMap.get(clazz1);
    if (typeAdapterFactory1 != null)
      return (typeAdapterFactory1 == paramTypeAdapterFactory); 
    JsonAdapter jsonAdapter = getAnnotation(clazz1);
    if (jsonAdapter == null)
      return false; 
    Class<?> clazz2 = jsonAdapter.value();
    if (!TypeAdapterFactory.class.isAssignableFrom(clazz2))
      return false; 
    Object object = createAdapter(this.constructorConstructor, clazz2);
    TypeAdapterFactory typeAdapterFactory2 = (TypeAdapterFactory)object;
    return (putFactoryAndGetCurrent(clazz1, typeAdapterFactory2) == paramTypeAdapterFactory);
  }
  
  private static class DummyTypeAdapterFactory implements TypeAdapterFactory {
    private DummyTypeAdapterFactory() {}
    
    public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
      throw new AssertionError("Factory should not be used");
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\JsonAdapterAnnotationTypeAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */