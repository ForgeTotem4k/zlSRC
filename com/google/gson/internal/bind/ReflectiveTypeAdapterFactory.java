package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  private final FieldNamingStrategy fieldNamingPolicy;
  
  private final Excluder excluder;
  
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  
  private final List<ReflectionAccessFilter> reflectionFilters;
  
  public ReflectiveTypeAdapterFactory(ConstructorConstructor paramConstructorConstructor, FieldNamingStrategy paramFieldNamingStrategy, Excluder paramExcluder, JsonAdapterAnnotationTypeAdapterFactory paramJsonAdapterAnnotationTypeAdapterFactory, List<ReflectionAccessFilter> paramList) {
    this.constructorConstructor = paramConstructorConstructor;
    this.fieldNamingPolicy = paramFieldNamingStrategy;
    this.excluder = paramExcluder;
    this.jsonAdapterFactory = paramJsonAdapterAnnotationTypeAdapterFactory;
    this.reflectionFilters = paramList;
  }
  
  private boolean includeField(Field paramField, boolean paramBoolean) {
    return !this.excluder.excludeField(paramField, paramBoolean);
  }
  
  private List<String> getFieldNames(Field paramField) {
    SerializedName serializedName = paramField.<SerializedName>getAnnotation(SerializedName.class);
    if (serializedName == null) {
      String str1 = this.fieldNamingPolicy.translateName(paramField);
      return Collections.singletonList(str1);
    } 
    String str = serializedName.value();
    String[] arrayOfString = serializedName.alternate();
    if (arrayOfString.length == 0)
      return Collections.singletonList(str); 
    ArrayList<String> arrayList = new ArrayList(arrayOfString.length + 1);
    arrayList.add(str);
    Collections.addAll(arrayList, arrayOfString);
    return arrayList;
  }
  
  public <T> TypeAdapter<T> create(Gson paramGson, TypeToken<T> paramTypeToken) {
    Class<?> clazz = paramTypeToken.getRawType();
    if (!Object.class.isAssignableFrom(clazz))
      return null; 
    if (ReflectionHelper.isAnonymousOrNonStaticLocal(clazz))
      return new TypeAdapter<T>() {
          public T read(JsonReader param1JsonReader) throws IOException {
            param1JsonReader.skipValue();
            return null;
          }
          
          public void write(JsonWriter param1JsonWriter, T param1T) throws IOException {
            param1JsonWriter.nullValue();
          }
          
          public String toString() {
            return "AnonymousOrNonStaticLocalClassAdapter";
          }
        }; 
    ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, clazz);
    if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL)
      throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + clazz + ". Register a TypeAdapter for this type or adjust the access filter."); 
    boolean bool = (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE) ? true : false;
    if (ReflectionHelper.isRecord(clazz))
      return new RecordAdapter(clazz, getBoundFields(paramGson, paramTypeToken, clazz, bool, true), bool); 
    ObjectConstructor<T> objectConstructor = this.constructorConstructor.get(paramTypeToken);
    return new FieldReflectionAdapter<>(objectConstructor, getBoundFields(paramGson, paramTypeToken, clazz, bool, false));
  }
  
  private static <M extends AccessibleObject & Member> void checkAccessible(Object paramObject, M paramM) {
    if (!ReflectionAccessFilterHelper.canAccess((AccessibleObject)paramM, Modifier.isStatic(((Member)paramM).getModifiers()) ? null : paramObject)) {
      String str = ReflectionHelper.getAccessibleObjectDescription((AccessibleObject)paramM, true);
      throw new JsonIOException(str + " is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type, adjust the access filter or increase the visibility of the element and its declaring type.");
    } 
  }
  
  private BoundField createBoundField(Gson paramGson, Field paramField, final Method accessor, String paramString, TypeToken<?> paramTypeToken, boolean paramBoolean1, final boolean blockInaccessible) {
    final TypeAdapter<?> writeTypeAdapter;
    final boolean isPrimitive = Primitives.isPrimitive(paramTypeToken.getRawType());
    int i = paramField.getModifiers();
    final boolean isStaticFinalField = (Modifier.isStatic(i) && Modifier.isFinal(i)) ? true : false;
    JsonAdapter jsonAdapter = paramField.<JsonAdapter>getAnnotation(JsonAdapter.class);
    TypeAdapter<?> typeAdapter1 = null;
    if (jsonAdapter != null)
      typeAdapter1 = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, paramGson, paramTypeToken, jsonAdapter, false); 
    boolean bool2 = (typeAdapter1 != null) ? true : false;
    if (typeAdapter1 == null)
      typeAdapter1 = paramGson.getAdapter(paramTypeToken); 
    final TypeAdapter<?> typeAdapter = typeAdapter1;
    if (paramBoolean1) {
      typeAdapter3 = bool2 ? typeAdapter2 : new TypeAdapterRuntimeTypeWrapper(paramGson, typeAdapter2, paramTypeToken.getType());
    } else {
      typeAdapter3 = typeAdapter2;
    } 
    return new BoundField(paramString, paramField) {
        void write(JsonWriter param1JsonWriter, Object param1Object) throws IOException, IllegalAccessException {
          Object object;
          if (blockInaccessible)
            if (accessor == null) {
              ReflectiveTypeAdapterFactory.checkAccessible(param1Object, (M)this.field);
            } else {
              ReflectiveTypeAdapterFactory.checkAccessible(param1Object, (M)accessor);
            }  
          if (accessor != null) {
            try {
              object = accessor.invoke(param1Object, new Object[0]);
            } catch (InvocationTargetException invocationTargetException) {
              String str = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
              throw new JsonIOException("Accessor " + str + " threw exception", invocationTargetException.getCause());
            } 
          } else {
            object = this.field.get(param1Object);
          } 
          if (object == param1Object)
            return; 
          param1JsonWriter.name(this.serializedName);
          writeTypeAdapter.write(param1JsonWriter, object);
        }
        
        void readIntoArray(JsonReader param1JsonReader, int param1Int, Object[] param1ArrayOfObject) throws IOException, JsonParseException {
          Object object = typeAdapter.read(param1JsonReader);
          if (object == null && isPrimitive)
            throw new JsonParseException("null is not allowed as value for record component '" + this.fieldName + "' of primitive type; at path " + param1JsonReader.getPath()); 
          param1ArrayOfObject[param1Int] = object;
        }
        
        void readIntoField(JsonReader param1JsonReader, Object param1Object) throws IOException, IllegalAccessException {
          Object object = typeAdapter.read(param1JsonReader);
          if (object != null || !isPrimitive) {
            if (blockInaccessible) {
              ReflectiveTypeAdapterFactory.checkAccessible(param1Object, (M)this.field);
            } else if (isStaticFinalField) {
              String str = ReflectionHelper.getAccessibleObjectDescription(this.field, false);
              throw new JsonIOException("Cannot set value of 'static final' " + str);
            } 
            this.field.set(param1Object, object);
          } 
        }
      };
  }
  
  private static IllegalArgumentException createDuplicateFieldException(Class<?> paramClass, String paramString, Field paramField1, Field paramField2) {
    throw new IllegalArgumentException("Class " + paramClass.getName() + " declares multiple JSON fields named '" + paramString + "'; conflict is caused by fields " + ReflectionHelper.fieldToString(paramField1) + " and " + ReflectionHelper.fieldToString(paramField2) + "\nSee " + TroubleshootingGuide.createUrl("duplicate-fields"));
  }
  
  private FieldsData getBoundFields(Gson paramGson, TypeToken<?> paramTypeToken, Class<?> paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramClass.isInterface())
      return FieldsData.EMPTY; 
    LinkedHashMap<Object, Object> linkedHashMap1 = new LinkedHashMap<>();
    LinkedHashMap<Object, Object> linkedHashMap2 = new LinkedHashMap<>();
    Class<?> clazz = paramClass;
    while (paramClass != Object.class) {
      Field[] arrayOfField = paramClass.getDeclaredFields();
      if (paramClass != clazz && arrayOfField.length > 0) {
        ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, paramClass);
        if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL)
          throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + paramClass + " (supertype of " + clazz + "). Register a TypeAdapter for this type or adjust the access filter."); 
        paramBoolean1 = (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE);
      } 
      for (Field field : arrayOfField) {
        boolean bool1 = includeField(field, true);
        boolean bool2 = includeField(field, false);
        if (bool1 || bool2) {
          Method method = null;
          if (paramBoolean2)
            if (Modifier.isStatic(field.getModifiers())) {
              bool2 = false;
            } else {
              method = ReflectionHelper.getAccessor(paramClass, field);
              if (!paramBoolean1)
                ReflectionHelper.makeAccessible(method); 
              if (method.getAnnotation(SerializedName.class) != null && field.getAnnotation(SerializedName.class) == null) {
                String str1 = ReflectionHelper.getAccessibleObjectDescription(method, false);
                throw new JsonIOException("@SerializedName on " + str1 + " is not supported");
              } 
            }  
          if (!paramBoolean1 && method == null)
            ReflectionHelper.makeAccessible(field); 
          Type type = .Gson.Types.resolve(paramTypeToken.getType(), paramClass, field.getGenericType());
          List<String> list = getFieldNames(field);
          String str = list.get(0);
          BoundField boundField = createBoundField(paramGson, field, method, str, TypeToken.get(type), bool1, paramBoolean1);
          if (bool2)
            for (String str1 : list) {
              BoundField boundField1 = (BoundField)linkedHashMap1.put(str1, boundField);
              if (boundField1 != null)
                throw createDuplicateFieldException(clazz, str1, boundField1.field, field); 
            }  
          if (bool1) {
            BoundField boundField1 = (BoundField)linkedHashMap2.put(str, boundField);
            if (boundField1 != null)
              throw createDuplicateFieldException(clazz, str, boundField1.field, field); 
          } 
        } 
      } 
      paramTypeToken = TypeToken.get(.Gson.Types.resolve(paramTypeToken.getType(), paramClass, paramClass.getGenericSuperclass()));
      paramClass = paramTypeToken.getRawType();
    } 
    return new FieldsData((Map)linkedHashMap1, new ArrayList<>(linkedHashMap2.values()));
  }
  
  private static final class RecordAdapter<T> extends Adapter<T, Object[]> {
    static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = primitiveDefaults();
    
    private final Constructor<T> constructor;
    
    private final Object[] constructorArgsDefaults;
    
    private final Map<String, Integer> componentIndices = new HashMap<>();
    
    RecordAdapter(Class<T> param1Class, ReflectiveTypeAdapterFactory.FieldsData param1FieldsData, boolean param1Boolean) {
      super(param1FieldsData);
      this.constructor = ReflectionHelper.getCanonicalRecordConstructor(param1Class);
      if (param1Boolean) {
        ReflectiveTypeAdapterFactory.checkAccessible(null, (M)this.constructor);
      } else {
        ReflectionHelper.makeAccessible(this.constructor);
      } 
      String[] arrayOfString = ReflectionHelper.getRecordComponentNames(param1Class);
      for (byte b1 = 0; b1 < arrayOfString.length; b1++)
        this.componentIndices.put(arrayOfString[b1], Integer.valueOf(b1)); 
      Class[] arrayOfClass = this.constructor.getParameterTypes();
      this.constructorArgsDefaults = new Object[arrayOfClass.length];
      for (byte b2 = 0; b2 < arrayOfClass.length; b2++)
        this.constructorArgsDefaults[b2] = PRIMITIVE_DEFAULTS.get(arrayOfClass[b2]); 
    }
    
    private static Map<Class<?>, Object> primitiveDefaults() {
      HashMap<Object, Object> hashMap = new HashMap<>();
      hashMap.put(byte.class, Byte.valueOf((byte)0));
      hashMap.put(short.class, Short.valueOf((short)0));
      hashMap.put(int.class, Integer.valueOf(0));
      hashMap.put(long.class, Long.valueOf(0L));
      hashMap.put(float.class, Float.valueOf(0.0F));
      hashMap.put(double.class, Double.valueOf(0.0D));
      hashMap.put(char.class, Character.valueOf(false));
      hashMap.put(boolean.class, Boolean.valueOf(false));
      return (Map)hashMap;
    }
    
    Object[] createAccumulator() {
      return (Object[])this.constructorArgsDefaults.clone();
    }
    
    void readField(Object[] param1ArrayOfObject, JsonReader param1JsonReader, ReflectiveTypeAdapterFactory.BoundField param1BoundField) throws IOException {
      Integer integer = this.componentIndices.get(param1BoundField.fieldName);
      if (integer == null)
        throw new IllegalStateException("Could not find the index in the constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' for field with name '" + param1BoundField.fieldName + "', unable to determine which argument in the constructor the field corresponds to. This is unexpected behavior, as we expect the RecordComponents to have the same names as the fields in the Java class, and that the order of the RecordComponents is the same as the order of the canonical constructor parameters."); 
      param1BoundField.readIntoArray(param1JsonReader, integer.intValue(), param1ArrayOfObject);
    }
    
    T finalize(Object[] param1ArrayOfObject) {
      try {
        return this.constructor.newInstance(param1ArrayOfObject);
      } catch (IllegalAccessException illegalAccessException) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(illegalAccessException);
      } catch (InstantiationException|IllegalArgumentException instantiationException) {
        throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(param1ArrayOfObject), instantiationException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(param1ArrayOfObject), invocationTargetException.getCause());
      } 
    }
  }
  
  private static final class FieldReflectionAdapter<T> extends Adapter<T, T> {
    private final ObjectConstructor<T> constructor;
    
    FieldReflectionAdapter(ObjectConstructor<T> param1ObjectConstructor, ReflectiveTypeAdapterFactory.FieldsData param1FieldsData) {
      super(param1FieldsData);
      this.constructor = param1ObjectConstructor;
    }
    
    T createAccumulator() {
      return (T)this.constructor.construct();
    }
    
    void readField(T param1T, JsonReader param1JsonReader, ReflectiveTypeAdapterFactory.BoundField param1BoundField) throws IllegalAccessException, IOException {
      param1BoundField.readIntoField(param1JsonReader, param1T);
    }
    
    T finalize(T param1T) {
      return param1T;
    }
  }
  
  public static abstract class Adapter<T, A> extends TypeAdapter<T> {
    private final ReflectiveTypeAdapterFactory.FieldsData fieldsData;
    
    Adapter(ReflectiveTypeAdapterFactory.FieldsData param1FieldsData) {
      this.fieldsData = param1FieldsData;
    }
    
    public void write(JsonWriter param1JsonWriter, T param1T) throws IOException {
      if (param1T == null) {
        param1JsonWriter.nullValue();
        return;
      } 
      param1JsonWriter.beginObject();
      try {
        for (ReflectiveTypeAdapterFactory.BoundField boundField : this.fieldsData.serializedFields)
          boundField.write(param1JsonWriter, param1T); 
      } catch (IllegalAccessException illegalAccessException) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(illegalAccessException);
      } 
      param1JsonWriter.endObject();
    }
    
    public T read(JsonReader param1JsonReader) throws IOException {
      if (param1JsonReader.peek() == JsonToken.NULL) {
        param1JsonReader.nextNull();
        return null;
      } 
      A a = createAccumulator();
      Map<String, ReflectiveTypeAdapterFactory.BoundField> map = this.fieldsData.deserializedFields;
      try {
        param1JsonReader.beginObject();
        while (param1JsonReader.hasNext()) {
          String str = param1JsonReader.nextName();
          ReflectiveTypeAdapterFactory.BoundField boundField = map.get(str);
          if (boundField == null) {
            param1JsonReader.skipValue();
            continue;
          } 
          readField(a, param1JsonReader, boundField);
        } 
      } catch (IllegalStateException illegalStateException) {
        throw new JsonSyntaxException(illegalStateException);
      } catch (IllegalAccessException illegalAccessException) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(illegalAccessException);
      } 
      param1JsonReader.endObject();
      return finalize(a);
    }
    
    abstract A createAccumulator();
    
    abstract void readField(A param1A, JsonReader param1JsonReader, ReflectiveTypeAdapterFactory.BoundField param1BoundField) throws IllegalAccessException, IOException;
    
    abstract T finalize(A param1A);
  }
  
  static abstract class BoundField {
    final String serializedName;
    
    final Field field;
    
    final String fieldName;
    
    protected BoundField(String param1String, Field param1Field) {
      this.serializedName = param1String;
      this.field = param1Field;
      this.fieldName = param1Field.getName();
    }
    
    abstract void write(JsonWriter param1JsonWriter, Object param1Object) throws IOException, IllegalAccessException;
    
    abstract void readIntoArray(JsonReader param1JsonReader, int param1Int, Object[] param1ArrayOfObject) throws IOException, JsonParseException;
    
    abstract void readIntoField(JsonReader param1JsonReader, Object param1Object) throws IOException, IllegalAccessException;
  }
  
  private static class FieldsData {
    public static final FieldsData EMPTY = new FieldsData(Collections.emptyMap(), Collections.emptyList());
    
    public final Map<String, ReflectiveTypeAdapterFactory.BoundField> deserializedFields;
    
    public final List<ReflectiveTypeAdapterFactory.BoundField> serializedFields;
    
    public FieldsData(Map<String, ReflectiveTypeAdapterFactory.BoundField> param1Map, List<ReflectiveTypeAdapterFactory.BoundField> param1List) {
      this.deserializedFields = param1Map;
      this.serializedFields = param1List;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\ReflectiveTypeAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */