package com.sun.jna;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Union extends Structure {
  private Structure.StructField activeField;
  
  protected Union() {}
  
  protected Union(Pointer paramPointer) {
    super(paramPointer);
  }
  
  protected Union(Pointer paramPointer, int paramInt) {
    super(paramPointer, paramInt);
  }
  
  protected Union(TypeMapper paramTypeMapper) {
    super(paramTypeMapper);
  }
  
  protected Union(Pointer paramPointer, int paramInt, TypeMapper paramTypeMapper) {
    super(paramPointer, paramInt, paramTypeMapper);
  }
  
  protected List<String> getFieldOrder() {
    List<Field> list = getFieldList();
    ArrayList<String> arrayList = new ArrayList(list.size());
    for (Field field : list)
      arrayList.add(field.getName()); 
    return arrayList;
  }
  
  public void setType(Class<?> paramClass) {
    ensureAllocated();
    for (Structure.StructField structField : fields().values()) {
      if (structField.type == paramClass) {
        this.activeField = structField;
        return;
      } 
    } 
    throw new IllegalArgumentException("No field of type " + paramClass + " in " + this);
  }
  
  public void setType(String paramString) {
    ensureAllocated();
    Structure.StructField structField = fields().get(paramString);
    if (structField != null) {
      this.activeField = structField;
    } else {
      throw new IllegalArgumentException("No field named " + paramString + " in " + this);
    } 
  }
  
  public Object readField(String paramString) {
    ensureAllocated();
    setType(paramString);
    return super.readField(paramString);
  }
  
  public void writeField(String paramString) {
    ensureAllocated();
    setType(paramString);
    super.writeField(paramString);
  }
  
  public void writeField(String paramString, Object paramObject) {
    ensureAllocated();
    setType(paramString);
    super.writeField(paramString, paramObject);
  }
  
  public Object getTypedValue(Class<?> paramClass) {
    ensureAllocated();
    for (Structure.StructField structField : fields().values()) {
      if (structField.type == paramClass) {
        this.activeField = structField;
        read();
        return getFieldValue(this.activeField.field);
      } 
    } 
    throw new IllegalArgumentException("No field of type " + paramClass + " in " + this);
  }
  
  public Object setTypedValue(Object paramObject) {
    Structure.StructField structField = findField(paramObject.getClass());
    if (structField != null) {
      this.activeField = structField;
      setFieldValue(structField.field, paramObject);
      return this;
    } 
    throw new IllegalArgumentException("No field of type " + paramObject.getClass() + " in " + this);
  }
  
  private Structure.StructField findField(Class<?> paramClass) {
    ensureAllocated();
    for (Structure.StructField structField : fields().values()) {
      if (structField.type.isAssignableFrom(paramClass))
        return structField; 
    } 
    return null;
  }
  
  protected void writeField(Structure.StructField paramStructField) {
    if (paramStructField == this.activeField)
      super.writeField(paramStructField); 
  }
  
  protected Object readField(Structure.StructField paramStructField) {
    return (paramStructField == this.activeField || (!Structure.class.isAssignableFrom(paramStructField.type) && !String.class.isAssignableFrom(paramStructField.type) && !WString.class.isAssignableFrom(paramStructField.type))) ? super.readField(paramStructField) : null;
  }
  
  protected int getNativeAlignment(Class<?> paramClass, Object paramObject, boolean paramBoolean) {
    return super.getNativeAlignment(paramClass, paramObject, true);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Union.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */