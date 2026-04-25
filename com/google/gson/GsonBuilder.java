package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GsonBuilder {
  private Excluder excluder = Excluder.DEFAULT;
  
  private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
  
  private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
  
  private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<>();
  
  private final List<TypeAdapterFactory> factories = new ArrayList<>();
  
  private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<>();
  
  private boolean serializeNulls = false;
  
  private String datePattern = Gson.DEFAULT_DATE_PATTERN;
  
  private int dateStyle = 2;
  
  private int timeStyle = 2;
  
  private boolean complexMapKeySerialization = false;
  
  private boolean serializeSpecialFloatingPointValues = false;
  
  private boolean escapeHtmlChars = true;
  
  private FormattingStyle formattingStyle = Gson.DEFAULT_FORMATTING_STYLE;
  
  private boolean generateNonExecutableJson = false;
  
  private Strictness strictness = Gson.DEFAULT_STRICTNESS;
  
  private boolean useJdkUnsafe = true;
  
  private ToNumberStrategy objectToNumberStrategy = Gson.DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
  
  private ToNumberStrategy numberToNumberStrategy = Gson.DEFAULT_NUMBER_TO_NUMBER_STRATEGY;
  
  private final ArrayDeque<ReflectionAccessFilter> reflectionFilters = new ArrayDeque<>();
  
  public GsonBuilder() {}
  
  GsonBuilder(Gson paramGson) {
    this.excluder = paramGson.excluder;
    this.fieldNamingPolicy = paramGson.fieldNamingStrategy;
    this.instanceCreators.putAll(paramGson.instanceCreators);
    this.serializeNulls = paramGson.serializeNulls;
    this.complexMapKeySerialization = paramGson.complexMapKeySerialization;
    this.generateNonExecutableJson = paramGson.generateNonExecutableJson;
    this.escapeHtmlChars = paramGson.htmlSafe;
    this.formattingStyle = paramGson.formattingStyle;
    this.strictness = paramGson.strictness;
    this.serializeSpecialFloatingPointValues = paramGson.serializeSpecialFloatingPointValues;
    this.longSerializationPolicy = paramGson.longSerializationPolicy;
    this.datePattern = paramGson.datePattern;
    this.dateStyle = paramGson.dateStyle;
    this.timeStyle = paramGson.timeStyle;
    this.factories.addAll(paramGson.builderFactories);
    this.hierarchyFactories.addAll(paramGson.builderHierarchyFactories);
    this.useJdkUnsafe = paramGson.useJdkUnsafe;
    this.objectToNumberStrategy = paramGson.objectToNumberStrategy;
    this.numberToNumberStrategy = paramGson.numberToNumberStrategy;
    this.reflectionFilters.addAll(paramGson.reflectionFilters);
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setVersion(double paramDouble) {
    if (Double.isNaN(paramDouble) || paramDouble < 0.0D)
      throw new IllegalArgumentException("Invalid version: " + paramDouble); 
    this.excluder = this.excluder.withVersion(paramDouble);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder excludeFieldsWithModifiers(int... paramVarArgs) {
    Objects.requireNonNull(paramVarArgs);
    this.excluder = this.excluder.withModifiers(paramVarArgs);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder generateNonExecutableJson() {
    this.generateNonExecutableJson = true;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
    this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder serializeNulls() {
    this.serializeNulls = true;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder enableComplexMapKeySerialization() {
    this.complexMapKeySerialization = true;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder disableInnerClassSerialization() {
    this.excluder = this.excluder.disableInnerClassSerialization();
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy paramLongSerializationPolicy) {
    this.longSerializationPolicy = Objects.<LongSerializationPolicy>requireNonNull(paramLongSerializationPolicy);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy paramFieldNamingPolicy) {
    return setFieldNamingStrategy(paramFieldNamingPolicy);
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy paramFieldNamingStrategy) {
    this.fieldNamingPolicy = Objects.<FieldNamingStrategy>requireNonNull(paramFieldNamingStrategy);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setObjectToNumberStrategy(ToNumberStrategy paramToNumberStrategy) {
    this.objectToNumberStrategy = Objects.<ToNumberStrategy>requireNonNull(paramToNumberStrategy);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setNumberToNumberStrategy(ToNumberStrategy paramToNumberStrategy) {
    this.numberToNumberStrategy = Objects.<ToNumberStrategy>requireNonNull(paramToNumberStrategy);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setExclusionStrategies(ExclusionStrategy... paramVarArgs) {
    Objects.requireNonNull(paramVarArgs);
    for (ExclusionStrategy exclusionStrategy : paramVarArgs)
      this.excluder = this.excluder.withExclusionStrategy(exclusionStrategy, true, true); 
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy paramExclusionStrategy) {
    Objects.requireNonNull(paramExclusionStrategy);
    this.excluder = this.excluder.withExclusionStrategy(paramExclusionStrategy, true, false);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy paramExclusionStrategy) {
    Objects.requireNonNull(paramExclusionStrategy);
    this.excluder = this.excluder.withExclusionStrategy(paramExclusionStrategy, false, true);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setPrettyPrinting() {
    return setFormattingStyle(FormattingStyle.PRETTY);
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setFormattingStyle(FormattingStyle paramFormattingStyle) {
    this.formattingStyle = Objects.<FormattingStyle>requireNonNull(paramFormattingStyle);
    return this;
  }
  
  @Deprecated
  @InlineMe(replacement = "this.setStrictness(Strictness.LENIENT)", imports = {"com.google.gson.Strictness"})
  @CanIgnoreReturnValue
  public GsonBuilder setLenient() {
    return setStrictness(Strictness.LENIENT);
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setStrictness(Strictness paramStrictness) {
    this.strictness = Objects.<Strictness>requireNonNull(paramStrictness);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder disableHtmlEscaping() {
    this.escapeHtmlChars = false;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(String paramString) {
    if (paramString != null)
      try {
        new SimpleDateFormat(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new IllegalArgumentException("The date pattern '" + paramString + "' is not valid", illegalArgumentException);
      }  
    this.datePattern = paramString;
    return this;
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(int paramInt) {
    this.dateStyle = checkDateFormatStyle(paramInt);
    this.datePattern = null;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(int paramInt1, int paramInt2) {
    this.dateStyle = checkDateFormatStyle(paramInt1);
    this.timeStyle = checkDateFormatStyle(paramInt2);
    this.datePattern = null;
    return this;
  }
  
  private static int checkDateFormatStyle(int paramInt) {
    if (paramInt < 0 || paramInt > 3)
      throw new IllegalArgumentException("Invalid style: " + paramInt); 
    return paramInt;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder registerTypeAdapter(Type paramType, Object paramObject) {
    Objects.requireNonNull(paramType);
    .Gson.Preconditions.checkArgument((paramObject instanceof JsonSerializer || paramObject instanceof JsonDeserializer || paramObject instanceof InstanceCreator || paramObject instanceof TypeAdapter));
    if (hasNonOverridableAdapter(paramType))
      throw new IllegalArgumentException("Cannot override built-in adapter for " + paramType); 
    if (paramObject instanceof InstanceCreator)
      this.instanceCreators.put(paramType, (InstanceCreator)paramObject); 
    if (paramObject instanceof JsonSerializer || paramObject instanceof JsonDeserializer) {
      TypeToken typeToken = TypeToken.get(paramType);
      this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, paramObject));
    } 
    if (paramObject instanceof TypeAdapter) {
      TypeAdapterFactory typeAdapterFactory = TypeAdapters.newFactory(TypeToken.get(paramType), (TypeAdapter)paramObject);
      this.factories.add(typeAdapterFactory);
    } 
    return this;
  }
  
  private static boolean hasNonOverridableAdapter(Type paramType) {
    return (paramType == Object.class);
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory paramTypeAdapterFactory) {
    Objects.requireNonNull(paramTypeAdapterFactory);
    this.factories.add(paramTypeAdapterFactory);
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder registerTypeHierarchyAdapter(Class<?> paramClass, Object paramObject) {
    Objects.requireNonNull(paramClass);
    .Gson.Preconditions.checkArgument((paramObject instanceof JsonSerializer || paramObject instanceof JsonDeserializer || paramObject instanceof TypeAdapter));
    if (paramObject instanceof JsonDeserializer || paramObject instanceof JsonSerializer)
      this.hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(paramClass, paramObject)); 
    if (paramObject instanceof TypeAdapter) {
      TypeAdapterFactory typeAdapterFactory = TypeAdapters.newTypeHierarchyFactory(paramClass, (TypeAdapter)paramObject);
      this.factories.add(typeAdapterFactory);
    } 
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder serializeSpecialFloatingPointValues() {
    this.serializeSpecialFloatingPointValues = true;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder disableJdkUnsafe() {
    this.useJdkUnsafe = false;
    return this;
  }
  
  @CanIgnoreReturnValue
  public GsonBuilder addReflectionAccessFilter(ReflectionAccessFilter paramReflectionAccessFilter) {
    Objects.requireNonNull(paramReflectionAccessFilter);
    this.reflectionFilters.addFirst(paramReflectionAccessFilter);
    return this;
  }
  
  public Gson create() {
    ArrayList<TypeAdapterFactory> arrayList1 = new ArrayList(this.factories.size() + this.hierarchyFactories.size() + 3);
    arrayList1.addAll(this.factories);
    Collections.reverse(arrayList1);
    ArrayList<TypeAdapterFactory> arrayList2 = new ArrayList<>(this.hierarchyFactories);
    Collections.reverse(arrayList2);
    arrayList1.addAll(arrayList2);
    addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, arrayList1);
    return new Gson(this.excluder, this.fieldNamingPolicy, new HashMap<>(this.instanceCreators), this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.formattingStyle, this.strictness, this.serializeSpecialFloatingPointValues, this.useJdkUnsafe, this.longSerializationPolicy, this.datePattern, this.dateStyle, this.timeStyle, new ArrayList<>(this.factories), new ArrayList<>(this.hierarchyFactories), arrayList1, this.objectToNumberStrategy, this.numberToNumberStrategy, new ArrayList<>(this.reflectionFilters));
  }
  
  private static void addTypeAdaptersForDate(String paramString, int paramInt1, int paramInt2, List<TypeAdapterFactory> paramList) {
    TypeAdapterFactory typeAdapterFactory1;
    boolean bool = SqlTypesSupport.SUPPORTS_SQL_TYPES;
    TypeAdapterFactory typeAdapterFactory2 = null;
    TypeAdapterFactory typeAdapterFactory3 = null;
    if (paramString != null && !paramString.trim().isEmpty()) {
      typeAdapterFactory1 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(paramString);
      if (bool) {
        typeAdapterFactory2 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(paramString);
        typeAdapterFactory3 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(paramString);
      } 
    } else if (paramInt1 != 2 || paramInt2 != 2) {
      typeAdapterFactory1 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(paramInt1, paramInt2);
      if (bool) {
        typeAdapterFactory2 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(paramInt1, paramInt2);
        typeAdapterFactory3 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(paramInt1, paramInt2);
      } 
    } else {
      return;
    } 
    paramList.add(typeAdapterFactory1);
    if (bool) {
      paramList.add(typeAdapterFactory2);
      paramList.add(typeAdapterFactory3);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\GsonBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */