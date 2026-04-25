package com.sun.jna;

import java.lang.reflect.Field;

public class StructureWriteContext extends ToNativeContext {
  private Structure struct;
  
  private Field field;
  
  StructureWriteContext(Structure paramStructure, Field paramField) {
    this.struct = paramStructure;
    this.field = paramField;
  }
  
  public Structure getStructure() {
    return this.struct;
  }
  
  public Field getField() {
    return this.field;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\StructureWriteContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */