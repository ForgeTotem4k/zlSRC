package com.google.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class FieldAttributes {
  private final Field field;
  
  public FieldAttributes(Field paramField) {
    this.field = Objects.<Field>requireNonNull(paramField);
  }
  
  public Class<?> getDeclaringClass() {
    return this.field.getDeclaringClass();
  }
  
  public String getName() {
    return this.field.getName();
  }
  
  public Type getDeclaredType() {
    return this.field.getGenericType();
  }
  
  public Class<?> getDeclaredClass() {
    return this.field.getType();
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
    return this.field.getAnnotation(paramClass);
  }
  
  public Collection<Annotation> getAnnotations() {
    return Arrays.asList(this.field.getAnnotations());
  }
  
  public boolean hasModifier(int paramInt) {
    return ((this.field.getModifiers() & paramInt) != 0);
  }
  
  public String toString() {
    return this.field.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\FieldAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */