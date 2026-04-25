package com.sun.jna;

public abstract class PointerType implements NativeMapped {
  private Pointer pointer = Pointer.NULL;
  
  protected PointerType() {}
  
  protected PointerType(Pointer paramPointer) {}
  
  public Class<?> nativeType() {
    return Pointer.class;
  }
  
  public Object toNative() {
    return getPointer();
  }
  
  public Pointer getPointer() {
    return this.pointer;
  }
  
  public void setPointer(Pointer paramPointer) {
    this.pointer = paramPointer;
  }
  
  public Object fromNative(Object paramObject, FromNativeContext paramFromNativeContext) {
    if (paramObject == null)
      return null; 
    PointerType pointerType = (PointerType)Klass.newInstance(getClass());
    pointerType.pointer = (Pointer)paramObject;
    return pointerType;
  }
  
  public int hashCode() {
    return (this.pointer != null) ? this.pointer.hashCode() : 0;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof PointerType) {
      Pointer pointer = ((PointerType)paramObject).getPointer();
      return (this.pointer == null) ? ((pointer == null)) : this.pointer.equals(pointer);
    } 
    return false;
  }
  
  public String toString() {
    return (this.pointer == null) ? "NULL" : (this.pointer.toString() + " (" + super.toString() + ")");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\PointerType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */