package com.sun.jna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArray extends Memory implements Function.PostCallRead {
  private String encoding;
  
  private List<NativeString> natives = new ArrayList<>();
  
  private Object[] original;
  
  public StringArray(String[] paramArrayOfString) {
    this(paramArrayOfString, false);
  }
  
  public StringArray(String[] paramArrayOfString, boolean paramBoolean) {
    this((Object[])paramArrayOfString, paramBoolean ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
  }
  
  public StringArray(String[] paramArrayOfString, String paramString) {
    this((Object[])paramArrayOfString, paramString);
  }
  
  public StringArray(WString[] paramArrayOfWString) {
    this((Object[])paramArrayOfWString, "--WIDE-STRING--");
  }
  
  private StringArray(Object[] paramArrayOfObject, String paramString) {
    super(((paramArrayOfObject.length + 1) * Native.POINTER_SIZE));
    this.original = paramArrayOfObject;
    this.encoding = paramString;
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      Pointer pointer = null;
      if (paramArrayOfObject[b] != null) {
        NativeString nativeString = new NativeString(paramArrayOfObject[b].toString(), paramString);
        this.natives.add(nativeString);
        pointer = nativeString.getPointer();
      } 
      setPointer((Native.POINTER_SIZE * b), pointer);
    } 
    setPointer((Native.POINTER_SIZE * paramArrayOfObject.length), null);
  }
  
  public void read() {
    boolean bool1 = this.original instanceof WString[];
    boolean bool2 = "--WIDE-STRING--".equals(this.encoding);
    for (byte b = 0; b < this.original.length; b++) {
      WString wString;
      Pointer pointer = getPointer((b * Native.POINTER_SIZE));
      String str = null;
      if (pointer != null) {
        str = bool2 ? pointer.getWideString(0L) : pointer.getString(0L, this.encoding);
        if (bool1)
          wString = new WString(str); 
      } 
      this.original[b] = wString;
    } 
  }
  
  public String toString() {
    boolean bool = "--WIDE-STRING--".equals(this.encoding);
    null = bool ? "const wchar_t*[]" : "const char*[]";
    return null + Arrays.<Object>asList(this.original);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\StringArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */