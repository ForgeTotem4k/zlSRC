package com.sun.jna;

import java.lang.reflect.Field;

public class StructureReadContext extends FromNativeContext {
  private Structure structure;
  
  private Field field;
  
  StructureReadContext(Structure paramStructure, Field paramField) {
    super(paramField.getType());
    this.structure = paramStructure;
    this.field = paramField;
  }
  
  public Structure getStructure() {
    return this.structure;
  }
  
  public Field getField() {
    return this.field;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\StructureReadContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */