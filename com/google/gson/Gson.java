package com.google.gson;

import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SerializationDelegatingTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
  static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
  
  static final Strictness DEFAULT_STRICTNESS = null;
  
  static final FormattingStyle DEFAULT_FORMATTING_STYLE = FormattingStyle.COMPACT;
  
  static final boolean DEFAULT_ESCAPE_HTML = true;
  
  static final boolean DEFAULT_SERIALIZE_NULLS = false;
  
  static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
  
  static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
  
  static final boolean DEFAULT_USE_JDK_UNSAFE = true;
  
  static final String DEFAULT_DATE_PATTERN = null;
  
  static final FieldNamingStrategy DEFAULT_FIELD_NAMING_STRATEGY = FieldNamingPolicy.IDENTITY;
  
  static final ToNumberStrategy DEFAULT_OBJECT_TO_NUMBER_STRATEGY = ToNumberPolicy.DOUBLE;
  
  static final ToNumberStrategy DEFAULT_NUMBER_TO_NUMBER_STRATEGY = ToNumberPolicy.LAZILY_PARSED_NUMBER;
  
  private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
  
  private final ThreadLocal<Map<TypeToken<?>, TypeAdapter<?>>> threadLocalAdapterResults = new ThreadLocal<>();
  
  private final ConcurrentMap<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new ConcurrentHashMap<>();
  
  private final ConstructorConstructor constructorConstructor;
  
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  
  final List<TypeAdapterFactory> factories;
  
  final Excluder excluder;
  
  final FieldNamingStrategy fieldNamingStrategy;
  
  final Map<Type, InstanceCreator<?>> instanceCreators;
  
  final boolean serializeNulls;
  
  final boolean complexMapKeySerialization;
  
  final boolean generateNonExecutableJson;
  
  final boolean htmlSafe;
  
  final FormattingStyle formattingStyle;
  
  final Strictness strictness;
  
  final boolean serializeSpecialFloatingPointValues;
  
  final boolean useJdkUnsafe;
  
  final String datePattern;
  
  final int dateStyle;
  
  final int timeStyle;
  
  final LongSerializationPolicy longSerializationPolicy;
  
  final List<TypeAdapterFactory> builderFactories;
  
  final List<TypeAdapterFactory> builderHierarchyFactories;
  
  final ToNumberStrategy objectToNumberStrategy;
  
  final ToNumberStrategy numberToNumberStrategy;
  
  final List<ReflectionAccessFilter> reflectionFilters;
  
  public Gson() {
    this(Excluder.DEFAULT, DEFAULT_FIELD_NAMING_STRATEGY, Collections.emptyMap(), false, false, false, true, DEFAULT_FORMATTING_STYLE, DEFAULT_STRICTNESS, false, true, LongSerializationPolicy.DEFAULT, DEFAULT_DATE_PATTERN, 2, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), DEFAULT_OBJECT_TO_NUMBER_STRATEGY, DEFAULT_NUMBER_TO_NUMBER_STRATEGY, Collections.emptyList());
  }
  
  Gson(Excluder paramExcluder, FieldNamingStrategy paramFieldNamingStrategy, Map<Type, InstanceCreator<?>> paramMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, FormattingStyle paramFormattingStyle, Strictness paramStrictness, boolean paramBoolean5, boolean paramBoolean6, LongSerializationPolicy paramLongSerializationPolicy, String paramString, int paramInt1, int paramInt2, List<TypeAdapterFactory> paramList1, List<TypeAdapterFactory> paramList2, List<TypeAdapterFactory> paramList3, ToNumberStrategy paramToNumberStrategy1, ToNumberStrategy paramToNumberStrategy2, List<ReflectionAccessFilter> paramList) {
    this.excluder = paramExcluder;
    this.fieldNamingStrategy = paramFieldNamingStrategy;
    this.instanceCreators = paramMap;
    this.constructorConstructor = new ConstructorConstructor(paramMap, paramBoolean6, paramList);
    this.serializeNulls = paramBoolean1;
    this.complexMapKeySerialization = paramBoolean2;
    this.generateNonExecutableJson = paramBoolean3;
    this.htmlSafe = paramBoolean4;
    this.formattingStyle = paramFormattingStyle;
    this.strictness = paramStrictness;
    this.serializeSpecialFloatingPointValues = paramBoolean5;
    this.useJdkUnsafe = paramBoolean6;
    this.longSerializationPolicy = paramLongSerializationPolicy;
    this.datePattern = paramString;
    this.dateStyle = paramInt1;
    this.timeStyle = paramInt2;
    this.builderFactories = paramList1;
    this.builderHierarchyFactories = paramList2;
    this.objectToNumberStrategy = paramToNumberStrategy1;
    this.numberToNumberStrategy = paramToNumberStrategy2;
    this.reflectionFilters = paramList;
    ArrayList<TypeAdapterFactory> arrayList = new ArrayList();
    arrayList.add(TypeAdapters.JSON_ELEMENT_FACTORY);
    arrayList.add(ObjectTypeAdapter.getFactory(paramToNumberStrategy1));
    arrayList.add(paramExcluder);
    arrayList.addAll(paramList3);
    arrayList.add(TypeAdapters.STRING_FACTORY);
    arrayList.add(TypeAdapters.INTEGER_FACTORY);
    arrayList.add(TypeAdapters.BOOLEAN_FACTORY);
    arrayList.add(TypeAdapters.BYTE_FACTORY);
    arrayList.add(TypeAdapters.SHORT_FACTORY);
    TypeAdapter<Number> typeAdapter = longAdapter(paramLongSerializationPolicy);
    arrayList.add(TypeAdapters.newFactory(long.class, Long.class, typeAdapter));
    arrayList.add(TypeAdapters.newFactory(double.class, Double.class, doubleAdapter(paramBoolean5)));
    arrayList.add(TypeAdapters.newFactory(float.class, Float.class, floatAdapter(paramBoolean5)));
    arrayList.add(NumberTypeAdapter.getFactory(paramToNumberStrategy2));
    arrayList.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
    arrayList.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
    arrayList.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(typeAdapter)));
    arrayList.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(typeAdapter)));
    arrayList.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
    arrayList.add(TypeAdapters.CHARACTER_FACTORY);
    arrayList.add(TypeAdapters.STRING_BUILDER_FACTORY);
    arrayList.add(TypeAdapters.STRING_BUFFER_FACTORY);
    arrayList.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
    arrayList.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
    arrayList.add(TypeAdapters.newFactory(LazilyParsedNumber.class, TypeAdapters.LAZILY_PARSED_NUMBER));
    arrayList.add(TypeAdapters.URL_FACTORY);
    arrayList.add(TypeAdapters.URI_FACTORY);
    arrayList.add(TypeAdapters.UUID_FACTORY);
    arrayList.add(TypeAdapters.CURRENCY_FACTORY);
    arrayList.add(TypeAdapters.LOCALE_FACTORY);
    arrayList.add(TypeAdapters.INET_ADDRESS_FACTORY);
    arrayList.add(TypeAdapters.BIT_SET_FACTORY);
    arrayList.add(DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY);
    arrayList.add(TypeAdapters.CALENDAR_FACTORY);
    if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
      arrayList.add(SqlTypesSupport.TIME_FACTORY);
      arrayList.add(SqlTypesSupport.DATE_FACTORY);
      arrayList.add(SqlTypesSupport.TIMESTAMP_FACTORY);
    } 
    arrayList.add(ArrayTypeAdapter.FACTORY);
    arrayList.add(TypeAdapters.CLASS_FACTORY);
    arrayList.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
    arrayList.add(new MapTypeAdapterFactory(this.constructorConstructor, paramBoolean2));
    this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor);
    arrayList.add(this.jsonAdapterFactory);
    arrayList.add(TypeAdapters.ENUM_FACTORY);
    arrayList.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, paramFieldNamingStrategy, paramExcluder, this.jsonAdapterFactory, paramList));
    this.factories = Collections.unmodifiableList(arrayList);
  }
  
  public GsonBuilder newBuilder() {
    return new GsonBuilder(this);
  }
  
  @Deprecated
  public Excluder excluder() {
    return this.excluder;
  }
  
  public FieldNamingStrategy fieldNamingStrategy() {
    return this.fieldNamingStrategy;
  }
  
  public boolean serializeNulls() {
    return this.serializeNulls;
  }
  
  public boolean htmlSafe() {
    return this.htmlSafe;
  }
  
  private TypeAdapter<Number> doubleAdapter(boolean paramBoolean) {
    return paramBoolean ? TypeAdapters.DOUBLE : new TypeAdapter<Number>() {
        public Double read(JsonReader param1JsonReader) throws IOException {
          if (param1JsonReader.peek() == JsonToken.NULL) {
            param1JsonReader.nextNull();
            return null;
          } 
          return Double.valueOf(param1JsonReader.nextDouble());
        }
        
        public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
          if (param1Number == null) {
            param1JsonWriter.nullValue();
            return;
          } 
          double d = param1Number.doubleValue();
          Gson.checkValidFloatingPoint(d);
          param1JsonWriter.value(d);
        }
      };
  }
  
  private TypeAdapter<Number> floatAdapter(boolean paramBoolean) {
    return paramBoolean ? TypeAdapters.FLOAT : new TypeAdapter<Number>() {
        public Float read(JsonReader param1JsonReader) throws IOException {
          if (param1JsonReader.peek() == JsonToken.NULL) {
            param1JsonReader.nextNull();
            return null;
          } 
          return Float.valueOf((float)param1JsonReader.nextDouble());
        }
        
        public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
          if (param1Number == null) {
            param1JsonWriter.nullValue();
            return;
          } 
          float f = param1Number.floatValue();
          Gson.checkValidFloatingPoint(f);
          Number number = (param1Number instanceof Float) ? param1Number : Float.valueOf(f);
          param1JsonWriter.value(number);
        }
      };
  }
  
  static void checkValidFloatingPoint(double paramDouble) {
    if (Double.isNaN(paramDouble) || Double.isInfinite(paramDouble))
      throw new IllegalArgumentException(paramDouble + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method."); 
  }
  
  private static TypeAdapter<Number> longAdapter(LongSerializationPolicy paramLongSerializationPolicy) {
    return (paramLongSerializationPolicy == LongSerializationPolicy.DEFAULT) ? TypeAdapters.LONG : new TypeAdapter<Number>() {
        public Number read(JsonReader param1JsonReader) throws IOException {
          if (param1JsonReader.peek() == JsonToken.NULL) {
            param1JsonReader.nextNull();
            return null;
          } 
          return Long.valueOf(param1JsonReader.nextLong());
        }
        
        public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
          if (param1Number == null) {
            param1JsonWriter.nullValue();
            return;
          } 
          param1JsonWriter.value(param1Number.toString());
        }
        
        static {
        
        }
      };
  }
  
  private static TypeAdapter<AtomicLong> atomicLongAdapter(final TypeAdapter<Number> longAdapter) {
    return (new TypeAdapter<AtomicLong>() {
        public void write(JsonWriter param1JsonWriter, AtomicLong param1AtomicLong) throws IOException {
          longAdapter.write(param1JsonWriter, Long.valueOf(param1AtomicLong.get()));
        }
        
        public AtomicLong read(JsonReader param1JsonReader) throws IOException {
          Number number = longAdapter.read(param1JsonReader);
          return new AtomicLong(number.longValue());
        }
      }).nullSafe();
  }
  
  private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapter<Number> longAdapter) {
    return (new TypeAdapter<AtomicLongArray>() {
        public void write(JsonWriter param1JsonWriter, AtomicLongArray param1AtomicLongArray) throws IOException {
          param1JsonWriter.beginArray();
          byte b = 0;
          int i = param1AtomicLongArray.length();
          while (b < i) {
            longAdapter.write(param1JsonWriter, Long.valueOf(param1AtomicLongArray.get(b)));
            b++;
          } 
          param1JsonWriter.endArray();
        }
        
        public AtomicLongArray read(JsonReader param1JsonReader) throws IOException {
          ArrayList<Long> arrayList = new ArrayList();
          param1JsonReader.beginArray();
          while (param1JsonReader.hasNext()) {
            long l = ((Number)longAdapter.read(param1JsonReader)).longValue();
            arrayList.add(Long.valueOf(l));
          } 
          param1JsonReader.endArray();
          int i = arrayList.size();
          AtomicLongArray atomicLongArray = new AtomicLongArray(i);
          for (byte b = 0; b < i; b++)
            atomicLongArray.set(b, ((Long)arrayList.get(b)).longValue()); 
          return atomicLongArray;
        }
      }).nullSafe();
  }
  
  public <T> TypeAdapter<T> getAdapter(TypeToken<T> paramTypeToken) {
    Objects.requireNonNull(paramTypeToken, "type must not be null");
    TypeAdapter<T> typeAdapter1 = (TypeAdapter)this.typeTokenCache.get(paramTypeToken);
    if (typeAdapter1 != null)
      return typeAdapter1; 
    Map<Object, Object> map = (Map)this.threadLocalAdapterResults.get();
    boolean bool = false;
    if (map == null) {
      map = new HashMap<>();
      this.threadLocalAdapterResults.set(map);
      bool = true;
    } else {
      TypeAdapter<T> typeAdapter = (TypeAdapter)map.get(paramTypeToken);
      if (typeAdapter != null)
        return typeAdapter; 
    } 
    TypeAdapter<T> typeAdapter2 = null;
    try {
      FutureTypeAdapter<T> futureTypeAdapter = new FutureTypeAdapter();
      map.put(paramTypeToken, futureTypeAdapter);
      for (TypeAdapterFactory typeAdapterFactory : this.factories) {
        typeAdapter2 = typeAdapterFactory.create(this, paramTypeToken);
        if (typeAdapter2 != null) {
          futureTypeAdapter.setDelegate(typeAdapter2);
          map.put(paramTypeToken, typeAdapter2);
          break;
        } 
      } 
    } finally {
      if (bool)
        this.threadLocalAdapterResults.remove(); 
    } 
    if (typeAdapter2 == null)
      throw new IllegalArgumentException("GSON (2.12.1) cannot handle " + paramTypeToken); 
    if (bool)
      this.typeTokenCache.putAll(map); 
    return typeAdapter2;
  }
  
  public <T> TypeAdapter<T> getAdapter(Class<T> paramClass) {
    return getAdapter(TypeToken.get(paramClass));
  }
  
  public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory paramTypeAdapterFactory, TypeToken<T> paramTypeToken) {
    JsonAdapterAnnotationTypeAdapterFactory jsonAdapterAnnotationTypeAdapterFactory;
    Objects.requireNonNull(paramTypeAdapterFactory, "skipPast must not be null");
    Objects.requireNonNull(paramTypeToken, "type must not be null");
    if (this.jsonAdapterFactory.isClassJsonAdapterFactory(paramTypeToken, paramTypeAdapterFactory))
      jsonAdapterAnnotationTypeAdapterFactory = this.jsonAdapterFactory; 
    boolean bool = false;
    for (TypeAdapterFactory typeAdapterFactory : this.factories) {
      if (!bool) {
        if (typeAdapterFactory == jsonAdapterAnnotationTypeAdapterFactory)
          bool = true; 
        continue;
      } 
      TypeAdapter<T> typeAdapter = typeAdapterFactory.create(this, paramTypeToken);
      if (typeAdapter != null)
        return typeAdapter; 
    } 
    if (bool)
      throw new IllegalArgumentException("GSON cannot serialize or deserialize " + paramTypeToken); 
    return getAdapter(paramTypeToken);
  }
  
  public JsonElement toJsonTree(Object paramObject) {
    return (paramObject == null) ? JsonNull.INSTANCE : toJsonTree(paramObject, paramObject.getClass());
  }
  
  public JsonElement toJsonTree(Object paramObject, Type paramType) {
    JsonTreeWriter jsonTreeWriter = new JsonTreeWriter();
    toJson(paramObject, paramType, (JsonWriter)jsonTreeWriter);
    return jsonTreeWriter.get();
  }
  
  public String toJson(Object paramObject) {
    return (paramObject == null) ? toJson(JsonNull.INSTANCE) : toJson(paramObject, paramObject.getClass());
  }
  
  public String toJson(Object paramObject, Type paramType) {
    StringWriter stringWriter = new StringWriter();
    toJson(paramObject, paramType, stringWriter);
    return stringWriter.toString();
  }
  
  public void toJson(Object paramObject, Appendable paramAppendable) throws JsonIOException {
    if (paramObject != null) {
      toJson(paramObject, paramObject.getClass(), paramAppendable);
    } else {
      toJson(JsonNull.INSTANCE, paramAppendable);
    } 
  }
  
  public void toJson(Object paramObject, Type paramType, Appendable paramAppendable) throws JsonIOException {
    try {
      JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(paramAppendable));
      toJson(paramObject, paramType, jsonWriter);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
  }
  
  public void toJson(Object paramObject, Type paramType, JsonWriter paramJsonWriter) throws JsonIOException {
    TypeAdapter<?> typeAdapter = getAdapter(TypeToken.get(paramType));
    Strictness strictness = paramJsonWriter.getStrictness();
    if (this.strictness != null) {
      paramJsonWriter.setStrictness(this.strictness);
    } else if (paramJsonWriter.getStrictness() == Strictness.LEGACY_STRICT) {
      paramJsonWriter.setStrictness(Strictness.LENIENT);
    } 
    boolean bool1 = paramJsonWriter.isHtmlSafe();
    boolean bool2 = paramJsonWriter.getSerializeNulls();
    paramJsonWriter.setHtmlSafe(this.htmlSafe);
    paramJsonWriter.setSerializeNulls(this.serializeNulls);
    try {
      typeAdapter.write(paramJsonWriter, paramObject);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } catch (AssertionError assertionError) {
      throw new AssertionError("AssertionError (GSON 2.12.1): " + assertionError.getMessage(), assertionError);
    } finally {
      paramJsonWriter.setStrictness(strictness);
      paramJsonWriter.setHtmlSafe(bool1);
      paramJsonWriter.setSerializeNulls(bool2);
    } 
  }
  
  public String toJson(JsonElement paramJsonElement) {
    StringWriter stringWriter = new StringWriter();
    toJson(paramJsonElement, stringWriter);
    return stringWriter.toString();
  }
  
  public void toJson(JsonElement paramJsonElement, Appendable paramAppendable) throws JsonIOException {
    try {
      JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(paramAppendable));
      toJson(paramJsonElement, jsonWriter);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
  }
  
  public void toJson(JsonElement paramJsonElement, JsonWriter paramJsonWriter) throws JsonIOException {
    Strictness strictness = paramJsonWriter.getStrictness();
    boolean bool1 = paramJsonWriter.isHtmlSafe();
    boolean bool2 = paramJsonWriter.getSerializeNulls();
    paramJsonWriter.setHtmlSafe(this.htmlSafe);
    paramJsonWriter.setSerializeNulls(this.serializeNulls);
    if (this.strictness != null) {
      paramJsonWriter.setStrictness(this.strictness);
    } else if (paramJsonWriter.getStrictness() == Strictness.LEGACY_STRICT) {
      paramJsonWriter.setStrictness(Strictness.LENIENT);
    } 
    try {
      Streams.write(paramJsonElement, paramJsonWriter);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } catch (AssertionError assertionError) {
      throw new AssertionError("AssertionError (GSON 2.12.1): " + assertionError.getMessage(), assertionError);
    } finally {
      paramJsonWriter.setStrictness(strictness);
      paramJsonWriter.setHtmlSafe(bool1);
      paramJsonWriter.setSerializeNulls(bool2);
    } 
  }
  
  public JsonWriter newJsonWriter(Writer paramWriter) throws IOException {
    if (this.generateNonExecutableJson)
      paramWriter.write(")]}'\n"); 
    JsonWriter jsonWriter = new JsonWriter(paramWriter);
    jsonWriter.setFormattingStyle(this.formattingStyle);
    jsonWriter.setHtmlSafe(this.htmlSafe);
    jsonWriter.setStrictness((this.strictness == null) ? Strictness.LEGACY_STRICT : this.strictness);
    jsonWriter.setSerializeNulls(this.serializeNulls);
    return jsonWriter;
  }
  
  public JsonReader newJsonReader(Reader paramReader) {
    JsonReader jsonReader = new JsonReader(paramReader);
    jsonReader.setStrictness((this.strictness == null) ? Strictness.LEGACY_STRICT : this.strictness);
    return jsonReader;
  }
  
  public <T> T fromJson(String paramString, Class<T> paramClass) throws JsonSyntaxException {
    Object object = fromJson(paramString, TypeToken.get(paramClass));
    return Primitives.wrap(paramClass).cast(object);
  }
  
  public <T> T fromJson(String paramString, Type paramType) throws JsonSyntaxException {
    return fromJson(paramString, TypeToken.get(paramType));
  }
  
  public <T> T fromJson(String paramString, TypeToken<T> paramTypeToken) throws JsonSyntaxException {
    if (paramString == null)
      return null; 
    StringReader stringReader = new StringReader(paramString);
    return fromJson(stringReader, paramTypeToken);
  }
  
  public <T> T fromJson(Reader paramReader, Class<T> paramClass) throws JsonSyntaxException, JsonIOException {
    Object object = fromJson(paramReader, TypeToken.get(paramClass));
    return Primitives.wrap(paramClass).cast(object);
  }
  
  public <T> T fromJson(Reader paramReader, Type paramType) throws JsonIOException, JsonSyntaxException {
    return fromJson(paramReader, TypeToken.get(paramType));
  }
  
  public <T> T fromJson(Reader paramReader, TypeToken<T> paramTypeToken) throws JsonIOException, JsonSyntaxException {
    JsonReader jsonReader = newJsonReader(paramReader);
    T t = (T)fromJson(jsonReader, (TypeToken)paramTypeToken);
    assertFullConsumption(t, jsonReader);
    return t;
  }
  
  public <T> T fromJson(JsonReader paramJsonReader, Type paramType) throws JsonIOException, JsonSyntaxException {
    return fromJson(paramJsonReader, TypeToken.get(paramType));
  }
  
  public <T> T fromJson(JsonReader paramJsonReader, TypeToken<T> paramTypeToken) throws JsonIOException, JsonSyntaxException {
    boolean bool = true;
    Strictness strictness = paramJsonReader.getStrictness();
    if (this.strictness != null) {
      paramJsonReader.setStrictness(this.strictness);
    } else if (paramJsonReader.getStrictness() == Strictness.LEGACY_STRICT) {
      paramJsonReader.setStrictness(Strictness.LENIENT);
    } 
    try {
      JsonToken jsonToken = paramJsonReader.peek();
      bool = false;
      TypeAdapter<T> typeAdapter = getAdapter(paramTypeToken);
      return typeAdapter.read(paramJsonReader);
    } catch (EOFException eOFException) {
      if (bool)
        return null; 
      throw new JsonSyntaxException(eOFException);
    } catch (IllegalStateException illegalStateException) {
      throw new JsonSyntaxException(illegalStateException);
    } catch (IOException iOException) {
      throw new JsonSyntaxException(iOException);
    } catch (AssertionError assertionError) {
      throw new AssertionError("AssertionError (GSON 2.12.1): " + assertionError.getMessage(), assertionError);
    } finally {
      paramJsonReader.setStrictness(strictness);
    } 
  }
  
  public <T> T fromJson(JsonElement paramJsonElement, Class<T> paramClass) throws JsonSyntaxException {
    Object object = fromJson(paramJsonElement, TypeToken.get(paramClass));
    return Primitives.wrap(paramClass).cast(object);
  }
  
  public <T> T fromJson(JsonElement paramJsonElement, Type paramType) throws JsonSyntaxException {
    return fromJson(paramJsonElement, TypeToken.get(paramType));
  }
  
  public <T> T fromJson(JsonElement paramJsonElement, TypeToken<T> paramTypeToken) throws JsonSyntaxException {
    return (paramJsonElement == null) ? null : fromJson((JsonReader)new JsonTreeReader(paramJsonElement), paramTypeToken);
  }
  
  private static void assertFullConsumption(Object paramObject, JsonReader paramJsonReader) {
    try {
      if (paramObject != null && paramJsonReader.peek() != JsonToken.END_DOCUMENT)
        throw new JsonSyntaxException("JSON document was not fully consumed."); 
    } catch (MalformedJsonException malformedJsonException) {
      throw new JsonSyntaxException(malformedJsonException);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
  }
  
  public String toString() {
    return "{serializeNulls:" + this.serializeNulls + ",factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
  }
  
  static class FutureTypeAdapter<T> extends SerializationDelegatingTypeAdapter<T> {
    private TypeAdapter<T> delegate = null;
    
    public void setDelegate(TypeAdapter<T> param1TypeAdapter) {
      if (this.delegate != null)
        throw new AssertionError("Delegate is already set"); 
      this.delegate = param1TypeAdapter;
    }
    
    private TypeAdapter<T> delegate() {
      TypeAdapter<T> typeAdapter = this.delegate;
      if (typeAdapter == null)
        throw new IllegalStateException("Adapter for type with cyclic dependency has been used before dependency has been resolved"); 
      return typeAdapter;
    }
    
    public TypeAdapter<T> getSerializationDelegate() {
      return delegate();
    }
    
    public T read(JsonReader param1JsonReader) throws IOException {
      return delegate().read(param1JsonReader);
    }
    
    public void write(JsonWriter param1JsonWriter, T param1T) throws IOException {
      delegate().write(param1JsonWriter, param1T);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\Gson.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */