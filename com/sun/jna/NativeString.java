package com.sun.jna;

class NativeString implements CharSequence, Comparable {
  static final String WIDE_STRING = "--WIDE-STRING--";
  
  private Pointer pointer;
  
  private String encoding;
  
  public NativeString(String paramString) {
    this(paramString, Native.getDefaultStringEncoding());
  }
  
  public NativeString(String paramString, boolean paramBoolean) {
    this(paramString, paramBoolean ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
  }
  
  public NativeString(WString paramWString) {
    this(paramWString.toString(), "--WIDE-STRING--");
  }
  
  public NativeString(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("String must not be null"); 
    this.encoding = paramString2;
    if ("--WIDE-STRING--".equals(this.encoding)) {
      int i = (paramString1.length() + 1) * Native.WCHAR_SIZE;
      this.pointer = new StringMemory(i);
      this.pointer.setWideString(0L, paramString1);
    } else {
      byte[] arrayOfByte = Native.getBytes(paramString1, paramString2);
      this.pointer = new StringMemory((arrayOfByte.length + 1));
      this.pointer.write(0L, arrayOfByte, 0, arrayOfByte.length);
      this.pointer.setByte(arrayOfByte.length, (byte)0);
    } 
  }
  
  public int hashCode() {
    return toString().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof CharSequence) ? ((compareTo(paramObject) == 0)) : false;
  }
  
  public String toString() {
    boolean bool = "--WIDE-STRING--".equals(this.encoding);
    return bool ? this.pointer.getWideString(0L) : this.pointer.getString(0L, this.encoding);
  }
  
  public Pointer getPointer() {
    return this.pointer;
  }
  
  public char charAt(int paramInt) {
    return toString().charAt(paramInt);
  }
  
  public int length() {
    return toString().length();
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) {
    return toString().subSequence(paramInt1, paramInt2);
  }
  
  public int compareTo(Object paramObject) {
    return (paramObject == null) ? 1 : toString().compareTo(paramObject.toString());
  }
  
  private class StringMemory extends Memory {
    public StringMemory(long param1Long) {
      super(param1Long);
    }
    
    public String toString() {
      return NativeString.this.toString();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\NativeString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */