package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
  private static final double IGNORE_VERSIONS = -1.0D;
  
  public static final Excluder DEFAULT = new Excluder();
  
  private double version = -1.0D;
  
  private int modifiers = 136;
  
  private boolean serializeInnerClasses = true;
  
  private boolean requireExpose;
  
  private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
  
  private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();
  
  protected Excluder clone() {
    try {
      return (Excluder)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError(cloneNotSupportedException);
    } 
  }
  
  public Excluder withVersion(double paramDouble) {
    Excluder excluder = clone();
    excluder.version = paramDouble;
    return excluder;
  }
  
  public Excluder withModifiers(int... paramVarArgs) {
    Excluder excluder = clone();
    excluder.modifiers = 0;
    for (int i : paramVarArgs)
      excluder.modifiers |= i; 
    return excluder;
  }
  
  public Excluder disableInnerClassSerialization() {
    Excluder excluder = clone();
    excluder.serializeInnerClasses = false;
    return excluder;
  }
  
  public Excluder excludeFieldsWithoutExposeAnnotation() {
    Excluder excluder = clone();
    excluder.requireExpose = true;
    return excluder;
  }
  
  public Excluder withExclusionStrategy(ExclusionStrategy paramExclusionStrategy, boolean paramBoolean1, boolean paramBoolean2) {
    Excluder excluder = clone();
    if (paramBoolean1) {
      excluder.serializationStrategies = new ArrayList<>(this.serializationStrategies);
      excluder.serializationStrategies.add(paramExclusionStrategy);
    } 
    if (paramBoolean2) {
      excluder.deserializationStrategies = new ArrayList<>(this.deserializationStrategies);
      excluder.deserializationStrategies.add(paramExclusionStrategy);
    } 
    return excluder;
  }
  
  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
    Class<?> clazz = type.getRawType();
    final boolean skipSerialize = excludeClass(clazz, true);
    final boolean skipDeserialize = excludeClass(clazz, false);
    return (!bool1 && !bool2) ? null : new TypeAdapter<T>() {
        private volatile TypeAdapter<T> delegate;
        
        public T read(JsonReader param1JsonReader) throws IOException {
          if (skipDeserialize) {
            param1JsonReader.skipValue();
            return null;
          } 
          return (T)delegate().read(param1JsonReader);
        }
        
        public void write(JsonWriter param1JsonWriter, T param1T) throws IOException {
          if (skipSerialize) {
            param1JsonWriter.nullValue();
            return;
          } 
          delegate().write(param1JsonWriter, param1T);
        }
        
        private TypeAdapter<T> delegate() {
          TypeAdapter<T> typeAdapter = this.delegate;
          return (typeAdapter != null) ? typeAdapter : (this.delegate = gson.getDelegateAdapter(Excluder.this, type));
        }
      };
  }
  
  public boolean excludeField(Field paramField, boolean paramBoolean) {
    if ((this.modifiers & paramField.getModifiers()) != 0)
      return true; 
    if (this.version != -1.0D && !isValidVersion(paramField.<Since>getAnnotation(Since.class), paramField.<Until>getAnnotation(Until.class)))
      return true; 
    if (paramField.isSynthetic())
      return true; 
    if (this.requireExpose) {
      Expose expose = paramField.<Expose>getAnnotation(Expose.class);
      if (expose == null || (paramBoolean ? !expose.serialize() : !expose.deserialize()))
        return true; 
    } 
    if (excludeClass(paramField.getType(), paramBoolean))
      return true; 
    List<ExclusionStrategy> list = paramBoolean ? this.serializationStrategies : this.deserializationStrategies;
    if (!list.isEmpty()) {
      FieldAttributes fieldAttributes = new FieldAttributes(paramField);
      for (ExclusionStrategy exclusionStrategy : list) {
        if (exclusionStrategy.shouldSkipField(fieldAttributes))
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean excludeClass(Class<?> paramClass, boolean paramBoolean) {
    if (this.version != -1.0D && !isValidVersion(paramClass.<Since>getAnnotation(Since.class), paramClass.<Until>getAnnotation(Until.class)))
      return true; 
    if (!this.serializeInnerClasses && isInnerClass(paramClass))
      return true; 
    if (!paramBoolean && !Enum.class.isAssignableFrom(paramClass) && ReflectionHelper.isAnonymousOrNonStaticLocal(paramClass))
      return true; 
    List<ExclusionStrategy> list = paramBoolean ? this.serializationStrategies : this.deserializationStrategies;
    for (ExclusionStrategy exclusionStrategy : list) {
      if (exclusionStrategy.shouldSkipClass(paramClass))
        return true; 
    } 
    return false;
  }
  
  private static boolean isInnerClass(Class<?> paramClass) {
    return (paramClass.isMemberClass() && !ReflectionHelper.isStatic(paramClass));
  }
  
  private boolean isValidVersion(Since paramSince, Until paramUntil) {
    return (isValidSince(paramSince) && isValidUntil(paramUntil));
  }
  
  private boolean isValidSince(Since paramSince) {
    if (paramSince != null) {
      double d = paramSince.value();
      return (this.version >= d);
    } 
    return true;
  }
  
  private boolean isValidUntil(Until paramUntil) {
    if (paramUntil != null) {
      double d = paramUntil.value();
      return (this.version < d);
    } 
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\Excluder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */