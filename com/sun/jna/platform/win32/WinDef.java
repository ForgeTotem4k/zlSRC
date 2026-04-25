package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.awt.Rectangle;

public interface WinDef {
  public static final int MAX_PATH = 260;
  
  public static class HGLRCByReference extends WinNT.HANDLEByReference {
    public HGLRCByReference() {}
    
    public HGLRCByReference(WinDef.HGLRC param1HGLRC) {
      super(param1HGLRC);
    }
  }
  
  public static class HGLRC extends WinNT.HANDLE {
    public HGLRC() {}
    
    public HGLRC(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class CHARByReference extends com.sun.jna.ptr.ByReference {
    public CHARByReference() {
      this(new WinDef.CHAR(0L));
    }
    
    public CHARByReference(WinDef.CHAR param1CHAR) {
      super(1);
      setValue(param1CHAR);
    }
    
    public void setValue(WinDef.CHAR param1CHAR) {
      getPointer().setByte(0L, param1CHAR.byteValue());
    }
    
    public WinDef.CHAR getValue() {
      return new WinDef.CHAR(getPointer().getByte(0L));
    }
  }
  
  public static class CHAR extends IntegerType implements Comparable<CHAR> {
    public static final int SIZE = 1;
    
    public CHAR() {
      this(0L);
    }
    
    public CHAR(byte param1Byte) {
      this((param1Byte & 0xFF));
    }
    
    public CHAR(long param1Long) {
      super(1, param1Long, false);
    }
    
    public int compareTo(CHAR param1CHAR) {
      return compare(this, param1CHAR);
    }
  }
  
  public static class BYTE extends UCHAR {
    public BYTE() {
      this(0L);
    }
    
    public BYTE(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class UCHAR extends IntegerType implements Comparable<UCHAR> {
    public static final int SIZE = 1;
    
    public UCHAR() {
      this(0L);
    }
    
    public UCHAR(char param1Char) {
      this((param1Char & 0xFF));
    }
    
    public UCHAR(long param1Long) {
      super(1, param1Long, true);
    }
    
    public int compareTo(UCHAR param1UCHAR) {
      return compare(this, param1UCHAR);
    }
  }
  
  public static class BOOLByReference extends com.sun.jna.ptr.ByReference {
    public BOOLByReference() {
      this(new WinDef.BOOL(0L));
    }
    
    public BOOLByReference(WinDef.BOOL param1BOOL) {
      super(4);
      setValue(param1BOOL);
    }
    
    public void setValue(WinDef.BOOL param1BOOL) {
      getPointer().setInt(0L, param1BOOL.intValue());
    }
    
    public WinDef.BOOL getValue() {
      return new WinDef.BOOL(getPointer().getInt(0L));
    }
  }
  
  public static class BOOL extends IntegerType implements Comparable<BOOL> {
    public static final int SIZE = 4;
    
    public BOOL() {
      this(0L);
    }
    
    public BOOL(boolean param1Boolean) {
      this(param1Boolean ? 1L : 0L);
    }
    
    public BOOL(long param1Long) {
      super(4, param1Long, false);
      assert param1Long == 0L || param1Long == 1L;
    }
    
    public boolean booleanValue() {
      return (intValue() > 0);
    }
    
    public String toString() {
      return Boolean.toString(booleanValue());
    }
    
    public int compareTo(BOOL param1BOOL) {
      return compare(this, param1BOOL);
    }
    
    public static int compare(BOOL param1BOOL1, BOOL param1BOOL2) {
      return (param1BOOL1 == param1BOOL2) ? 0 : ((param1BOOL1 == null) ? 1 : ((param1BOOL2 == null) ? -1 : compare(param1BOOL1.booleanValue(), param1BOOL2.booleanValue())));
    }
    
    public static int compare(BOOL param1BOOL, boolean param1Boolean) {
      return (param1BOOL == null) ? 1 : compare(param1BOOL.booleanValue(), param1Boolean);
    }
    
    public static int compare(boolean param1Boolean1, boolean param1Boolean2) {
      return (param1Boolean1 == param1Boolean2) ? 0 : (param1Boolean1 ? 1 : -1);
    }
  }
  
  public static class LCID extends DWORD {
    public LCID() {
      super(0L);
    }
    
    public LCID(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class SCODEByReference extends com.sun.jna.ptr.ByReference {
    public SCODEByReference() {
      this(new WinDef.SCODE(0L));
    }
    
    public SCODEByReference(WinDef.SCODE param1SCODE) {
      super(WinDef.SCODE.SIZE);
      setValue(param1SCODE);
    }
    
    public void setValue(WinDef.SCODE param1SCODE) {
      getPointer().setInt(0L, param1SCODE.intValue());
    }
    
    public WinDef.SCODE getValue() {
      return new WinDef.SCODE(getPointer().getInt(0L));
    }
  }
  
  public static class SCODE extends ULONG {
    public SCODE() {
      this(0L);
    }
    
    public SCODE(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class UINTByReference extends com.sun.jna.ptr.ByReference {
    public UINTByReference() {
      this(new WinDef.UINT(0L));
    }
    
    public UINTByReference(WinDef.UINT param1UINT) {
      super(4);
      setValue(param1UINT);
    }
    
    public void setValue(WinDef.UINT param1UINT) {
      getPointer().setInt(0L, param1UINT.intValue());
    }
    
    public WinDef.UINT getValue() {
      return new WinDef.UINT(getPointer().getInt(0L));
    }
  }
  
  public static class UINT extends IntegerType implements Comparable<UINT> {
    public static final int SIZE = 4;
    
    public UINT() {
      this(0L);
    }
    
    public UINT(long param1Long) {
      super(4, param1Long, true);
    }
    
    public int compareTo(UINT param1UINT) {
      return compare(this, param1UINT);
    }
  }
  
  public static class SHORT extends IntegerType implements Comparable<SHORT> {
    public static final int SIZE = 2;
    
    public SHORT() {
      this(0L);
    }
    
    public SHORT(long param1Long) {
      super(2, param1Long, false);
    }
    
    public int compareTo(SHORT param1SHORT) {
      return compare(this, param1SHORT);
    }
  }
  
  public static class USHORTByReference extends com.sun.jna.ptr.ByReference {
    public USHORTByReference() {
      this(new WinDef.USHORT(0L));
    }
    
    public USHORTByReference(WinDef.USHORT param1USHORT) {
      super(2);
      setValue(param1USHORT);
    }
    
    public USHORTByReference(short param1Short) {
      super(2);
      setValue(new WinDef.USHORT(param1Short));
    }
    
    public void setValue(WinDef.USHORT param1USHORT) {
      getPointer().setShort(0L, param1USHORT.shortValue());
    }
    
    public WinDef.USHORT getValue() {
      return new WinDef.USHORT(getPointer().getShort(0L));
    }
  }
  
  public static class USHORT extends IntegerType implements Comparable<USHORT> {
    public static final int SIZE = 2;
    
    public USHORT() {
      this(0L);
    }
    
    public USHORT(long param1Long) {
      super(2, param1Long, true);
    }
    
    public int compareTo(USHORT param1USHORT) {
      return compare(this, param1USHORT);
    }
  }
  
  @FieldOrder({"x", "y"})
  public static class POINT extends Structure {
    public int x;
    
    public int y;
    
    public POINT() {}
    
    public POINT(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public POINT(int param1Int1, int param1Int2) {
      this.x = param1Int1;
      this.y = param1Int2;
    }
    
    public static class ByValue extends POINT implements Structure.ByValue {
      public ByValue() {}
      
      public ByValue(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByValue(int param2Int1, int param2Int2) {
        super(param2Int1, param2Int2);
      }
    }
    
    public static class ByReference extends POINT implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByReference(int param2Int1, int param2Int2) {
        super(param2Int1, param2Int2);
      }
    }
  }
  
  public static class LPVOID extends PointerType {
    public LPVOID() {}
    
    public LPVOID(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class PVOID extends PointerType {
    public PVOID() {}
    
    public PVOID(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class ATOM extends WORD {
    public ATOM() {
      this(0L);
    }
    
    public ATOM(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class HBRUSH extends WinNT.HANDLE {
    public HBRUSH() {}
    
    public HBRUSH(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class DWORDLONG extends IntegerType implements Comparable<DWORDLONG> {
    public static final int SIZE = 8;
    
    public DWORDLONG() {
      this(0L);
    }
    
    public DWORDLONG(long param1Long) {
      super(8, param1Long, true);
    }
    
    public int compareTo(DWORDLONG param1DWORDLONG) {
      return compare(this, param1DWORDLONG);
    }
  }
  
  public static class ULONGLONGByReference extends com.sun.jna.ptr.ByReference {
    public ULONGLONGByReference() {
      this(new WinDef.ULONGLONG(0L));
    }
    
    public ULONGLONGByReference(WinDef.ULONGLONG param1ULONGLONG) {
      super(8);
      setValue(param1ULONGLONG);
    }
    
    public void setValue(WinDef.ULONGLONG param1ULONGLONG) {
      getPointer().setLong(0L, param1ULONGLONG.longValue());
    }
    
    public WinDef.ULONGLONG getValue() {
      return new WinDef.ULONGLONG(getPointer().getLong(0L));
    }
  }
  
  public static class ULONGLONG extends IntegerType implements Comparable<ULONGLONG> {
    public static final int SIZE = 8;
    
    public ULONGLONG() {
      this(0L);
    }
    
    public ULONGLONG(long param1Long) {
      super(8, param1Long, true);
    }
    
    public int compareTo(ULONGLONG param1ULONGLONG) {
      return compare(this, param1ULONGLONG);
    }
  }
  
  public static class ULONGByReference extends com.sun.jna.ptr.ByReference {
    public ULONGByReference() {
      this(new WinDef.ULONG(0L));
    }
    
    public ULONGByReference(WinDef.ULONG param1ULONG) {
      super(WinDef.ULONG.SIZE);
      setValue(param1ULONG);
    }
    
    public void setValue(WinDef.ULONG param1ULONG) {
      getPointer().setInt(0L, param1ULONG.intValue());
    }
    
    public WinDef.ULONG getValue() {
      return new WinDef.ULONG(getPointer().getInt(0L));
    }
  }
  
  public static class ULONG extends IntegerType implements Comparable<ULONG> {
    public static final int SIZE = Native.LONG_SIZE;
    
    public ULONG() {
      this(0L);
    }
    
    public ULONG(long param1Long) {
      super(SIZE, param1Long, true);
    }
    
    public int compareTo(ULONG param1ULONG) {
      return compare(this, param1ULONG);
    }
  }
  
  @FieldOrder({"left", "top", "right", "bottom"})
  public static class RECT extends Structure {
    public int left;
    
    public int top;
    
    public int right;
    
    public int bottom;
    
    public Rectangle toRectangle() {
      return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
    }
    
    public String toString() {
      return "[(" + this.left + "," + this.top + ")(" + this.right + "," + this.bottom + ")]";
    }
  }
  
  public static class WPARAM extends UINT_PTR {
    public WPARAM() {
      this(0L);
    }
    
    public WPARAM(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class UINT_PTR extends IntegerType {
    public UINT_PTR() {
      super(Native.POINTER_SIZE);
    }
    
    public UINT_PTR(long param1Long) {
      super(Native.POINTER_SIZE, param1Long, true);
    }
    
    public Pointer toPointer() {
      return Pointer.createConstant(longValue());
    }
  }
  
  public static class INT_PTR extends IntegerType {
    public INT_PTR() {
      super(Native.POINTER_SIZE);
    }
    
    public INT_PTR(long param1Long) {
      super(Native.POINTER_SIZE, param1Long);
    }
    
    public Pointer toPointer() {
      return Pointer.createConstant(longValue());
    }
  }
  
  public static class LRESULT extends BaseTSD.LONG_PTR {
    public LRESULT() {
      this(0L);
    }
    
    public LRESULT(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class LPARAM extends BaseTSD.LONG_PTR {
    public LPARAM() {
      this(0L);
    }
    
    public LPARAM(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class HKL extends WinNT.HANDLE {
    public HKL() {}
    
    public HKL(Pointer param1Pointer) {
      super(param1Pointer);
    }
    
    public HKL(int param1Int) {
      super(Pointer.createConstant(param1Int));
    }
    
    public int getLanguageIdentifier() {
      return (int)(Pointer.nativeValue(getPointer()) & 0xFFFFL);
    }
    
    public int getDeviceHandle() {
      return (int)(Pointer.nativeValue(getPointer()) >> 16L & 0xFFFFL);
    }
    
    public String toString() {
      return String.format("%08x", new Object[] { Long.valueOf(Pointer.nativeValue(getPointer())) });
    }
  }
  
  public static class HFONT extends WinNT.HANDLE {
    public HFONT() {}
    
    public HFONT(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HMODULE extends HINSTANCE {}
  
  public static class HINSTANCE extends WinNT.HANDLE {}
  
  public static class HWND extends WinNT.HANDLE {
    public HWND() {}
    
    public HWND(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HRGN extends WinNT.HANDLE {
    public HRGN() {}
    
    public HRGN(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HBITMAP extends WinNT.HANDLE {
    public HBITMAP() {}
    
    public HBITMAP(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HPALETTE extends WinNT.HANDLE {
    public HPALETTE() {}
    
    public HPALETTE(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HRSRC extends WinNT.HANDLE {
    public HRSRC() {}
    
    public HRSRC(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HPEN extends WinNT.HANDLE {
    public HPEN() {}
    
    public HPEN(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HMENU extends WinNT.HANDLE {
    public HMENU() {}
    
    public HMENU(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HCURSOR extends HICON {
    public HCURSOR() {}
    
    public HCURSOR(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HICON extends WinNT.HANDLE {
    public HICON() {}
    
    public HICON(WinNT.HANDLE param1HANDLE) {
      this(param1HANDLE.getPointer());
    }
    
    public HICON(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HDC extends WinNT.HANDLE {
    public HDC() {}
    
    public HDC(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class LONGLONGByReference extends com.sun.jna.ptr.ByReference {
    public LONGLONGByReference() {
      this(new WinDef.LONGLONG(0L));
    }
    
    public LONGLONGByReference(WinDef.LONGLONG param1LONGLONG) {
      super(WinDef.LONGLONG.SIZE);
      setValue(param1LONGLONG);
    }
    
    public void setValue(WinDef.LONGLONG param1LONGLONG) {
      getPointer().setLong(0L, param1LONGLONG.longValue());
    }
    
    public WinDef.LONGLONG getValue() {
      return new WinDef.LONGLONG(getPointer().getLong(0L));
    }
  }
  
  public static class LONGLONG extends IntegerType implements Comparable<LONGLONG> {
    public static final int SIZE = Native.LONG_SIZE * 2;
    
    public LONGLONG() {
      this(0L);
    }
    
    public LONGLONG(long param1Long) {
      super(8, param1Long, false);
    }
    
    public int compareTo(LONGLONG param1LONGLONG) {
      return compare(this, param1LONGLONG);
    }
  }
  
  public static class LONGByReference extends com.sun.jna.ptr.ByReference {
    public LONGByReference() {
      this(new WinDef.LONG(0L));
    }
    
    public LONGByReference(WinDef.LONG param1LONG) {
      super(WinDef.LONG.SIZE);
      setValue(param1LONG);
    }
    
    public void setValue(WinDef.LONG param1LONG) {
      getPointer().setInt(0L, param1LONG.intValue());
    }
    
    public WinDef.LONG getValue() {
      return new WinDef.LONG(getPointer().getInt(0L));
    }
  }
  
  public static class LONG extends IntegerType implements Comparable<LONG> {
    public static final int SIZE = Native.LONG_SIZE;
    
    public LONG() {
      this(0L);
    }
    
    public LONG(long param1Long) {
      super(SIZE, param1Long);
    }
    
    public int compareTo(LONG param1LONG) {
      return compare(this, param1LONG);
    }
  }
  
  public static class DWORDByReference extends com.sun.jna.ptr.ByReference {
    public DWORDByReference() {
      this(new WinDef.DWORD(0L));
    }
    
    public DWORDByReference(WinDef.DWORD param1DWORD) {
      super(4);
      setValue(param1DWORD);
    }
    
    public void setValue(WinDef.DWORD param1DWORD) {
      getPointer().setInt(0L, param1DWORD.intValue());
    }
    
    public WinDef.DWORD getValue() {
      return new WinDef.DWORD(getPointer().getInt(0L));
    }
  }
  
  public static class DWORD extends IntegerType implements Comparable<DWORD> {
    public static final int SIZE = 4;
    
    public DWORD() {
      this(0L);
    }
    
    public DWORD(long param1Long) {
      super(4, param1Long, true);
    }
    
    public WinDef.WORD getLow() {
      return new WinDef.WORD(longValue() & 0xFFFFL);
    }
    
    public WinDef.WORD getHigh() {
      return new WinDef.WORD(longValue() >> 16L & 0xFFFFL);
    }
    
    public int compareTo(DWORD param1DWORD) {
      return compare(this, param1DWORD);
    }
  }
  
  public static class WORDByReference extends com.sun.jna.ptr.ByReference {
    public WORDByReference() {
      this(new WinDef.WORD(0L));
    }
    
    public WORDByReference(WinDef.WORD param1WORD) {
      super(2);
      setValue(param1WORD);
    }
    
    public void setValue(WinDef.WORD param1WORD) {
      getPointer().setShort(0L, param1WORD.shortValue());
    }
    
    public WinDef.WORD getValue() {
      return new WinDef.WORD(getPointer().getShort(0L));
    }
  }
  
  public static class WORD extends IntegerType implements Comparable<WORD> {
    public static final int SIZE = 2;
    
    public WORD() {
      this(0L);
    }
    
    public WORD(long param1Long) {
      super(2, param1Long, true);
    }
    
    public int compareTo(WORD param1WORD) {
      return compare(this, param1WORD);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinDef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */